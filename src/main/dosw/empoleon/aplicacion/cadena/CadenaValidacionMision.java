package main.dosw.empoleon.aplicacion.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.EslabonValidacion;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;

/**
 * Orquestador del patrón Chain of Responsibility que ensambla y ejecuta
 * la cadena de validación para el despacho de misiones en AquaPort Enterprise.
 */
public class CadenaValidacionMision {

    private final EslabonValidacion primerEslabon;

    public CadenaValidacionMision() {
        this.primerEslabon = construirCadenaPorDefecto();
    }

    public CadenaValidacionMision(EslabonValidacion primerEslabon) {
        this.primerEslabon = primerEslabon;
    }

    public static EslabonValidacion construirCadenaPorDefecto() {
        ValidadorBateria valBateria = new ValidadorBateria();
        ValidadorCapacidadCarga valCarga = new ValidadorCapacidadCarga();
        ValidadorZonaActiva valZona = new ValidadorZonaActiva();
        ValidadorCondicionesHidricas valHidrico = new ValidadorCondicionesHidricas();

        valBateria.setSiguiente(valCarga);
        valCarga.setSiguiente(valZona);
        valZona.setSiguiente(valHidrico);

        return valBateria;
    }

    public ResultadoValidacion validar(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    ) {
        if (primerEslabon == null) {
            return ResultadoValidacion.exito();
        }
        return primerEslabon.validar(mision, drone, condiciones);
    }
}
