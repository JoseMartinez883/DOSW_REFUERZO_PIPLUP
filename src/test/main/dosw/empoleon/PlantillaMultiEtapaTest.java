package main.dosw.empoleon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import main.dosw.empoleon.aplicacion.OrquestadorMisionMultiEtapa;
import main.dosw.empoleon.dominio.CustodiaMuestra;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
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
 * Suite de pruebas unitarias que verifica formalmente la especificación
 * de requerimientos DOSW para el RF "AP-15 Ejecutar misión multi-etapa con waypoints".
 */
class PlantillaMultiEtapaTest {

    private List<DroneEnterprise> flota;
    private Waypoint wp1;
    private Waypoint wp2;
    private Waypoint wp3;
    private Waypoint wp4;

    @BeforeEach
    void setUp() {
        flota = new ArrayList<>();
        flota.add(new DroneEnterprise("DRN-01", "Aqua-Multi 1", TipoDrone.SUPERFICIAL, 90,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 600, 10));
        flota.add(new DroneEnterprise("DRN-02", "Aqua-Multi 2", TipoDrone.SUPERFICIAL, 85,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 600, 12));
        flota.add(new DroneEnterprise("DRN-03", "Aqua-Multi 3", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, ZonaHidrica.RED_CANALES.getNombre(), 600, 8));
        flota.add(new DroneEnterprise("DRN-04", "Aqua-Multi 4", TipoDrone.SUPERFICIAL, 75,
                EstadoDrone.DISPONIBLE, ZonaHidrica.RED_CANALES.getNombre(), 600, 9));

        wp1 = new Waypoint("WP-01", "Muelle Central", ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1);
        wp2 = new Waypoint("WP-02", "Esclusa Alfa", ZonaHidrica.RED_CANALES.getNombre(), 2);
        wp3 = new Waypoint("WP-03", "Boya Beta", ZonaHidrica.RED_CANALES.getNombre(), 3);
        wp4 = new Waypoint("WP-04", "Laboratorio", ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 4);
    }

    @Test
    @DisplayName("Flujo Básico AP-15: Ejecución exitosa de misión multi-etapa con 3 tramos y 4 waypoints")
    void testFlujoBasicoCompleto8Pasos() {
        TramoRuta tramo1 = new TramoRuta("TR-01", wp1, wp2, 400, flota.get(0));
        TramoRuta tramo2 = new TramoRuta("TR-02", wp2, wp3, 500, flota.get(2));
        TramoRuta tramo3 = new TramoRuta("TR-03", wp3, wp4, 600, flota.get(3));

        MisionEnterprise mision = new MisionEnterprise(
                "MIS-MULTI-01", "Transporte de Muestras Multi-Etapa",
                wp1.getZona(), wp4.getZona(),
                List.of(wp1, wp2, wp3, wp4),
                List.of(tramo1, tramo2, tramo3),
                EstadoMisionEnterprise.PLANIFICADA,
                Prioridad.ALTA, 250
        );

        String hashInicial = CustodiaMuestra.generarHash("MUESTRA-AQ-999:PARAMETROS_CALIDAD");
        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-AQ-999", hashInicial);

        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.5, 10.0, 0.3, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(exito);
        assertEquals(EstadoMisionEnterprise.COMPLETADA, mision.getEstado());
        assertTrue(custodia.getRegistrosTrazabilidad().size() >= 4);
    }

    @Test
    @DisplayName("Flujo Alterno AP-15: Fallo de drone en waypoint intermedio dispara reasignación y traspaso de custodia")
    void testFlujoAlternoFalloDroneWaypointReasignacion() {
        DroneEnterprise droneAveriado = flota.get(0);
        droneAveriado.setEstado(EstadoDrone.FALLO);

        TramoRuta tramo1 = new TramoRuta("TR-01", wp1, wp2, 400, droneAveriado);
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-FALLO-01", "Misión con avería",
                wp1.getZona(), wp2.getZona(),
                List.of(wp1, wp2), List.of(tramo1),
                EstadoMisionEnterprise.PLANIFICADA,
                Prioridad.CRITICA, 300
        );

        String hashInicial = CustodiaMuestra.generarHash("MUESTRA-CRITICA-01");
        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-CRITICA-01", hashInicial);

        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.5, 12.0, 0.4, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(exito);
        assertEquals(EstadoMisionEnterprise.COMPLETADA, mision.getEstado());
        assertTrue(custodia.getRegistrosTrazabilidad().stream().anyMatch(r -> r.contains("TRANSFERENCIA:WP-01:DRN-01->DRN-02")));
    }

    @Test
    @DisplayName("Flujo Alterno AP-15: Condiciones hídricas adversas activan desvío seguro de ruta")
    void testFlujoAlternoCondicionesAdversasDesvio() {
        TramoRuta tramo1 = new TramoRuta("TR-01", wp1, wp2, 400, flota.get(0));
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-DESVIO-01", "Misión con oleaje fuerte",
                wp1.getZona(), wp2.getZona(),
                List.of(wp1, wp2), List.of(tramo1),
                EstadoMisionEnterprise.PLANIFICADA,
                Prioridad.NORMAL, 200
        );

        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-OLEAJE", CustodiaMuestra.generarHash("OLEAJE"));
        // Proveedor con condiciones severas (oleaje 2.0m > 1.2m)
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(0.5, 160.0, 2.0, false);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(exito);
        assertEquals(EstadoMisionEnterprise.COMPLETADA, mision.getEstado());
    }

    @Test
    @DisplayName("Flujo Alterno AP-15: Zona de destino inactiva coloca misión en espera segura")
    void testFlujoAlternoZonaDestinoInactivaHoldSeguro() {
        TramoRuta tramo1 = new TramoRuta("TR-01", wp1, wp4, 800, flota.get(0));
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-HOLD-01", "Misión con destino inactivo",
                wp1.getZona(), wp4.getZona(),
                List.of(wp1, wp4), List.of(tramo1),
                EstadoMisionEnterprise.PLANIFICADA,
                Prioridad.NORMAL, 200
        );

        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-HOLD", CustodiaMuestra.generarHash("HOLD"));
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.5, 10.0, 0.2, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(
                mision, flota, custodia, List.of(ZonaHidrica.LABORATORIO_CENTRAL.getNombre())
        );

        assertFalse(exito);
        assertEquals(EstadoMisionEnterprise.EN_ESPERA_SEGURA, mision.getEstado());
    }

    @Test
    @DisplayName("Regla de Negocio RN-EM-01: Cadena de custodia criptográfica es inmutable y verificable")
    void testReglaNegocioCadenaCustodiaCriptografica() {
        String hashInicial = CustodiaMuestra.generarHash("MUESTRA-QUIMICA-88");
        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-QUIMICA-88", hashInicial);

        assertNotNull(custodia.getHashOrigen());
        assertEquals(hashInicial, custodia.getHashActual());

        custodia.registrarPasoWaypoint("WP-ESCLUSA");
        assertFalse(custodia.verificarIntegridad(hashInicial));
        assertTrue(custodia.verificarIntegridad(custodia.getHashActual()));
    }

    @Test
    @DisplayName("Regla de Negocio RN-EM-02: Umbral de batería menor a 25% dispara reasignación preventiva")
    void testReglaNegocioUmbralBateriaReasignacion() {
        DroneEnterprise droneBateriaBaja = flota.get(0);
        droneBateriaBaja.setBateria(20); // Menor al umbral del 25%

        TramoRuta tramo1 = new TramoRuta("TR-01", wp1, wp2, 400, droneBateriaBaja);
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-BAT-01", "Misión con batería insuficiente",
                wp1.getZona(), wp2.getZona(),
                List.of(wp1, wp2), List.of(tramo1),
                EstadoMisionEnterprise.PLANIFICADA,
                Prioridad.ALTA, 200
        );

        CustodiaMuestra custodia = new CustodiaMuestra("MUESTRA-BAT", CustodiaMuestra.generarHash("BAT"));
        ProveedorCondicionesHidricas proveedor = zona -> new CondicionesHidricasDominio(2.5, 10.0, 0.2, true);
        OrquestadorMisionMultiEtapa orquestador = new OrquestadorMisionMultiEtapa(proveedor);

        boolean exito = orquestador.ejecutarMisionCompleta(mision, flota, custodia, List.of());

        assertTrue(exito);
        assertTrue(custodia.getRegistrosTrazabilidad().stream()
                .anyMatch(r -> r.contains("TRANSFERENCIA:WP-01:DRN-01->DRN-02")));
    }
}
