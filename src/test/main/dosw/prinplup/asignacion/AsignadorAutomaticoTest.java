package main.dosw.prinplup.asignacion;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Mision;
import main.dosw.prinplup.Prioridad;
import main.dosw.prinplup.TipoDrone;
import main.dosw.prinplup.alertas.ObservadorMision;
import main.dosw.prinplup.fabrica.FabricaDrones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Suite TDD con Mockito para el AsignadorAutomatico de AquaPort v2 (Ejercicio 12 - Fase Prinplup).
 * Verifica los 5 escenarios obligatorios: Happy Path, Misión Crítica sin drones,
 * Drone en FALLO con reasignación de respaldo, Lista vacía y Casos Edge de umbrales de batería.
 */
@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    @Mock
    private EstrategiaSeleccion estrategiaMock;

    @Mock
    private ObservadorMision observadorMock;

    @Mock
    private ObservadorMision mockCentroControl;

    @Mock
    private ObservadorMision mockTecnicoMantenimiento;

    private AsignadorAutomatico asignador;
    private List<DroneAcuatico> flotaPruebas;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(estrategiaMock);

        // Flota base para escenarios generales
        flotaPruebas = List.of(
                FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "DRN-SUP-01", 90, EstadoDrone.DISPONIBLE, "Embalse Norte"),
                FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "DRN-SEMI-01", 75, EstadoDrone.DISPONIBLE, "Canal Central"),
                FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "DRN-BUC-01", 85, EstadoDrone.DISPONIBLE, "Laguna Sur")
        );
    }

    @Test
    @DisplayName("Escenario 1 (Happy Path): Asignación exitosa cambia estado a EN_MISION y retorna el dron seleccionado")
    void asignacionExitosa_happyPath_asignaYCambiaEstado() {
        // ARRANGE
        DroneAcuatico droneSeleccionado = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-SUP-01", 85, EstadoDrone.DISPONIBLE, "Embalse Norte"
        );
        Mision mision = new Mision(
                "MIS-101", null, "Embalse Norte", "Laboratorio",
                Prioridad.NORMAL, "MUESTRA_AGUA", 250
        );

        when(estrategiaMock.seleccionar(any(), eq("Laboratorio")))
                .thenReturn(Optional.of(droneSeleccionado));
        asignador.registrarObservador(observadorMock);

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(mision, flotaPruebas);

        // ASSERT
        assertTrue(resultado.isPresent(), "Debe asignar un dron exitosamente");
        assertEquals("DRN-SUP-01", resultado.get().getId());
        assertEquals(EstadoDrone.EN_MISION, resultado.get().getEstado(), "El estado del dron debe transicionar a EN_MISION");

        // Verificación con Mockito: no debe haberse disparado ninguna alerta de fallo
        verify(observadorMock, never()).notificarFalloAsignacion(any());
        verify(observadorMock, never()).notificarFallo(any(), any());
    }

    @Test
    @DisplayName("Escenario 2 (Obligatorio Guía): Misión CRÍTICA sin drones disponibles notifica a todos los observadores")
    void misionCritica_sinDrones_notificaObservadores() {
        // ARRANGE
        Mision mision = new Mision(
                "MIS-CRIT-999", null, "Lago Profundo", "Base ECI",
                Prioridad.CRITICA, "SENSOR_TURBIDEZ", 300
        );

        when(estrategiaMock.seleccionar(any(), any()))
                .thenReturn(Optional.empty());

        asignador.registrarObservador(observadorMock);
        asignador.registrarObservador(mockCentroControl);

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(mision);

        // ASSERT
        assertTrue(resultado.isEmpty(), "No debe asignar ningún dron");
        verify(observadorMock, times(1)).notificarFalloAsignacion(mision);
        verify(mockCentroControl, times(1)).notificarFalloAsignacion(mision);
    }

    @Test
    @DisplayName("Escenario 3: Drone encontrado en FALLO durante asignación notifica a observadores y busca alternativo")
    void droneEncontradoEnFallo_notificaYBuscaAlternativoExitosamente() {
        // ARRANGE
        DroneAcuatico droneAveriado = FabricaDrones.crearDrone(
                TipoDrone.SEMISUMERGIDO, "DRN-SEMI-AVERIADO", 70, EstadoDrone.FALLO, "Canal Central"
        );
        DroneAcuatico droneAlternativo = FabricaDrones.crearDrone(
                TipoDrone.SEMISUMERGIDO, "DRN-SEMI-RESPALDO", 80, EstadoDrone.DISPONIBLE, "Canal Central"
        );

        List<DroneAcuatico> flotaConFallo = List.of(droneAveriado, droneAlternativo);
        Mision mision = new Mision(
                "MIS-FALLO-01", null, "Canal Central", "Muelle Sur",
                Prioridad.NORMAL, "MUESTRA_AGUA", 400
        );

        // Primera llamada retorna el dron en fallo; la segunda llamada (con lista filtrada) retorna el alternativo
        when(estrategiaMock.seleccionar(flotaConFallo, "Muelle Sur"))
                .thenReturn(Optional.of(droneAveriado));
        when(estrategiaMock.seleccionar(argThat(lista -> lista != null && lista.contains(droneAlternativo) && !lista.contains(droneAveriado)), eq("Muelle Sur")))
                .thenReturn(Optional.of(droneAlternativo));

        asignador.registrarObservador(observadorMock);

        // ACT
        Optional<DroneAcuatico> resultado = asignador.asignar(mision, flotaConFallo);

        // ASSERT
        assertTrue(resultado.isPresent(), "Debe reasignar y retornar el dron alternativo");
        assertEquals("DRN-SEMI-RESPALDO", resultado.get().getId());
        assertEquals(EstadoDrone.EN_MISION, droneAlternativo.getEstado());

        // Verificamos que se alertó del dron averiado exactamente una vez
        verify(observadorMock, times(1)).notificarFallo(eq(droneAveriado), contains("FALLO"));
    }

    @Test
    @DisplayName("Escenario 4a: Lista de drones vacía retorna Optional.empty()")
    void listaDronesVacia_retornaEmpty() {
        Mision misionNormal = new Mision(
                "MIS-VACIA-01", null, "Embalse Norte", "Lab",
                Prioridad.NORMAL, "SENSOR", 100
        );

        asignador.registrarObservador(observadorMock);
        Optional<DroneAcuatico> resultado = asignador.asignar(misionNormal, Collections.emptyList());

        assertTrue(resultado.isEmpty());
        verify(observadorMock, never()).notificarFalloAsignacion(any());
    }

    @Test
    @DisplayName("Escenario 4b: Lista de drones vacía en misión CRÍTICA notifica fallo a observadores")
    void listaDronesVacia_misionCritica_notificaObservadores() {
        Mision misionCritica = new Mision(
                "MIS-CRIT-VACIA", null, "Embalse Norte", "Lab",
                Prioridad.CRITICA, "SENSOR", 100
        );

        asignador.registrarObservador(observadorMock);
        Optional<DroneAcuatico> resultado = asignador.asignar(misionCritica, Collections.emptyList());

        assertTrue(resultado.isEmpty());
        verify(observadorMock, times(1)).notificarFalloAsignacion(misionCritica);
    }

    @Test
    @DisplayName("Escenario 5a (Edge Case): Batería exactamente en 35% (umbral nominal) es asignada exitosamente")
    void bateriaExactamenteEnUmbralMinimoNominal_asignaExitosamente() {
        // Batería exactamente 35%
        DroneAcuatico droneUmbral35 = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-EDGE-35", 35, EstadoDrone.DISPONIBLE, "Embalse Norte"
        );
        Mision mision = new Mision(
                "MIS-EDGE-35", null, "Embalse Norte", "Lab",
                Prioridad.NORMAL, "MUESTRA", 150
        );

        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.of(droneUmbral35));

        Optional<DroneAcuatico> resultado = asignador.asignar(mision, List.of(droneUmbral35));

        assertTrue(resultado.isPresent(), "El umbral de 35% es inclusivo y debe permitirse");
        assertEquals("DRN-EDGE-35", resultado.get().getId());
        assertEquals(EstadoDrone.EN_MISION, resultado.get().getEstado());
    }

    @Test
    @DisplayName("Escenario 5b (Edge Case): Batería en 34% (1% por debajo de umbral nominal) es rechazada")
    void bateriaPorDebajoDeUmbralNominal_rechazada() {
        DroneAcuatico droneBajo34 = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-EDGE-34", 34, EstadoDrone.DISPONIBLE, "Embalse Norte"
        );
        Mision mision = new Mision(
                "MIS-EDGE-34", null, "Embalse Norte", "Lab",
                Prioridad.NORMAL, "MUESTRA", 150
        );

        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.of(droneBajo34));

        Optional<DroneAcuatico> resultado = asignador.asignar(mision, List.of(droneBajo34));

        assertTrue(resultado.isEmpty(), "Batería < 35% no debe permitirse para zarpe");
        assertEquals(EstadoDrone.DISPONIBLE, droneBajo34.getEstado(), "No debe cambiar a EN_MISION");
    }

    @Test
    @DisplayName("Escenario 5c (Edge Case): Misión CRÍTICA con batería exactamente en 50% (umbral crítico) es asignada")
    void bateriaExactamenteEnUmbralCritico50_asignaExitosamente() {
        DroneAcuatico droneCritico50 = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-EDGE-CRIT-50", 50, EstadoDrone.DISPONIBLE, "Laguna Sur"
        );
        Mision misionCritica = new Mision(
                "MIS-EDGE-CRIT-50", null, "Laguna Sur", "Base",
                Prioridad.CRITICA, "SENSOR", 200
        );

        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.of(droneCritico50));

        Optional<DroneAcuatico> resultado = asignador.asignar(misionCritica, List.of(droneCritico50));

        assertTrue(resultado.isPresent(), "El umbral de reserva de 50% para misión crítica es inclusivo");
        assertEquals("DRN-EDGE-CRIT-50", resultado.get().getId());
    }

    @Test
    @DisplayName("Escenario 5d (Edge Case): Misión CRÍTICA con batería en 49% es rechazada y notifica observadores")
    void bateriaPorDebajoDeUmbralCritico_rechazadaYNotifica() {
        DroneAcuatico droneCritico49 = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-EDGE-CRIT-49", 49, EstadoDrone.DISPONIBLE, "Laguna Sur"
        );
        Mision misionCritica = new Mision(
                "MIS-EDGE-CRIT-49", null, "Laguna Sur", "Base",
                Prioridad.CRITICA, "SENSOR", 200
        );

        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.of(droneCritico49));
        asignador.registrarObservador(observadorMock);

        Optional<DroneAcuatico> resultado = asignador.asignar(misionCritica, List.of(droneCritico49));

        assertTrue(resultado.isEmpty());
        verify(observadorMock, times(1)).notificarFalloAsignacion(misionCritica);
    }

    @Test
    @DisplayName("Pruebas de Integración y Observer del Reto 03: registrarMisionConDrone con drone en FALLO")
    void registrarMisionConDroneEnFallo_notificaObservadores() {
        asignador.registrarObservador(mockCentroControl);
        asignador.registrarObservador(mockTecnicoMantenimiento);

        DroneAcuatico droneFallo = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-FALLO-99", 10, EstadoDrone.FALLO, "Embalse Norte"
        );
        Mision misionConFallo = new Mision(
                "MIS-REG-99", droneFallo, "Embalse Norte", "Lab",
                Prioridad.CRITICA, "MUESTRA", 250
        );

        boolean resultado = asignador.registrarMisionConDrone(misionConFallo);

        assertFalse(resultado);
        verify(mockCentroControl, times(1)).notificarFallo(eq(droneFallo), contains("FALLO"));
        verify(mockTecnicoMantenimiento, times(1)).notificarFallo(eq(droneFallo), contains("FALLO"));
    }

    @Test
    @DisplayName("Validación de métodos auxiliares: getFlota, setFlota, setEstrategia, removerObservador")
    void metodosAuxiliaresYExcepciones() {
        assertThrows(IllegalArgumentException.class, () -> asignador.asignar(null));
        assertThrows(IllegalArgumentException.class, () -> asignador.registrarMisionConDrone(null));

        AsignadorAutomatico porDefecto = new AsignadorAutomatico();
        assertNotNull(porDefecto.getEstrategia());
        assertNotNull(porDefecto.getNotificador());

        asignador.setFlota(null);
        assertTrue(asignador.getFlota().isEmpty());

        asignador.setFlota(flotaPruebas);
        assertEquals(3, asignador.getFlota().size());

        asignador.setEstrategia(new MayorBateriaStrategy());
        assertNotNull(asignador.getEstrategia());
        assertNotNull(asignador.getNotificador());

        asignador.registrarObservador(observadorMock);
        asignador.removerObservador(observadorMock);

        // Reportar fallo directo
        DroneAcuatico drone = flotaPruebas.get(0);
        asignador.reportarFalloDrone(drone, "Fallo manual de prueba");
        assertEquals(EstadoDrone.FALLO, drone.getEstado());

        // Reportar fallo con null
        asignador.reportarFalloDrone(null, "Ignorado");

        // Registrar mision con drone nulo
        Mision misionSinDrone = new Mision("MIS-NULL", null, "A", "B", Prioridad.NORMAL, "C", 10);
        assertTrue(asignador.registrarMisionConDrone(misionSinDrone));

        // Registrar mision con drone disponible
        DroneAcuatico droneOk = flotaPruebas.get(1);
        droneOk.setEstado(EstadoDrone.DISPONIBLE);
        Mision misionDroneOk = new Mision("MIS-OK", droneOk, "A", "B", Prioridad.NORMAL, "C", 10);
        assertTrue(asignador.registrarMisionConDrone(misionDroneOk));
    }

    @Test
    @DisplayName("Drone en fallo y sin alternativos disponibles en misión crítica notifica a observadores")
    void droneEnFallo_sinAlternativos_misionCritica_notifica() {
        DroneAcuatico droneAveriado = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "D-AVERIADO", 60, EstadoDrone.FALLO, "Zona"
        );
        Mision misionCritica = new Mision("MIS-CRIT-FAIL", null, "Zona", "Destino", Prioridad.CRITICA, "MUESTRA", 100);

        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.of(droneAveriado));
        asignador.registrarObservador(observadorMock);

        Optional<DroneAcuatico> res = asignador.asignar(misionCritica, List.of(droneAveriado));
        assertTrue(res.isEmpty());
        verify(observadorMock, times(1)).notificarFalloAsignacion(misionCritica);
    }

    @Test
    @DisplayName("Carga que supera la capacidad máxima del dron seleccionado es rechazada")
    void cargaSuperaCapacidad_rechazada() {
        DroneAcuatico drone = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "D-SUP", 80, EstadoDrone.DISPONIBLE, "Zona"
        ); // Capacidad 500g
        Mision misionPesada = new Mision("MIS-PESADA", null, "Zona", "Destino", Prioridad.NORMAL, "PESADA", 600); // 600g > 500g

        when(estrategiaMock.seleccionar(any(), any())).thenReturn(Optional.of(drone));

        Optional<DroneAcuatico> res = asignador.asignar(misionPesada, List.of(drone));
        assertTrue(res.isEmpty());
        assertEquals(EstadoDrone.DISPONIBLE, drone.getEstado());
    }
}
