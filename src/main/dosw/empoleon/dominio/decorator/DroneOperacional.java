package main.dosw.empoleon.dominio.decorator;

import java.util.List;
import main.dosw.empoleon.dominio.DroneEnterprise;

/**
 * Interfaz Component para el patrón Decorator, que modela las operaciones
 * de navegación, telemetría y ejecución de misiones de un dron en AquaPort Enterprise.
 */
public interface DroneOperacional {
    String getId();
    int getBateria();
    String getZona();
    int getCapacidadCargaGramos();
    boolean isDisponible();
    void consumirBateria(int porcentaje);
    boolean ejecutarMision(String misionId, String tramoId);
    List<String> getRegistroTelemetria();
    DroneEnterprise getDroneBase();
}
