package main.dosw.empoleon.dominio.decorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.prinplup.EstadoDrone;

/**
 * Concrete Component del patrón Decorator: envuelve un DroneEnterprise base.
 */
public class DroneOperacionalBase implements DroneOperacional {

    private final DroneEnterprise droneBase;
    private final List<String> registroTelemetria;

    public DroneOperacionalBase(DroneEnterprise droneBase) {
        this.droneBase = Objects.requireNonNull(droneBase, "El dron base no puede ser nulo");
        this.registroTelemetria = new ArrayList<>();
    }

    @Override
    public String getId() {
        return droneBase.getId();
    }

    @Override
    public int getBateria() {
        return droneBase.getBateria();
    }

    @Override
    public String getZona() {
        return droneBase.getZona();
    }

    @Override
    public int getCapacidadCargaGramos() {
        return droneBase.getCapacidadCargaGramos();
    }

    @Override
    public boolean isDisponible() {
        return droneBase.isDisponible();
    }

    @Override
    public void consumirBateria(int porcentaje) {
        int nuevaBateria = Math.max(0, droneBase.getBateria() - porcentaje);
        droneBase.setBateria(nuevaBateria);
        if (nuevaBateria == 0) {
            droneBase.setEstado(EstadoDrone.MANTENIMIENTO);
        }
    }

    @Override
    public boolean ejecutarMision(String misionId, String tramoId) {
        if (!isDisponible()) {
            return false;
        }
        consumirBateria(10);
        droneBase.incrementarEntregasExitosas();
        return true;
    }

    @Override
    public List<String> getRegistroTelemetria() {
        return Collections.unmodifiableList(registroTelemetria);
    }

    @Override
    public DroneEnterprise getDroneBase() {
        return droneBase;
    }

    protected void agregarLog(String mensaje) {
        this.registroTelemetria.add(mensaje);
    }
}
