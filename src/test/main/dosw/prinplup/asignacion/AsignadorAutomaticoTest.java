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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignadorAutomaticoTest {

    @Mock
    private ObservadorMision mockCentroControl;

    @Mock
    private ObservadorMision mockTecnicoMantenimiento;

    private AsignadorAutomatico asignador;
    private List<DroneAcuatico> flota;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorAutomatico(new MayorBateriaStrategy());

        // Drones creados con el patrón Factory Method
        flota = List.of(
                FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "DRN-SUP-01", 90, EstadoDrone.DISPONIBLE, "Embalse Norte"),
                FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "DRN-SEMI-01", 75, EstadoDrone.DISPONIBLE, "Canal Central"),
                FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "DRN-BUC-01", 85, EstadoDrone.DISPONIBLE, "Laguna Sur")
        );
    }

    @Test
    @DisplayName("RETO 03 OBLIGATORIO: Al registrar una misión con drone en FALLO, todos los observadores son notificados exactamente una vez")
    void debeNotificarATodosLosObservadoresExactamenteUnaVezAlRegistrarMisionConDroneEnFallo() {
        // Registrar ambos observadores en el asignador
        asignador.registrarObservador(mockCentroControl);
        asignador.registrarObservador(mockTecnicoMantenimiento);

        // Crear un drone en estado FALLO utilizando la fábrica
        DroneAcuatico droneEnFallo = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-FALLO-99", 10, EstadoDrone.FALLO, "Embalse Norte"
        );

        Mision misionConFallo = new Mision(
                "MIS-CRIT-999",
                droneEnFallo,
                "Embalse Norte",
                "Laboratorio Hídrico",
                Prioridad.CRITICA,
                "MUESTRA_AGUA",
                250
        );

        // Ejecutar el registro de la misión
        boolean resultado = asignador.registrarMisionConDrone(misionConFallo);

        // Validaciones
        assertFalse(resultado, "El registro de la misión debe ser rechazado debido al fallo del drone");

        // Verificación con Mockito: notificado exactamente una vez
        verify(mockCentroControl, times(1)).notificarFallo(eq(droneEnFallo), contains("FALLO"));
        verify(mockTecnicoMantenimiento, times(1)).notificarFallo(eq(droneEnFallo), contains("FALLO"));
    }

    @Test
    @DisplayName("Al registrar una misión con drone DISPONIBLE no debe emitir alertas a los observadores")
    void noDebeNotificarObservadoresSiDroneEstaDisponible() {
        asignador.registrarObservador(mockCentroControl);
        asignador.registrarObservador(mockTecnicoMantenimiento);

        DroneAcuatico droneOk = flota.get(0);
        Mision misionValida = new Mision("MIS-OK-101", droneOk, "Embalse Norte", "Laboratorio", Prioridad.NORMAL, "SENSOR", 100);

        boolean resultado = asignador.registrarMisionConDrone(misionValida);

        assertTrue(resultado);
        verify(mockCentroControl, never()).notificarFallo(any(), any());
        verify(mockTecnicoMantenimiento, never()).notificarFallo(any(), any());
    }

    @Test
    @DisplayName("Debe delegar en la estrategia activa y permitir alternar dinámicamente entre estrategias (Strategy Pattern)")
    void debeIntercambiarEstrategiasDinamicasSinModificarAsignador() {
        // 1. Con MayorBateriaStrategy: Debe elegir el de 90% (DRN-SUP-01)
        asignador.setEstrategia(new MayorBateriaStrategy());
        Optional<DroneAcuatico> porBateria = asignador.seleccionarDrone(flota, "Canal Central");
        assertTrue(porBateria.isPresent());
        assertEquals("DRN-SUP-01", porBateria.get().getId());

        // 2. Con ZonaCercanaStrategy: Debe priorizar el que está en 'Canal Central' (DRN-SEMI-01 con 75%)
        asignador.setEstrategia(new ZonaCercanaStrategy());
        Optional<DroneAcuatico> porZona = asignador.seleccionarDrone(flota, "Canal Central");
        assertTrue(porZona.isPresent());
        assertEquals("DRN-SEMI-01", porZona.get().getId());

        // 3. Con PrioridadCriticaStrategy: Selecciona drone con reserva alta (>= 50%)
        asignador.setEstrategia(new PrioridadCriticaStrategy());
        Optional<DroneAcuatico> porCritica = asignador.seleccionarDrone(flota, "Laguna Sur");
        assertTrue(porCritica.isPresent());
        assertTrue(porCritica.get().getBateria() >= 50);
    }
}
