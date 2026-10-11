package main.dosw.empoleon.dominio;

import main.dosw.prinplup.TipoDrone;

/**
 * Zonas hídricas interconectadas del campus ECI para AquaPort Enterprise.
 */
public enum ZonaHidrica {
    EMBALSE_INVESTIGACION("Embalse de Investigación", "Norte", TipoDrone.BUCEADOR),
    RED_CANALES("Red de Canales", "Campus", TipoDrone.SUPERFICIAL),
    LAGUNA_RESERVA("Laguna de Reserva", "Sur", TipoDrone.SEMISUMERGIDO),
    LABORATORIO_CENTRAL("Laboratorio Hídrico Central", "Centro", TipoDrone.SUPERFICIAL);

    private final String nombre;
    private final String ubicacion;
    private final TipoDrone tipoRecomendado;

    ZonaHidrica(String nombre, String ubicacion, TipoDrone tipoRecomendado) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tipoRecomendado = tipoRecomendado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public TipoDrone getTipoRecomendado() {
        return tipoRecomendado;
    }
}
