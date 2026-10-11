package main.dosw.empoleon;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import main.dosw.empoleon.aplicacion.DespachadorMisionesEnterprise;
import main.dosw.empoleon.aplicacion.cadena.CadenaValidacionMision;
import main.dosw.empoleon.aplicacion.cadena.ValidadorBateria;
import main.dosw.empoleon.aplicacion.cadena.ValidadorCapacidadCarga;
import main.dosw.empoleon.aplicacion.cadena.ValidadorCondicionesHidricas;
import main.dosw.empoleon.aplicacion.cadena.ValidadorZonaActiva;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.adapter.ProveedorCondicionesHidricas;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolidArchitectureAuditTest {

    private static final Path DOMINIO_PATH = Paths.get("src", "main", "dosw", "empoleon", "dominio");
    private static final Path APLICACION_PATH = Paths.get("src", "main", "dosw", "empoleon", "aplicacion");

    @Test
    @DisplayName("SOLID REGLA DE ORO: El paquete dominio no tiene imports externos ni de infraestructura")
    void testReglaDeOroDominioSinDependenciasExternasNiInfraestructura() throws IOException {
        try (Stream<Path> paths = Files.walk(DOMINIO_PATH)) {
            List<Path> javaFiles = paths.filter(p -> p.toString().endsWith(".java")).toList();
            assertFalse(javaFiles.isEmpty(), "Deben existir archivos Java en la capa de dominio");

            for (Path file : javaFiles) {
                List<String> lines = Files.readAllLines(file);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("import ")) {
                        String importedClass = trimmed.substring(7, trimmed.indexOf(';')).trim();
                        assertFalse(importedClass.contains("infraestructura"),
                                "Violación de arquitectura: La clase de dominio " + file.getFileName()
                                        + " importa infraestructura: " + importedClass);
                        assertFalse(importedClass.startsWith("org.mockito"),
                                "Violación de regla de oro: Dominio importa Mockito: " + importedClass);
                        assertFalse(importedClass.startsWith("com.fasterxml"),
                                "Violación de regla de oro: Dominio importa Jackson: " + importedClass);
                        assertFalse(importedClass.startsWith("java.net.http"),
                                "Violación de regla de oro: Dominio importa HttpClient: " + importedClass);
                        assertFalse(importedClass.startsWith("org.springframework"),
                                "Violación de regla de oro: Dominio importa Spring: " + importedClass);
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("SOLID DIP: Ni dominio ni aplicación instancian directamente clases de infraestructura")
    void testInversionDeDependenciasSinNewInfraestructuraEnDominioNiAplicacion() throws IOException {
        List<Path> targetDirs = List.of(DOMINIO_PATH, APLICACION_PATH);

        for (Path dir : targetDirs) {
            try (Stream<Path> paths = Files.walk(dir)) {
                List<Path> javaFiles = paths.filter(p -> p.toString().endsWith(".java")).toList();
                for (Path file : javaFiles) {
                    List<String> lines = Files.readAllLines(file);
                    for (String line : lines) {
                        assertFalse(line.contains("new AdaptadorAPIHidrica"),
                                "Violación de DIP: " + file.getFileName() + " contiene 'new AdaptadorAPIHidrica'");
                        assertFalse(line.contains("new ClienteApiHidricaExterna"),
                                "Violación de DIP: " + file.getFileName() + " contiene 'new ClienteApiHidricaExterna'");
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("SOLID CONSTRUCTOR INJECTION: Dependencias inyectadas por constructor e inmutables (final)")
    void testConstructorInjectionEnServiciosYAdaptadores() {
        Class<?>[] classes = {
                DespachadorMisionesEnterprise.class,
                AdaptadorAPIHidrica.class,
                CadenaValidacionMision.class
        };

        for (Class<?> clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    assertTrue(Modifier.isFinal(field.getModifiers()),
                            "El campo '" + field.getName() + "' en " + clazz.getSimpleName()
                                    + " debe ser final para garantizar inmutabilidad e inyección por constructor");
                }
            }
        }
    }

    @Test
    @DisplayName("SOLID LSP: Decoradores sustituyen transparentemente la interfaz DroneOperacional")
    void testLiskovSubstitutionPrincipleEnDecoradores() {
        DroneEnterprise droneFisico = new DroneEnterprise(
                "DRN-LSP-01", "Aqua-Surface", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, "Canal Central", 500, 10
        );
        DroneOperacional base = new DroneOperacionalBase(droneFisico);
        DroneOperacional conMonitoreo = new DroneConMonitoreo(base);
        DroneOperacional conCifrado = new DroneConCifrado(conMonitoreo);

        // Los subtipos responden idénticamente al contrato
        assertEquals("DRN-LSP-01", conCifrado.getId());
        assertEquals("Canal Central", conCifrado.getZona());
        assertEquals(80, conCifrado.getBateria());
        assertTrue(conCifrado.isDisponible());

        // Ejecutar misión preserva comportamiento sin alterar invariantes de dominio
        boolean exito = conCifrado.ejecutarMision("MIS-LSP", "TR-1");
        assertTrue(exito);
        assertEquals(70, conCifrado.getBateria());
    }

    @Test
    @DisplayName("SOLID ISP: Puertos de dominio definen contratos segregados y cohesivos")
    void testInterfaceSegregationPrincipleEnPuertos() {
        Method[] metodosProveedor = ProveedorCondicionesHidricas.class.getDeclaredMethods();
        assertEquals(1, metodosProveedor.length,
                "ProveedorCondicionesHidricas debe estar altamente segregada (solo 1 método de consulta)");
        assertEquals("consultarCondiciones", metodosProveedor[0].getName());

        Method[] metodosEslabon = EslabonValidacion.class.getDeclaredMethods();
        assertEquals(2, metodosEslabon.length,
                "EslabonValidacion solo debe definir los métodos estrictamente necesarios para la cadena");
    }

    @Test
    @DisplayName("SOLID SRP: Cada eslabón de validación tiene una única responsabilidad aislada")
    void testSingleResponsibilityPrincipleEnValidadores() {
        ValidadorBateria valBateria = new ValidadorBateria();
        ValidadorCapacidadCarga valCarga = new ValidadorCapacidadCarga();
        ValidadorZonaActiva valZona = new ValidadorZonaActiva();
        ValidadorCondicionesHidricas valHidrico = new ValidadorCondicionesHidricas();

        assertNotNull(valBateria);
        assertNotNull(valCarga);
        assertNotNull(valZona);
        assertNotNull(valHidrico);
    }

    @Test
    @DisplayName("INTEGRACIÓN SOLID: DespachadorMisionesEnterprise coordina validación e inyección exitosamente")
    void testDespachadorMisionesEnterpriseFlujoCompleto() {
        ClienteApiHidricaExterna clienteStub = zona -> new DatosHidricosExternosJson(3.0, 10.0, 0.4, true);
        AdaptadorAPIHidrica adaptador = new AdaptadorAPIHidrica(clienteStub);
        CadenaValidacionMision cadena = new CadenaValidacionMision();

        DespachadorMisionesEnterprise despachador = new DespachadorMisionesEnterprise(cadena, adaptador);

        DroneEnterprise droneValido = new DroneEnterprise(
                "DRN-DESP-01", "Aqua-Surface", TipoDrone.SUPERFICIAL, 90,
                EstadoDrone.DISPONIBLE, "Canal Central", 600, 12
        );
        DroneOperacional droneOperacional = new DroneConCifrado(
                new DroneConMonitoreo(new DroneOperacionalBase(droneValido))
        );

        MisionEnterprise misionValida = new MisionEnterprise(
                "MIS-DESP-001", "Transporte muestra", "Canal Central", "Embalse Norte",
                List.of(), List.of(), EstadoMisionEnterprise.PLANIFICADA, Prioridad.ALTA, 400
        );

        ResultadoValidacion resultado = despachador.validarDespacho(misionValida, droneValido, "Canal Central");
        assertTrue(resultado.exitoso());

        boolean misionDespachada = despachador.despacharMision(
                misionValida, droneOperacional, "Canal Central", "TR-1"
        );
        assertTrue(misionDespachada);
        assertEquals(80, droneOperacional.getBateria());
    }
}
