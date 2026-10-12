package main.dosw.empoleon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import main.dosw.empoleon.aplicacion.CoordinadorRutasEmpoleon;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.empoleon.infraestructura.GeneradorFlotaEmpoleon;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Prioridad;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoordinadorRutasEmpoleonTest {

    private CoordinadorRutasEmpoleon coordinador;
    private List<DroneEnterprise> flota60;

    @BeforeEach
    void setUp() {
        coordinador = new CoordinadorRutasEmpoleon();
        flota60 = GeneradorFlotaEmpoleon.generarFlota60Drones();
    }

    @Test
    @DisplayName("Generador genera exactamente 60 drones distribuidos en 4 zonas")
    void testGeneradorFlota60Drones() {
        assertEquals(60, flota60.size());
        long buceadores = flota60.stream().filter(d -> d.getTipo() == TipoDrone.BUCEADOR).count();
        long semis = flota60.stream().filter(d -> d.getTipo() == TipoDrone.SEMISUMERGIDO).count();
        long superf = flota60.stream().filter(d -> d.getTipo() == TipoDrone.SUPERFICIAL).count();
        assertEquals(15, buceadores);
        assertEquals(15, semis);
        assertEquals(30, superf);
    }

    @Test
    @DisplayName("Calcular eficiencia por zona calcula correctamente el ratio de misiones completadas")
    void testCalcularEficienciaPorZona() {
        String zonaNorte = ZonaHidrica.EMBALSE_INVESTIGACION.getNombre();
        String zonaSur = ZonaHidrica.LAGUNA_RESERVA.getNombre();

        List<MisionEnterprise> misiones = List.of(
                new MisionEnterprise("M1", "Desc1", zonaNorte, zonaSur,
                        List.of(), List.of(), EstadoMisionEnterprise.COMPLETADA, Prioridad.NORMAL, 100),
                new MisionEnterprise("M2", "Desc2", zonaNorte, zonaSur,
                        List.of(), List.of(), EstadoMisionEnterprise.FALLIDA, Prioridad.NORMAL, 100),
                new MisionEnterprise("M3", "Desc3", zonaSur, zonaNorte,
                        List.of(), List.of(), EstadoMisionEnterprise.COMPLETADA, Prioridad.NORMAL, 100)
        );

        Map<String, Double> eficiencia = coordinador.calcularEficienciaPorZona(misiones);
        assertEquals(0.5, eficiencia.get(zonaNorte));
        assertEquals(1.0, eficiencia.get(zonaSur));
    }

    @Test
    @DisplayName("Calcular eficiencia con lista vacía retorna mapa vacío y con nulo lanza excepción")
    void testCalcularEficienciaCasosLimite() {
        assertTrue(coordinador.calcularEficienciaPorZona(Collections.emptyList()).isEmpty());
        assertThrows(NullPointerException.class, () -> coordinador.calcularEficienciaPorZona(null));
    }

    @Test
    @DisplayName("Encontrar drone con mejor historial retorna el de mayor entregas exitosas")
    void testEncontrarDroneConMejorHistorial() {
        DroneEnterprise d1 = new DroneEnterprise("D1", "M", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, "Z1", 500, 10);
        DroneEnterprise d2 = new DroneEnterprise("D2", "M", TipoDrone.SUPERFICIAL, 90,
                EstadoDrone.DISPONIBLE, "Z1", 500, 25);
        DroneEnterprise d3 = new DroneEnterprise("D3", "M", TipoDrone.SUPERFICIAL, 70,
                EstadoDrone.DISPONIBLE, "Z1", 500, 15);

        Optional<DroneEnterprise> mejor = coordinador.encontrarDroneConMejorHistorial(List.of(d1, d2, d3));
        assertTrue(mejor.isPresent());
        assertEquals("D2", mejor.get().getId());
        assertEquals(25, mejor.get().getEntregasExitosas());
    }

    @Test
    @DisplayName("Encontrar drone con mejor historial con lista vacía retorna Optional vacío y nulo lanza excepción")
    void testEncontrarDroneHistorialCasosLimite() {
        assertFalse(coordinador.encontrarDroneConMejorHistorial(Collections.emptyList()).isPresent());
        assertThrows(NullPointerException.class, () -> coordinador.encontrarDroneConMejorHistorial(null));
    }

    @Test
    @DisplayName("Planificar ruta óptima multi-etapa asigna drones idóneos por tramo consecutivo")
    void testPlanificarRutaOptimaMultiEtapa() {
        List<Waypoint> waypoints = List.of(
                new Waypoint("WP1", "W1", ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 0),
                new Waypoint("WP2", "W2", ZonaHidrica.RED_CANALES.getNombre(), 1),
                new Waypoint("WP3", "W3", ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 2)
        );

        List<TramoRuta> tramos = coordinador.planificarRutaOptimaMultiEtapa(waypoints, flota60, 200);
        assertEquals(2, tramos.size());
        assertEquals("TR-1", tramos.get(0).id());
        assertEquals("WP1", tramos.get(0).origen().getId());
        assertEquals("WP2", tramos.get(0).destino().getId());
        assertNotNull(tramos.get(0).droneAsignado());
        assertEquals(ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), tramos.get(0).droneAsignado().getZona());

        assertEquals("TR-2", tramos.get(1).id());
        assertNotNull(tramos.get(1).droneAsignado());
        assertEquals(ZonaHidrica.RED_CANALES.getNombre(), tramos.get(1).droneAsignado().getZona());
    }

    @Test
    @DisplayName("Planificar ruta con menos de dos waypoints lanza IllegalArgumentException")
    void testPlanificarRutaWaypointsInsuficientes() {
        List<Waypoint> unSoloWP = List.of(new Waypoint("WP1", "W1", "Z1", 0));
        assertThrows(IllegalArgumentException.class, () ->
                coordinador.planificarRutaOptimaMultiEtapa(unSoloWP, flota60, 100));
        assertThrows(NullPointerException.class, () ->
                coordinador.planificarRutaOptimaMultiEtapa(null, flota60, 100));
        assertThrows(NullPointerException.class, () ->
                coordinador.planificarRutaOptimaMultiEtapa(unSoloWP, null, 100));
    }

    @Test
    @DisplayName("Planificar ruta sin drones disponibles en zona asigna null en drone del tramo")
    void testPlanificarRutaSinDronesDisponibles() {
        List<DroneEnterprise> flotaAgotada = List.of(
                new DroneEnterprise("D-NO", "M", TipoDrone.SUPERFICIAL, 10,
                        EstadoDrone.MANTENIMIENTO, "Zona Remota", 500, 0)
        );
        List<Waypoint> waypoints = List.of(
                new Waypoint("WP1", "W1", "Zona Remota", 0),
                new Waypoint("WP2", "W2", "Zona Remota", 1)
        );

        List<TramoRuta> tramos = coordinador.planificarRutaOptimaMultiEtapa(waypoints, flotaAgotada, 100);
        assertEquals(1, tramos.size());
        assertNull(tramos.get(0).droneAsignado());
    }

    @Test
    @DisplayName("Collector personalizado BateriaPromedioPorZona calcula promedio en una sola pasada")
    void testCustomCollectorBateriaPromedio() {
        Map<String, Double> promedios = coordinador.calcularBateriaPromedioPorZona(flota60);
        assertEquals(4, promedios.size());
        assertTrue(promedios.containsKey(ZonaHidrica.EMBALSE_INVESTIGACION.getNombre()));
        assertTrue(promedios.containsKey(ZonaHidrica.RED_CANALES.getNombre()));
        assertTrue(promedios.containsKey(ZonaHidrica.LAGUNA_RESERVA.getNombre()));
        assertTrue(promedios.containsKey(ZonaHidrica.LABORATORIO_CENTRAL.getNombre()));

        // Verificar que ningún promedio sea negativo o cero con la flota cargada
        promedios.values().forEach(val -> assertTrue(val > 0.0));
    }

    @Test
    @DisplayName("Calcular carga de trabajo por zona cuenta correctamente waypoints de misiones en tránsito")
    void testCalcularCargaTrabajoPorZona() {
        Waypoint wp1 = new Waypoint("W1", "P1", "Norte", 0);
        Waypoint wp2 = new Waypoint("W2", "P2", "Sur", 1);
        Waypoint wp3 = new Waypoint("W3", "P3", "Norte", 2);

        MisionEnterprise mTransito = new MisionEnterprise("M1", "D1", "Norte", "Sur",
                List.of(wp1, wp2, wp3), List.of(), EstadoMisionEnterprise.EN_TRANSITO, Prioridad.NORMAL, 100);
        MisionEnterprise mPlanificada = new MisionEnterprise("M2", "D2", "Norte", "Sur",
                List.of(wp1), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.NORMAL, 100);

        Map<String, Long> carga = coordinador.calcularCargaTrabajoPorZona(List.of(mTransito, mPlanificada));
        assertEquals(2L, carga.get("Norte"));
        assertEquals(1L, carga.get("Sur"));
    }

    @Test
    @DisplayName("Cálculo paralelo de salud de flota agrupa y calcula correctamente")
    void testCalcularSaludFlotaParalelo() {
        Map<String, Double> salud = coordinador.calcularSaludFlotaParalelo(flota60);
        assertEquals(4, salud.size());
        salud.values().forEach(val -> assertTrue(val > 0.0));
    }

    @Test
    @DisplayName("Validaciones de dominio en Waypoint, TramoRuta y DroneEnterprise")
    void testValidacionesDominio() {
        assertThrows(NullPointerException.class, () -> new Waypoint(null, "W", "Z", 0));
        assertThrows(NullPointerException.class, () -> new Waypoint("1", null, "Z", 0));
        assertThrows(NullPointerException.class, () -> new Waypoint("1", "W", null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Waypoint("1", "W", "Z", -1));

        Waypoint w1 = new Waypoint("1", "W1", "Z1", 0);
        Waypoint w2 = new Waypoint("2", "W2", "Z2", 1);
        assertThrows(NullPointerException.class, () -> new TramoRuta(null, w1, w2, 100, null));
        assertThrows(IllegalArgumentException.class, () -> new TramoRuta("T", w1, w2, 0, null));

        TramoRuta tramo = new TramoRuta("T1", w1, w2, 200, null);
        DroneEnterprise d = new DroneEnterprise("D1", "M", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, "Z1", 500, 5);
        TramoRuta tramoConDrone = tramo.withDrone(d);
        assertEquals(d, tramoConDrone.droneAsignado());

        assertThrows(IllegalArgumentException.class, () ->
                new DroneEnterprise("D", "M", TipoDrone.SUPERFICIAL, 80, EstadoDrone.DISPONIBLE, "Z", 500, -1));
        assertThrows(IllegalArgumentException.class, () -> d.setEntregasExitosas(-2));

        d.incrementarEntregasExitosas();
        assertEquals(6, d.getEntregasExitosas());
    }

    @Test
    @DisplayName("Prueba de integración de ejecución del método main de Ejercicio01Empoleon")
    void testEjercicio01EmpoleonMain() {
        Ejercicio01Empoleon.main(new String[]{});
        assertTrue(true);
    }
}
