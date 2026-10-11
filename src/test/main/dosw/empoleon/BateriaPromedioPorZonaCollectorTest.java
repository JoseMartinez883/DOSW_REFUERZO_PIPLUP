package main.dosw.empoleon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import main.dosw.empoleon.dominio.BateriaPromedioPorZonaCollector;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BateriaPromedioPorZonaCollectorTest {

    @Test
    @DisplayName("Collector personalizado characteristics y supplier funcionan correctamente")
    void testCollectorCharacteristicsYSupplier() {
        BateriaPromedioPorZonaCollector collector = new BateriaPromedioPorZonaCollector();
        assertNotNull(collector.supplier());
        assertNotNull(collector.accumulator());
        assertNotNull(collector.combiner());
        assertNotNull(collector.finisher());
        assertTrue(collector.characteristics().contains(Collector.Characteristics.UNORDERED));
    }

    @Test
    @DisplayName("AcumuladorBateria calcula promedio correctamente y maneja division por cero")
    void testAcumuladorBateriaDirecto() {
        BateriaPromedioPorZonaCollector.AcumuladorBateria acum = new BateriaPromedioPorZonaCollector.AcumuladorBateria();
        assertEquals(0.0, acum.promedio());

        acum.agregar(80);
        acum.agregar(90);
        assertEquals(85.0, acum.promedio());

        BateriaPromedioPorZonaCollector.AcumuladorBateria acum2 = new BateriaPromedioPorZonaCollector.AcumuladorBateria();
        acum2.agregar(100);
        acum.combinar(acum2);
        assertEquals(90.0, acum.promedio());
    }

    @Test
    @DisplayName("Combiner une mapas de diferentes hilos correctamente")
    void testCombinerMapas() {
        BateriaPromedioPorZonaCollector collector = new BateriaPromedioPorZonaCollector();
        Map<String, BateriaPromedioPorZonaCollector.AcumuladorBateria> m1 = new HashMap<>();
        Map<String, BateriaPromedioPorZonaCollector.AcumuladorBateria> m2 = new HashMap<>();

        BateriaPromedioPorZonaCollector.AcumuladorBateria a1 = new BateriaPromedioPorZonaCollector.AcumuladorBateria();
        a1.agregar(50);
        m1.put("ZonaA", a1);

        BateriaPromedioPorZonaCollector.AcumuladorBateria a2 = new BateriaPromedioPorZonaCollector.AcumuladorBateria();
        a2.agregar(70);
        m2.put("ZonaA", a2);

        Map<String, BateriaPromedioPorZonaCollector.AcumuladorBateria> combinado = collector.combiner().apply(m1, m2);
        assertEquals(60.0, combinado.get("ZonaA").promedio());
    }

    @Test
    @DisplayName("Maneja drones con valores nulos sin fallar")
    void testDronesNulos() {
        BateriaPromedioPorZonaCollector collector = new BateriaPromedioPorZonaCollector();
        DroneEnterprise dValido = new DroneEnterprise("D1", "M", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, "ZonaX", 500, 2);
        DroneEnterprise dZonaNula = new DroneEnterprise("D2", "M", TipoDrone.SUPERFICIAL, 80,
                EstadoDrone.DISPONIBLE, null, 500, 2);

        List<DroneEnterprise> lista = java.util.Arrays.asList(dValido, null, dZonaNula);
        Map<String, Double> res = lista.stream().collect(collector);

        assertEquals(1, res.size());
        assertEquals(80.0, res.get("ZonaX"));
    }
}
