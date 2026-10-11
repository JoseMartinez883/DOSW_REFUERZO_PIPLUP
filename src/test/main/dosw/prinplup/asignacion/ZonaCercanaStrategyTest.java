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

class ZonaCercanaStrategyTest {

    private ZonaCercanaStrategy estrategia;

    @BeforeEach
    void setUp() {
        estrategia = new ZonaCercanaStrategy();
    }

    @Test
    @DisplayName("Debe retornar vacío si la lista es nula o vacía")
    void listaVaciaONula() {
        assertTrue(estrategia.seleccionarDrone(null, "Canal Central").isEmpty());
        assertTrue(estrategia.seleccionarDrone(Collections.emptyList(), "Canal Central").isEmpty());
    }

    @Test
    @DisplayName("Debe priorizar el dron que ya está en la misma zona con batería segura")
    void seleccionaDroneEnMismaZona() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 95, EstadoDrone.DISPONIBLE, "Embalse Norte");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 70, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico d3 = FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "D-3", 60, EstadoDrone.DISPONIBLE, "Canal Central");

        Optional<DroneAcuatico> res = estrategia.seleccionarDrone(List.of(d1, d2, d3), "Canal Central");
        assertTrue(res.isPresent());
        assertEquals("D-2", res.get().getId(), "Debe seleccionar el de mayor batería de la misma zona");
    }

    @Test
    @DisplayName("Si no hay ningún dron en la misma zona, selecciona el de mayor batería de otra zona")
    void fallbackAOtraZonaConMayorBateria() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 50, EstadoDrone.DISPONIBLE, "Embalse Norte");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 85, EstadoDrone.DISPONIBLE, "Laguna Sur");

        Optional<DroneAcuatico> res = estrategia.seleccionarDrone(List.of(d1, d2), "Punto Ribereño");
        assertTrue(res.isPresent());
        assertEquals("D-2", res.get().getId());
    }

    @Test
    @DisplayName("Si ningún dron tiene batería suficiente (>= 20%), retorna Optional.empty()")
    void ningunDroneConBateriaSuficiente() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 10, EstadoDrone.DISPONIBLE, "Canal Central");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 15, EstadoDrone.DISPONIBLE, "Embalse Norte");

        Optional<DroneAcuatico> res = estrategia.seleccionarDrone(List.of(d1, d2), "Canal Central");
        assertTrue(res.isEmpty());
    }
}
