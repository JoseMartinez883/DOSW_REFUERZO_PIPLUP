package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;

/**
 * Observador concreto que despacha órdenes de servicio técnico y mantenimiento.
 */
public class TecnicoMantenimientoObserver implements ObservadorMision {

    @Override
    public void notificarFallo(DroneAcuatico drone, String motivo) {
        System.out.println("[TÉCNICO MANTENIMIENTO] ORDEN DE SERVICIO: Asignada revisión física urgente al drone " +
                drone.getId() + " en zona " + drone.getZona() + " por causa: " + motivo);
    }
}
