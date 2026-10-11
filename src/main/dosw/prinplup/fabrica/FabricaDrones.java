package main.dosw.prinplup.fabrica;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;

/**
 * Fábrica de drones acuáticos (Patrón Factory Method).
 * Encapsula la creación e instanciación de las subclases de DroneAcuatico
 * desacoplando a los clientes de las clases concretas.
 */
public class FabricaDrones {

    private FabricaDrones() {
        // Constructor privado para clase de utilidad / factory
    }

    /**
     * Crea una instancia de DroneAcuatico adecuada según el TipoDrone solicitado.
     *
     * @param tipo Tipo de drone (SUPERFICIAL, SEMISUMERGIDO, BUCEADOR)
     * @param id Identificador único del drone
     * @param bateria Nivel de batería (0-100)
     * @param estado Estado operativo del drone
     * @param zona Zona de despliegue
     * @return Instancia especializada de DroneAcuatico
     */
    public static DroneAcuatico crearDrone(TipoDrone tipo, String id, int bateria, EstadoDrone estado, String zona) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de drone no puede ser nulo.");
        }
        return switch (tipo) {
            case SUPERFICIAL -> new DroneSuperficial(id, bateria, estado, zona);
            case SEMISUMERGIDO -> new DroneSemisumergido(id, bateria, estado, zona);
            case BUCEADOR -> new DroneBuceador(id, bateria, estado, zona);
        };
    }
}
