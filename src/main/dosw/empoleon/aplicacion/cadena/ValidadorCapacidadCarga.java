package main.dosw.empoleon.aplicacion.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;

/**
 * Eslabón 2: Valida que el peso de la carga de la misión no supere la capacidad estructural del dron.
 */
public class ValidadorCapacidadCarga extends ValidadorBase {

    @Override
    public ResultadoValidacion validar(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    ) {
        if (mision == null || drone == null || !drone.puedeCargar(mision.getPesoCargaGramos())) {
            int pesoCarga = mision != null ? mision.getPesoCargaGramos() : 0;
            int capacidad = drone != null ? drone.getCapacidadCargaGramos() : 0;
            return ResultadoValidacion.fallo(
                    "Peso de carga excesivo (" + pesoCarga + "g). Capacidad máxima: " + capacidad + "g",
                    "ValidadorCapacidadCarga"
            );
        }
        return pasarAlSiguiente(mision, drone, condiciones);
    }
}
