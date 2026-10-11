package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;
import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto observable que centraliza y despacha las alertas de fallos a todos los observadores registrados.
 */
public class NotificadorAlertas {

    private final List<ObservadorMision> observadores = new ArrayList<>();

    public void registrarObservador(ObservadorMision observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(ObservadorMision observador) {
        observadores.remove(observador);
    }

    public void emitirAlertaFallo(DroneAcuatico drone, String motivo) {
        for (ObservadorMision observador : observadores) {
            observador.notificarFallo(drone, motivo);
        }
    }

    public List<ObservadorMision> getObservadores() {
        return List.copyOf(observadores);
    }
}
