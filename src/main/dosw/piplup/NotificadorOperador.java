package main.dosw.piplup;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Notificador de eventos al operador hídrico de AquaPort MVP.
 * Aplica SRP encargándose exclusivamente de emitir y formatear mensajes para el operador.
 */
public class NotificadorOperador {

    private static final Logger LOGGER = Logger.getLogger(NotificadorOperador.class.getName());

    public void notificarRegistroExitoso(Mision mision) {
        if (mision != null) {
            LOGGER.log(Level.INFO, "[AquaPort - Notificación] Misión {0} registrada exitosamente con drone {1} de ruta: {2} -> {3}",
                    new Object[]{mision.getId(), mision.getDrone().id(), mision.getPuntoPartida(), mision.getPuntoLlegada()});
        }
    }

    public void notificarError(String mensaje) {
        LOGGER.log(Level.SEVERE, "[AquaPort - Alerta] Error en operación: {0}", mensaje);
    }

    public void notificarConsulta(String detalle) {
        LOGGER.log(Level.INFO, "[AquaPort - Consulta] {0}", detalle);
    }
}
