package main.dosw.empoleon.dominio.adapter;

/**
 * Modelo de dominio en Java puro que representa las condiciones meteorológicas
 * e hidrológicas de una zona hídrica para determinar su navegabilidad.
 */
public record CondicionesHidricasDominio(
        double nivelAguaMetros,
        double turbidezNtu,
        double alturaOlasMetros,
        boolean esNavegable
) {

    public static CondicionesHidricasDominio optimas() {
        return new CondicionesHidricasDominio(3.5, 12.0, 0.2, true);
    }

    public static CondicionesHidricasDominio adversas() {
        return new CondicionesHidricasDominio(0.4, 180.0, 1.8, false);
    }
}
