package co.com.crediya.model.usuario.exceptions;

public class DocumentoYaRegistradoException extends RuntimeException {
    public DocumentoYaRegistradoException(String documento) {
        super("El documento de identidad ya está registrado: " + documento);
    }
}
