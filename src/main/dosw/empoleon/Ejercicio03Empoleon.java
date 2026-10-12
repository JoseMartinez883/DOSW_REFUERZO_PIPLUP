package main.dosw.empoleon;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.dosw.empoleon.aplicacion.cadena.CadenaValidacionMision;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.ZonaHidrica;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
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
 * Clase ejecutable de demostración para el Ejercicio 03 de la Fase Empoleon:
 * Patrones de Diseño Integrados (Chain of Responsibility + Decorator + Adapter).
 */
public class Ejercicio03Empoleon {

    private static final Logger LOGGER = Logger.getLogger(Ejercicio03Empoleon.class.getName());

    public static void main(String[] args) {
        LOGGER.info("=== AquaPort Enterprise — Fase Empoleon: Ejercicio 03 (Patrones de Diseño) ===");

        // 1. PATRÓN ADAPTER: Consultar API externa en inglés y adaptar a modelo de dominio
        ClienteApiHidricaExterna mockClienteApi = zona -> new DatosHidricosExternosJson(3.2, 14.5, 0.4, true);
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(mockClienteApi);
        CondicionesHidricasDominio condiciones = adaptador.consultarCondiciones(ZonaHidrica.EMBALSE_INVESTIGACION.getNombre());
        LOGGER.log(Level.INFO, "1. ADAPTER: Condiciones adaptadas para 'Embalse de Investigación': {0}", condiciones);

        // 2. PATRÓN CHAIN OF RESPONSIBILITY: Validar misión a través de los 4 eslabones
        DroneEnterprise droneBase = new DroneEnterprise(
                "DRN-ENT-01", "AquaEnterprise-BUC", TipoDrone.BUCEADOR, 85,
                EstadoDrone.DISPONIBLE, ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), 300, 12
        );
        MisionEnterprise mision = new MisionEnterprise(
                "MIS-ENT-001", "Transporte muestra biológica profunda",
                ZonaHidrica.EMBALSE_INVESTIGACION.getNombre(), ZonaHidrica.LABORATORIO_CENTRAL.getNombre(),
                List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.ALTA, 200
        );

        CadenaValidacionMision cadena = new CadenaValidacionMision();
        ResultadoValidacion resultado = cadena.validar(mision, droneBase, condiciones);
        LOGGER.log(Level.INFO, "2. CHAIN OF RESPONSIBILITY: Resultado de validación previa: {0}", resultado);

        // 3. PATRÓN DECORATOR: Apilamiento de monitoreo y cifrado de seguridad
        DroneOperacional droneOperacional = new DroneOperacionalBase(droneBase);
        DroneOperacional droneMonitoreado = new DroneConMonitoreo(droneOperacional);
        DroneOperacional droneCifradoYMonitoreado = new DroneConCifrado(droneMonitoreado);

        LOGGER.log(Level.INFO, "3. DECORATOR: Ejecutando misión con capas de Monitoreo + Cifrado AES256...");
        boolean ejecucion = droneCifradoYMonitoreado.ejecutarMision("MIS-ENT-001", "TR-1");
        LOGGER.log(Level.INFO, "   * Ejecución exitosa: {0}. Batería final: {1}%",
                new Object[]{ejecucion, droneCifradoYMonitoreado.getBateria()});

        LOGGER.info("   * Registro consolidado de telemetría y seguridad:");
        for (String log : droneCifradoYMonitoreado.getRegistroTelemetria()) {
            LOGGER.log(Level.INFO, "     -> {0}", log);
        }
    }
}
