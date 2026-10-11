package main.dosw.prinplup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultorFlotaPrinplup {

    private static final String CANAL_CENTRAL = "Canal Central";
    private static final String EMBALSE_NORTE = "Embalse Norte";
    private static final String LAGUNA_SUR = "Laguna Sur";
    private static final String LAB_HIDRICO = "Laboratorio Hídrico";

    /**
     * Valida si un tipo de drone es compatible con las condiciones de una zona hídrica.
     * - SUPERFICIAL: Aguas tranquilas (no apto para aguas agitadas de Canal Central).
     * - SEMISUMERGIDO: Apto para todas las zonas, especialmente aguas agitadas con corriente.
     * - BUCEADOR: Apto para cuerpos con profundidad de inmersión y toma de muestras.
     */
    public boolean esCompatibleConZona(TipoDrone tipo, String zona) {
        if (tipo == null || zona == null) {
            return false;
        }
        return switch (tipo) {
            case SUPERFICIAL -> !zona.equalsIgnoreCase(CANAL_CENTRAL);
            case SEMISUMERGIDO -> true;
            case BUCEADOR -> zona.equalsIgnoreCase(EMBALSE_NORTE)
                    || zona.equalsIgnoreCase(LAGUNA_SUR)
                    || zona.equalsIgnoreCase(LAB_HIDRICO);
        };
    }

    /**
     * Consulta 1: Agrupa los drones disponibles por tipo especializado.
     */
    public Map<TipoDrone, List<DroneAcuatico>> agruparDronesDisponiblesPorTipo(List<DroneAcuatico> drones) {
        if (drones == null) {
            return Collections.emptyMap();
        }
        return drones.stream()
                .filter(DroneAcuatico::isDisponible)
                .collect(Collectors.groupingBy(DroneAcuatico::tipo));
    }

    /**
     * Consulta 2: Obtiene el drone óptimo para una zona dada (ubicado en la zona, disponible, compatible y con mayor batería).
     */
    public Optional<DroneAcuatico> obtenerDroneOptimoParaZona(List<DroneAcuatico> drones, String zona) {
        if (drones == null || zona == null || zona.isBlank()) {
            return Optional.empty();
        }
        return drones.stream()
                .filter(DroneAcuatico::isDisponible)
                .filter(drone -> drone.zona().equalsIgnoreCase(zona))
                .filter(drone -> esCompatibleConZona(drone.tipo(), zona))
                .max(Comparator.comparingInt(DroneAcuatico::bateria));
    }

    /**
     * Consulta 3: Calcula el promedio de nivel de batería (%) por tipo de drone.
     */
    public Map<TipoDrone, Double> calcularPromedioBateriaPorTipo(List<DroneAcuatico> drones) {
        if (drones == null) {
            return Collections.emptyMap();
        }
        return drones.stream()
                .collect(Collectors.groupingBy(
                        DroneAcuatico::tipo,
                        Collectors.averagingDouble(DroneAcuatico::bateria)
                ));
    }

    /**
     * Consulta 4: Separa misiones CRÍTICAS de las demás usando partitioningBy.
     * Retorna Map con true -> misiones críticas, false -> misiones no críticas.
     */
    public Map<Boolean, List<Mision>> separarMisionesCriticas(List<Mision> misiones) {
        if (misiones == null) {
            return Collections.emptyMap();
        }
        return misiones.stream()
                .collect(Collectors.partitioningBy(
                        m -> m.prioridad() == Prioridad.CRITICA
                ));
    }

    /**
     * Consulta 5: Lista todas las zonas únicas cubiertas por los drones en estado DISPONIBLE.
     */
    public List<String> listarZonasUnicasFlotaActiva(List<DroneAcuatico> drones) {
        if (drones == null) {
            return Collections.emptyList();
        }
        return drones.stream()
                .filter(DroneAcuatico::isDisponible)
                .map(DroneAcuatico::zona)
                .filter(Objects::nonNull)
                .filter(z -> !z.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Consulta adicional de apoyo: Ordena misiones por prioridad compuesta y luego por batería descendente.
     */
    public List<Mision> ordenarMisionesPorPrioridadYBateria(List<Mision> misiones) {
        if (misiones == null) {
            return Collections.emptyList();
        }
        return misiones.stream()
                .sorted(Comparator.comparing(Mision::getPrioridad)
                        .thenComparing(m -> m.getDrone().bateria(), Comparator.reverseOrder()))
                .toList();
    }
}
