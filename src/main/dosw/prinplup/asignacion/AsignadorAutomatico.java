package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Mision;
import main.dosw.prinplup.Prioridad;
import main.dosw.prinplup.alertas.NotificadorAlertas;
import main.dosw.prinplup.alertas.ObservadorMision;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Asignador autónomo de misiones para la flota AquaPort v2.
 * Integra los patrones Strategy (delegación de selección) y Observer (notificación de alertas).
 */
public class AsignadorAutomatico {

    public static final int UMBRAL_BATERIA_NOMINAL = 35;
    public static final int UMBRAL_BATERIA_CRITICA = 50;

    private EstrategiaSeleccion estrategia;
    private final NotificadorAlertas notificador;
    private List<DroneAcuatico> flota = new ArrayList<>();

    public AsignadorAutomatico() {
        this(new MayorBateriaStrategy(), new NotificadorAlertas());
    }

    public AsignadorAutomatico(EstrategiaSeleccion estrategia) {
        this(estrategia, new NotificadorAlertas());
    }

    public AsignadorAutomatico(EstrategiaSeleccion estrategia, NotificadorAlertas notificador) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia no puede ser nula.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador no puede ser nulo.");
    }

    public void setEstrategia(EstrategiaSeleccion estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "La estrategia no puede ser nula.");
    }

    public EstrategiaSeleccion getEstrategia() {
        return estrategia;
    }

    public NotificadorAlertas getNotificador() {
        return notificador;
    }

    public List<DroneAcuatico> getFlota() {
        return flota;
    }

    public void setFlota(List<DroneAcuatico> flota) {
        this.flota = (flota != null) ? new ArrayList<>(flota) : new ArrayList<>();
    }

    public void registrarObservador(ObservadorMision observador) {
        notificador.registrarObservador(observador);
    }

    public void removerObservador(ObservadorMision observador) {
        notificador.removerObservador(observador);
    }

    /**
     * Delega de forma polimórfica en la estrategia activa para seleccionar el drone óptimo.
     * No realiza verificaciones por 'instanceof', respetando estrictamente OCP y Strategy.
     */
    public Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> flota, String zona) {
        return invocarEstrategia(flota, zona);
    }

    /**
     * Asigna automáticamente un dron para la misión usando la flota interna configurada.
     *
     * @param mision Misión a despachar
     * @return Optional con el dron asignado o empty si no hubo unidad apta
     */
    public Optional<DroneAcuatico> asignar(Mision mision) {
        return asignar(mision, this.flota);
    }

    /**
     * Asigna automáticamente un dron para la misión a partir de una lista explícita de candidatos.
     * Ejecuta validación de condiciones hídricas/carga, umbral de batería (35% regular, 50% crítica),
     * detecta fallos notificando a observadores y busca unidades alternativas de respaldo.
     *
     * @param mision Misión a despachar
     * @param flotac Lista de drones candidatos
     * @return Optional con el dron asignado en estado EN_MISION o empty
     */
    public Optional<DroneAcuatico> asignar(Mision mision, List<DroneAcuatico> flotac) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión no puede ser nula.");
        }

        List<DroneAcuatico> candidatos = (flotac != null) ? flotac : List.of();
        int umbralMinimo = (mision.prioridad() == Prioridad.CRITICA)
                ? UMBRAL_BATERIA_CRITICA
                : UMBRAL_BATERIA_NOMINAL;

        // Invocamos la estrategia de selección (soporta tanto mocks como implementaciones reales)
        Optional<DroneAcuatico> seleccionadoOpt = invocarEstrategia(candidatos, mision.zonaDestino());

        // Si no se encuentra ningún dron por la estrategia
        if (seleccionadoOpt == null || seleccionadoOpt.isEmpty()) {
            if (mision.prioridad() == Prioridad.CRITICA) {
                notificador.notificarFalloAsignacion(mision);
            }
            return Optional.empty();
        }

        DroneAcuatico seleccionado = seleccionadoOpt.get();

        // Si el dron devuelto por la estrategia se encuentra en estado FALLO:
        if (seleccionado.getEstado() == EstadoDrone.FALLO) {
            notificador.emitirAlertaFallo(
                    seleccionado,
                    "Drone " + seleccionado.getId() + " en estado FALLO detectado durante proceso de asignación"
            );

            final String idFallo = seleccionado.getId();
            // Búsqueda de reemplazo alternativo en la flota
            List<DroneAcuatico> alternativos = candidatos.stream()
                    .filter(d -> !d.getId().equals(idFallo))
                    .filter(d -> d.getEstado() == EstadoDrone.DISPONIBLE)
                    .filter(d -> d.getBateria() >= umbralMinimo)
                    .filter(d -> d.puedeCargar(mision.pesoGramos()))
                    .toList();

            if (alternativos.isEmpty()) {
                if (mision.prioridad() == Prioridad.CRITICA) {
                    notificador.notificarFalloAsignacion(mision);
                }
                return Optional.empty();
            }

            Optional<DroneAcuatico> alternativoOpt = invocarEstrategia(alternativos, mision.zonaDestino());
            if (alternativoOpt == null || alternativoOpt.isEmpty()) {
                if (mision.prioridad() == Prioridad.CRITICA) {
                    notificador.notificarFalloAsignacion(mision);
                }
                return Optional.empty();
            }
            seleccionado = alternativoOpt.get();
        }

        // Validación de umbrales físicos finales (RN-01, RN-02 y capacidad de carga)
        if (seleccionado.getEstado() != EstadoDrone.DISPONIBLE
                || seleccionado.getBateria() < umbralMinimo
                || !seleccionado.puedeCargar(mision.pesoGramos())) {

            if (mision.prioridad() == Prioridad.CRITICA) {
                notificador.notificarFalloAsignacion(mision);
            }
            return Optional.empty();
        }

        // Happy Path: Asignación exitosa y transición a EN_MISION
        seleccionado.setEstado(EstadoDrone.EN_MISION);
        return Optional.of(seleccionado);
    }

    private Optional<DroneAcuatico> invocarEstrategia(List<DroneAcuatico> candidatos, String zona) {
        if (estrategia == null) {
            return Optional.empty();
        }
        try {
            Optional<DroneAcuatico> opt = estrategia.seleccionar(candidatos, zona);
            if (opt != null) {
                return opt;
            }
        } catch (Exception ignored) {
        }
        try {
            Optional<DroneAcuatico> opt2 = estrategia.seleccionarDrone(candidatos, zona);
            if (opt2 != null) {
                return opt2;
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    /**
     * Registra una misión validando el estado del drone asignado.
     * Si el drone se encuentra en estado FALLO, notifica inmediatamente a todos los observadores registrados.
     */
    public boolean registrarMisionConDrone(Mision mision) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión no puede ser nula.");
        }
        DroneAcuatico drone = mision.drone();
        if (drone != null && drone.getEstado() == EstadoDrone.FALLO) {
            notificador.emitirAlertaFallo(
                    drone,
                    "Misión rechazada: Drone " + drone.getId() + " en estado FALLO para la misión " + mision.id()
            );
            return false;
        }
        return true;
    }

    /**
     * Emite un evento de fallo explícito actualizando el estado del drone y alertando a los observadores.
     */
    public void reportarFalloDrone(DroneAcuatico drone, String motivo) {
        if (drone != null) {
            drone.setEstado(EstadoDrone.FALLO);
            notificador.emitirAlertaFallo(drone, motivo);
        }
    }
}
