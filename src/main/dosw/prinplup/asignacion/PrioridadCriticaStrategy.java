package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Estrategia de selección para misiones críticas.
 * Exige un umbral riguroso de reserva energética (mínimo 50% de batería)
 * y selecciona el drone con mayor capacidad de respaldo.
 */
public class PrioridadCriticaStrategy implements EstrategiaSeleccion {

    public static final int UMBRAL_BATERIA_CRITICA = 50;

    @Override
    public Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> dronesDisponibles, String zona) {
        if (dronesDisponibles == null || dronesDisponibles.isEmpty()) {
            return Optional.empty();
        }

        // Filtra por drones con reserva superior al 50% para salvaguardar misiones críticas
        Optional<DroneAcuatico> droneOptimo = dronesDisponibles.stream()
                .filter(d -> d.isDisponible() && d.getBateria() >= UMBRAL_BATERIA_CRITICA)
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));

        if (droneOptimo.isPresent()) {
            return droneOptimo;
        }

        // Si ninguno supera el 50%, selecciona el de mayor batería disponible que cumpla el mínimo de seguridad
        return dronesDisponibles.stream()
                .filter(d -> d.isDisponible() && d.getBateria() >= MayorBateriaStrategy.UMBRAL_BATERIA_MINIMA)
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));
    }
}
