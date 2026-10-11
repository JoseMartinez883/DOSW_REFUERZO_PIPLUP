package main.dosw.empoleon.dominio.adapter;

/**
 * Puerto del dominio para consultar condiciones ambientales de zonas acuáticas.
 */
public interface ProveedorCondicionesHidricas {
    CondicionesHidricasDominio consultarCondiciones(String zona);
}
