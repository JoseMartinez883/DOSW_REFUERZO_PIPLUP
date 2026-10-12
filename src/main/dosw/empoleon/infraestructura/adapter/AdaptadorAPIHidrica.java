package main.dosw.empoleon.infraestructura.adapter;

import java.util.Objects;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.adapter.ProveedorCondicionesHidricas;

/**
 * Patrón Adapter: Adapta los datos provistos en inglés y formato JSON de la API
 * externa al modelo de dominio puro `CondicionesHidricasDominio`.
 */
public class AdaptadorAPIHidrica implements ProveedorCondicionesHidricas {

    private final ClienteApiHidricaExterna clienteExterno;

    public AdaptadorAPIHidrica(ClienteApiHidricaExterna clienteExterno) {
        this.clienteExterno = Objects.requireNonNull(clienteExterno, "El cliente externo no puede ser nulo");
    }

    @Override
    public CondicionesHidricasDominio consultarCondiciones(String zona) {
        if (zona == null || zona.isBlank()) {
            return CondicionesHidricasDominio.adversas();
        }

        DatosHidricosExternosJson respuesta = clienteExterno.fetchWaterConditions(zona);
        if (respuesta == null) {
            return CondicionesHidricasDominio.adversas();
        }

        // Sanitización y normalización de valores límite
        double nivelAgua = Math.max(0.0, respuesta.waterLevel());
        double turbidez = Math.max(0.0, respuesta.turbidity());
        double alturaOlas = Math.max(0.0, respuesta.waveHeight());
        boolean navegable = respuesta.safeForNavigation() && nivelAgua >= 0.5 && alturaOlas <= 1.5;

        return new CondicionesHidricasDominio(nivelAgua, turbidez, alturaOlas, navegable);
    }
}
