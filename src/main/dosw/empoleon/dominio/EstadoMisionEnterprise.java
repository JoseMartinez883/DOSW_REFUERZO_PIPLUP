package main.dosw.empoleon.dominio;

/**
 * Estados del ciclo de vida de una misión multi-etapa en AquaPort Enterprise.
 */
public enum EstadoMisionEnterprise {
    PLANIFICADA,
    EN_TRANSITO,
    EN_ESPERA_SEGURA,
    COMPLETADA,
    FALLIDA,
    CANCELADA
}
