package main.dosw.empoleon.aplicacion;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import main.dosw.empoleon.dominio.CustodiaMuestra;
import main.dosw.empoleon.dominio.DroneEnterprise;
import main.dosw.empoleon.dominio.EstadoMisionEnterprise;
import main.dosw.empoleon.dominio.MisionEnterprise;
import main.dosw.empoleon.dominio.TramoRuta;
import main.dosw.empoleon.dominio.Waypoint;
import main.dosw.empoleon.dominio.adapter.CondicionesHidricasDominio;
import main.dosw.empoleon.dominio.adapter.ProveedorCondicionesHidricas;
import main.dosw.prinplup.EstadoDrone;

/**
 * Orquestador de aplicación para la ejecución de misiones multi-etapa con waypoints (AP-15).
 * Coordina flujo básico, flujos alternos (fallo, desvío, zona inactiva) y custodia.
 */
public class OrquestadorMisionMultiEtapa {

    private static final Logger LOGGER = Logger.getLogger(OrquestadorMisionMultiEtapa.class.getName());
    private static final int UMBRAL_BATERIA_MINIMO = 25;

    private final ProveedorCondicionesHidricas proveedorCondiciones;

    public OrquestadorMisionMultiEtapa(ProveedorCondicionesHidricas proveedorCondiciones) {
        this.proveedorCondiciones = Objects.requireNonNull(proveedorCondiciones, "El proveedor no puede ser nulo");
    }

    public boolean ejecutarMisionCompleta(
            MisionEnterprise mision,
            List<DroneEnterprise> flota,
            CustodiaMuestra custodia,
            List<String> zonasInactivas
    ) {
        validarParametros(mision, flota, custodia);
        mision.setEstado(EstadoMisionEnterprise.EN_TRANSITO);
        for (TramoRuta tramo : mision.getTramos()) {
            if (!procesarTramo(tramo, mision, flota, custodia, zonasInactivas)) {
                return false;
            }
        }
        mision.setEstado(EstadoMisionEnterprise.COMPLETADA);
        LOGGER.info(() -> "[AP-15] Misión completada exitosamente: " + mision.getId());
        return true;
    }

    private boolean procesarTramo(
            TramoRuta tramo,
            MisionEnterprise mision,
            List<DroneEnterprise> flota,
            CustodiaMuestra custodia,
            List<String> zonasInactivas
    ) {
        if (verificarZonaInactiva(mision, tramo.destino(), zonasInactivas)) {
            return false;
        }
        TramoRuta tramoAjustado = resolverCondicionesTramo(tramo);
        resolverSaludDrone(tramoAjustado, flota, custodia);
        custodia.registrarPasoWaypoint(tramoAjustado.destino().getId());
        return true;
    }

    private void validarParametros(MisionEnterprise m, List<DroneEnterprise> f, CustodiaMuestra c) {
        Objects.requireNonNull(m, "La misión no puede ser nula");
        Objects.requireNonNull(f, "La flota no puede ser nula");
        Objects.requireNonNull(c, "La custodia no puede ser nula");
        if (m.getTramos().isEmpty()) {
            throw new IllegalArgumentException("La misión debe contar con al menos un tramo de ruta");
        }
    }

    private boolean verificarZonaInactiva(
            MisionEnterprise mision,
            Waypoint destino,
            List<String> zonasInactivas
    ) {
        if (zonasInactivas != null && zonasInactivas.contains(destino.getZona())) {
            mision.setEstado(EstadoMisionEnterprise.EN_ESPERA_SEGURA);
            LOGGER.warning(() -> "[AP-15] Destino inactivo en " + destino.getZona() + ". Espera segura.");
            return true;
        }
        return false;
    }

    private TramoRuta resolverCondicionesTramo(TramoRuta tramo) {
        CondicionesHidricasDominio cond = proveedorCondiciones.consultarCondiciones(tramo.origen().getZona());
        if (cond.alturaOlasMetros() > 1.2 || !cond.esNavegable()) {
            LOGGER.info(() -> "[AP-15] Condiciones adversas en tramo " + tramo.id() + ". Desvío.");
            Waypoint wpDesvio = new Waypoint(tramo.destino().getId() + "-ALT",
                    tramo.destino().getNombre() + " (Canal Protegido)", tramo.destino().getZona(), tramo.destino().getOrden());
            return new TramoRuta(tramo.id() + "-DESVIO", tramo.origen(), wpDesvio,
                    tramo.distanciaMetros() + 200, tramo.droneAsignado());
        }
        return tramo;
    }

    private TramoRuta resolverSaludDrone(
            TramoRuta tramo,
            List<DroneEnterprise> flota,
            CustodiaMuestra custodia
    ) {
        DroneEnterprise actual = (DroneEnterprise) tramo.droneAsignado();
        if (actual == null || actual.getEstado() == EstadoDrone.FALLO || actual.getBateria() < UMBRAL_BATERIA_MINIMO) {
            DroneEnterprise reemplazo = buscarReemplazoEnZona(flota, tramo.origen().getZona(), actual);
            String idActual = actual != null ? actual.getId() : "NINGUNO";
            custodia.registrarTransferencia(tramo.origen().getId(), idActual, reemplazo.getId());
            LOGGER.info(() -> "[AP-15] Reasignación: " + idActual + " -> " + reemplazo.getId());
            return tramo.withDrone(reemplazo);
        }
        return tramo;
    }

    private DroneEnterprise buscarReemplazoEnZona(
            List<DroneEnterprise> flota,
            String zona,
            DroneEnterprise droneExcluido
    ) {
        Optional<DroneEnterprise> opt = flota.stream()
                .filter(DroneEnterprise::isDisponible)
                .filter(d -> d.getZona().equalsIgnoreCase(zona))
                .filter(d -> droneExcluido == null || !d.getId().equals(droneExcluido.getId()))
                .filter(d -> d.getBateria() >= UMBRAL_BATERIA_MINIMO)
                .findFirst();

        return opt.orElseGet(() -> flota.stream()
                .filter(DroneEnterprise::isDisponible)
                .filter(d -> d.getBateria() >= UMBRAL_BATERIA_MINIMO)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay drones disponibles para reasignación")));
    }
}
