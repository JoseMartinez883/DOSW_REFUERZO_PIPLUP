package main.dosw.prinplup;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Ejercicio01Prinplup {

    private static final String MODELO_SUPERFICIAL = "Aqua-Surface 200";
    private static final String MODELO_SEMISUMERGIDO = "Aqua-SemiSub 300";
    private static final String MODELO_BUCEADOR = "Aqua-Diver 500";

    private static final String EMBALSE_NORTE = "Embalse Norte";
    private static final String CANAL_CENTRAL = "Canal Central";
    private static final String LAGUNA_SUR = "Laguna Sur";
    private static final String RIBERA_ESTE = "Punto Ribereño Este";
    private static final String LAB_HIDRICO = "Laboratorio Hídrico";

    private static final String CARGA_MUESTRA = "MUESTRA_AGUA";
    private static final String CARGA_SENSOR = "SENSOR";

    public static List<DroneAcuatico> crearFlota15Drones() {
        return List.of(
                // 5 Drones Superficiales (capacidad hasta 500g, zonas tranquilas)
                new DroneAcuatico("AR-01", MODELO_SUPERFICIAL, TipoDrone.SUPERFICIAL, 90, EstadoDrone.DISPONIBLE, EMBALSE_NORTE, 500),
                new DroneAcuatico("AR-02", MODELO_SUPERFICIAL, TipoDrone.SUPERFICIAL, 45, EstadoDrone.DISPONIBLE, LAGUNA_SUR, 500),
                new DroneAcuatico("AR-03", MODELO_SUPERFICIAL, TipoDrone.SUPERFICIAL, 20, EstadoDrone.RECARGANDO, LAB_HIDRICO, 500),
                new DroneAcuatico("AR-04", MODELO_SUPERFICIAL, TipoDrone.SUPERFICIAL, 75, EstadoDrone.EN_MISION, RIBERA_ESTE, 500),
                new DroneAcuatico("AR-05", MODELO_SUPERFICIAL, TipoDrone.SUPERFICIAL, 15, EstadoDrone.MANTENIMIENTO, EMBALSE_NORTE, 500),

                // 5 Drones Semisumergidos (capacidad hasta 1500g, aptos para aguas agitadas)
                new DroneAcuatico("AR-06", MODELO_SEMISUMERGIDO, TipoDrone.SEMISUMERGIDO, 88, EstadoDrone.DISPONIBLE, CANAL_CENTRAL, 1500),
                new DroneAcuatico("AR-07", MODELO_SEMISUMERGIDO, TipoDrone.SEMISUMERGIDO, 65, EstadoDrone.DISPONIBLE, EMBALSE_NORTE, 1500),
                new DroneAcuatico("AR-08", MODELO_SEMISUMERGIDO, TipoDrone.SEMISUMERGIDO, 80, EstadoDrone.EN_MISION, CANAL_CENTRAL, 1500),
                new DroneAcuatico("AR-09", MODELO_SEMISUMERGIDO, TipoDrone.SEMISUMERGIDO, 30, EstadoDrone.RECARGANDO, LAB_HIDRICO, 1500),
                new DroneAcuatico("AR-10", MODELO_SEMISUMERGIDO, TipoDrone.SEMISUMERGIDO, 95, EstadoDrone.DISPONIBLE, RIBERA_ESTE, 1500),

                // 5 Drones Buceadores (capacidad hasta 300g, toma de muestras profunda)
                new DroneAcuatico("AR-11", MODELO_BUCEADOR, TipoDrone.BUCEADOR, 85, EstadoDrone.DISPONIBLE, EMBALSE_NORTE, 300),
                new DroneAcuatico("AR-12", MODELO_BUCEADOR, TipoDrone.BUCEADOR, 70, EstadoDrone.SUMERGIDO, LAGUNA_SUR, 300),
                new DroneAcuatico("AR-13", MODELO_BUCEADOR, TipoDrone.BUCEADOR, 92, EstadoDrone.DISPONIBLE, LAB_HIDRICO, 300),
                new DroneAcuatico("AR-14", MODELO_BUCEADOR, TipoDrone.BUCEADOR, 40, EstadoDrone.DISPONIBLE, EMBALSE_NORTE, 300),
                new DroneAcuatico("AR-15", MODELO_BUCEADOR, TipoDrone.BUCEADOR, 10, EstadoDrone.MANTENIMIENTO, LAB_HIDRICO, 300)
        );
    }

    public static List<Mision> crearMisionesEjemplo(List<DroneAcuatico> flota) {
        return List.of(
                new Mision("MIS-201", flota.get(0), EMBALSE_NORTE, LAB_HIDRICO, Prioridad.CRITICA, CARGA_MUESTRA, 250),
                new Mision("MIS-202", flota.get(5), CANAL_CENTRAL, RIBERA_ESTE, Prioridad.ALTA, CARGA_SENSOR, 800),
                new Mision("MIS-203", flota.get(10), EMBALSE_NORTE, LAB_HIDRICO, Prioridad.CRITICA, CARGA_MUESTRA, 150),
                new Mision("MIS-204", flota.get(1), LAGUNA_SUR, LAB_HIDRICO, Prioridad.NORMAL, CARGA_MUESTRA, 300),
                new Mision("MIS-205", flota.get(9), RIBERA_ESTE, CANAL_CENTRAL, Prioridad.BAJA, CARGA_SENSOR, 500)
        );
    }

    public static void main(String[] args) {
        System.out.println("=== AquaPort v2 — Nivel Prinplup: Ejercicio 01 (Streams Avanzados) ===");

        List<DroneAcuatico> flota = crearFlota15Drones();
        List<Mision> misiones = crearMisionesEjemplo(flota);
        ConsultorFlotaPrinplup consultor = new ConsultorFlotaPrinplup();

        // 1. Agrupar drones disponibles por tipo
        System.out.println("\n1. Drones disponibles agrupados por tipo:");
        Map<TipoDrone, List<DroneAcuatico>> porTipo = consultor.agruparDronesDisponiblesPorTipo(flota);
        System.out.println(porTipo);

        // 2. Obtener el drone óptimo para una zona dada (ej. Canal Central)
        System.out.println("\n2. Drone óptimo para 'Canal Central' (aguas agitadas):");
        Optional<DroneAcuatico> optimoCanal = consultor.obtenerDroneOptimoParaZona(flota, CANAL_CENTRAL);
        System.out.println(optimoCanal);

        // 3. Calcular el promedio de batería por tipo de drone
        System.out.println("\n3. Promedio de batería por tipo de drone:");
        Map<TipoDrone, Double> promedios = consultor.calcularPromedioBateriaPorTipo(flota);
        System.out.println(promedios);

        // 4. Separar misiones CRÍTICAS de las demás usando partitioningBy
        System.out.println("\n4. Partición de misiones (CRÍTICAS vs Demás):");
        Map<Boolean, List<Mision>> particionMisiones = consultor.separarMisionesCriticas(misiones);
        System.out.println("- Críticas: " + particionMisiones.get(true));
        System.out.println("- Regulares: " + particionMisiones.get(false));

        // 5. Listar todas las zonas únicas que cubre la flota activa
        System.out.println("\n5. Zonas únicas cubiertas por la flota activa:");
        List<String> zonasUnicas = consultor.listarZonasUnicasFlotaActiva(flota);
        System.out.println(zonasUnicas);
    }
}
