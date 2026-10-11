package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.Mision;

/**
 * Interfaz Observer para recepción de notificaciones y alertas críticas de la flota.
 */
public interface ObservadorMision {
    void notificarFallo(DroneAcuatico drone, String motivo);

    default void notificarFalloAsignacion(Mision mision) {
        notificarFallo(mision != null ? mision.drone() : null,
                "Fallo al asignar misión " + (mision != null ? mision.id() : "desconocida"));
    }
}
