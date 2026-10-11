package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;

/**
 * Observador concreto que representa la consola principal del Centro de Control de AquaPort.
 */
public class CentroControlObserver implements ObservadorMision {

    @Override
    public void notificarFallo(DroneAcuatico drone, String motivo) {
        System.out.println("[CENTRO DE CONTROL] ALERTA CRÍTICA: Drone " + drone.getId() +
                " (" + drone.getTipo() + ") en estado FALLO. Motivo: " + motivo +
                ". Batería: " + drone.getBateria() + "% | Zona: " + drone.getZona());
    }
}
