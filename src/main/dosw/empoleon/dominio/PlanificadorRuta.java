package main.dosw.empoleon.dominio;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Servicio de dominio responsable de planificar rutas multi-etapa,
 * segmentar tramos entre waypoints y evaluar viabilidad física y energética.
 */
public class PlanificadorRuta {

    private static final int CONSUMO_BATERIA_POR_KM = 8;
    private static final int BATERIA_MINIMA_OPERATIVA = 25;

    public List<TramoRuta> planificarRuta(List<Waypoint> waypoints, List<DroneEnterprise> flota, int pesoGramos) {
        validarParametrosEntrada(waypoints, flota, pesoGramos);
        return IntStream.range(0, waypoints.size() - 1)
                .mapToObj(i -> construirTramoConDrone(i, waypoints.get(i), waypoints.get(i + 1), flota, pesoGramos))
                .toList();
    }

    private void validarParametrosEntrada(List<Waypoint> wps, List<DroneEnterprise> flota, int peso) {
        Objects.requireNonNull(wps, "La lista de waypoints no puede ser nula");
        Objects.requireNonNull(flota, "La flota disponible no puede ser nula");
        if (wps.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 waypoints para generar una ruta");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso de la carga debe ser positivo");
        }
    }

    private TramoRuta construirTramoConDrone(
            int idx, Waypoint origen, Waypoint destino, List<DroneEnterprise> flota, int peso
    ) {
        int distanciaMetros = calcularDistanciaEntreWaypoints(origen, destino, idx);
        DroneEnterprise seleccionado = seleccionarDroneOptimo(origen, flota, peso)
                .orElse(null);
        return new TramoRuta("TR-EM-" + (idx + 1), origen, destino, distanciaMetros, seleccionado);
    }

    public Optional<DroneEnterprise> seleccionarDroneOptimo(
            Waypoint origen, List<DroneEnterprise> flota, int pesoGramos
    ) {
        Objects.requireNonNull(origen, "El waypoint de origen no puede ser nulo");
        Objects.requireNonNull(flota, "La flota no puede ser nula");
        return flota.stream()
                .filter(DroneEnterprise::isDisponible)
                .filter(d -> d.puedeCargar(pesoGramos))
                .filter(d -> d.getZona().equalsIgnoreCase(origen.getZona()))
                .filter(d -> d.getBateria() >= BATERIA_MINIMA_OPERATIVA)
                .max(Comparator.comparingInt(DroneEnterprise::getBateria));
    }

    public int calcularDistanciaTotal(List<TramoRuta> tramos) {
        Objects.requireNonNull(tramos, "Los tramos no pueden ser nulos");
        return tramos.stream()
                .mapToInt(TramoRuta::distanciaMetros)
                .sum();
    }

    public int estimarConsumoBateria(TramoRuta tramo) {
        Objects.requireNonNull(tramo, "El tramo no puede ser nulo");
        int kilometros = Math.max(1, tramo.distanciaMetros() / 1000);
        return kilometros * CONSUMO_BATERIA_POR_KM;
    }

    public boolean esTramoViable(TramoRuta tramo, DroneEnterprise drone) {
        if (tramo == null || drone == null || !drone.isDisponible()) {
            return false;
        }
        int consumoEstimado = estimarConsumoBateria(tramo);
        return (drone.getBateria() - consumoEstimado) >= BATERIA_MINIMA_OPERATIVA;
    }

    private int calcularDistanciaEntreWaypoints(Waypoint origen, Waypoint destino, int indice) {
        int base = Math.abs(origen.getOrden() - destino.getOrden()) * 450;
        return Math.max(300, base + (indice * 50));
    }
}
