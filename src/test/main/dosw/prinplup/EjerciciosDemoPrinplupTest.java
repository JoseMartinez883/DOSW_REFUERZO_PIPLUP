package main.dosw.prinplup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Pruebas unitarias para los ejecutables y demostraciones de consola de la Fase Prinplup (Ejercicio 13).
 */
class EjerciciosDemoPrinplupTest {

    @Test
    @DisplayName("Debe ejecutar exitosamente la demo de Ejercicio01Prinplup (Streams Avanzados)")
    void testEjercicio01Main() {
        assertDoesNotThrow(() -> Ejercicio01Prinplup.main(new String[]{}));
    }

    @Test
    @DisplayName("Debe ejecutar exitosamente la demo de Ejercicio03Prinplup (Patrones Integrados)")
    void testEjercicio03Main() {
        assertDoesNotThrow(() -> Ejercicio03Prinplup.main(new String[]{}));
    }
}
