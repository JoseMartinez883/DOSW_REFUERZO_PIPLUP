package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;
import main.dosw.prinplup.fabrica.FabricaDrones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PrioridadCriticaStrategyTest {

    private PrioridadCriticaStrategy estrategia;

    @BeforeEach
    void setUp() {
        estrategia = new PrioridadCriticaStrategy();
    }

    @Test
    @DisplayName("Debe retornar vacío si la flota es nula o vacía")
    void listaVaciaONula() {
        assertTrue(estrategia.seleccionarDrone(null, "Zona Norte").isEmpty());
        assertTrue(estrategia.seleccionarDrone(Collections.emptyList(), "Zona Norte").isEmpty());
    }

    @Test
    @DisplayName("Debe priorizar el dron con mayor batería que supere el umbral crítico del 50%")
    void seleccionaDroneMayorBateriaMayor50() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 45, EstadoDrone.DISPONIBLE, "Norte");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 80, EstadoDrone.DISPONIBLE, "Norte");
        DroneAcuatico d3 = FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "D-3", 65, EstadoDrone.DISPONIBLE, "Norte");

        Optional<DroneAcuatico> res = estrategia.seleccionarDrone(List.of(d1, d2, d3), "Norte");
        assertTrue(res.isPresent());
        assertEquals("D-2", res.get().getId());
    }

    @Test
    @DisplayName("Si ninguno supera el 50%, selecciona el de mayor batería que cumpla el umbral mínimo (35%)")
    void fallbackCuandoNingunoSupera50() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 40, EstadoDrone.DISPONIBLE, "Norte");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 48, EstadoDrone.DISPONIBLE, "Norte");
        DroneAcuatico d3 = FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "D-3", 20, EstadoDrone.DISPONIBLE, "Norte"); // < 35%

        Optional<DroneAcuatico> res = estrategia.seleccionarDrone(List.of(d1, d2, d3), "Norte");
        assertTrue(res.isPresent());
        assertEquals("D-2", res.get().getId());
    }

    @Test
    @DisplayName("Si ninguno cumple el umbral mínimo ni el crítico, retorna Optional.empty()")
    void ningunoCumpleUmbrales() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 15, EstadoDrone.DISPONIBLE, "Norte");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 18, EstadoDrone.DISPONIBLE, "Norte");

        Optional<DroneAcuatico> res = estrategia.seleccionarDrone(List.of(d1, d2), "Norte");
        assertTrue(res.isEmpty());
    }
}
