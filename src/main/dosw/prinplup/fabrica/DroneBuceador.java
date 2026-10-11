package main.dosw.prinplup.fabrica;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;

/**
 * Especialización de DroneAcuatico para toma de muestras y monitoreo a profundidad.
 * Capacidad máxima de carga: 300g.
 */
public class DroneBuceador extends DroneAcuatico {

    public static final int CAPACIDAD_MAXIMA_GRAMOS = 300;
    public static final String MODELO_DEFECTO = "Aqua-Diver 500";

    public DroneBuceador(String id, int bateria, EstadoDrone estado, String zona) {
        super(id, MODELO_DEFECTO, TipoDrone.BUCEADOR, bateria, estado, zona, CAPACIDAD_MAXIMA_GRAMOS);
    }

    public DroneBuceador(String id, String modelo, int bateria, EstadoDrone estado, String zona, int capacidadCargaGramos) {
        super(id, modelo, TipoDrone.BUCEADOR, bateria, estado, zona, Math.min(capacidadCargaGramos, CAPACIDAD_MAXIMA_GRAMOS));
    }
}
