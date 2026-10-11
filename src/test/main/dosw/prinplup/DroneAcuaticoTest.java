package main.dosw.prinplup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de cobertura exhaustiva para DroneAcuatico (Ejercicio 13 - JaCoCo).
 * Resuelve y elimina las ramas amarillas en comparaciones, branches de equals, hashcode y ciclo de vida.
 */
class DroneAcuaticoTest {

    @Test
    @DisplayName("Debe verificar todos los accessors y mutadores de estado")
    void testGettersSettersYMetodosOperacionales() {
        DroneAcuatico drone = new DroneAcuatico(
                "DRN-TEST-01", "Aqua-Model-X", TipoDrone.SUPERFICIAL,
                80, EstadoDrone.DISPONIBLE, "Embalse Norte", 500
        );

        assertEquals("DRN-TEST-01", drone.id());
        assertEquals("DRN-TEST-01", drone.getId());
        assertEquals("Aqua-Model-X", drone.modelo());
        assertEquals("Aqua-Model-X", drone.getModelo());
        assertEquals(TipoDrone.SUPERFICIAL, drone.tipo());
        assertEquals(TipoDrone.SUPERFICIAL, drone.getTipo());
        assertEquals(80, drone.bateria());
        assertEquals(80, drone.getBateria());
        assertEquals(EstadoDrone.DISPONIBLE, drone.estado());
        assertEquals(EstadoDrone.DISPONIBLE, drone.getEstado());
        assertEquals("Embalse Norte", drone.zona());
        assertEquals("Embalse Norte", drone.getZona());
        assertEquals(500, drone.capacidadCargaGramos());
        assertEquals(500, drone.getCapacidadCargaGramos());

        assertTrue(drone.isDisponible());
        assertTrue(drone.puedeCargar(400));
        assertTrue(drone.puedeCargar(500));
        assertFalse(drone.puedeCargar(501));

        // Mutaciones
        drone.setBateria(40);
        assertEquals(40, drone.getBateria());

        drone.setEstado(EstadoDrone.EN_MISION);
        assertEquals(EstadoDrone.EN_MISION, drone.getEstado());
        assertFalse(drone.isDisponible());

        drone.setZona("Laguna Sur");
        assertEquals("Laguna Sur", drone.getZona());
    }

    @Test
    @DisplayName("Debe cubrir todas las ramas condicionales de equals(), hashCode() y toString()")
    void testEqualsHashCodeYToString() {
        DroneAcuatico base = new DroneAcuatico("D-1", "Mod-1", TipoDrone.BUCEADOR, 90, EstadoDrone.DISPONIBLE, "Z-1", 300);
        DroneAcuatico identico = new DroneAcuatico("D-1", "Mod-1", TipoDrone.BUCEADOR, 90, EstadoDrone.DISPONIBLE, "Z-1", 300);

        // Reflexividad
        assertEquals(base, base);
        // Simetría
        assertEquals(base, identico);
        assertEquals(identico, base);
        assertEquals(base.hashCode(), identico.hashCode());

        // toString no nulo
        assertNotNull(base.toString());
        assertTrue(base.toString().contains("D-1"));

        // No nulo
        assertNotEquals(null, base);
        // Tipo diferente
        assertNotEquals("un string cualquiera", base);

        // Ramas de desigualdad campo por campo
        assertNotEquals(base, new DroneAcuatico("D-2", "Mod-1", TipoDrone.BUCEADOR, 90, EstadoDrone.DISPONIBLE, "Z-1", 300));
        assertNotEquals(base, new DroneAcuatico("D-1", "Mod-OTRO", TipoDrone.BUCEADOR, 90, EstadoDrone.DISPONIBLE, "Z-1", 300));
        assertNotEquals(base, new DroneAcuatico("D-1", "Mod-1", TipoDrone.SUPERFICIAL, 90, EstadoDrone.DISPONIBLE, "Z-1", 300));
        assertNotEquals(base, new DroneAcuatico("D-1", "Mod-1", TipoDrone.BUCEADOR, 89, EstadoDrone.DISPONIBLE, "Z-1", 300));
        assertNotEquals(base, new DroneAcuatico("D-1", "Mod-1", TipoDrone.BUCEADOR, 90, EstadoDrone.FALLO, "Z-1", 300));
        assertNotEquals(base, new DroneAcuatico("D-1", "Mod-1", TipoDrone.BUCEADOR, 90, EstadoDrone.DISPONIBLE, "Z-OTRA", 300));
        assertNotEquals(base, new DroneAcuatico("D-1", "Mod-1", TipoDrone.BUCEADOR, 90, EstadoDrone.DISPONIBLE, "Z-1", 301));
    }
}
