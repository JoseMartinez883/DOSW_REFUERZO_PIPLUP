package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;
import java.util.logging.Logger;

/**
 * Observador concreto que representa la consola principal del Centro de Control de AquaPort.
 */
public class CentroControlObserver implements ObservadorMision {

    private static final Logger LOGGER = Logger.getLogger(CentroControlObserver.class.getName());

    @Override
    public void notificarFallo(DroneAcuatico drone, String motivo) {
        String info = (drone != null)
                ? "Drone " + drone.getId() + " (" + drone.getTipo() + "). Batería: " + drone.getBateria() + "% | Zona: " + drone.getZona()
                : "Sin drone físico asignado";
        LOGGER.warning(() -> "[CENTRO DE CONTROL] ALERTA CRÍTICA: " + info + ". Motivo: " + motivo);
    }
}
