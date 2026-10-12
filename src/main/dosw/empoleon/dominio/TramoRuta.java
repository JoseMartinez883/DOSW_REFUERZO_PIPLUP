package main.dosw.empoleon.dominio;

import java.util.Objects;
import main.dosw.prinplup.DroneAcuatico;

/**
 * Segmento o etapa de navegación entre dos waypoints dentro de una ruta multi-etapa.
 */
public record TramoRuta(
        String id,
        Waypoint origen,
        Waypoint destino,
        int distanciaMetros,
        DroneAcuatico droneAsignado
) {

    public TramoRuta {
        Objects.requireNonNull(id, "El id del tramo no puede ser nulo");
        Objects.requireNonNull(origen, "El waypoint de origen no puede ser nulo");
        Objects.requireNonNull(destino, "El waypoint de destino no puede ser nulo");
        if (distanciaMetros <= 0) {
            throw new IllegalArgumentException("La distancia del tramo debe ser positiva");
        }
    }

    public TramoRuta withDrone(DroneAcuatico nuevoDrone) {
        return new TramoRuta(id, origen, destino, distanciaMetros, nuevoDrone);
    }
}
