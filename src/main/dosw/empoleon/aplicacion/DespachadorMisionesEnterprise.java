package main.dosw.empoleon.aplicacion;

import java.util.Objects;
import main.dosw.empoleon.aplicacion.cadena.CadenaValidacionMision;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.adapter.ProveedorCondicionesHidricas;
import main.dosw.empoleon.dominio.cadena.ResultadoValidacion;
import main.dosw.empoleon.dominio.decorator.DroneOperacional;

/**
 * Servicio de aplicación que ejemplifica la adhesión rigurosa a los principios SOLID:
 * - SRP: Se enfoca exclusivamente en orquestar el despacho de misiones.
 * - DIP: Depende de abstracciones (ProveedorCondicionesHidricas y CadenaValidacionMision).
 * - Inyección por Constructor: Todas las dependencias son inmutables (final) y se inyectan en el constructor.
 */
public class DespachadorMisionesEnterprise {

    private final CadenaValidacionMision cadenaValidacion;
    private final ProveedorCondicionesHidricas proveedorCondiciones;

    public DespachadorMisionesEnterprise(
            CadenaValidacionMision cadenaValidacion,
            ProveedorCondicionesHidricas proveedorCondiciones
    ) {
        this.cadenaValidacion = Objects.requireNonNull(cadenaValidacion, "La cadena no puede ser nula");
        this.proveedorCondiciones = Objects.requireNonNull(proveedorCondiciones, "El proveedor no puede ser nulo");
    }

    public ResultadoValidacion validarDespacho(
            MisionEnterprise mision,
            DroneEnterprise drone,
            String zona
    ) {
        CondicionesHidricasDominio condiciones = proveedorCondiciones.consultarCondiciones(zona);
        return cadenaValidacion.validar(mision, drone, condiciones);
    }

    public boolean despacharMision(
            MisionEnterprise mision,
            DroneOperacional droneOperacional,
            String zona,
            String tramoId
    ) {
        ResultadoValidacion resultado = validarDespacho(mision, droneOperacional.getDroneBase(), zona);
        if (!resultado.exitoso()) {
            return false;
        }
        return droneOperacional.ejecutarMision(mision.getId(), tramoId);
    }
}
