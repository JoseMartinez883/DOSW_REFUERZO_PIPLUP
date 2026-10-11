package main.dosw.piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias TDD — ValidadorMision")
class ValidadorMisionTest {

    private ValidadorMision validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorMision();
    }

    @Test
    @DisplayName("1. Batería suficiente: Drone con batería >= 35% puede ser asignado")
    void droneBateriaSuficiente_puedeAsignarse() {
        // Arrange
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");

        // Act
        boolean resultado = validador.tieneBateriaSuficiente(drone);

        // Assert
        assertTrue(resultado, "Un drone con 85% de batería debe ser admitido para la misión");
    }

    @Test
    @DisplayName("2. Batería crítica: Drone con batería < 35% NO puede ser asignado")
    void droneBateriaCritica_noAsignable() {
        // Arrange
        DroneAcuatico drone = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // Act
        boolean resultado = validador.tieneBateriaSuficiente(drone);

        // Assert
        assertFalse(resultado, "Un drone con 18% de batería (< 35%) no debe ser admitido");
    }

    @Test
    @DisplayName("3. Punto de llegada nulo: Lanza IllegalArgumentException")
    void puntoLlegadaNulo_lanzaExcepcion() {
        // Arrange, Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada(null));
        assertEquals("El punto de llegada no puede ser nulo ni estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("4. Punto de llegada vacío: Lanza IllegalArgumentException")
    void puntoLlegadaVacio_lanzaExcepcion() {
        // Arrange, Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoLlegada("   "));
        assertEquals("El punto de llegada no puede ser nulo ni estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("5. Drone no disponible: Lanza IllegalStateException en validación de asignación")
    void droneNoDisponible_lanzaExcepcion() {
        // Arrange
        DroneAcuatico droneNoDisponible = new DroneAcuatico("AR-02", "Aqua-Ranger 100", 70, false, "Canal Central");
        
        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validador.validarDroneDisponible(droneNoDisponible));
        assertEquals("El drone asignado AR-02 no está disponible para operar", ex.getMessage());
    }

    @Test
    @DisplayName("6. Zona inválida: Lanza IllegalArgumentException para zonas fuera del campus (Edge Case)")
    void zonaInvalida_lanzaExcepcion() {
        // Arrange, Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarZona("Océano Atlántico"));
        assertTrue(ex.getMessage().contains("Zona no autorizada"), 
                "Debe indicar que la zona no está autorizada en el campus");
    }

    @Test
    @DisplayName("7. Zona válida del campus: Pasa validación sin excepciones")
    void zonaValida_noLanzaExcepcion() {
        // Arrange, Act & Assert
        assertDoesNotThrow(() -> validador.validarZona("Embalse Norte"));
        assertDoesNotThrow(() -> validador.validarZona("Canal Central"));
        assertDoesNotThrow(() -> validador.validarZona("Laguna Sur"));
        assertDoesNotThrow(() -> validador.validarZona("Punto Ribereño Este"));
        assertDoesNotThrow(() -> validador.validarZona("Laboratorio Hídrico"));
    }
}
