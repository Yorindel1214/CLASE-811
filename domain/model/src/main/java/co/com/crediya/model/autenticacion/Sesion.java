package co.com.crediya.model.autenticacion;

import co.com.crediya.model.usuario.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Resultado de un inicio de sesión exitoso: el token y a quién pertenece.
 */
@Getter
@Builder
@AllArgsConstructor
public class Sesion {
    private final String token;
    private final long expiraEnSegundos;
    private final String correoElectronico;
    private final Rol rol;
}
