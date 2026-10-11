package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Mision;
import main.dosw.prinplup.alertas.NotificadorAlertas;
import main.dosw.prinplup.alertas.ObservadorMision;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Asignador autónomo de misiones para la flota AquaPort v2.
 * Integra los patrones Strategy (delegación de selección) y Observer (notificación de alertas).
 */
public class AsignadorAutomatico {

    private EstrategiaSeleccion estrategia;
    private final NotificadorAlertas notificador;

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
        return estrategia.seleccionarDrone(flota, zona);
    }

    /**
     * Registra una misión validando el estado del drone asignado.
     * Si el drone se encuentra en estado FALLO, notifica inmediatamente a todos los observadores registrados.
     *
     * @param mision Misión a registrar
     * @return true si la misión fue registrada sin incidencias, false si el drone está en fallo
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
