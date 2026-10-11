package main.dosw.empoleon.dominio.decorator;

/**
 * Concrete Decorator 1: Agrega registro transparente de telemetría en tiempo real
 * a cada navegación y consumo de energía del dron.
 */
public class DroneConMonitoreo extends DroneDecorator {

    public DroneConMonitoreo(DroneOperacional decorado) {
        super(decorado);
    }

    @Override
    public boolean ejecutarMision(String misionId, String tramoId) {
        registrarLog("[TELEMETRIA] Dron " + getId() + " en zona '" + getZona()
                + "' iniciando mision " + misionId + " tramo " + tramoId + " (Bateria: " + getBateria() + "%)");
        boolean resultado = super.ejecutarMision(misionId, tramoId);
        registrarLog("[TELEMETRIA] Finalizado tramo " + tramoId + " con resultado: "
                + (resultado ? "EXITOSO" : "RECHAZADO"));
        return resultado;
    }

    @Override
    public void consumirBateria(int porcentaje) {
        registrarLog("[TELEMETRIA] Registrado consumo de " + porcentaje + "% de bateria en " + getId());
        super.consumirBateria(porcentaje);
    }
}
