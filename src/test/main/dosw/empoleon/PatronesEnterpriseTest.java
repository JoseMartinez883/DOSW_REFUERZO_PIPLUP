package main.dosw.empoleon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import main.dosw.empoleon.aplicacion.cadena.CadenaValidacionMision;
import main.dosw.empoleon.aplicacion.cadena.ValidadorBateria;
import main.dosw.empoleon.aplicacion.cadena.ValidadorCapacidadCarga;
import main.dosw.empoleon.aplicacion.cadena.ValidadorCondicionesHidricas;
import main.dosw.empoleon.aplicacion.cadena.ValidadorZonaActiva;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.EslabonValidacion;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatronesEnterpriseTest {

    @Mock
    private ClienteApiHidricaExterna clienteApiMock;

    @Mock
    private EslabonValidacion eslabonMock;

    @Mock
    private DroneOperacional droneOperacionalMock;

    private DroneEnterprise droneValido;
    private MisionEnterprise misionValida;
    private CondicionesHidricasDominio condicionesValidas;

    @BeforeEach
    void setUp() {
        droneValido = new DroneEnterprise(
                "D-01", "AquaEnterprise-BUC", TipoDrone.BUCEADOR, 85,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación", 300, 10
        );
        misionValida = new MisionEnterprise(
                "M-01", "Transporte muestra", "Embalse de Investigación", "Laboratorio Hídrico Central",
                List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.ALTA, 200
        );
        condicionesValidas = CondicionesHidricasDominio.optimas();
    }

    // =========================================================================
    // 1. CHAIN OF RESPONSIBILITY
    // =========================================================================

    @Test
    @DisplayName("RETO MOCKITO 1: La cadena de validación se detiene en el primer validador que falla")
    void testCadenaSeDetieneEnPrimerValidadorQueFalla() {
        ValidadorBateria valBateria = new ValidadorBateria();
        valBateria.setSiguiente(eslabonMock);

        // Drone con batería por debajo del umbral del 20%
        DroneEnterprise droneBateriaBaja = new DroneEnterprise(
                "D-LOW", "Aqua", TipoDrone.SUPERFICIAL, 15,
                EstadoDrone.DISPONIBLE, "Embalse de Investigación", 500, 0
        );

        ResultadoValidacion resultado = valBateria.validar(misionValida, droneBateriaBaja, condicionesValidas);

        assertFalse(resultado.exitoso());
        assertEquals("ValidadorBateria", resultado.validadorFalla());
        // Verificación clave Mockito: el eslabón siguiente NUNCA fue consultado
        verify(eslabonMock, never()).validar(any(), any(), any());
    }

    @Test
    @DisplayName("Cadena completa aprueba misión cuando todos los eslabones son válidos")
    void testCadenaCompletaExitosa() {
        CadenaValidacionMision cadena = new CadenaValidacionMision();
        ResultadoValidacion resultado = cadena.validar(misionValida, droneValido, condicionesValidas);

        assertTrue(resultado.exitoso());
        assertNull(resultado.validadorFalla());
    }

    @Test
    @DisplayName("Cadena detecta fallo por sobrepeso de carga en ValidadorCapacidadCarga")
    void testCadenaFallaPorCapacidadCarga() {
        MisionEnterprise misionPesada = new MisionEnterprise(
                "M-HEAVY", "Carga pesada", "Embalse de Investigación", "Laboratorio",
                List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.NORMAL, 450
        );

        CadenaValidacionMision cadena = new CadenaValidacionMision();
        ResultadoValidacion resultado = cadena.validar(misionPesada, droneValido, condicionesValidas);

        assertFalse(resultado.exitoso());
        assertEquals("ValidadorCapacidadCarga", resultado.validadorFalla());
    }

    @Test
    @DisplayName("Cadena detecta fallo por zona o dron no disponible en ValidadorZonaActiva")
    void testCadenaFallaPorZonaActiva() {
        DroneEnterprise droneOtraZona = new DroneEnterprise(
                "D-ZONA", "Aqua", TipoDrone.BUCEADOR, 80,
                EstadoDrone.DISPONIBLE, "Laguna de Reserva", 300, 0
        );

        CadenaValidacionMision cadena = new CadenaValidacionMision();
        ResultadoValidacion resultado = cadena.validar(misionValida, droneOtraZona, condicionesValidas);

        assertFalse(resultado.exitoso());
        assertEquals("ValidadorZonaActiva", resultado.validadorFalla());
    }

    @Test
    @DisplayName("Cadena detecta fallo en ValidadorCondicionesHidricas cuando agua no es navegable")
    void testCadenaFallaPorCondicionesHidricas() {
        CondicionesHidricasDominio condicionesAdversas = CondicionesHidricasDominio.adversas();

        CadenaValidacionMision cadena = new CadenaValidacionMision();
        ResultadoValidacion resultado = cadena.validar(misionValida, droneValido, condicionesAdversas);

        assertFalse(resultado.exitoso());
        assertEquals("ValidadorCondicionesHidricas", resultado.validadorFalla());
    }

    @Test
    @DisplayName("Cadena vacía o sin eslabones retorna éxito por defecto")
    void testCadenaSinEslabones() {
        CadenaValidacionMision cadenaVacia = new CadenaValidacionMision(null);
        ResultadoValidacion resultado = cadenaVacia.validar(misionValida, droneValido, condicionesValidas);
        assertTrue(resultado.exitoso());
    }

    // =========================================================================
    // 2. DECORATOR PATTERN
    // =========================================================================

    @Test
    @DisplayName("RETO MOCKITO 2: Decorator registra telemetría sin alterar el comportamiento base del drone")
    void testDecoratorRegistraSinAlterarComportamientoBase() {
        when(droneOperacionalMock.getId()).thenReturn("DRN-MOCK-01");
        when(droneOperacionalMock.getZona()).thenReturn("Canales");
        when(droneOperacionalMock.getBateria()).thenReturn(75);
        when(droneOperacionalMock.ejecutarMision("M-100", "TR-1")).thenReturn(true);
        when(droneOperacionalMock.getRegistroTelemetria()).thenReturn(List.of("Log previo"));

        DroneConMonitoreo droneMonitoreado = new DroneConMonitoreo(droneOperacionalMock);

        boolean resultado = droneMonitoreado.ejecutarMision("M-100", "TR-1");

        // El comportamiento booleano se mantiene intacto
        assertTrue(resultado);
        // Verificación Mockito: se delegó exactamente una vez
        verify(droneOperacionalMock, times(1)).ejecutarMision("M-100", "TR-1");

        // El decorador registró logs adicionales transparentemente
        List<String> logs = droneMonitoreado.getRegistroTelemetria();
        assertTrue(logs.size() >= 3);
        assertTrue(logs.stream().anyMatch(l -> l.contains("[TELEMETRIA]")));
    }

    @Test
    @DisplayName("Apilamiento de Decorators (Monitoreo + Cifrado) acumula registros de ambas capas")
    void testApilamientoDecorators() {
        DroneOperacionalBase base = new DroneOperacionalBase(droneValido);
        DroneConMonitoreo conMonitoreo = new DroneConMonitoreo(base);
        DroneConCifrado conCifradoYMonitoreo = new DroneConCifrado(conMonitoreo);

        boolean ejecutado = conCifradoYMonitoreo.ejecutarMision("M-01", "T-1");
        assertTrue(ejecutado);
        assertEquals(75, conCifradoYMonitoreo.getBateria());
        assertEquals(11, droneValido.getEntregasExitosas());

        List<String> logs = conCifradoYMonitoreo.getRegistroTelemetria();
        assertTrue(logs.stream().anyMatch(l -> l.contains("[TELEMETRIA]")));
        assertTrue(logs.stream().anyMatch(l -> l.contains("[SEGURIDAD-AES256]")));
    }

    @Test
    @DisplayName("DroneOperacionalBase transiciona a MANTENIMIENTO si la batería se reduce a cero")
    void testConsumoBateriaTotalTransicionaEstado() {
        DroneOperacionalBase base = new DroneOperacionalBase(droneValido);
        base.consumirBateria(100);
        assertEquals(0, base.getBateria());
        assertEquals(EstadoDrone.MANTENIMIENTO, droneValido.getEstado());
        assertFalse(base.ejecutarMision("M", "T"));
    }

    // =========================================================================
    // 3. ADAPTER PATTERN
    // =========================================================================

    @Test
    @DisplayName("RETO MOCKITO 3: Adapter convierte correctamente campos con valores límite")
    void testAdapterConvierteCamposValoresLimite() {
        // Simular respuesta con valores negativos límite y extremos
        DatosHidricosExternosJson respuestaLimite = new DatosHidricosExternosJson(
                -5.0, -10.0, -2.5, true
        );
        when(clienteApiMock.fetchWaterConditions("Canal Central")).thenReturn(respuestaLimite);

        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteApiMock);
        CondicionesHidricasDominio resultado = adaptador.consultarCondiciones("Canal Central");

        // Verificaciones de sanitización en el adapter
        assertEquals(0.0, resultado.nivelAguaMetros());
        assertEquals(0.0, resultado.turbidezNtu());
        assertEquals(0.0, resultado.alturaOlasMetros());
        // Aunque safeForNavigation era true, el nivel de agua 0.0 < 0.5 marca no navegable por seguridad
        assertFalse(resultado.esNavegable());
    }

    @Test
    @DisplayName("Adapter traduce valores óptimos y preserva navegabilidad")
    void testAdapterValoresOptimos() {
        DatosHidricosExternosJson respuestaOptima = new DatosHidricosExternosJson(
                3.5, 15.0, 0.3, true
        );
        when(clienteApiMock.fetchWaterConditions("Embalse Norte")).thenReturn(respuestaOptima);

        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteApiMock);
        CondicionesHidricasDominio resultado = adaptador.consultarCondiciones("Embalse Norte");

        assertEquals(3.5, resultado.nivelAguaMetros());
        assertEquals(15.0, resultado.turbidezNtu());
        assertEquals(0.3, resultado.alturaOlasMetros());
        assertTrue(resultado.esNavegable());
    }

    @Test
    @DisplayName("Adapter retorna condiciones adversas si el cliente retorna nulo o zona inválida")
    void testAdapterCasosNulos() {
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteApiMock);

        CondicionesHidricasDominio resZonaNula = adaptador.consultarCondiciones(null);
        assertFalse(resZonaNula.esNavegable());

        when(clienteApiMock.fetchWaterConditions("Desconocida")).thenReturn(null);
        CondicionesHidricasDominio resApiNula = adaptador.consultarCondiciones("Desconocida");
        assertFalse(resApiNula.esNavegable());

        assertThrows(NullPointerException.class, () -> new AdaptadorAPIHidrica(null));
    }

    @Test
    @DisplayName("Ejecución demostrativa del main en Ejercicio03Empoleon")
    void testEjercicio03EmpoleonMain() {
        Ejercicio03Empoleon.main(new String[]{});
        assertTrue(true);
    }
}
