package main.dosw.piplup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Pruebas de Ejecución de Métodos Main de Ejercicios")
class EjerciciosMainTest {

    @Test
    @DisplayName("Ejecución de Ejercisio1 main")
    void testEjercisio1Main() {
        assertDoesNotThrow(() -> Ejercisio1.main(new String[0]));
    }

    @Test
    @DisplayName("Ejecución de Ejercicio03 main")
    void testEjercicio03Main() {
        assertDoesNotThrow(() -> Ejercicio03.main(new String[0]));
    }

    @Test
    @DisplayName("Ejecución de Ejercicio04 main")
    void testEjercicio04Main() {
        assertDoesNotThrow(() -> Ejercicio04.main(new String[0]));
    }
}
