package main.dosw.piplup;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio03 {

    private static final Logger LOGGER = Logger.getLogger(Ejercicio03.class.getName());
    private static final String EMBALSE_NORTE = "Embalse Norte";

    public static void main(String[] args) {
        DroneAcuatico droneValido = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85, true, EMBALSE_NORTE);
        DroneAcuatico droneNoDisponible = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        Mision misionValida = new Mision.Builder()
                .id("M-001")
                .drone(droneValido)
                .puntoPartida(EMBALSE_NORTE)
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        LOGGER.log(Level.INFO, "Misión construida correctamente: {0} - {1}",
                new Object[]{misionValida.getId(), misionValida.getEstadoMision()});

        // 2. Validación de disponibilidad del drone
        try {
            new Mision.Builder()
                    .id("M-002")
                    .drone(droneNoDisponible)
                    .puntoPartida("Laguna Sur")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.log(Level.INFO, "Excepción capturada esperada: {0}", e.getMessage());
        }

        // 3. Validación de campos obligatorios o vacíos
        try {
            new Mision.Builder()
                    .id("")
                    .drone(droneValido)
                    .puntoPartida(EMBALSE_NORTE)
                    .puntoLlegada("   ")
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.log(Level.INFO, "Excepción capturada esperada: {0}", e.getMessage());
        }
    }
}