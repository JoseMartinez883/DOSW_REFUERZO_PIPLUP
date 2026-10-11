package main.dosw.empoleon.aplicacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import main.dosw.empoleon.dominio.BateriaPromedioPorZonaCollector;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;

/**
 * Servicio de aplicación que coordina rutas multi-etapa entre 60 drones
 * y ejecuta analíticas avanzadas utilizando la API de Streams y Lambdas de Java.
 */
public class CoordinadorRutasEmpoleon {

    /**
     * 1) Calcula la eficiencia de cada zona: (misiones completadas / misiones totales).
     */
    public Map<String, Double> calcularEficienciaPorZona(List<MisionEnterprise> misiones) {
        Objects.requireNonNull(misiones, "La lista de misiones no puede ser nula");
        Map<String, List<MisionEnterprise>> porZona = misiones.stream()
                .collect(Collectors.groupingBy(MisionEnterprise::getZonaOrigen));

        return porZona.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calcularRatioExito(entry.getValue())
                ));
    }

    private double calcularRatioExito(List<MisionEnterprise> lista) {
        if (lista.isEmpty()) {
            return 0.0;
        }
        long completadas = lista.stream()
                .filter(m -> m.getEstado() == EstadoMisionEnterprise.COMPLETADA)
                .count();
        return Math.round(((double) completadas / lista.size()) * 100.0) / 100.0;
    }

    /**
     * 2) Encuentra el drone con el mejor historial de entregas exitosas.
     */
    public Optional<DroneEnterprise> encontrarDroneConMejorHistorial(List<DroneEnterprise> flota) {
        Objects.requireNonNull(flota, "La flota no puede ser nula");
        return flota.stream()
                .reduce((d1, d2) -> d1.getEntregasExitosas() >= d2.getEntregasExitosas() ? d1 : d2);
    }

    /**
     * 3) Planifica la ruta óptima multi-etapa seleccionando el drone disponible más apto por tramo.
     */
    public List<TramoRuta> planificarRutaOptimaMultiEtapa(
            List<Waypoint> waypoints,
            List<DroneEnterprise> flota,
            int pesoCargaGramos
    ) {
        validarParametrosRuta(waypoints, flota);
        return IntStream.range(0, waypoints.size() - 1)
                .mapToObj(i -> crearTramoConDroneOptimo(
                        i,
                        waypoints.get(i),
                        waypoints.get(i + 1),
                        flota,
                        pesoCargaGramos
                ))
                .toList();
    }

    private void validarParametrosRuta(List<Waypoint> waypoints, List<DroneEnterprise> flota) {
        Objects.requireNonNull(waypoints, "Los waypoints no pueden ser nulos");
        Objects.requireNonNull(flota, "La flota no puede ser nula");
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 waypoints para una ruta");
        }
    }

    private TramoRuta crearTramoConDroneOptimo(
            int indice,
            Waypoint origen,
            Waypoint destino,
            List<DroneEnterprise> flota,
            int pesoCargaGramos
    ) {
        DroneEnterprise seleccionado = seleccionarDroneParaTramo(flota, origen.getZona(), pesoCargaGramos)
                .orElse(null);
        int distanciaEstimada = 500 * (indice + 1);
        return new TramoRuta("TR-" + (indice + 1), origen, destino, distanciaEstimada, seleccionado);
    }

    private Optional<DroneEnterprise> seleccionarDroneParaTramo(
            List<DroneEnterprise> flota,
            String zona,
            int pesoCargaGramos
    ) {
        return flota.stream()
                .filter(DroneEnterprise::isDisponible)
                .filter(d -> d.puedeCargar(pesoCargaGramos))
                .filter(d -> d.getZona().equalsIgnoreCase(zona))
                .max(Comparator.comparingInt(DroneEnterprise::getBateria));
    }

    /**
     * 4) Collector personalizado en una sola pasada: batería promedio por zona.
     */
    public Map<String, Double> calcularBateriaPromedioPorZona(List<DroneEnterprise> flota) {
        Objects.requireNonNull(flota, "La flota no puede ser nula");
        return flota.stream().collect(new BateriaPromedioPorZonaCollector());
    }

    /**
     * Calcula la carga de trabajo total por zona para misiones en tránsito.
     */
    public Map<String, Long> calcularCargaTrabajoPorZona(List<MisionEnterprise> misiones) {
        Objects.requireNonNull(misiones, "La lista de misiones no puede ser nula");
        return misiones.stream()
                .filter(m -> m.getEstado() == EstadoMisionEnterprise.EN_TRANSITO)
                .flatMap(m -> m.getWaypoints().stream())
                .collect(Collectors.groupingBy(Waypoint::getZona, Collectors.counting()));
    }

    /**
     * Procesamiento paralelo de telemetría y salud para flota enterprise de 60 drones.
     */
    public Map<String, Double> calcularSaludFlotaParalelo(List<DroneEnterprise> flota) {
        Objects.requireNonNull(flota, "La flota no puede ser nula");
        return flota.parallelStream()
                .collect(Collectors.groupingBy(
                        DroneEnterprise::getZona,
                        Collectors.averagingDouble(d -> (d.getBateria() * 0.7) + (d.getEntregasExitosas() * 0.3))
                ));
    }
}
