package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MayorBateriaStrategyTest {

    private final MayorBateriaStrategy estrategia = new MayorBateriaStrategy();

    @Test
    @DisplayName("Debe seleccionar el drone disponible con mayor batería respetando el umbral de seguridad (>= 20%)")
    void debeSeleccionarDroneConMayorBateriaSegura() {
        DroneAcuatico d1 = new DroneAcuatico("D-1", "M1", TipoDrone.SUPERFICIAL, 45, EstadoDrone.DISPONIBLE, "Norte", 500);
        DroneAcuatico d2 = new DroneAcuatico("D-2", "M1", TipoDrone.SUPERFICIAL, 85, EstadoDrone.DISPONIBLE, "Norte", 500);
        DroneAcuatico d3 = new DroneAcuatico("D-3", "M1", TipoDrone.SUPERFICIAL, 15, EstadoDrone.DISPONIBLE, "Norte", 500);

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionarDrone(List.of(d1, d2, d3), "Norte");

        assertTrue(seleccionado.isPresent());
        assertEquals("D-2", seleccionado.get().getId());
        assertEquals(85, seleccionado.get().getBateria());
    }

    @Test
    @DisplayName("No debe seleccionar un drone con batería crítica (< 20%) aunque esté en estado DISPONIBLE")
    void noDebeSeleccionarDroneConBateriaCritica() {
        DroneAcuatico dCritico = new DroneAcuatico("D-CRIT", "M1", TipoDrone.SUPERFICIAL, 12, EstadoDrone.DISPONIBLE, "Norte", 500);

        Optional<DroneAcuatico> seleccionado = estrategia.seleccionarDrone(List.of(dCritico), "Norte");

        assertTrue(seleccionado.isEmpty());
    }

    @Test
    @DisplayName("Debe retornar Optional.empty() si la lista es nula o vacía")
    void debeManejarListasVaciasONulas() {
        assertTrue(estrategia.seleccionarDrone(null, "Norte").isEmpty());
        assertTrue(estrategia.seleccionarDrone(List.of(), "Norte").isEmpty());
    }
}
