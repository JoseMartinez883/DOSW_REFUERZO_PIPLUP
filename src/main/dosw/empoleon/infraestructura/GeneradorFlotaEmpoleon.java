package main.dosw.empoleon.infraestructura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;

/**
 * Generador de infraestructura para instanciar la flota completa de 60 drones
 * distribuidos uniformemente entre las 4 zonas hídricas de AquaPort Enterprise.
 */
public final class GeneradorFlotaEmpoleon {

    private GeneradorFlotaEmpoleon() {
        // Constructor privado para clase utilitaria
    }

    public static List<DroneEnterprise> generarFlota60Drones() {
        List<DroneEnterprise> flota = new ArrayList<>(60);

        // 1. Embalse de Investigación (15 Buceadores)
        agregarDronesZona(flota, "BUC", TipoDrone.BUCEADOR, 300,
                ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1, 15);

        // 2. Red de Canales (15 Superficiales)
        agregarDronesZona(flota, "SUP", TipoDrone.SUPERFICIAL, 500,
                ZonaHidrica.RED_CANALES.getNombre(), 16, 30);

        // 3. Laguna de Reserva (15 Semisumergidos)
        agregarDronesZona(flota, "SEMI", TipoDrone.SEMISUMERGIDO, 1500,
                ZonaHidrica.LAGUNA_RESERVA.getNombre(), 31, 45);

        // 4. Laboratorio Hídrico Central (15 Drones de enlace rápido)
        agregarDronesZona(flota, "LAB", TipoDrone.SUPERFICIAL, 600,
                ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 46, 60);

        return Collections.unmodifiableList(flota);
    }

    private static void agregarDronesZona(
            List<DroneEnterprise> lista,
            String prefijo,
            TipoDrone tipo,
            int capacidad,
            String zona,
            int inicio,
            int fin
    ) {
        for (int i = inicio; i <= fin; i++) {
            int bateria = 40 + ((i * 7) % 60); // Baterías entre 40% y 99%
            int entregas = (i * 3) % 25;       // Historial de entregas realistas
            EstadoDrone estado = (i % 8 == 0) ? EstadoDrone.MANTENIMIENTO : EstadoDrone.DISPONIBLE;
            lista.add(new DroneEnterprise(
                    "DRN-ENT-" + i,
                    "AquaEnterprise-" + prefijo,
                    tipo,
                    bateria,
                    estado,
                    zona,
                    capacidad,
                    entregas
            ));
        }
    }
}
