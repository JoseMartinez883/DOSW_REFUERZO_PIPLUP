package main.dosw.piplup;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para las misiones de AquaPort.
 * Aplica el Principio de Inversión de Dependencias (DIP) desacoplando la lógica
 * de negocio de cualquier mecanismo de almacenamiento concreto.
 */
public interface RepositorioMisiones {

    void guardar(Mision mision);

    Optional<Mision> buscarPorId(String id);

    List<Mision> obtenerTodas();

    boolean existePorId(String id);
}
