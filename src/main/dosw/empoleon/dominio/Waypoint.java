package main.dosw.empoleon.dominio;

import java.util.Objects;

/**
 * Representa un punto de control intermedio o destino en una ruta multi-etapa.
 */
public record Waypoint(String id, String nombre, String zona, int orden) {

    public Waypoint {
        Objects.requireNonNull(id, "El id del waypoint no puede ser nulo");
        Objects.requireNonNull(nombre, "El nombre del waypoint no puede ser nulo");
        Objects.requireNonNull(zona, "La zona del waypoint no puede ser nula");
        if (orden < 0) {
            throw new IllegalArgumentException("El orden del waypoint debe ser mayor o igual a 0");
        }
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getZona() {
        return zona;
    }

    public int getOrden() {
        return orden;
    }
}
