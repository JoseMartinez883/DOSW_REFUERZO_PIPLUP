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

    @Test
    @DisplayName("8. Drone nulo en evaluación de batería: Retorna false")
    void droneNulo_bateriaRetornaFalse() {
        // Act & Assert
        assertFalse(validador.tieneBateriaSuficiente(null), "Un drone nulo no debe ser considerado con batería suficiente");
    }

    @Test
    @DisplayName("9. Punto de llegada válido: Pasa sin lanzar excepción")
    void puntoLlegadaValido_noLanzaExcepcion() {
        // Act & Assert
        assertDoesNotThrow(() -> validador.validarPuntoLlegada("Laboratorio Hídrico"));
    }

    @Test
    @DisplayName("10. Punto de partida nulo: Lanza IllegalArgumentException")
    void puntoPartidaNulo_lanzaExcepcion() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoPartida(null));
        assertEquals("El punto de partida no puede ser nulo ni estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("11. Punto de partida vacío: Lanza IllegalArgumentException")
    void puntoPartidaVacio_lanzaExcepcion() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarPuntoPartida("   "));
        assertEquals("El punto de partida no puede ser nulo ni estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("12. Punto de partida válido: Pasa sin lanzar excepción")
    void puntoPartidaValido_noLanzaExcepcion() {
        // Act & Assert
        assertDoesNotThrow(() -> validador.validarPuntoPartida("Embalse Norte"));
    }

    @Test
    @DisplayName("13. Drone disponible válido: No lanza excepción")
    void droneDisponibleValido_noLanzaExcepcion() {
        // Arrange
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 80, true, "Embalse Norte");

        // Act & Assert
        assertDoesNotThrow(() -> validador.validarDroneDisponible(drone));
    }

    @Test
    @DisplayName("14. Drone nulo en disponibilidad: Lanza IllegalArgumentException")
    void droneNuloEnDisponibilidad_lanzaExcepcion() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarDroneDisponible(null));
        assertEquals("El drone no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("15. Zona nula o vacía: Lanza IllegalArgumentException")
    void zonaNulaOVacia_lanzaExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> validador.validarZona(null));
        assertThrows(IllegalArgumentException.class, () -> validador.validarZona("   "));
    }

    @Test
    @DisplayName("16. Asignación con misión nula: Lanza IllegalArgumentException")
    void asignacionMisionNula_lanzaExcepcion() {
        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validador.validarAsignacion(null));
        assertEquals("La misión a validar no puede ser nula", ex.getMessage());
    }

    @Test
    @DisplayName("17. Asignación con misión sin drone: Lanza IllegalStateException")
    void asignacionMisionSinDrone_lanzaExcepcion() {
        // Arrange
        Mision misionSinDrone = new Mision("MIS-201", null, "Laboratorio Hídrico", "Embalse Norte",
                TipoCarga.MUESTRA_AGUA, EstadoMision.PENDIENTE);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validador.validarAsignacion(misionSinDrone));
        assertEquals("La misión requiere un drone acuático asignado", ex.getMessage());
    }

    @Test
    @DisplayName("18. Asignación con drone no disponible: Lanza IllegalStateException")
    void asignacionMisionDroneNoDisponible_lanzaExcepcion() {
        // Arrange
        DroneAcuatico noDisponible = new DroneAcuatico("AR-02", "Aqua-Ranger 100", 90, false, "Canal Central");
        Mision mision = new Mision("MIS-202", noDisponible, "Laboratorio Hídrico", "Canal Central",
                TipoCarga.MUESTRA_AGUA, EstadoMision.PENDIENTE);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validador.validarAsignacion(mision));
        assertTrue(ex.getMessage().contains("no está disponible para operar"));
    }

    @Test
    @DisplayName("19. Asignación con drone batería insuficiente: Lanza IllegalStateException")
    void asignacionMisionDroneBateriaBaja_lanzaExcepcion() {
        // Arrange
        DroneAcuatico bateriaBaja = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 20, true, "Laguna Sur");
        Mision mision = new Mision("MIS-203", bateriaBaja, "Laboratorio Hídrico", "Laguna Sur",
                TipoCarga.MUESTRA_AGUA, EstadoMision.PENDIENTE);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> validador.validarAsignacion(mision));
        assertTrue(ex.getMessage().contains("batería insuficiente"));
    }

    @Test
    @DisplayName("20. Asignación de misión válida: Pasa validación completa sin excepciones")
    void asignacionMisionValida_noLanzaExcepcion() {
        // Arrange
        DroneAcuatico droneOptimo = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 95, true, "Embalse Norte");
        Mision misionValida = new Mision.Builder()
                .id("MIS-204")
                .drone(droneOptimo)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> validador.validarAsignacion(misionValida));
    }
}
