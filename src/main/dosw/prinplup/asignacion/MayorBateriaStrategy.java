package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MayorBateriaStrategy implements EstrategiaSeleccion {

    @Override
    public Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> dronesDisponibles, String zona) {
        if (dronesDisponibles == null || dronesDisponibles.isEmpty()) {
            return Optional.empty();
        }
        return dronesDisponibles.stream()
                .filter(DroneAcuatico::isDisponible)
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }
}
