package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;

/**
 * Interfaz Observer para recepción de notificaciones y alertas críticas de la flota.
 */
public interface ObservadorMision {
    void notificarFallo(DroneAcuatico drone, String motivo);
}
