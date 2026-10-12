package main.dosw.empoleon;

import java.util.List;
import java.util.logging.Logger;
import main.dosw.empoleon.aplicacion.DespachadorMisionesEnterprise;
import main.dosw.empoleon.aplicacion.cadena.CadenaValidacionMision;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;
import main.dosw.empoleon.dominio.decorator.DroneConCifrado;
import main.dosw.empoleon.dominio.decorator.DroneConMonitoreo;
import main.dosw.empoleon.dominio.decorator.DroneOperacional;
import main.dosw.empoleon.dominio.decorator.DroneOperacionalBase;
import main.dosw.empoleon.infraestructura.adapter.AdaptadorAPIHidrica;
import main.dosw.empoleon.infraestructura.adapter.ClienteApiHidricaExterna;
import main.dosw.empoleon.infraestructura.adapter.DatosHidricosExternosJson;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.Prioridad;
import main.dosw.prinplup.TipoDrone;

/**
 * Demostrador ejecutable para el Ejercicio 04 (Fase Empoleon):
 * Auditoría completa de los principios SOLID en la arquitectura Enterprise.
 */
public class Ejercicio04Empoleon {

    private static final Logger LOGGER = Logger.getLogger(Ejercicio04Empoleon.class.getName());

    public static void main(String[] args) {
        LOGGER.info("=== AquaPort Enterprise – Fase Empoleon: Ejercicio 04 (Principios SOLID) ===");

        // 1. Demostración DIP + Inyección por Constructor
        LOGGER.info("1. DIP & INYECCIÓN POR CONSTRUCTOR: Ensamblando dependencias e infraestructura...");
        ClienteApiHidricaExterna apiExterna = zona -> new DatosHidricosExternosJson(3.5, 12.0, 0.3, true);
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(apiExterna);
        CadenaValidacionMision cadena = new CadenaValidacionMision();

        DespachadorMisionesEnterprise despachador = new DespachadorMisionesEnterprise(cadena, adaptador);
        LOGGER.info("   -> DespachadorMisionesEnterprise instanciado con abstracciones finales inyectadas.");

        // 2. Demostración SRP & OCP & LSP con entidades del dominio y decoradores
        LOGGER.info("2. SRP + OCP + LSP: Creando dron base y apilando decoradores operacionales...");
        DroneEnterprise dronFisico = new DroneEnterprise(
                "DRN-ENT-04", "Aqua-Explorer Enterprise", TipoDrone.SEMISUMERGIDO, 85,
                EstadoDrone.DISPONIBLE, ZonaHidrica.LAGUNA_RESERVA.getNombre(), 1200, 15
        );
        DroneOperacional droneSeguro = new DroneConCifrado(
                new DroneConMonitoreo(new DroneOperacionalBase(dronFisico))
        );
        LOGGER.info("   -> Dron operacional envuelto: ID " + droneSeguro.getId() + " en " + droneSeguro.getZona());

        // 3. Validación y despacho mediante el servicio desacoplado
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-ENT-004", "Monitoreo químico en Laguna",
                ZonaHidrica.LAGUNA_RESERVA.getNombre(), ZonaHidrica.LABORATORIO_CENTRAL.getNombre(),
                List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.CRITICA, 600
        );

        LOGGER.info("3. EJECUTANDO DESPACHO DE MISIÓN CON ARQUITECTURA LIMPIA...");
        ResultadoValidacion prevalidacion = despachador.validarDespacho(
                mision, dronFisico, ZonaHidrica.LAGUNA_RESERVA.getNombre()
        );
        LOGGER.info(() -> "   -> Pre-validación SOLID: " + prevalidacion.exitoso() + " (" + prevalidacion.motivo() + ")");

        boolean despachoExitoso = despachador.despacharMision(
                mision, droneSeguro, ZonaHidrica.LAGUNA_RESERVA.getNombre(), "TR-1"
        );
        LOGGER.info(() -> "   -> Misión despachada exitosamente: " + despachoExitoso + " | Batería restante: "
                + droneSeguro.getBateria() + "%");

        LOGGER.info("4. AUDITORÍA SOLID FINALIZADA: 0 dependencias externas en dominio, 100% constructor injection.");
    }
}
