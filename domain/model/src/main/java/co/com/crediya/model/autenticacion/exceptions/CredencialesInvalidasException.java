package co.com.crediya.model.autenticacion.exceptions;

/**
 * Correo o clave incorrectos. El mensaje no dice cuál de los dos falló, a propósito.
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Correo o clave incorrectos");
    }
}
