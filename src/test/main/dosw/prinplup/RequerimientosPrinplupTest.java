package main.dosw.prinplup;

import main.dosw.prinplup.alertas.CentroControlObserver;
import main.dosw.prinplup.alertas.NotificadorAlertas;
import main.dosw.prinplup.alertas.TecnicoMantenimientoObserver;
import main.dosw.prinplup.asignacion.AsignadorAutomatico;
import main.dosw.prinplup.asignacion.MayorBateriaStrategy;
import main.dosw.prinplup.asignacion.PrioridadCriticaStrategy;
import main.dosw.prinplup.asignacion.ZonaCercanaStrategy;
import main.dosw.prinplup.fabrica.FabricaDrones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas automatizadas para verificar cuantitativamente los
 * Requerimientos No Funcionales (RNF) y la resolución de tensión AP-07 vs AP-08
 * de AquaPort v2 (Fase Prinplup).
 */
class RequerimientosPrinplupTest {

    private List<DroneAcuatico> flota30Drones;
    private AsignadorAutomatico asignador;
    private NotificadorAlertas notificador;

    @BeforeEach
    void setUp() {
        flota30Drones = new ArrayList<>();
        String[] zonas = {"Embalse Norte", "Canal Central", "Laguna Sur", "Bahía Este", "Punto Ribereño"};
        TipoDrone[] tipos = {TipoDrone.SUPERFICIAL, TipoDrone.SEMISUMERGIDO, TipoDrone.BUCEADOR};

        // Generar flota sintética de 30 drones con diversas cargas y estados
        for (int i = 1; i <= 30; i++) {
            TipoDrone tipo = tipos[(i - 1) % tipos.length];
            String zona = zonas[(i - 1) % zonas.length];
            int bateria = 10 + (i * 3) % 90; // baterías variables entre 10% y 97%
            EstadoDrone estado = (i % 7 == 0) ? EstadoDrone.FALLO : EstadoDrone.DISPONIBLE;
            flota30Drones.add(FabricaDrones.crearDrone(tipo, "DRN-TEST-" + i, bateria, estado, zona));
        }

        notificador = new NotificadorAlertas();
        asignador = new AsignadorAutomatico(new MayorBateriaStrategy(), notificador);
    }

    @Test
    @DisplayName("RNF-01 [Rendimiento]: El algoritmo de asignación debe seleccionar el drone óptimo en < 400ms para 30 drones")
    void testRnf01LatenciaAsignacionMenor400ms() {
        assertTimeoutPreemptively(Duration.ofMillis(400), () -> {
            Optional<DroneAcuatico> seleccionado = asignador.seleccionarDrone(flota30Drones, "Canal Central");
            assertTrue(seleccionado.isPresent(), "Debe encontrar un dron óptimo disponible");
            assertTrue(seleccionado.get().getBateria() >= MayorBateriaStrategy.UMBRAL_BATERIA_MINIMA);
        }, "La selección algorítmica para 30 drones excedió el SLA límite de 400ms");
    }

    @Test
    @DisplayName("RNF-02 [Rendimiento / Alertas]: La notificación de fallo debe propagarse a todos los observadores en < 50ms")
    void testRnf02LatenciaNotificacionAlertaMenor50ms() {
        notificador.registrarObservador(new CentroControlObserver());
        notificador.registrarObservador(new TecnicoMantenimientoObserver());

        DroneAcuatico droneEnFallo = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-ALERT-01", 14, EstadoDrone.FALLO, "Embalse Norte"
        );

        assertTimeoutPreemptively(Duration.ofMillis(50), () -> {
            notificador.emitirAlertaFallo(droneEnFallo, "Falla en propulsor sumergible");
        }, "La propagación de alerta a los observadores excedió el límite de 50ms");
    }

    @Test
    @DisplayName("RNF-04 [Capacidad y Concurrencia]: Procesar 50 solicitudes concurrentes de asignación en < 800ms")
    void testRnf04CapacidadThroughput50SolicitudesConcurrentes() {
        assertTimeoutPreemptively(Duration.ofMillis(800), () -> {
            List<CompletableFuture<Optional<DroneAcuatico>>> futuros = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                final String zona = (i % 2 == 0) ? "Canal Central" : "Embalse Norte";
                futuros.add(CompletableFuture.supplyAsync(() -> asignador.seleccionarDrone(flota30Drones, zona)));
            }
            CompletableFuture.allOf(futuros.toArray(new CompletableFuture[0])).join();
            for (CompletableFuture<Optional<DroneAcuatico>> f : futuros) {
                assertTrue(f.join().isPresent(), "Cada solicitud concurrente debe retornar un dron válido");
            }
        }, "El procesamiento de 50 solicitudes concurrentes excedió el SLA límite de 800ms");
    }

    @Test
    @DisplayName("Resolución Tensión AP-07 vs AP-08: Misión CRÍTICA prioriza aptitud y cercanía sobre mera batería")
    void testResolucionTensionAp07VsAp08() {
        // Drone A: Mayor batería (95%), pero en 'Embalse Norte' (lejos de Laguna Sur)
        DroneAcuatico droneMayorBateriaLejano = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-SUP-95", 95, EstadoDrone.DISPONIBLE, "Embalse Norte"
        );

        // Drone B: Batería media-alta (75%), pero ya ubicado en 'Laguna Sur' y apto para inmersión
        DroneAcuatico droneAptoEnZona = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-BUC-75", 75, EstadoDrone.DISPONIBLE, "Laguna Sur"
        );

        List<DroneAcuatico> flotaCandidatos = List.of(droneMayorBateriaLejano, droneAptoEnZona);

        // Bajo AP-07 (Misiones ordinarias): Prioriza batería máxima (elige DRN-SUP-95)
        asignador.setEstrategia(new MayorBateriaStrategy());
        Optional<DroneAcuatico> seleccionOrdinaria = asignador.seleccionarDrone(flotaCandidatos, "Laguna Sur");
        assertTrue(seleccionOrdinaria.isPresent());
        assertEquals("DRN-SUP-95", seleccionOrdinaria.get().getId());

        // Bajo AP-08 (Misiones CRÍTICAS en Laguna Sur): Resuelve tensión priorizando aptitud en zona (elige DRN-BUC-75)
        asignador.setEstrategia(new ZonaCercanaStrategy());
        Optional<DroneAcuatico> seleccionCritica = asignador.seleccionarDrone(flotaCandidatos, "Laguna Sur");
        assertTrue(seleccionCritica.isPresent());
        assertEquals("DRN-BUC-75", seleccionCritica.get().getId(),
                "Para misión crítica, la aptitud en zona prevalece sobre el porcentaje bruto de batería");
    }
}
