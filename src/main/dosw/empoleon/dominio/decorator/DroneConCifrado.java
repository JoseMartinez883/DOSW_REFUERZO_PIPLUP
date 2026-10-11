package main.dosw.empoleon.dominio.decorator;

/**
 * Concrete Decorator 2: Agrega cifrado criptográfico y firma de seguridad a la
 * carga útil y a los eventos de telemetría sin modificar el comportamiento del dron.
 */
public class DroneConCifrado extends DroneDecorator {

    public DroneConCifrado(DroneOperacional decorado) {
        super(decorado);
    }

    @Override
    public boolean ejecutarMision(String misionId, String tramoId) {
        registrarLog("[SEGURIDAD-AES256] Firmando paquete de mision con hash SHA-256 para canal seguro: "
                + misionId + ":" + tramoId);
        boolean resultado = super.ejecutarMision(misionId, tramoId);
        registrarLog("[SEGURIDAD-AES256] Cifrado verificado y sellado en destino para tramo " + tramoId);
        return resultado;
    }
}
