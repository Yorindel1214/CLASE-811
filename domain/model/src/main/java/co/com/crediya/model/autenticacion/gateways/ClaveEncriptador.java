package co.com.crediya.model.autenticacion.gateways;

/**
 * Puerto de salida: cifrar claves y comprobarlas sin guardar nunca la clave en texto plano.
 */
public interface ClaveEncriptador {
    String encriptar(String claveEnTextoPlano);
    boolean coincide(String claveEnTextoPlano, String claveEncriptada);
}
