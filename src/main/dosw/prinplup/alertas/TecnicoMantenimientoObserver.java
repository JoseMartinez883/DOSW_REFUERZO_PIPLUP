package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;
import java.util.logging.Logger;

/**
 * Observador concreto que despacha órdenes de servicio técnico y mantenimiento.
 */
public class TecnicoMantenimientoObserver implements ObservadorMision {

    private static final Logger LOGGER = Logger.getLogger(TecnicoMantenimientoObserver.class.getName());

    @Override
    public void notificarFallo(DroneAcuatico drone, String motivo) {
        String droneId = (drone != null) ? drone.getId() : "N/A";
        String zona = (drone != null) ? drone.getZona() : "Zona General";
        LOGGER.warning(() -> "[TÉCNICO MANTENIMIENTO] ORDEN DE SERVICIO: Asignada revisión física urgente al drone " +
                droneId + " en zona " + zona + " por causa: " + motivo);
    }
}
