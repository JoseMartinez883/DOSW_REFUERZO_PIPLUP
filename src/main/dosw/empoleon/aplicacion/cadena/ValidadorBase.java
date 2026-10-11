package main.dosw.empoleon.aplicacion.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.EslabonValidacion;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;

/**
 * Clase base abstracta que gestiona el puntero al siguiente eslabón en la cadena.
 */
public abstract class ValidadorBase implements EslabonValidacion {

    protected EslabonValidacion siguiente;

    @Override
    public void setSiguiente(EslabonValidacion siguiente) {
        this.siguiente = siguiente;
    }

    protected ResultadoValidacion pasarAlSiguiente(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    ) {
        if (siguiente == null) {
            return ResultadoValidacion.exito();
        }
        return siguiente.validar(mision, drone, condiciones);
    }
}
