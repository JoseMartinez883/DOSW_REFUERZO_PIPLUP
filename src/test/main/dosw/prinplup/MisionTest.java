package main.dosw.prinplup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de cobertura exhaustiva para el record Mision (Ejercicio 13 - JaCoCo).
 */
class MisionTest {

    @Test
    @DisplayName("Debe validar todos los componentes, getters explícitos y alias del record Mision")
    void testMisionComponentesYGetters() {
        DroneAcuatico drone = new DroneAcuatico(
                "AR-01", "Surface-200", TipoDrone.SUPERFICIAL,
                90, EstadoDrone.DISPONIBLE, "Embalse Norte", 500
        );

        Mision mision = new Mision(
                "MIS-500", drone, "Muelle 1", "Laboratorio Hídrico",
                Prioridad.CRITICA, "MUESTRA_AGUA", 350
        );

        assertEquals("MIS-500", mision.id());
        assertEquals("MIS-500", mision.getId());
        assertEquals(drone, mision.drone());
        assertEquals(drone, mision.getDrone());
        assertEquals("Muelle 1", mision.puntoPartida());
        assertEquals("Muelle 1", mision.getPuntoPartida());
        assertEquals("Laboratorio Hídrico", mision.puntoLlegada());
        assertEquals("Laboratorio Hídrico", mision.getPuntoLlegada());
        assertEquals("Laboratorio Hídrico", mision.zonaDestino());
        assertEquals(Prioridad.CRITICA, mision.prioridad());
        assertEquals(Prioridad.CRITICA, mision.getPrioridad());
        assertEquals("MUESTRA_AGUA", mision.tipoCarga());
        assertEquals("MUESTRA_AGUA", mision.getTipoCarga());
        assertEquals(350, mision.pesoCargaGramos());
        assertEquals(350, mision.getPesoCargaGramos());
        assertEquals(350, mision.pesoGramos());

        assertNotNull(mision.toString());
        assertEquals(mision, new Mision("MIS-500", drone, "Muelle 1", "Laboratorio Hídrico", Prioridad.CRITICA, "MUESTRA_AGUA", 350));
        assertEquals(mision.hashCode(), new Mision("MIS-500", drone, "Muelle 1", "Laboratorio Hídrico", Prioridad.CRITICA, "MUESTRA_AGUA", 350).hashCode());
    }
}
