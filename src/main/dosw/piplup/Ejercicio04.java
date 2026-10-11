package main.dosw.piplup;

public class Ejercicio04 {

    public static void main(String[] args) {
        System.out.println("=== AquaPort MVP — Ejercicio 04: Principios SOLID (SRP y DIP) ===");

        // Inversión de Dependencias (DIP): Se inyecta la abstracción RepositorioMisiones
        RepositorioMisiones repositorio = new RepositorioMisionesMemoria();
        ValidadorMision validador = new ValidadorMision();
        NotificadorOperador notificador = new NotificadorOperador();

        // Responsabilidad Única (SRP): RegistradorMisiones solo coordina el registro
        RegistradorMisiones registrador = new RegistradorMisiones(repositorio, validador, notificador);

        // Flota de prueba del MVP
        DroneAcuatico droneOptimo = new DroneAcuatico("AR-01", "Aqua-Ranger 100", 92, true, "Embalse Norte");
        DroneAcuatico droneBateriaBaja = new DroneAcuatico("AR-03", "Aqua-Ranger 100", 18, false, "Laguna Sur");

        // 1. Caso exitoso: Misión válida
        System.out.println("\n1. Registrando misión válida:");
        Mision misionValida = new Mision.Builder()
                .id("MIS-101")
                .drone(droneOptimo)
                .puntoPartida("Embalse Norte")
                .puntoLlegada("Laboratorio Hídrico")
                .tipoCarga(TipoCarga.MUESTRA_AGUA)
                .build();

        registrador.registrarMision(misionValida);

        // 2. Caso fallido: Violación de regla de negocio (batería insuficiente o no disponible)
        System.out.println("\n2. Intentando registrar misión con drone no apto:");
        try {
            Mision misionInvalida = new Mision.Builder()
                    .id("MIS-102")
                    .drone(droneBateriaBaja)
                    .puntoPartida("Laguna Sur")
                    .puntoLlegada("Laboratorio Hídrico")
                    .tipoCarga(TipoCarga.MUESTRA_AGUA)
                    .build();
            registrador.registrarMision(misionInvalida);
        } catch (IllegalStateException e) {
            notificador.notificarError("Validación rechazada: " + e.getMessage());
        }

        // 3. Caso de consulta a través del repositorio inyectado
        System.out.println("\n3. Consultando todas las misiones registradas:");
        registrador.consultarTodas().forEach(m ->
                notificador.notificarConsulta("ID: " + m.getId() + " | Drone: " + m.getDrone().id()
                        + " | Carga: " + m.getTipoCarga() + " | Estado: " + m.getEstadoMision())
        );

        System.out.println("\n4. Consultando misión por ID 'MIS-101':");
        registrador.consultarPorId("MIS-101").ifPresentOrElse(
                m -> notificador.notificarConsulta("Encontrada: " + m.getId() + " hacia " + m.getPuntoLlegada()),
                () -> notificador.notificarError("Misión no encontrada")
        );
    }
}
