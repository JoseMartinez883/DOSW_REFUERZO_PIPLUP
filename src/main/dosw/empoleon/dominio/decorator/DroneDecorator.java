package main.dosw.empoleon.dominio.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import main.dosw.empoleon.dominio.DroneEnterprise;

/**
 * Clase Decorator abstracta que implementa DroneOperacional delegando al objeto envuelto.
 */
public abstract class DroneDecorator implements DroneOperacional {

    protected final DroneOperacional decorado;
    private final List<String> logsPropios;

    protected DroneDecorator(DroneOperacional decorado) {
        this.decorado = Objects.requireNonNull(decorado, "El componente decorado no puede ser nulo");
        this.logsPropios = new ArrayList<>();
    }

    @Override
    public String getId() {
        return decorado.getId();
    }

    @Override
    public int getBateria() {
        return decorado.getBateria();
    }

    @Override
    public String getZona() {
        return decorado.getZona();
    }

    @Override
    public int getCapacidadCargaGramos() {
        return decorado.getCapacidadCargaGramos();
    }

    @Override
    public boolean isDisponible() {
        return decorado.isDisponible();
    }

    @Override
    public void consumirBateria(int porcentaje) {
        decorado.consumirBateria(porcentaje);
    }

    @Override
    public boolean ejecutarMision(String misionId, String tramoId) {
        return decorado.ejecutarMision(misionId, tramoId);
    }

    @Override
    public List<String> getRegistroTelemetria() {
        List<String> combinados = new ArrayList<>(decorado.getRegistroTelemetria());
        combinados.addAll(logsPropios);
        return List.copyOf(combinados);
    }

    @Override
    public DroneEnterprise getDroneBase() {
        return decorado.getDroneBase();
    }

    protected void registrarLog(String mensaje) {
        this.logsPropios.add(mensaje);
    }
}
