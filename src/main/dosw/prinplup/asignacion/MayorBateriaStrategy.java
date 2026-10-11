package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MayorBateriaStrategy implements EstrategiaSeleccion {

    /** Umbral mínimo de batería de seguridad operacional (20%) para evitar varamientos críticos */
    public static final int UMBRAL_BATERIA_MINIMA = 20;

    @Override
    public Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> dronesDisponibles, String zona) {
        if (dronesDisponibles == null || dronesDisponibles.isEmpty()) {
            return Optional.empty();
        }
        return dronesDisponibles.stream()
                .filter(d -> d.isDisponible() && d.getBateria() >= UMBRAL_BATERIA_MINIMA)
                .max(Comparator.comparingInt(DroneAcuatico::getBateria));
    }
}
