package main.dosw.prinplup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias — ConsultorFlotaPrinplup (Streams Avanzados)")
class ConsultorFlotaPrinplupTest {

    private ConsultorFlotaPrinplup consultor;
    private List<DroneAcuatico> flota;
    private List<Mision> misiones;

    @BeforeEach
    void setUp() {
        consultor = new ConsultorFlotaPrinplup();
        flota = Ejercicio01Prinplup.crearFlota15Drones();
        misiones = Ejercicio01Prinplup.crearMisionesEjemplo(flota);
    }

    @Test
    @DisplayName("1. Agrupar disponibles por tipo: solo incluye drones en estado DISPONIBLE")
    void agruparDronesDisponiblesPorTipo_agrupaSoloDisponibles() {
        // Act
        Map<TipoDrone, List<DroneAcuatico>> agrupados = consultor.agruparDronesDisponiblesPorTipo(flota);

        // Assert
        assertNotNull(agrupados);
        assertTrue(agrupados.containsKey(TipoDrone.SUPERFICIAL));
        assertTrue(agrupados.containsKey(TipoDrone.SEMISUMERGIDO));
        assertTrue(agrupados.containsKey(TipoDrone.BUCEADOR));

        // En la flota de 15:
        // Superficial disponibles: AR-01 (90%), AR-02 (45%) -> 2 drones
        assertEquals(2, agrupados.get(TipoDrone.SUPERFICIAL).size());
        // Semisumergido disponibles: AR-06 (88%), AR-07 (65%), AR-10 (95%) -> 3 drones
        assertEquals(3, agrupados.get(TipoDrone.SEMISUMERGIDO).size());
        // Buceador disponibles: AR-11 (85%), AR-13 (92%), AR-14 (40%) -> 3 drones
        assertEquals(3, agrupados.get(TipoDrone.BUCEADOR).size());

        // Verificar que ningún drone agrupado esté no disponible
        agrupados.values().forEach(lista ->
                lista.forEach(d -> assertEquals(EstadoDrone.DISPONIBLE, d.estado()))
        );
    }

    @Test
    @DisplayName("2. Agrupar disponibles por tipo: con lista nula retorna mapa vacío")
    void agruparDronesDisponiblesPorTipo_listaNula_retornaMapaVacio() {
        assertTrue(consultor.agruparDronesDisponiblesPorTipo(null).isEmpty());
    }

    @Test
    @DisplayName("3. Drone óptimo para zona agitada (Canal Central): debe seleccionar Semisumergido con mayor batería")
    void obtenerDroneOptimoParaZona_canalCentral_retornaSemisumergidoMayorBateria() {
        // Act
        Optional<DroneAcuatico> optimo = consultor.obtenerDroneOptimoParaZona(flota, "Canal Central");

        // Assert
        assertTrue(optimo.isPresent());
        // En Canal Central disponible con mayor batería: AR-06 (88% Semisumergido)
        assertEquals("AR-06", optimo.get().id());
        assertEquals(TipoDrone.SEMISUMERGIDO, optimo.get().tipo());
        assertEquals(88, optimo.get().bateria());
    }

    @Test
    @DisplayName("4. Drone óptimo para Embalse Norte: debe seleccionar el disponible y compatible con mayor batería")
    void obtenerDroneOptimoParaZona_embalseNorte_retornaMayorBateriaCompatible() {
        // Act
        Optional<DroneAcuatico> optimo = consultor.obtenerDroneOptimoParaZona(flota, "Embalse Norte");

        // Assert
        assertTrue(optimo.isPresent());
        // En Embalse Norte compatibles y disponibles:
        // AR-01 (Superficial 90%), AR-07 (Semisumergido 65%), AR-11 (Buceador 85%), AR-14 (Buceador 40%) -> AR-01 gana con 90%
        assertEquals("AR-01", optimo.get().id());
        assertEquals(90, optimo.get().bateria());
    }

    @Test
    @DisplayName("5. Drone óptimo para zona: parámetros nulos o vacíos retornan Optional vacío")
    void obtenerDroneOptimoParaZona_parametrosInvalidos_retornaEmpty() {
        assertTrue(consultor.obtenerDroneOptimoParaZona(null, "Canal Central").isEmpty());
        assertTrue(consultor.obtenerDroneOptimoParaZona(flota, null).isEmpty());
        assertTrue(consultor.obtenerDroneOptimoParaZona(flota, "   ").isEmpty());
    }

    @Test
    @DisplayName("6. Promedio de batería por tipo: calcula correctamente la media aritmética")
    void calcularPromedioBateriaPorTipo_calculaPromedioCorrecto() {
        // Act
        Map<TipoDrone, Double> promedios = consultor.calcularPromedioBateriaPorTipo(flota);

        // Assert
        assertNotNull(promedios);
        // Superficial: (90+45+20+75+15)/5 = 245/5 = 49.0
        assertEquals(49.0, promedios.get(TipoDrone.SUPERFICIAL), 0.01);
        // Semisumergido: (88+65+80+30+95)/5 = 358/5 = 71.6
        assertEquals(71.6, promedios.get(TipoDrone.SEMISUMERGIDO), 0.01);
        // Buceador: (85+70+92+40+10)/5 = 297/5 = 59.4
        assertEquals(59.4, promedios.get(TipoDrone.BUCEADOR), 0.01);
    }

    @Test
    @DisplayName("7. Promedio de batería: con lista nula retorna mapa vacío")
    void calcularPromedioBateriaPorTipo_listaNula_retornaMapaVacio() {
        assertTrue(consultor.calcularPromedioBateriaPorTipo(null).isEmpty());
    }

    @Test
    @DisplayName("8. Separar misiones críticas: particiona exactamente en críticas (true) y no críticas (false)")
    void separarMisionesCriticas_divideCorrectamente() {
        // Act
        Map<Boolean, List<Mision>> particion = consultor.separarMisionesCriticas(misiones);

        // Assert
        assertNotNull(particion);
        assertEquals(2, particion.get(true).size()); // MIS-201 y MIS-203 son CRÍTICAS
        assertEquals(3, particion.get(false).size()); // MIS-202, MIS-204, MIS-205 no son críticas

        assertTrue(particion.get(true).stream().allMatch(m -> m.prioridad() == Prioridad.CRITICA));
        assertTrue(particion.get(false).stream().noneMatch(m -> m.prioridad() == Prioridad.CRITICA));
    }

    @Test
    @DisplayName("9. Separar misiones críticas: con lista nula retorna mapa vacío")
    void separarMisionesCriticas_listaNula_retornaMapaVacio() {
        assertTrue(consultor.separarMisionesCriticas(null).isEmpty());
    }

    @Test
    @DisplayName("10. Listar zonas únicas: retorna zonas sin duplicados de drones disponibles")
    void listarZonasUnicasFlotaActiva_retornaZonasUnicasOrdenadas() {
        // Act
        List<String> zonas = consultor.listarZonasUnicasFlotaActiva(flota);

        // Assert
        assertNotNull(zonas);
        assertFalse(zonas.isEmpty());
        // Debe contener las 5 zonas activas
        assertTrue(zonas.contains("Canal Central"));
        assertTrue(zonas.contains("Embalse Norte"));
        assertTrue(zonas.contains("Laboratorio Hídrico"));
        assertTrue(zonas.contains("Laguna Sur"));
        assertTrue(zonas.contains("Punto Ribereño Este"));

        // No debe tener duplicados
        assertEquals(zonas.size(), zonas.stream().distinct().count());
    }

    @Test
    @DisplayName("11. Listar zonas únicas: con lista nula retorna lista vacía")
    void listarZonasUnicasFlotaActiva_listaNula_retornaListaVacia() {
        assertTrue(consultor.listarZonasUnicasFlotaActiva(null).isEmpty());
    }

    @Test
    @DisplayName("12. Ordenar misiones por prioridad y batería: aplica comparador compuesto")
    void ordenarMisionesPorPrioridadYBateria_ordenaCorrectamente() {
        // Act
        List<Mision> ordenadas = consultor.ordenarMisionesPorPrioridadYBateria(misiones);

        // Assert
        assertEquals(5, ordenadas.size());
        // Las primeras deben ser CRÍTICAS
        assertEquals(Prioridad.CRITICA, ordenadas.get(0).prioridad());
        assertEquals(Prioridad.CRITICA, ordenadas.get(1).prioridad());
        // Entre las dos críticas, gana la de mayor batería (AR-01 tiene 90% vs AR-11 que tiene 85%)
        assertEquals("MIS-201", ordenadas.get(0).id());
        assertEquals("MIS-203", ordenadas.get(1).id());
    }

    @Test
    @DisplayName("13. Ordenar misiones: con lista nula retorna lista vacía")
    void ordenarMisionesPorPrioridadYBateria_listaNula_retornaListaVacia() {
        assertTrue(consultor.ordenarMisionesPorPrioridadYBateria(null).isEmpty());
    }

    @Test
    @DisplayName("14. Compatibilidad con zona: valida correctamente cada tipo contra zonas")
    void esCompatibleConZona_evaluaCorrectamente() {
        assertFalse(consultor.esCompatibleConZona(null, "Embalse Norte"));
        assertFalse(consultor.esCompatibleConZona(TipoDrone.SUPERFICIAL, null));
        // Superficial no compatible con Canal Central
        assertFalse(consultor.esCompatibleConZona(TipoDrone.SUPERFICIAL, "Canal Central"));
        assertTrue(consultor.esCompatibleConZona(TipoDrone.SUPERFICIAL, "Laguna Sur"));

        // Semisumergido compatible con todas
        assertTrue(consultor.esCompatibleConZona(TipoDrone.SEMISUMERGIDO, "Canal Central"));
        assertTrue(consultor.esCompatibleConZona(TipoDrone.SEMISUMERGIDO, "Embalse Norte"));

        // Buceador solo con aguas profundas
        assertTrue(consultor.esCompatibleConZona(TipoDrone.BUCEADOR, "Embalse Norte"));
        assertTrue(consultor.esCompatibleConZona(TipoDrone.BUCEADOR, "Laboratorio Hídrico"));
        assertFalse(consultor.esCompatibleConZona(TipoDrone.BUCEADOR, "Canal Central"));
    }

    @Test
    @DisplayName("15. Cobertura del ejecutable principal Ejercicio01Prinplup")
    void ejecutablePrincipal_seEjecutaCorrectamente() {
        assertDoesNotThrow(() -> Ejercicio01Prinplup.main(new String[]{}));
    }
}
