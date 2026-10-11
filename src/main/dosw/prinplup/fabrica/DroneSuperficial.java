package main.dosw.prinplup.fabrica;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;

/**
 * Especialización de DroneAcuatico para operaciones en aguas tranquilas de superficie.
 * Capacidad máxima de carga: 500g.
 */
public class DroneSuperficial extends DroneAcuatico {

    public static final int CAPACIDAD_MAXIMA_GRAMOS = 500;
    public static final String MODELO_DEFECTO = "Aqua-Surface 200";

    public DroneSuperficial(String id, int bateria, EstadoDrone estado, String zona) {
        super(id, MODELO_DEFECTO, TipoDrone.SUPERFICIAL, bateria, estado, zona, CAPACIDAD_MAXIMA_GRAMOS);
    }

    public DroneSuperficial(String id, String modelo, int bateria, EstadoDrone estado, String zona, int capacidadCargaGramos) {
        super(id, modelo, TipoDrone.SUPERFICIAL, bateria, estado, zona, Math.min(capacidadCargaGramos, CAPACIDAD_MAXIMA_GRAMOS));
    }
}
