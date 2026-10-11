package main.dosw.prinplup.fabrica;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;

/**
 * Especialización de DroneAcuatico para operaciones en aguas agitadas.
 * Capacidad máxima de carga: 1500g (1.5 kg).
 */
public class DroneSemisumergido extends DroneAcuatico {

    public static final int CAPACIDAD_MAXIMA_GRAMOS = 1500;
    public static final String MODELO_DEFECTO = "Aqua-SemiSub 300";

    public DroneSemisumergido(String id, int bateria, EstadoDrone estado, String zona) {
        super(id, MODELO_DEFECTO, TipoDrone.SEMISUMERGIDO, bateria, estado, zona, CAPACIDAD_MAXIMA_GRAMOS);
    }

    public DroneSemisumergido(String id, String modelo, int bateria, EstadoDrone estado, String zona, int capacidadCargaGramos) {
        super(id, modelo, TipoDrone.SEMISUMERGIDO, bateria, estado, zona, Math.min(capacidadCargaGramos, CAPACIDAD_MAXIMA_GRAMOS));
    }
}
