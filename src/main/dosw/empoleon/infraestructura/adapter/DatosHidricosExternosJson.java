package main.dosw.empoleon.infraestructura.adapter;

/**
 * DTO que simula la respuesta JSON provista por la API externa meteorológica/hídrica,
 * utilizando nomenclatura en inglés según el estándar de la plataforma externa.
 */
public record DatosHidricosExternosJson(
        double waterLevel,
        double turbidity,
        double waveHeight,
        boolean safeForNavigation
) {
}
