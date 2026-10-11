package main.dosw.empoleon.dominio.cadena;

import java.util.Objects;

/**
 * Resultado inmutable emitido por la cadena de validación de misiones Enterprise.
 */
public record ResultadoValidacion(boolean exitoso, String motivo, String validadorFalla) {

    public ResultadoValidacion {
        Objects.requireNonNull(motivo, "El motivo no puede ser nulo");
    }

    public static ResultadoValidacion exito() {
        return new ResultadoValidacion(true, "Validación exitosa en todos los eslabones", null);
    }

    public static ResultadoValidacion fallo(String motivo, String validadorFalla) {
        return new ResultadoValidacion(false, motivo, validadorFalla);
    }
}
