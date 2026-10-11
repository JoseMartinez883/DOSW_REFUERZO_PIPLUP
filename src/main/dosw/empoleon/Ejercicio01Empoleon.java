package main.dosw.empoleon;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.dosw.empoleon.aplicacion.CoordinadorRutasEmpoleon;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.empoleon.infraestructura.GeneradorFlotaEmpoleon;
import main.dosw.prinplup.Prioridad;

/**
 * Clase ejecutable de demostración para el Ejercicio 01 de la Fase Empoleon:
 * Streams y Lambdas para coordinar rutas multi-etapa entre 60 drones.
 */
public class Ejercicio01Empoleon {

    private static final Logger LOGGER = Logger.getLogger(Ejercicio01Empoleon.class.getName());

    public static void main(String[] args) {
        LOGGER.info("=== AquaPort Enterprise — Fase Empoleon: Ejercicio 01 (Streams Avanzados) ===");

        // 1. Cargar la flota de 60 drones
        List<DroneEnterprise> flota = GeneradorFlotaEmpoleon.generarFlota60Drones();
        CoordinadorRutasEmpoleon coordinador = new CoordinadorRutasEmpoleon();
        LOGGER.log(Level.INFO, "Flota cargada exitosamente: {0} drones.", flota.size());

        // 2. Demostración 1: Eficiencia por zona
        List<MisionEnterprise> misiones = crearMisionesDemostracion();
        Map<String, Double> eficiencia = coordinador.calcularEficienciaPorZona(misiones);
        LOGGER.log(Level.INFO, "1. Eficiencia por zona (completadas / totales): {0}", eficiencia);

        // 3. Demostración 2: Drone con mejor historial
        Optional<DroneEnterprise> mejorDrone = coordinador.encontrarDroneConMejorHistorial(flota);
        mejorDrone.ifPresent(d -> LOGGER.log(
                Level.INFO,
                "2. Drone con mejor historial: ID {0} ({1}) con {2} entregas exitosas.",
                new Object[]{d.getId(), d.getZona(), d.getEntregasExitosas()}
        ));

        // 4. Demostración 3: Planificación de ruta óptima multi-etapa
        List<Waypoint> waypoints = crearWaypointsRutaEjemplo();
        List<TramoRuta> ruta = coordinador.planificarRutaOptimaMultiEtapa(waypoints, flota, 250);
        LOGGER.log(Level.INFO, "3. Ruta multi-etapa planificada ({0} tramos):", ruta.size());
        for (TramoRuta tramo : ruta) {
            String droneInfo = tramo.droneAsignado() != null
                    ? tramo.droneAsignado().getId() + " (" + tramo.droneAsignado().getBateria() + "% bat)"
                    : "SIN DRONE";
            LOGGER.log(
                    Level.INFO,
                    "   * Tramo {0} [{1} -> {2}]: Drone {3} (dist: {4}m)",
                    new Object[]{tramo.id(), tramo.origen().getZona(), tramo.destino().getZona(), droneInfo, tramo.distanciaMetros()}
            );
        }

        // 5. Demostración 4: Collector personalizado en una sola pasada
        Map<String, Double> bateriaPromedio = coordinador.calcularBateriaPromedioPorZona(flota);
        LOGGER.log(Level.INFO, "4. Batería promedio por zona (Custom Collector 1 pasada): {0}", bateriaPromedio);

        // 6. Demostración 5: Salud de la flota con parallelStream
        Map<String, Double> saludParalelo = coordinador.calcularSaludFlotaParalelo(flota);
        LOGGER.log(Level.INFO, "5. Índice de salud de flota (parallelStream): {0}", saludParalelo);
    }

    private static List<Waypoint> crearWaypointsRutaEjemplo() {
        return List.of(
                new Waypoint("WP-01", "Punto Norte A", ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 0),
                new Waypoint("WP-02", "Intercambiador Campus", ZonaHidrica.RED_CANALES.getNombre(), 1),
                new Waypoint("WP-03", "Bahía Sur", ZonaHidrica.LAGUNA_RESERVA.getNombre(), 2),
                new Waypoint("WP-04", "Laboratorio Recepción", ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 3)
        );
    }

    private static List<MisionEnterprise> crearMisionesDemostracion() {
        String zonaNorte = ZonaHidrica.EMBALSE_INVESTIGACION.getNombre();
        String zonaCanales = ZonaHidrica.RED_CANALES.getNombre();
        String zonaSur = ZonaHidrica.LAGUNA_RESERVA.getNombre();

        return List.of(
                new MisionEnterprise("MIS-E1", "Muestra Norte 1", zonaNorte, zonaCanales,
                        List.of(), List.of(), EstadoMisionEnterprise.COMPLETADA, Prioridad.ALTA, 200),
                new MisionEnterprise("MIS-E2", "Muestra Norte 2", zonaNorte, zonaCanales,
                        List.of(), List.of(), EstadoMisionEnterprise.COMPLETADA, Prioridad.NORMAL, 150),
                new MisionEnterprise("MIS-E3", "Muestra Norte 3", zonaNorte, zonaCanales,
                        List.of(), List.of(), EstadoMisionEnterprise.FALLIDA, Prioridad.BAJA, 100),
                new MisionEnterprise("MIS-E4", "Muestra Canales 1", zonaCanales, zonaSur,
                        List.of(), List.of(), EstadoMisionEnterprise.COMPLETADA, Prioridad.CRITICA, 300),
                new MisionEnterprise("MIS-E5", "Muestra Sur 1", zonaSur, zonaNorte,
                        List.of(), List.of(), EstadoMisionEnterprise.COMPLETADA, Prioridad.NORMAL, 400),
                new MisionEnterprise("MIS-E6", "Muestra Sur 2", zonaSur, zonaNorte,
                        List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.BAJA, 250)
        );
    }
}
