package main.dosw.piplup;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercisio1 {

    private static final Logger LOGGER = Logger.getLogger(Ejercisio1.class.getName());
    private static final String MODELO_DRONE = "Aqua-Ranger 100";

    public static void main(String[] args){
        List<DroneAcuatico> flota = List.of(
            new DroneAcuatico("AR-01", MODELO_DRONE, 92, true,  "Embalse Norte"),
            new DroneAcuatico("AR-02", MODELO_DRONE, 45, true,  "Canal Central"),
            new DroneAcuatico("AR-03", MODELO_DRONE, 18, false, "Laguna Sur"),
            new DroneAcuatico("AR-04", MODELO_DRONE, 73, true,  "Punto Ribereño Este")
        );

        ConsultorFlota consultor = new ConsultorFlota();

        LOGGER.info("1. Disponibles con batería >= 35% ordenados:");
        LOGGER.log(Level.INFO, "{0}", consultor.obtainDroneAvailableWithEnoughBattery(flota));

        LOGGER.info("\n2. IDs de drones disponibles:");
        LOGGER.log(Level.INFO, "{0}", consultor.obtainIdsDronesAvailable(flota));

        LOGGER.info("\n3. ¿Existe algún disponible con batería >= 35%?");
        LOGGER.log(Level.INFO, "{0}", consultor.existsDroneAvailableWithEnoughBattery(flota));

        LOGGER.info("\n4. Total de drones disponibles:");
        LOGGER.log(Level.INFO, "{0}", consultor.amountDronesAvailable(flota));

        LOGGER.info("\n5. Drone con mayor batería:");
        LOGGER.log(Level.INFO, "{0}", consultor.obtainDroneWithLargestBattery(flota));
    }
}
