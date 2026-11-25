package co.com.crediya.model.usuario.exceptions;

/**
 * El usuario inició sesión, pero su rol no le permite hacer la acción.
 */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) { super(mensaje); }
}
