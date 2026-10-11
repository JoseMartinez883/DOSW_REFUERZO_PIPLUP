package main.dosw.prinplup.fabrica;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FabricaDronesTest {

    @Test
    @DisplayName("Debe crear DroneSuperficial con sus características correspondientes")
    void debeCrearDroneSuperficial() {
        DroneAcuatico drone = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-SUP-01", 90, EstadoDrone.DISPONIBLE, "Embalse Norte"
        );

        assertInstanceOf(DroneSuperficial.class, drone);
        assertEquals(TipoDrone.SUPERFICIAL, drone.getTipo());
        assertEquals(500, drone.getCapacidadCargaGramos());
        assertEquals("Aqua-Surface 200", drone.getModelo());
        assertEquals("DRN-SUP-01", drone.getId());
        assertEquals(90, drone.getBateria());
        assertEquals(EstadoDrone.DISPONIBLE, drone.getEstado());
        assertEquals("Embalse Norte", drone.getZona());
        assertTrue(drone.isDisponible());
    }

    @Test
    @DisplayName("Debe crear DroneSemisumergido con sus características correspondientes")
    void debeCrearDroneSemisumergido() {
        DroneAcuatico drone = FabricaDrones.crearDrone(
                TipoDrone.SEMISUMERGIDO, "DRN-SEMI-01", 85, EstadoDrone.DISPONIBLE, "Canal Central"
        );

        assertInstanceOf(DroneSemisumergido.class, drone);
        assertEquals(TipoDrone.SEMISUMERGIDO, drone.getTipo());
        assertEquals(1500, drone.getCapacidadCargaGramos());
        assertEquals("Aqua-SemiSub 300", drone.getModelo());
        assertEquals("DRN-SEMI-01", drone.getId());
        assertEquals(85, drone.getBateria());
        assertEquals("Canal Central", drone.getZona());
    }

    @Test
    @DisplayName("Debe crear DroneBuceador con sus características correspondientes")
    void debeCrearDroneBuceador() {
        DroneAcuatico drone = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-BUC-01", 75, EstadoDrone.SUMERGIDO, "Laguna Sur"
        );

        assertInstanceOf(DroneBuceador.class, drone);
        assertEquals(TipoDrone.BUCEADOR, drone.getTipo());
        assertEquals(300, drone.getCapacidadCargaGramos());
        assertEquals("Aqua-Diver 500", drone.getModelo());
        assertEquals("DRN-BUC-01", drone.getId());
        assertEquals(EstadoDrone.SUMERGIDO, drone.getEstado());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el tipo es nulo")
    void debeLanzarExcepcionSiTipoEsNulo() {
        assertThrows(IllegalArgumentException.class, () ->
                FabricaDrones.crearDrone(null, "DRN-ERR", 50, EstadoDrone.DISPONIBLE, "Zona Central")
        );
    }

    @Test
    @DisplayName("Debe instanciar constructores sobrecargados de especializaciones de drones")
    void testConstructoresSobrecargadosEspecializaciones() {
        DroneSuperficial sup = new DroneSuperficial("S-1", "Modelo-Custom-S", 85, EstadoDrone.DISPONIBLE, "Zona-S", 600);
        assertEquals("Modelo-Custom-S", sup.getModelo());
        assertEquals(500, sup.getCapacidadCargaGramos()); // Math.min(600, 500)

        DroneSemisumergido semi = new DroneSemisumergido("M-1", "Modelo-Custom-M", 80, EstadoDrone.DISPONIBLE, "Zona-M", 2000);
        assertEquals("Modelo-Custom-M", semi.getModelo());
        assertEquals(1500, semi.getCapacidadCargaGramos()); // Math.min(2000, 1500)

        DroneBuceador buc = new DroneBuceador("B-1", "Modelo-Custom-B", 75, EstadoDrone.DISPONIBLE, "Zona-B", 500);
        assertEquals("Modelo-Custom-B", buc.getModelo());
        assertEquals(300, buc.getCapacidadCargaGramos()); // Math.min(500, 300)
    }
}
