package main.dosw.empoleon.dominio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio pura que representa la cadena de custodia criptográfica
 * para muestras y sensores en rutas multi-etapa con waypoints.
 */
public class CustodiaMuestra {

    private final String idMuestra;
    private final String hashOrigen;
    private final List<String> registrosTrazabilidad;
    private String hashActual;

    public CustodiaMuestra(String idMuestra, String hashOrigen) {
        this.idMuestra = Objects.requireNonNull(idMuestra, "El id de muestra no puede ser nulo");
        this.hashOrigen = Objects.requireNonNull(hashOrigen, "El hash de origen no puede ser nulo");
        this.hashActual = hashOrigen;
        this.registrosTrazabilidad = new ArrayList<>();
        this.registrosTrazabilidad.add("SELLADO_ORIGEN:" + hashOrigen);
    }

    public static String generarHash(String datos) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(datos.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 no disponible", e);
        }
    }

    public void registrarTransferencia(String waypointId, String droneSaliente, String droneEntrante) {
        String nuevoHash = generarHash(this.hashActual + ":" + waypointId + ":" + droneEntrante);
        this.hashActual = nuevoHash;
        this.registrosTrazabilidad.add("TRANSFERENCIA:" + waypointId + ":" + droneSaliente + "->"
                + droneEntrante + ":" + nuevoHash);
    }

    public void registrarPasoWaypoint(String waypointId) {
        String nuevoHash = generarHash(this.hashActual + ":" + waypointId);
        this.hashActual = nuevoHash;
        this.registrosTrazabilidad.add("WAYPOINT_VERIFICADO:" + waypointId + ":" + nuevoHash);
    }

    public boolean verificarIntegridad(String hashEsperado) {
        return Objects.equals(this.hashActual, hashEsperado);
    }

    public String getIdMuestra() {
        return idMuestra;
    }

    public String getHashOrigen() {
        return hashOrigen;
    }

    public String getHashActual() {
        return hashActual;
    }

    public List<String> getRegistrosTrazabilidad() {
        return Collections.unmodifiableList(registrosTrazabilidad);
    }
}
