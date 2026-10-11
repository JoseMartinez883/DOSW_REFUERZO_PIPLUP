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

/**
 * Demostración interactiva del Ejercicio 03 — Nivel Prinplup:
 * Integración de los 3 patrones de diseño (Factory Method + Strategy + Observer).
 */
public class Ejercicio03Prinplup {

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("  AquaPort v2 — Nivel Prinplup: Ejercicio 03 (Patrones de Diseño Integrados)");
        System.out.println("===============================================================================\n");

        // 1. PATRÓN FACTORY METHOD: Creación de drones especializados mediante FabricaDrones
        System.out.println("--- 1. PATRÓN FACTORY METHOD: Instanciación de Drones Especializados ---");
        DroneAcuatico dSuperficial = FabricaDrones.crearDrone(
                TipoDrone.SUPERFICIAL, "DRN-SUP-01", 90, EstadoDrone.DISPONIBLE, "Embalse Norte"
        );
        DroneAcuatico dSemisumergido = FabricaDrones.crearDrone(
                TipoDrone.SEMISUMERGIDO, "DRN-SEMI-01", 75, EstadoDrone.DISPONIBLE, "Canal Central"
        );
        DroneAcuatico dBuceador = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-BUC-01", 85, EstadoDrone.DISPONIBLE, "Laguna Sur"
        );
        DroneAcuatico dEnFallo = FabricaDrones.crearDrone(
                TipoDrone.BUCEADOR, "DRN-BUC-99", 12, EstadoDrone.FALLO, "Embalse Norte"
        );

        List<DroneAcuatico> flota = List.of(dSuperficial, dSemisumergido, dBuceador, dEnFallo);

        for (DroneAcuatico d : flota) {
            System.out.println("-> Creado: [" + d.getClass().getSimpleName() + "] ID: " + d.getId() +
                    " | Modelo: " + d.getModelo() + " | Tipo: " + d.getTipo() +
                    " | Capacidad: " + d.getCapacidadCargaGramos() + "g | Batería: " + d.getBateria() + "%" +
                    " | Estado: " + d.getEstado() + " | Zona: " + d.getZona());
        }

        // 2. PATRÓN STRATEGY: Selección dinámica de drones sin acoplamiento
        System.out.println("\n--- 2. PATRÓN STRATEGY: Selección Autónoma de Drones ---");
        AsignadorAutomatico asignador = new AsignadorAutomatico(new MayorBateriaStrategy());

        System.out.println("[Estrategia: MayorBateriaStrategy] Seleccionando para 'Canal Central'...");
        Optional<DroneAcuatico> optBateria = asignador.seleccionarDrone(flota, "Canal Central");
        optBateria.ifPresent(d -> System.out.println("  ✓ Drone seleccionado: " + d.getId() + " con " + d.getBateria() + "% de batería"));

        System.out.println("\n[Cambiando dinámicamente a ZonaCercanaStrategy] Seleccionando para 'Canal Central'...");
        asignador.setEstrategia(new ZonaCercanaStrategy());
        Optional<DroneAcuatico> optZona = asignador.seleccionarDrone(flota, "Canal Central");
        optZona.ifPresent(d -> System.out.println("  ✓ Drone seleccionado: " + d.getId() + " en zona " + d.getZona() + " con " + d.getBateria() + "% de batería"));

        System.out.println("\n[Cambiando dinámicamente a PrioridadCriticaStrategy] Seleccionando para misión crítica...");
        asignador.setEstrategia(new PrioridadCriticaStrategy());
        Optional<DroneAcuatico> optCritica = asignador.seleccionarDrone(flota, "Laguna Sur");
        optCritica.ifPresent(d -> System.out.println("  ✓ Drone seleccionado: " + d.getId() + " con reserva de seguridad (" + d.getBateria() + "%)"));

        // 3. PATRÓN OBSERVER: Notificación automática a suscriptores ante Fallo de Drone
        System.out.println("\n--- 3. PATRÓN OBSERVER: Alertas Automáticas ante Fallo de Drone ---");
        asignador.registrarObservador(new CentroControlObserver());
        asignador.registrarObservador(new TecnicoMantenimientoObserver());

        System.out.println("Intentando registrar misión con drone en estado FALLO (DRN-BUC-99)...");
        Mision misionCritica = new Mision(
                "MIS-CRIT-777",
                dEnFallo,
                "Embalse Norte",
                "Laboratorio Hídrico",
                Prioridad.CRITICA,
                "MUESTRA_AGUA",
                200
        );

        boolean asignada = asignador.registrarMisionConDrone(misionCritica);
        System.out.println("Resultado de registro de misión: " + (asignada ? "EXITOSA" : "RECHAZADA"));

        System.out.println("\n===============================================================================");
        System.out.println("  Demostración de los 3 Patrones de Diseño Finalizada Exitosamente");
        System.out.println("===============================================================================");
    }
}
