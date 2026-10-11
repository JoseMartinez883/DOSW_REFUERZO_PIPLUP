package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;

/**
 * Observador concreto que representa la consola principal del Centro de Control de AquaPort.
 */
public class CentroControlObserver implements ObservadorMision {

    @Override
    public void notificarFallo(DroneAcuatico drone, String motivo) {
        String info = (drone != null)
                ? "Drone " + drone.getId() + " (" + drone.getTipo() + "). Batería: " + drone.getBateria() + "% | Zona: " + drone.getZona()
                : "Sin drone físico asignado";
        System.out.println("[CENTRO DE CONTROL] ALERTA CRÍTICA: " + info + ". Motivo: " + motivo);
    }
}
