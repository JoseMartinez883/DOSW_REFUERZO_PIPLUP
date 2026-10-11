package main.dosw.prinplup;

import main.dosw.prinplup.alertas.CentroControlObserver;
import main.dosw.prinplup.alertas.ObservadorMision;
import main.dosw.prinplup.asignacion.AsignadorAutomatico;
import main.dosw.prinplup.asignacion.EstrategiaSeleccion;
import main.dosw.prinplup.asignacion.MayorBateriaStrategy;
import main.dosw.prinplup.fabrica.DroneBuceador;
import main.dosw.prinplup.fabrica.DroneSemisumergido;
import main.dosw.prinplup.fabrica.DroneSuperficial;
import main.dosw.prinplup.fabrica.FabricaDrones;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de verificación y auditoría programática de los principios SOLID en la arquitectura de AquaPort v2.
 */
class SolidPrinciplesAuditTest {

    @Test
    @DisplayName("LSP (Liskov Substitution Principle): Subclases de DroneAcuatico son completamente sustituibles")
    void debeCumplirPrincipioDeSustitucionDeLiskov() {
        // Colección polimórfica que opera exclusivamente con el tipo base DroneAcuatico
        List<DroneAcuatico> flotaHeterogenea = List.of(
                new DroneSuperficial("D-SUP", 80, EstadoDrone.DISPONIBLE, "Embalse"),
                new DroneSemisumergido("D-SEMI", 70, EstadoDrone.DISPONIBLE, "Canal"),
                new DroneBuceador("D-BUC", 60, EstadoDrone.DISPONIBLE, "Laguna")
        );

        // Verificación de invariantes del contrato base DroneAcuatico en cada subtipo
        for (DroneAcuatico drone : flotaHeterogenea) {
            assertNotNull(drone.getId());
            assertNotNull(drone.getModelo());
            assertNotNull(drone.getTipo());
            assertNotNull(drone.getEstado());
            assertNotNull(drone.getZona());
            assertTrue(drone.getBateria() >= 0 && drone.getBateria() <= 100);
            assertTrue(drone.getCapacidadCargaGramos() > 0);
            assertTrue(drone.isDisponible());
            // No lanza excepciones al invocar métodos heredados
            assertDoesNotThrow(drone::toString);
        }

        // AsignadorAutomatico procesa la lista heterogénea sin requerir casteo ni verificación de tipo
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy());
        Optional<DroneAcuatico> seleccionado = asignador.seleccionarDrone(flotaHeterogenea, "Embalse");

        assertTrue(seleccionado.isPresent());
        assertEquals("D-SUP", seleccionado.get().getId());
    }

    @Test
    @DisplayName("OCP (Open/Closed Principle): Sistema abierto a nuevas estrategias y observadores sin modificar AsignadorAutomatico")
    void debeCumplirPrincipioAbiertoCerradoSinModificarAsignador() {
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy());

        // Nueva estrategia implementada al vuelo (extensión sin modificar código fuente existente)
        EstrategiaSeleccion estrategiaPersonalizada = (drones, zona) -> {
            if (drones == null || drones.isEmpty()) return Optional.empty();
            return Optional.of(drones.get(drones.size() - 1)); // Siempre elige el último
        };

        asignador.setEstrategia(estrategiaPersonalizada);

        List<DroneAcuatico> drones = List.of(
                FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 95, EstadoDrone.DISPONIBLE, "Zona A"),
                FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 40, EstadoDrone.DISPONIBLE, "Zona A")
        );

        Optional<DroneAcuatico> seleccionado = asignador.seleccionarDrone(drones, "Zona A");
        assertTrue(seleccionado.isPresent());
        assertEquals("D-2", seleccionado.get().getId());

        // Nuevo observador al vuelo (extensión del sistema de alertas)
        AtomicBoolean alertaPersonalizadaRecibida = new AtomicBoolean(false);
        ObservadorMision observadorAuditoria = (drone, motivo) -> alertaPersonalizadaRecibida.set(true);

        asignador.registrarObservador(observadorAuditoria);

        DroneAcuatico droneFallo = FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "D-FAIL", 10, EstadoDrone.FALLO, "Zona A");
        Mision misionFallo = new Mision("M-FAIL", droneFallo, "A", "B", Prioridad.CRITICA, "MUESTRA", 100);

        asignador.registrarMisionConDrone(misionFallo);
        assertTrue(alertaPersonalizadaRecibida.get(), "El nuevo observador debe ser notificado respetando OCP");
    }

    @Test
    @DisplayName("DIP (Dependency Inversion Principle): AsignadorAutomatico depende de abstracciones y admite inyección")
    void debeCumplirPrincipioDeInversionDeDependencias() {
        // Se inyecta la abstracción EstrategiaSeleccion en el constructor
        EstrategiaSeleccion mockEstrategia = (drones, zona) -> Optional.empty();
        AsignadorAutomatico asignador = new AsignadorAutomatico(mockEstrategia);

        assertSame(mockEstrategia, asignador.getEstrategia());
        assertNotNull(asignador.getNotificador());
    }

    @Test
    @DisplayName("ISP (Interface Segregation Principle): Interfaces son atómicas y focalizadas en su único propósito")
    void debeCumplirPrincipioDeSegregacionDeInterfaces() {
        // ObservadorMision solo expone el contrato de notificación de fallos
        ObservadorMision obs = new CentroControlObserver();
        assertDoesNotThrow(() -> obs.notificarFallo(
                new DroneSuperficial("D-1", 50, EstadoDrone.FALLO, "Norte"), "Test"
        ));

        // EstrategiaSeleccion solo expone el contrato de selección de drone
        EstrategiaSeleccion strat = new MayorBateriaStrategy();
        assertNotNull(strat.seleccionarDrone(List.of(), "Norte"));
    }

    @Test
    @DisplayName("SRP (Single Responsibility Principle): FabricaDrones únicamente encapsula instanciación de subtipos")
    void debeCumplirPrincipioDeResponsabilidadUnicaEnFabrica() {
        DroneAcuatico d1 = FabricaDrones.crearDrone(TipoDrone.SUPERFICIAL, "D-1", 90, EstadoDrone.DISPONIBLE, "Z1");
        DroneAcuatico d2 = FabricaDrones.crearDrone(TipoDrone.SEMISUMERGIDO, "D-2", 90, EstadoDrone.DISPONIBLE, "Z1");
        DroneAcuatico d3 = FabricaDrones.crearDrone(TipoDrone.BUCEADOR, "D-3", 90, EstadoDrone.DISPONIBLE, "Z1");

        assertInstanceOf(DroneSuperficial.class, d1);
        assertInstanceOf(DroneSemisumergido.class, d2);
        assertInstanceOf(DroneBuceador.class, d3);

        assertEquals(500, d1.getCapacidadCargaGramos());
        assertEquals(1500, d2.getCapacidadCargaGramos());
        assertEquals(300, d3.getCapacidadCargaGramos());
    }
}
