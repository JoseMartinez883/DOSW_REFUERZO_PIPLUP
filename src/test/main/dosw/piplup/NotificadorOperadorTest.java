package main.dosw.piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Pruebas Unitarias — NotificadorOperador")
class NotificadorOperadorTest {

    private NotificadorOperador notificador;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorOperador();
    }

    @Test
    @DisplayName("1. Notificar registro exitoso con misión válida")
    void notificarRegistroExitoso_conMisionValida() {
        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 85, true, "Embalse Norte");
        Mision mision = new Mision.Builder()
                .id("M-100")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        assertDoesNotThrow(() -> notificador.notificarRegistroExitoso(mision));
    }

    @Test
    @DisplayName("2. Notificar registro exitoso con misión nula no lanza excepción")
    void notificarRegistroExitoso_conMisionNula() {
        assertDoesNotThrow(() -> notificador.notificarRegistroExitoso(null));
    }

    @Test
    @DisplayName("3. Notificar error")
    void notificarError_noLanzaExcepcion() {
        assertDoesNotThrow(() -> notificador.notificarError("Falla de batería"));
    }

    @Test
    @DisplayName("4. Notificar consulta")
    void notificarConsulta_noLanzaExcepcion() {
        assertDoesNotThrow(() -> notificador.notificarConsulta("Consulta realizada"));
    }
}
