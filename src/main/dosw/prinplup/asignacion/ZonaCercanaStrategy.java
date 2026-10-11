package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Estrategia de selección que prioriza drones disponibles que ya se encuentren
 * en la misma zona geográfica de la misión para minimizar tiempos de traslado.
 */
public class ZonaCercanaStrategy implements EstrategiaSeleccion {

    public static final int UMBRAL_BATERIA_MINIMA = 20;

    @Override
    public Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> dronesDisponibles, String zona) {
        if (dronesDisponibles == null || dronesDisponibles.isEmpty()) {
            return Optional.empty();
        }

        // 1. Intentar encontrar drone disponible en la misma zona con batería segura
        Optional<DroneAcuatico> enMismaZona = dronesDisponibles.stream()
                .filter(d -> d.isDisponible() && d.getBateria() >= UMBRAL_BATERIA_MINIMA)
                .filter(d -> d.getZona() != null && d.getZona().equalsIgnoreCase(zona))
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));

        if (enMismaZona.isPresent()) {
            return enMismaZona;
        }

        // 2. Si no hay en la misma zona, seleccionar el de mayor batería general disponible
        return dronesDisponibles.stream()
                .filter(d -> d.isDisponible() && d.getBateria() >= UMBRAL_BATERIA_MINIMA)
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));
    }
}
