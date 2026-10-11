package main.dosw.piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — RegistradorMisiones")
class RegistradorMisionesTest {

    private RepositorioMisionesMemoria repositorio;
    private ValidadorMision validador;
    private NotificadorOperador notificador;
    private RegistradorMisiones registrador;

    private Mision misionValida;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioMisionesMemoria();
        validador = new ValidadorMision();
        notificador = new NotificadorOperador();
        registrador = new RegistradorMisiones(repositorio, validador, notificador);

        DroneAcuatico drone = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        misionValida = new Mision.Builder()
                .id("MIS-001")
                .drone(drone)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();
    }

    @Test
    @DisplayName("1. Inyección de dependencias nulas lanza NullPointerException")
    void constructor_dependenciasNulasLanzanExcepcion() {
        assertThrows(NullPointerException.class, () -> new RegistradorMisiones(null, validador, notificador));
        assertThrows(NullPointerException.class, () -> new RegistradorMisiones(repositorio, null, notificador));
        assertThrows(NullPointerException.class, () -> new RegistradorMisiones(repositorio, validador, null));
    }

    @Test
    @DisplayName("2. Registrar misión válida exitosamente")
    void registrarMision_exitosa() {
        assertDoesNotThrow(() -> registrador.registrarMision(misionValida));
        Optional<Mision> consultada = registrador.consultarPorId("MIS-001");
        assertTrue(consultada.isPresent());
        assertEquals("MIS-001", consultada.get().getId());
    }

    @Test
    @DisplayName("3. Registrar misión nula lanza NullPointerException")
    void registrarMisionNula_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> registrador.registrarMision(null));
    }

    @Test
    @DisplayName("4. Registrar misión con ID duplicado lanza IllegalStateException")
    void registrarMisionIdDuplicado_lanzaExcepcion() {
        registrador.registrarMision(misionValida);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> registrador.registrarMision(misionValida));
        assertTrue(ex.getMessage().contains("Ya existe una misión"));
    }

    @Test
    @DisplayName("5. Consultar todas las misiones registradas")
    void consultarTodas_retornaLista() {
        registrador.registrarMision(misionValida);
        List<Mision> lista = registrador.consultarTodas();
        assertEquals(1, lista.size());
    }
}
