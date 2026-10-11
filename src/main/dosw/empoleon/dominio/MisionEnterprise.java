package main.dosw.empoleon.dominio;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import main.dosw.prinplup.Prioridad;

/**
 * Entidad de dominio que representa una misión multi-etapa con waypoints y tramos.
 */
public class MisionEnterprise {

    private final String id;
    private final String descripcion;
    private final String zonaOrigen;
    private final String zonaDestino;
    private final List<Waypoint> waypoints;
    private final List<TramoRuta> tramos;
    private EstadoMisionEnterprise estado;
    private final Prioridad prioridad;
    private final int pesoCargaGramos;

    public MisionEnterprise(
            String id,
            String descripcion,
            String zonaOrigen,
            String zonaDestino,
            List<Waypoint> waypoints,
            List<TramoRuta> tramos,
            EstadoMisionEnterprise estado,
            Prioridad prioridad,
            int pesoCargaGramos
    ) {
        this.id = Objects.requireNonNull(id, "El id no puede ser nulo");
        this.descripcion = Objects.requireNonNull(descripcion, "La descripcion no puede ser nula");
        this.zonaOrigen = Objects.requireNonNull(zonaOrigen, "La zona de origen no puede ser nula");
        this.zonaDestino = Objects.requireNonNull(zonaDestino, "La zona de destino no puede ser nula");
        this.waypoints = waypoints != null ? List.copyOf(waypoints) : Collections.emptyList();
        this.tramos = tramos != null ? List.copyOf(tramos) : Collections.emptyList();
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.prioridad = Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
        this.pesoCargaGramos = pesoCargaGramos;
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getZonaOrigen() {
        return zonaOrigen;
    }

    public String getZonaDestino() {
        return zonaDestino;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public List<TramoRuta> getTramos() {
        return tramos;
    }

    public EstadoMisionEnterprise getEstado() {
        return estado;
    }

    public void setEstado(EstadoMisionEnterprise estado) {
        this.estado = Objects.requireNonNull(estado, "El nuevo estado no puede ser nulo");
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public int getPesoCargaGramos() {
        return pesoCargaGramos;
    }
}
