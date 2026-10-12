package main.dosw.empoleon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.PlanificadorRuta;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para el servicio de dominio PlanificadorRuta (Capa de Dominio - TDD).
 */
class PlanificadorRutaTest {

    private PlanificadorRuta planificador;
    private List<DroneEnterprise> flota;
    private Waypoint wpOrigen;
    private Waypoint wpIntermedio;
    private Waypoint wpDestino;

    @BeforeEach
    void setUp() {
        planificador = new PlanificadorRuta();
        flota = new ArrayList<>();

        flota.add(new DroneEnterprise("DRN-01", "Surface-Alpha", TipoDrone.SUPERFICIAL, 95,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1000, 15));
        flota.add(new DroneEnterprise("DRN-02", "Surface-Beta", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 800, 10));
        flota.add(new DroneEnterprise("DRN-03", "SemiSub-Gamma", TipoDrone.SEMISUMERGIDO, 88,
                EstadoDrone.DISPONIBLE, ZonaHidrica.RED_CANALES.getNombre(), 1500, 20));

        wpOrigen = new Waypoint("WP-01", "Muelle Alfa", ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1);
        wpIntermedio = new Waypoint("WP-02", "Esclusa Beta", ZonaHidrica.RED_CANALES.getNombre(), 2);
        wpDestino = new Waypoint("WP-03", "Laboratorio", ZonaHidrica.LABORATORIO_CENTRAL.getNombre(), 3);
    }

    @Test
    @DisplayName("Planificar ruta exitosamente dividiendo en tramos con asignación óptima")
    void testPlanificarRutaExitosamente() {
        List<Waypoint> ruta = List.of(wpOrigen, wpIntermedio, wpDestino);
        List<TramoRuta> tramos = planificador.planificarRuta(ruta, flota, 500);

        assertEquals(2, tramos.size());
        assertEquals("TR-EM-1", tramos.get(0).id());
        assertNotNull(tramos.get(0).droneAsignado());
        assertEquals("DRN-01", tramos.get(0).droneAsignado().getId()); // Mayor batería (95 vs 80)
        assertEquals("DRN-03", tramos.get(1).droneAsignado().getId());
    }

    @Test
    @DisplayName("Validar error si la lista de waypoints tiene menos de dos puntos")
    void testValidarExcepcionMenosDeDosWaypoints() {
        List<Waypoint> insuficiente = List.of(wpOrigen);
        assertThrows(IllegalArgumentException.class, () ->
                planificador.planificarRuta(insuficiente, flota, 300));
    }

    @Test
    @DisplayName("Validar error si el peso de carga es cero o negativo")
    void testValidarExcepcionPesoInvalido() {
        List<Waypoint> ruta = List.of(wpOrigen, wpIntermedio);
        assertThrows(IllegalArgumentException.class, () ->
                planificador.planificarRuta(ruta, flota, 0));
        assertThrows(IllegalArgumentException.class, () ->
                planificador.planificarRuta(ruta, flota, -50));
    }

    @Test
    @DisplayName("Seleccionar drone óptimo retorna vacío si no hay unidades en la zona")
    void testSeleccionarDroneOptimoVacioSinUnidadesEnZona() {
        Waypoint wpSinDrones = new Waypoint("WP-X", "Remoto", "Zona Inexistente", 1);
        Optional<DroneEnterprise> opt = planificador.seleccionarDroneOptimo(wpSinDrones, flota, 200);

        assertTrue(opt.isEmpty());
    }

    @Test
    @DisplayName("Seleccionar drone óptimo descarta drones con batería crítica (< 25%)")
    void testSeleccionarDroneDescartaBateriaCritica() {
        List<DroneEnterprise> flotaBaja = List.of(
                new DroneEnterprise("DRN-LOW", "LowBat", TipoDrone.SUPERFICIAL, 20,
                        EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1000, 5)
        );
        Optional<DroneEnterprise> opt = planificador.seleccionarDroneOptimo(wpOrigen, flotaBaja, 200);

        assertTrue(opt.isEmpty());
    }

    @Test
    @DisplayName("Calcular distancia total acumulada de la ruta")
    void testCalcularDistanciaTotal() {
        TramoRuta t1 = new TramoRuta("TR-1", wpOrigen, wpIntermedio, 750, flota.get(0));
        TramoRuta t2 = new TramoRuta("TR-2", wpIntermedio, wpDestino, 1250, flota.get(2));

        int total = planificador.calcularDistanciaTotal(List.of(t1, t2));
        assertEquals(2000, total);
    }

    @Test
    @DisplayName("Estimar consumo de batería y validar viabilidad energética")
    void testEstimarConsumoYViabilidadEnergetica() {
        TramoRuta t1 = new TramoRuta("TR-1", wpOrigen, wpIntermedio, 2500, flota.get(0));
        int consumo = planificador.estimarConsumoBateria(t1);
        assertEquals(16, consumo); // 2 km * 8 = 16%

        assertTrue(planificador.esTramoViable(t1, flota.get(0))); // 95 - 16 = 79 >= 25

        DroneEnterprise dronAlLimite = new DroneEnterprise("DRN-LIM", "Lim", TipoDrone.SUPERFICIAL, 30,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1000, 5);
        assertFalse(planificador.esTramoViable(t1, dronAlLimite)); // 30 - 16 = 14 < 25
    }

    @Test
    @DisplayName("Evaluar tramo viable con parámetros nulos o drone no disponible retorna falso")
    void testEvaluarTramoViableConParametrosNulos() {
        assertFalse(planificador.esTramoViable(null, flota.get(0)));
        assertFalse(planificador.esTramoViable(new TramoRuta("T", wpOrigen, wpIntermedio, 500, null), null));

        DroneEnterprise ocupado = new DroneEnterprise("D-OCU", "Busy", TipoDrone.SUPERFICIAL, 90,
                EstadoDrone.EN_MISION, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 1000, 5);
        TramoRuta t = new TramoRuta("T", wpOrigen, wpIntermedio, 500, ocupado);
        assertFalse(planificador.esTramoViable(t, ocupado));
    }
}
