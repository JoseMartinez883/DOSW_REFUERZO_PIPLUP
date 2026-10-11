package main.dosw.prinplup;

import java.util.Objects;

/**
 * Clase base del dominio Prinplup que representa un drone acuático autónomo.
 * Permite extensión para especializaciones por Factory Method.
 */
public class DroneAcuatico {
    private final String id;
    private final String modelo;
    private final TipoDrone tipo;
    private int bateria;
    private EstadoDrone estado;
    private String zona;
    private final int capacidadCargaGramos;

    public DroneAcuatico(
            String id,
            String modelo,
            TipoDrone tipo,
            int bateria,
            EstadoDrone estado,
            String zona,
            int capacidadCargaGramos
    ) {
        this.id = id;
        this.modelo = modelo;
        this.tipo = tipo;
        this.bateria = bateria;
        this.estado = estado;
        this.zona = zona;
        this.capacidadCargaGramos = capacidadCargaGramos;
    }

    public String id() { return id; }
    public String getId() { return id; }

    public String modelo() { return modelo; }
    public String getModelo() { return modelo; }

    public TipoDrone tipo() { return tipo; }
    public TipoDrone getTipo() { return tipo; }

    public int bateria() { return bateria; }
    public int getBateria() { return bateria; }
    public void setBateria(int bateria) { this.bateria = bateria; }

    public EstadoDrone estado() { return estado; }
    public EstadoDrone getEstado() { return estado; }
    public void setEstado(EstadoDrone estado) { this.estado = estado; }

    public String zona() { return zona; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public int capacidadCargaGramos() { return capacidadCargaGramos; }
    public int getCapacidadCargaGramos() { return capacidadCargaGramos; }

    public boolean isDisponible() {
        return estado == EstadoDrone.DISPONIBLE;
    }

    public boolean puedeCargar(int pesoGramos) {
        return pesoGramos <= this.capacidadCargaGramos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroneAcuatico that = (DroneAcuatico) o;
        return bateria == that.bateria &&
                capacidadCargaGramos == that.capacidadCargaGramos &&
                Objects.equals(id, that.id) &&
                Objects.equals(modelo, that.modelo) &&
                tipo == that.tipo &&
                estado == that.estado &&
                Objects.equals(zona, that.zona);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modelo, tipo, bateria, estado, zona, capacidadCargaGramos);
    }

    @Override
    public String toString() {
        return "DroneAcuatico[id=" + id +
                ", modelo=" + modelo +
                ", tipo=" + tipo +
                ", bateria=" + bateria +
                ", estado=" + estado +
                ", zona=" + zona +
                ", capacidadCargaGramos=" + capacidadCargaGramos + "]";
    }
}
