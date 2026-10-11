package main.dosw.empoleon.aplicacion.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;

/**
 * Eslabón 1: Valida que el dron cuente con reserva de batería superior al umbral mínimo (20%).
 */
public class ValidadorBateria extends ValidadorBase {

    private static final int UMBRAL_BATERIA_MINIMA = 20;

    @Override
    public ResultadoValidacion validar(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    ) {
        if (drone == null || drone.getBateria() < UMBRAL_BATERIA_MINIMA) {
            int bateriaActual = drone != null ? drone.getBateria() : 0;
            return ResultadoValidacion.fallo(
                    "Batería insuficiente (" + bateriaActual + "%). Mínimo requerido: " + UMBRAL_BATERIA_MINIMA + "%",
                    "ValidadorBateria"
            );
        }
        return pasarAlSiguiente(mision, drone, condiciones);
    }
}
