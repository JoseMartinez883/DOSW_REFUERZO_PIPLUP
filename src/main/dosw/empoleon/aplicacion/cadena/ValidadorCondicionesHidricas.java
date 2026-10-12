package main.dosw.empoleon.aplicacion.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;

/**
 * Eslabón 4: Valida que las condiciones meteorológicas e hidrológicas de la zona sean navegables.
 */
public class ValidadorCondicionesHidricas extends ValidadorBase {

    @Override
    public ResultadoValidacion validar(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    ) {
        if (condiciones == null || !condiciones.esNavegable()) {
            return ResultadoValidacion.fallo(
                    "Condiciones hídricas adversas: navegación acuática suspendida por seguridad",
                    "ValidadorCondicionesHidricas"
            );
        }
        return pasarAlSiguiente(mision, drone, condiciones);
    }
}
