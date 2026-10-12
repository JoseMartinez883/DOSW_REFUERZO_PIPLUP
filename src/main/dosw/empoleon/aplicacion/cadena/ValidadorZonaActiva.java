package main.dosw.empoleon.aplicacion.cadena;

import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;

/**
 * Eslabón 3: Valida que el dron se encuentre disponible y en la zona correspondiente a la misión.
 */
public class ValidadorZonaActiva extends ValidadorBase {

    @Override
    public ResultadoValidacion validar(
            MisionEnterprise mision,
            DroneEnterprise drone,
            CondicionesHidricasDominio condiciones
    ) {
        if (drone == null || !drone.isDisponible()) {
            return ResultadoValidacion.fallo("El dron asignado no está en estado DISPONIBLE", "ValidadorZonaActiva");
        }
        if (mision == null || !drone.getZona().equalsIgnoreCase(mision.getZonaOrigen())) {
            String zonaDron = drone.getZona();
            String zonaMision = mision != null ? mision.getZonaOrigen() : "N/A";
            return ResultadoValidacion.fallo(
                    "Zona incompatible. Dron en '" + zonaDron + "' pero misión requiere '" + zonaMision + "'",
                    "ValidadorZonaActiva"
            );
        }
        return pasarAlSiguiente(mision, drone, condiciones);
    }
}
