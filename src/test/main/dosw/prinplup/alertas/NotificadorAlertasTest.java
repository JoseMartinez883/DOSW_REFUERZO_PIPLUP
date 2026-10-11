package main.dosw.prinplup.alertas;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificadorAlertasTest {

    @Mock
    private ObservadorMision mockCentroControl;

    @Mock
    private ObservadorMision mockTecnicoMantenimiento;

    private NotificadorAlertas notificador;
    private DroneAcuatico droneFallo;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorAlertas();
        droneFallo = new DroneAcuatico("DRN-001", "SubDeep-X", TipoDrone.BUCEADOR, 12, EstadoDrone.FALLO, "Bahía Norte", 500);
    }

    @Test
    @DisplayName("Debe registrar observadores y notificar a todos exactamente una vez ante un fallo")
    void debeNotificarATodosLosObservadoresExactamenteUnaVez() {
        notificador.registrarObservador(mockCentroControl);
        notificador.registrarObservador(mockTecnicoMantenimiento);

        assertEquals(2, notificador.getObservadores().size());

        notificador.emitirAlertaFallo(droneFallo, "Batería crítica menor al 15%");

        verify(mockCentroControl, times(1)).notificarFallo(droneFallo, "Batería crítica menor al 15%");
        verify(mockTecnicoMantenimiento, times(1)).notificarFallo(droneFallo, "Batería crítica menor al 15%");
    }

    @Test
    @DisplayName("No debe notificar a un observador que fue removido")
    void noDebeNotificarAObservadorRemovido() {
        notificador.registrarObservador(mockCentroControl);
        notificador.registrarObservador(mockTecnicoMantenimiento);
        notificador.removerObservador(mockCentroControl);

        notificador.emitirAlertaFallo(droneFallo, "Fallo de propulsor");

        verify(mockCentroControl, never()).notificarFallo(any(), any());
        verify(mockTecnicoMantenimiento, times(1)).notificarFallo(droneFallo, "Fallo de propulsor");
    }

    @Test
    @DisplayName("Los observadores concretos CentroControl y TecnicoMantenimiento deben ejecutar su notificación sin lanzar excepciones")
    void observadoresConcretosEjecutanSinExcepcion() {
        CentroControlObserver centro = new CentroControlObserver();
        TecnicoMantenimientoObserver tecnico = new TecnicoMantenimientoObserver();

        assertDoesNotThrow(() -> centro.notificarFallo(droneFallo, "Sensores anómalos"));
        assertDoesNotThrow(() -> tecnico.notificarFallo(droneFallo, "Sensores anómalos"));
    }
}
