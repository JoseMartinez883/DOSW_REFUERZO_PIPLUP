package main.dosw.prinplup;

public record DroneAcuatico(
        String id,
        String modelo,
        TipoDrone tipo,
        int bateria,
        EstadoDrone estado,
        String zona,
        int capacidadCargaGramos
) {
    public boolean isDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }

    public TipoDrone getTipo() {
        return tipo;
    }

    public int getBateria() {
        return bateria;
    }

    public EstadoDrone getEstado() {
        return estado;
    }

    public String getZona() {
        return zona;
    }

    public int getCapacidadCargaGramos() {
        return capacidadCargaGramos;
    }
}
