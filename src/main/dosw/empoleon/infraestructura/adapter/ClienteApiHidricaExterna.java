package main.dosw.empoleon.infraestructura.adapter;

/**
 * Cliente de infraestructura que interactúa con la API externa de hidrología.
 */
public interface ClienteApiHidricaExterna {
    DatosHidricosExternosJson fetchWaterConditions(String zoneIdentifier);
}
