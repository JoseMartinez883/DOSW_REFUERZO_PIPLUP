package main.dosw.prinplup;

public record Mision(
        String id,
        DroneAcuatico drone,
        String puntoPartida,
        String puntoLlegada,
        Prioridad prioridad,
        String tipoCarga,
        int pesoCargaGramos
) {
    public Prioridad getPrioridad() {
        return prioridad;
    }

    public DroneAcuatico getDrone() {
        return drone;
    }

    public String getId() {
        return id;
    }

    public String getPuntoPartida() {
        return puntoPartida;
    }

    public String getPuntoLlegada() {
        return puntoLlegada;
    }

    public String getTipoCarga() {
        return tipoCarga;
    }

    public int getPesoCargaGramos() {
        return pesoCargaGramos;
    }

    public String zonaDestino() {
        return puntoLlegada;
    }

    public int pesoGramos() {
        return pesoCargaGramos;
    }
}
