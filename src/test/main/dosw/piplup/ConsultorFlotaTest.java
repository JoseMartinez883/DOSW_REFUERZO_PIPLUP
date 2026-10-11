package main.dosw.piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — ConsultorFlota")
class ConsultorFlotaTest {

    private ConsultorFlota consultor;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlota();
        flota = List.of(
                new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte"),
                new DroneAcuatico("AR-02", "Aqua-Ranger 100", 45, true, "Canal Central"),
                new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur"),
                new DroneAcuatico("AR-04", "Aqua-Ranger 100", 73, true, "Punto Ribereño Este")
        );
    }

    @Test
    @DisplayName("1. Filtrar drones disponibles con batería >= 35% ordenados descendentemente")
    void obtainDroneAvailableWithEnoughBattery_retornaOrdenados() {
        List<DroneAcuatico> resultado = consultor.obtainDroneAvailableWithEnoughBattery(flota);

        assertEquals(3, resultado.size());
        assertEquals("AR-01", resultado.get(0).id());
        assertEquals(92, resultado.get(0).bateria());
        assertEquals("AR-04", resultado.get(1).id());
        assertEquals(73, resultado.get(1).bateria());
        assertEquals("AR-02", resultado.get(2).id());
        assertEquals(45, resultado.get(2).bateria());
    }

    @Test
    @DisplayName("2. Obtener IDs únicamente de drones disponibles")
    void obtainIdsDronesAvailable_retornaSoloDisponibles() {
        List<String> ids = consultor.obtainIdsDronesAvailable(flota);

        assertEquals(3, ids.size());
        assertTrue(ids.containsAll(List.of("AR-01", "AR-02", "AR-04")));
        assertFalse(ids.contains("AR-03"));
    }

    @Test
    @DisplayName("3. Existe al menos un drone disponible con batería >= 35%: Retorna true")
    void existsDroneAvailableWithEnoughBattery_retornaTrue() {
        assertTrue(consultor.existsDroneAvailableWithEnoughBattery(flota));
    }

    @Test
    @DisplayName("4. No existen drones aptos: Retorna false")
    void existsDroneAvailableWithEnoughBattery_retornaFalseCuandoNoHayAptos() {
        List<DroneAcuatico> flotaSinAptos = List.of(
                new DroneAcuatico("AR-10", "Aqua-Ranger 100", 20, true, "Laguna Sur"),
                new DroneAcuatico("AR-11", "Aqua-Ranger 100", 90, false, "Embalse Norte")
        );
        assertFalse(consultor.existsDroneAvailableWithEnoughBattery(flotaSinAptos));
    }

    @Test
    @DisplayName("5. Contar cantidad total de drones disponibles")
    void amountDronesAvailable_retornaConteoCorrecto() {
        assertEquals(3, consultor.amountDronesAvailable(flota));
    }

    @Test
    @DisplayName("6. Drone con mayor batería en la flota")
    void obtainDroneWithLargestBattery_retornaMayor() {
        Optional<DroneAcuatico> mayor = consultor.obtainDroneWithLargestBattery(flota);

        assertTrue(mayor.isPresent());
        assertEquals("AR-01", mayor.get().id());
        assertEquals(92, mayor.get().bateria());
    }

    @Test
    @DisplayName("7. Drone con mayor batería con lista vacía: Retorna Optional vacío")
    void obtainDroneWithLargestBattery_listaVaciaRetornaEmpty() {
        Optional<DroneAcuatico> vacio = consultor.obtainDroneWithLargestBattery(Collections.emptyList());
        assertTrue(vacio.isEmpty());
    }
}
