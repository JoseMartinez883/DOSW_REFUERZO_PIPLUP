package main.dosw.empoleon.dominio;

import main.dosw.prinplup.DroneAcuatico;
import main.dosw.prinplup.EstadoDrone;
import main.dosw.prinplup.TipoDrone;

/**
 * Entidad del dominio Enterprise que extiende a DroneAcuatico incorporando
 * el historial acumulado de entregas exitosas y métricas de rendimiento.
 */
public class DroneEnterprise extends DroneAcuatico {

    private int entregasExitosas;

    @SuppressWarnings("java:S107")
    public DroneEnterprise(
            String id,
            String modelo,
            TipoDrone tipo,
            int bateria,
            EstadoDrone estado,
            String zona,
            int capacidadCargaGramos,
            int entregasExitosas
    ) {
        super(id, modelo, tipo, bateria, estado, zona, capacidadCargaGramos);
        if (entregasExitosas < 0) {
            throw new IllegalArgumentException("Las entregas exitosas no pueden ser negativas");
        }
        this.entregasExitosas = entregasExitosas;
    }

    public int getEntregasExitosas() {
        return entregasExitosas;
    }

    public void setEntregasExitosas(int entregasExitosas) {
        if (entregasExitosas < 0) {
            throw new IllegalArgumentException("Las entregas exitosas no pueden ser negativas");
        }
        this.entregasExitosas = entregasExitosas;
    }

    public void incrementarEntregasExitosas() {
        this.entregasExitosas++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        DroneEnterprise that = (DroneEnterprise) o;
        return entregasExitosas == that.entregasExitosas;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), entregasExitosas);
    }
}
