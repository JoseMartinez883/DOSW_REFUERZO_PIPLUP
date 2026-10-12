package main.dosw.empoleon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import main.dosw.empoleon.aplicacion.OrquestadorMisionMultiEtapa;
import main.dosw.empoleon.dominio.CustodiaMuestra;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.PlanificadorRuta;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.adapter.ProveedorCondicionesHidricas;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Prioridad;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Suite de pruebas de integración (Capa de Aplicación e Integración - TDD)
 * que valida el flujo completo Enterprise y los 5 flujos alternos/excepcionales.
 */
class FlujoCompletoEnterpriseIntegrationTest {

    private PlanificadorRuta planificador;
    private List<DroneEnterprise> flota;
    private Waypoint wp1;
    private Waypoint wp2;
    private Waypoint wp3;
    private Waypoint wp4;

    @BeforeEach
    void setUp() {
        planificador = new PlanificadorRuta();
        flota = new ArrayList<>();

        flota.add(new DroneEnterprise("DRN-01", "Explorer-1", TipoDrone.SUPERFICIAL, 92,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 800, 14));
        flota.add(new DroneEnterprise("DRN-02", "Explorer-2", TipoDrone.SUPERFICIAL, 85,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 800, 11));
        flota.add(new DroneEnterprise("DRN-03", "SemiSub-1", TipoDrone.SEMISUMERGIDO, 88,
                EstadoDrone.DISPONIBLE, ZonaHidrica.RED_CANALES.getNombre(), 1200, 18));
        flota.add(new DroneEnterprise("DRN-04", "SemiSub-2", TipoDrone.SEMISUMERGIDO, 78,
                EstadoDrone.DISPONIBLE, ZonaHidrica.RED_CANALES.getNombre(), 1200, 12));
        flota.add(new DroneEnterprise("DRN-05", "Diver-1", TipoDrone.BUCEADOR, 90,
                EstadoDrone.DISPONIBLE, ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 500, 22));

        wp1 = new Waypoint("WP-01", "Muelle Alfa", ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1);
        wp2 = new Waypoint("WP-02", "Esclusa Central", ZonaHidrica.RED_CANALES.getNombre(), 2);
        wp3 = new Waypoint("WP-03", "Boya Intermedia", ZonaHidrica.RED_CANALES.getNombre(), 3);
        wp4 = new Waypoint("WP-04", "Dársena Laboratorio", ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 4);
    }

    @Test
    @DisplayName("Flujo Completo: Solicitud -> Planificación -> Asignación -> Notificación de Waypoints")
    void testFlujoCompletoIntegracionFeliz() {
        // 1. Planificación de ruta por tramos
        List<Waypoint> waypoints = List.of(wp1, wp2, wp3, wp4);
        List<TramoRuta> tramos = planificador.planificarRuta(waypoints, flota, 400);
        assertEquals(3, tramos.size());

        // 2. Creación de misión multi-etapa
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-FULL-01", "Transporte Completo",
                wp1.getZona(), wp4.getZona(), waypoints, tramos,
                EstadoMisionEnterprise.PLANIFICADA, Prioridad.ALTA, 400
        );

        // 3. Sellado criptográfico de la muestra
        String hashInicial = CustodiaMuestra.generarHash("MUESTRA-INTEGRAL-2026:SEDIMENTO");
        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-INTEGRAL-2026", hashInicial);

        // 4. Ejecución del orquestador y paso por waypoints
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.0, 15.0, 0.4, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean resultado = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(resultado);
        assertEquals(EstadoMisionEnterprise.COMPLETADA, mision.getEstado());
        assertEquals(4, custodia.getRegistrosTrazabilidad().size());
        assertTrue(custodia.verificarIntegridad(custodia.getHashActual()));
    }

    @Test
    @DisplayName("Flujo Alterno 1: Fallo de drone en waypoint activa reasignación automática (< 1.5s)")
    void testFlujoAlterno1_FalloDroneEnWaypointConReasignacionAutonoma() {
        assertTimeoutPreemptively(Duration.ofMillis(1500), () -> {
            flota.get(0).setEstado(EstadoDrone.FALLO); // DRN-01 averiado en WP-1
            List<TramoRuta> tramos = List.of(new TramoRuta("TR-1", wp1, wp2, 500, flota.get(0)));

            MisionEnterprise mision = new MisionEnterprise(
                    "MIS-FAILOVER-01", "Misión Failover", wp1.getZona(), wp2.getZona(),
                    List.of(wp1, wp2), tramos, EstadoMisionEnterprise.PLANIFICADA, Prioridad.CRITICA, 300
            );

            CustodiaMuestra custodia = new CustodiaMuestra("M-FAILOVER", CustodiaMuestra.generarHash("M-FAILOVER"));
            ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.0, 10.0, 0.3, true);
            OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

            boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

            assertTrue(exito);
            assertEquals(EstadoMisionEnterprise.COMPLETADA, mision.getEstado());
            assertTrue(custodia.getRegistrosTrazabilidad().stream()
                    .anyMatch(r -> r.contains("TRANSFERENCIA:WP-01:DRN-01->DRN-02")));
        });
    }

    @Test
    @DisplayName("Flujo Alterno 2: Zona destino inactiva suspende misión a espera segura (Hold)")
    void testFlujoAlterno2_ZonaDestinoInactivaConPasoAHoldSeguro() {
        List<TramoRuta> tramos = List.of(new TramoRuta("TR-1", wp1, wp4, 900, flota.get(0)));
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-HOLD-01", "Misión a zona bloqueada", wp1.getZona(), wp4.getZona(),
                List.of(wp1, wp4), tramos, EstadoMisionEnterprise.PLANIFICADA, Prioridad.NORMAL, 250
        );

        CustodiaMuestra custodia = new CustodiaMuestra("M-HOLD", CustodiaMuestra.generarHash("M-HOLD"));
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.0, 10.0, 0.2, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(
                mision, flota, custodia, List.of(ZonaHidrica.LABORATORIO_CENTRAL.getNombre())
        );

        assertFalse(exito);
        assertEquals(EstadoMisionEnterprise.EN_ESPERA_SEGURA, mision.getEstado());
    }

    @Test
    @DisplayName("Flujo Alterno 3: Batería crítica en ruta (< 25%) dispara reasignación y retorno seguro")
    void testFlujoAlterno3_BateriaCriticaEnMitadDeRutaConRetornoEmergencia() {
        DroneEnterprise droneCritico = flota.get(0);
        droneCritico.setBateria(22); // Batería crítica < 25%

        List<TramoRuta> tramos = List.of(new TramoRuta("TR-1", wp1, wp2, 500, droneCritico));
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-BATCRIT-01", "Misión con batería baja", wp1.getZona(), wp2.getZona(),
                List.of(wp1, wp2), tramos, EstadoMisionEnterprise.PLANIFICADA, Prioridad.ALTA, 350
        );

        CustodiaMuestra custodia = new CustodiaMuestra("M-BATCRIT", CustodiaMuestra.generarHash("M-BATCRIT"));
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(1.8, 12.0, 0.3, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(exito);
        assertTrue(custodia.getRegistrosTrazabilidad().stream()
                .anyMatch(r -> r.contains("TRANSFERENCIA:WP-01:DRN-01->DRN-02")));
    }

    @Test
    @DisplayName("Flujo Alterno 4: Cadena de custodia interrumpida detecta alteración de hash y alerta")
    void testFlujoAlterno4_CadenaCustodiaInterrumpidaConAlertaYPeritaje() {
        String hashInicial = CustodiaMuestra.generarHash("MUESTRA-ORIGINAL");
        CustodiaMuestra custodia = new CustodiaMuestra("M-CUSTODIA-ANOMALIA", hashInicial);

        custodia.registrarPasoWaypoint("WP-01");
        custodia.registrarPasoWaypoint("WP-02");

        // Simulación de hash corrompido o adulterado externamente
        String hashAdulterado = CustodiaMuestra.generarHash("MUESTRA-MODIFICADA-ILEGAL");
        assertFalse(custodia.verificarIntegridad(hashAdulterado));
        assertTrue(custodia.verificarIntegridad(custodia.getHashActual()));
        assertEquals(3, custodia.getRegistrosTrazabilidad().size());
    }

    @Test
    @DisplayName("Flujo Alterno 5: Condición hídrica adversa (oleaje > 1.2m) activa desvío dinámico por canal")
    void testFlujoAlterno5_CondicionHidricaAdversaConDesvioCanalProtegido() {
        List<TramoRuta> tramos = List.of(new TramoRuta("TR-1", wp1, wp2, 600, flota.get(0)));
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-DESVIO-02", "Misión con tormenta", wp1.getZona(), wp2.getZona(),
                List.of(wp1, wp2), tramos, EstadoMisionEnterprise.PLANIFICADA, Prioridad.NORMAL, 200
        );

        CustodiaMuestra custodia = new CustodiaMuestra("M-TORMENTA", CustodiaMuestra.generarHash("M-TORMENTA"));
        // API reporta oleaje severo de 1.8 metros
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(0.8, 180.0, 1.8, false);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(exito);
        assertEquals(EstadoMisionEnterprise.COMPLETADA, mision.getEstado());
    }
}
