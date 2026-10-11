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
     */
    public Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> flota, String zona) {
        return invocarEstrategia(flota, zona);
    }

    /**
     * Asigna automáticamente un dron para la misión usando la flota interna configurada.
     */
    public Optional<DroneAcuatico> asignar(Mision mision) {
        return asignar(mision, this.flota);
    }

    /**
     * Asigna automáticamente un dron para la misión a partir de una lista explícita de candidatos.
     */
    public Optional<DroneAcuatico> asignar(Mision mision, List<DroneAcuatico> flotac) {
        if (mision == null) {
            throw new IllegalArgumentException("La misión no puede ser nula.");
        }

        List<DroneAcuatico> candidatos = (flotac != null) ? flotac : List.of();
        int umbralMinimo = (mision.prioridad() == Prioridad.CRITICA)
                ? UMBRAL_BATERIA_CRITICA
                : UMBRAL_BATERIA_NOMINAL;

        Optional<DroneAcuatico> seleccionadoOpt = invocarEstrategia(candidatos, mision.zonaDestino());

        if (seleccionadoOpt.isEmpty()) {
            notificarSiCritica(mision);
            return Optional.empty();
        }

        DroneAcuatico seleccionado = seleccionadoOpt.get();

        if (seleccionado.getEstado() == EstadoDrone.FALLO) {
            Optional<DroneAcuatico> alternativoOpt = resolverFalloConAlternativo(seleccionado, candidatos, mision, umbralMinimo);
            if (alternativoOpt.isEmpty()) {
                notificarSiCritica(mision);
                return Optional.empty();
            }
            seleccionado = alternativoOpt.get();
        }

        if (!esDroneAptoParaMision(seleccionado, mision, umbralMinimo)) {
            notificarSiCritica(mision);
            return Optional.empty();
        }

        seleccionado.setEstado(EstadoDrone.EN_MISION);
        return Optional.of(seleccionado);
    }

    private Optional<DroneAcuatico> resolverFalloConAlternativo(
            DroneAcuatico enFallo, List<DroneAcuatico> candidatos, Mision mision, int umbralMinimo
    ) {
        notificador.emitirAlertaFallo(
                enFallo,
                "Drone " + enFallo.getId() + " en estado FALLO detectado durante proceso de asignación"
        );

        final String idFallo = enFallo.getId();
        List<DroneAcuatico> alternativos = candidatos.stream()
                .filter(d -> !d.getId().equals(idFallo))
                .filter(d -> esDroneAptoParaMision(d, mision, umbralMinimo))
                .toList();

        if (alternativos.isEmpty()) {
            return Optional.empty();
        }

        return invocarEstrategia(alternativos, mision.zonaDestino());
    }

    private boolean esDroneAptoParaMision(DroneAcuatico drone, Mision mision, int umbralMinimo) {
        return drone.getEstado() == EstadoDrone.DISPONIBLE
                && drone.getBateria() >= umbralMinimo
                && drone.puedeCargar(mision.pesoGramos());
    }

    private void notificarSiCritica(Mision mision) {
        if (mision.prioridad() == Prioridad.CRITICA) {
            notificador.notificarFalloAsignacion(mision);
        }
    }

    private Optional<DroneAcuatico> invocarEstrategia(List<DroneAcuatico> candidatos, String zona) {
        if (estrategia == null) {
            return Optional.empty();
        }
        return Objects.requireNonNullElseGet(
                estrategia.seleccionar(candidatos, zona),
                Optional::empty
        );
    }

    /**
     * Registra una misión validando el estado del drone asignado.
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
