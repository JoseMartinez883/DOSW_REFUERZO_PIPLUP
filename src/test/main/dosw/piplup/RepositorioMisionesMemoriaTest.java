package main.dosw.piplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — RepositorioMisionesMemoria")
class RepositorioMisionesMemoriaTest {

    private RepositorioMisionesMemoria repositorio;
    private Mision mision1;
    private Mision mision2;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioMisionesMemoria();
        DroneAcuatico drone1 = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 90, true, "Embalse Norte");
        DroneAcuatico drone2 = new DroneAcuatico("AR-02", "Aqua-Ranger 100", 75, true, "Canal Central");

        mision1 = new Mision.Builder()
                .id("M-1")
                .drone(drone1)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        mision2 = new Mision.Builder()
                .id("M-2")
                .drone(drone2)
                .puntoPartida("Canal Central")
                .puntoLlegada("Laguna Sur")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();
    }

    @Test
    @DisplayName("1. Guardar y buscar por ID exitosamente")
    void guardarYBuscarPorId_exitoso() {
        repositorio.guardar(mision1);
        Optional<Mision> encontrada = repositorio.buscarPorId("M-1");

        assertTrue(encontrada.isPresent());
        assertEquals("M-1", encontrada.get().getId());
    }

    @Test
    @DisplayName("2. Guardar misión nula: Lanza NullPointerException")
    void guardarMisionNula_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> repositorio.guardar(null));
    }

    @Test
    @DisplayName("3. Buscar por ID inexistente: Retorna Optional vacío")
    void buscarPorIdInexistente_retornaEmpty() {
        Optional<Mision> resultado = repositorio.buscarPorId("NO-EXISTE");
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("4. Buscar por ID nulo o vacío: Retorna Optional vacío")
    void buscarPorIdNuloOVacio_retornaEmpty() {
        assertTrue(repositorio.buscarPorId(null).isEmpty());
        assertTrue(repositorio.buscarPorId("   ").isEmpty());
    }

    @Test
    @DisplayName("5. Obtener todas las misiones registradas")
    void obtenerTodas_retornaListaInmutableConMisiones() {
        repositorio.guardar(mision1);
        repositorio.guardar(mision2);

        List<Mision> todas = repositorio.obtenerTodas();
        assertEquals(2, todas.size());
        assertThrows(UnsupportedOperationException.class, () -> todas.add(mision1));
    }

    @Test
    @DisplayName("6. Verificar existencia de misión por ID")
    void existePorId_comportamientoCorrecto() {
        repositorio.guardar(mision1);

        assertTrue(repositorio.existePorId("M-1"));
        assertFalse(repositorio.existePorId("M-99"));
        assertFalse(repositorio.existePorId(null));
        assertFalse(repositorio.existePorId("  "));
    }
}
