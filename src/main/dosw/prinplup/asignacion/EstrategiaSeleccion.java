package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import java.util.List;
import java.util.Optional;

public interface EstrategiaSeleccion {
    Optional<DroneAcuatico> seleccionarDrone(List<DroneAcuatico> dronesDisponibles, String zona);

    default Optional<DroneAcuatico> seleccionar(List<DroneAcuatico> dronesDisponibles, String zona) {
        return seleccionarDrone(dronesDisponibles, zona);
    }
}
