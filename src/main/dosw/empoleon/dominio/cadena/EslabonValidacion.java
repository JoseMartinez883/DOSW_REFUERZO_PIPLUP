package main.dosw.empoleon.dominio.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;

/**
 * Contrato para cada eslabón en el patrón Chain of Responsibility.
 */
public interface EslabonValidacion {
    void setSiguiente(EslabonValidacion siguiente);
    ResultadoValidacion validar(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    );
}
