package main.dosw.empoleon.dominio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import main.dosw.prinplup.DroneAcuatico;

/**
 * Collector personalizado que agrupa los drones por zona y calcula la batería
 * promedio por zona en una sola pasada a través del stream.
 */
public class BateriaPromedioPorZonaCollector
        implements Collector<DroneAcuatico, Map<String, BateriaPromedioPorZonaCollector.AcumuladorBateria>, Map<String, Double>> {

    public static class AcumuladorBateria {
        private long suma;
        private long cantidad;

        public AcumuladorBateria() {
            this.suma = 0;
            this.cantidad = 0;
        }

        public void agregar(int bateria) {
            this.suma += bateria;
            this.cantidad++;
        }

        public AcumuladorBateria combinar(AcumuladorBateria otro) {
            this.suma += otro.suma;
            this.cantidad += otro.cantidad;
            return this;
        }

        public double promedio() {
            return cantidad == 0 ? 0.0 : (double) suma / cantidad;
        }
    }

    @Override
    public Supplier<Map<String, AcumuladorBateria>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, AcumuladorBateria>, DroneAcuatico> accumulator() {
        return (map, drone) -> {
            if (drone != null && drone.getZona() != null) {
                map.computeIfAbsent(drone.getZona(), k -> new AcumuladorBateria())
                        .agregar(drone.getBateria());
            }
        };
    }

    @Override
    public BinaryOperator<Map<String, AcumuladorBateria>> combiner() {
        return (map1, map2) -> {
            map2.forEach((zona, acum) ->
                    map1.computeIfAbsent(zona, k -> new AcumuladorBateria()).combinar(acum)
            );
            return map1;
        };
    }

    @Override
    public Function<Map<String, AcumuladorBateria>, Map<String, Double>> finisher() {
        return map -> {
            Map<String, Double> resultado = new HashMap<>();
            map.forEach((zona, acum) -> resultado.put(zona, acum.promedio()));
            return Collections.unmodifiableMap(resultado);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
