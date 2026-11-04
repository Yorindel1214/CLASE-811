package co.com.crediya.model.usuario.exceptions;

public class CorreoYaRegistradoException extends RuntimeException {
    public CorreoYaRegistradoException(String correo) {
        super("El correo electrónico ya está registrado: " + correo);
    }
}