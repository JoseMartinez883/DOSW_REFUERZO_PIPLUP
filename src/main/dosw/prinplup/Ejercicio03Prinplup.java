package main.dosw.prinplup;

import main.dosw.prinplup.alertas.CentroControlObserver;
import main.dosw.prinplup.alertas.TecnicoMantenimientoObserver;
import main.dosw.prinplup.asignacion.AsignadorAutomatico;
import main.dosw.prinplup.asignacion.MayorBateriaStrategy;
import main.dosw.prinplup.asignacion.PrioridadCriticaStrategy;
import main.dosw.prinplup.asignacion.ZonaCercanaStrategy;
import main.dosw.prinplup.fabrica.FabricaDrones;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Demostración interactiva del Ejercicio 03 — Nivel Prinplup:
 * Integración de los 3 patrones de diseño (Factory Method + Strategy + Observer).
 */
public class Ejercicio03Prinplup {

    private static final Logger LOGGER = Logger.getLogger(Ejercicio03Prinplup.class.getName());
    private static final String EMBALSE_NORTE = "Embalse Norte";
    private static final String CANAL_CENTRAL = "Canal Central";
    private static final String DRONE_SELECCIONADO = "  ✓ Drone seleccionado: ";

    public static void main(String[] args) {
        LOGGER.info("===============================================================================");
        LOGGER.info("  AquaPort v2 — Nivel Prinplup: Ejercicio 03 (Patrones de Diseño Integrados)");
        LOGGER.info("===============================================================================\n");

        // 1. PATRÓN FACTORY METHOD: Creación de drones especializados mediante FabricaDrones
        LOGGER.info("--- 1. PATRÓN FACTORY METHOD: Instanciación de Drones Especializados ---");
        DroneAcuatico dSuperficial = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-SUP-01", 90, EstadoDrone.DISPONIBLE, EMBALSE_NORTE
        );
        DroneAcuatico dSemisumergido = FabricaDrones.crearDrone(
                TipoDrone.SEMISUMERGIDO, "DRN-SEMI-01", 75, EstadoDrone.DISPONIBLE, CANAL_CENTRAL
        );
        DroneAcuatico dBuceador = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-BUC-01", 85, EstadoDrone.DISPONIBLE, "Laguna Sur"
        );
        DroneAcuatico dEnFallo = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-BUC-99", 12, EstadoDrone.FALLO, EMBALSE_NORTE
        );

        List<DroneAcuatico> flota = List.of(dSuperficial, dSemisumergido, dBuceador, dEnFallo);

        for (DroneAcuatico d : flota) {
            LOGGER.info(() -> "-> Creado: [" + d.getClass().getSimpleName() + "] ID: " + d.getId() +
                    " | Modelo: " + d.getModelo() + " | Tipo: " + d.getTipo() +
                    " | Capacidad: " + d.getCapacidadCargaGramos() + "g | Batería: " + d.getBateria() + "%" +
                    " | Estado: " + d.getEstado() + " | Zona: " + d.getZona());
        }

        // 2. PATRÓN STRATEGY: Selección dinámica de drones sin acoplamiento
        LOGGER.info("\n--- 2. PATRÓN STRATEGY: Selección Autónoma de Drones ---");
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy());

        LOGGER.info("[Estrategia: MayorBateriaStrategy] Seleccionando para 'Canal Central'...");
        Optional<DroneAcuatico> optBateria = asignador.seleccionarDrone(flota, CANAL_CENTRAL);
        optBateria.ifPresent(d -> LOGGER.info(() -> DRONE_SELECCIONADO + d.getId() + " con " + d.getBateria() + "% de batería"));

        LOGGER.info("\n[Cambiando dinámicamente a ZonaCercanaStrategy] Seleccionando para 'Canal Central'...");
        asignador.setEstrategia(new ZonaCercanaStrategy());
        Optional<DroneAcuatico> optZona = asignador.seleccionarDrone(flota, CANAL_CENTRAL);
        optZona.ifPresent(d -> LOGGER.info(() -> DRONE_SELECCIONADO + d.getId() + " en zona " + d.getZona() + " con " + d.getBateria() + "% de batería"));

        LOGGER.info("\n[Cambiando dinámicamente a PrioridadCriticaStrategy] Seleccionando para misión crítica...");
        asignador.setEstrategia(new PrioridadCriticaStrategy());
        Optional<DroneAcuatico> optCritica = asignador.seleccionarDrone(flota, "Laguna Sur");
        optCritica.ifPresent(d -> LOGGER.info(() -> DRONE_SELECCIONADO + d.getId() + " con reserva de seguridad (" + d.getBateria() + "%)"));

        // 3. PATRÓN OBSERVER: Notificación automática a suscriptores ante Fallo de Drone
        LOGGER.info("\n--- 3. PATRÓN OBSERVER: Alertas Automáticas ante Fallo de Drone ---");
        asignador.registrarObservador(new CentroControlObserver());
        asignador.registrarObservador(new TecnicoMantenimientoObserver());

        LOGGER.info("Intentando registrar misión con drone en estado FALLO (DRN-BUC-99)...");
        Mision misionCritica = new Mision(
                "MIS-CRIT-777",
                dEnFallo,
                EMBALSE_NORTE,
                "Laboratorio Hídrico",
                Prioridad.CRITICA,
                "MUESTRA_AGUA",
                200
        );

        boolean asignada = asignador.registrarMisionConDrone(misionCritica);
        LOGGER.info(() -> "Resultado de registro de misión: " + (asignada ? "EXITOSA" : "RECHAZADA"));

        LOGGER.info("\n===============================================================================");
        LOGGER.info("  Demostración de los 3 Patrones de Diseño Finalizada Exitosamente");
        LOGGER.info("===============================================================================");
    }
}
