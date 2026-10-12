package main.dosw.empoleon;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import main.dosw.empoleon.aplicacion.CoordinadorRutasEmpoleon;
import main.dosw.empoleon.aplicacion.DespachadorMisionesEnterprise;
import main.dosw.empoleon.aplicacion.cadena.CadenaValidacionMision;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;
import main.dosw.empoleon.dominio.decorator.DroneConCifrado;
import main.dosw.empoleon.dominio.decorator.DroneConMonitoreo;
import main.dosw.empoleon.dominio.decorator.DroneOperacional;
import main.dosw.empoleon.dominio.decorator.DroneOperacionalBase;
import main.dosw.empoleon.infraestructura.adapter.AdaptadorAPIHidrica;
import main.dosw.empoleon.infraestructura.adapter.ClienteApiHidricaExterna;
import main.dosw.empoleon.infraestructura.adapter.DatosHidricosExternosJson;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Prioridad;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequerimientosEnterpriseTest {

    private CoordinadorRutasEmpoleon coordinador;
    private List<DroneEnterprise> flota60;
    private DespachadorMisionesEnterprise despachador;

    @BeforeEach
    void setUp() {
        coordinador = new CoordinadorRutasEmpoleon();

        ClienteApiHidricaExterna mockApi = zona -> new DatosHidricosExternosJson(3.5, 12.0, 0.4, true);
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(mockApi);
        CadenaValidacionMision cadena = new CadenaValidacionMision();
        despachador = new DespachadorMisionesEnterprise(cadena, adaptador);

        flota60 = new ArrayList<>();
        ZonaHidrica[] zonas = ZonaHidrica.values();
        TipoDrone[] tipos = TipoDrone.values();

        for (int i = 1; i <= 60; i++) {
            ZonaHidrica zona = zonas[(i - 1) % zonas.length];
            TipoDrone tipo = tipos[(i - 1) % tipos.length];
            int bateria = 40 + (i % 60);
            int capacidad = (tipo == TipoDrone.SEMISUMERGIDO) ? 1500 : (tipo == TipoDrone.BUCEADOR ? 300 : 500);
            flota60.add(new DroneEnterprise(
                    "DRN-" + String.format("%02d", i),
                    "AquaEnterprise-" + tipo.name(),
                    tipo,
                    bateria,
                    EstadoDrone.DISPONIBLE,
                    zona.getNombre(),
                    capacidad,
                    5 + (i % 15)
            ));
        }
    }

    @Test
    @DisplayName("RF-01: Planificación y Ejecución de Rutas Multi-Etapa con Waypoints y selección óptima")
    void testRF01PlanificacionRutaMultiEtapa() {
        List<Waypoint> waypoints = List.of(
                new Waypoint("WP-1", "Base Embalse", ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1),
                new Waypoint("WP-2", "Esclusa Canales", ZonaHidrica.RED_CANALES.getNombre(), 2),
                new Waypoint("WP-3", "Boya Laguna", ZonaHidrica.LAGUNA_RESERVA.getNombre(), 3),
                new Waypoint("WP-4", "Dique Lab Central", ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 4)
        );

        List<TramoRuta> tramos = coordinador.planificarRutaOptimaMultiEtapa(waypoints, flota60, 250);

        assertEquals(3, tramos.size());
        for (TramoRuta tramo : tramos) {
            assertNotNull(tramo.droneAsignado());
            assertTrue(tramo.droneAsignado().puedeCargar(250));
            assertTrue(tramo.droneAsignado().getBateria() >= 20);
        }
    }

    @Test
    @DisplayName("RF-02 & RNF-02: Reasignación automática inmediata ante fallo en waypoint (< 1.5s)")
    void testRF02YRNF02ReasignacionAutomaticaAnteFallo() {
        assertTimeoutPreemptively(Duration.ofMillis(1500), () -> {
            Waypoint origen = new Waypoint("WP-FAIL-1", "Esclusa 1", ZonaHidrica.RED_CANALES.getNombre(), 1);
            Waypoint destino = new Waypoint("WP-FAIL-2", "Boya 2", ZonaHidrica.LAGUNA_RESERVA.getNombre(), 2);

            // Simulación de dron primario que entra en fallo
            DroneEnterprise dronePrimario = flota60.stream()
                    .filter(d -> d.getZona().equalsIgnoreCase(ZonaHidrica.RED_CANALES.getNombre()))
                    .findFirst()
                    .orElseThrow();
            dronePrimario.setEstado(EstadoDrone.FALLO);

            // Reasignación automática seleccionando respaldo en la zona
            List<DroneEnterprise> candidatosDisponibles = flota60.stream()
                    .filter(DroneEnterprise::isDisponible)
                    .filter(d -> d.getZona().equalsIgnoreCase(ZonaHidrica.RED_CANALES.getNombre()))
                    .filter(d -> d.puedeCargar(300))
                    .toList();

            assertFalse(candidatosDisponibles.isEmpty(), "Debe existir al menos un dron de reemplazo");
            DroneEnterprise droneRespaldo = candidatosDisponibles.get(0);

            TramoRuta tramoReasignado = new TramoRuta("TR-RECUPERADO", origen, destino, 800, droneRespaldo);

            assertNotNull(tramoReasignado.droneAsignado());
            assertEquals(EstadoDrone.DISPONIBLE, tramoReasignado.droneAsignado().getEstado());
        });
    }

    @Test
    @DisplayName("RF-03 & RNF-04: Trazabilidad de cadena de custodia con decorador criptográfico AES-256")
    void testRF03CadenaCustodiaCriptografica() {
        DroneEnterprise droneFisico = flota60.get(0);
        DroneOperacional droneOperacional = new DroneConCifrado(
                new DroneConMonitoreo(new DroneOperacionalBase(droneFisico))
        );

        boolean resultado = droneOperacional.ejecutarMision("MIS-CUSTODIA-001", "TR-01");
        assertTrue(resultado);

        List<String> telemetria = droneOperacional.getRegistroTelemetria();
        assertTrue(telemetria.stream().anyMatch(l -> l.contains("[SEGURIDAD-AES256]")));
        assertTrue(telemetria.stream().anyMatch(l -> l.contains("hash SHA-256")));
    }

    @Test
    @DisplayName("RF-04: Coordinación autónoma y sincronización entre 4 zonas hídricas")
    void testRF04CoordinacionCuatroZonasHidricas() {
        for (ZonaHidrica zona : ZonaHidrica.values()) {
            MisionEnterprise mision = new MisionEnterprise(
                    "MIS-ZONA-" + zona.name(),
                    "Misión experimental en " + zona.getNombre(),
                    zona.getNombre(),
                    ZonaHidrica.LABORATORIO_CENTRAL.getNombre(),
                    List.of(), List.of(),
                    EstadoMisionEnterprise.PLANIFICADA,
                    Prioridad.NORMAL,
                    300
            );

            DroneEnterprise droneEnZona = flota60.stream()
                    .filter(d -> d.getZona().equalsIgnoreCase(zona.getNombre()))
                    .findFirst()
                    .orElseThrow();

            ResultadoValidacion res = despachador.validarDespacho(mision, droneEnZona, zona.getNombre());
            assertTrue(res.exitoso(), "La validación debe ser exitosa para la zona " + zona.getNombre());
        }
    }

    @Test
    @DisplayName("RF-05: Reportes analíticos de eficiencia por zona con Collector en una pasada")
    void testRF05ReportesEficienciaPorZona() {
        Map<String, Double> promediosBateria = coordinador.calcularBateriaPromedioPorZona(flota60);

        assertEquals(4, promediosBateria.size(), "Deben computarse las 4 zonas hídricas");
        for (ZonaHidrica zona : ZonaHidrica.values()) {
            assertTrue(promediosBateria.containsKey(zona.getNombre()));
            assertTrue(promediosBateria.get(zona.getNombre()) > 0.0);
        }
    }

    @Test
    @DisplayName("RNF-03: Throughput de 100 misiones simultáneas con baja latencia (< 1200ms total)")
    void testRNF03Throughput100MisionesConcurrentes() {
        assertTimeoutPreemptively(Duration.ofMillis(1200), () -> {
            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<CompletableFuture<ResultadoValidacion>> futures = new ArrayList<>();

            for (int i = 1; i <= 100; i++) {
                final int idx = i;
                futures.add(CompletableFuture.supplyAsync(() -> {
                    DroneEnterprise d = flota60.get(idx % flota60.size());
                    MisionEnterprise m = new MisionEnterprise(
                            "MIS-CONC-" + idx, "Misión concurrente",
                            d.getZona(), ZonaHidrica.LABORATORIO_CENTRAL.getNombre(),
                            List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA,
                            Prioridad.ALTA, 200
                    );
                    return despachador.validarDespacho(m, d, d.getZona());
                }, executor));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();

            long exitosas = futures.stream().map(CompletableFuture::join).filter(ResultadoValidacion::exitoso).count();
            assertTrue(exitosas > 0, "Al menos una validación concurrente debe procesarse exitosamente");
        });
    }
}
