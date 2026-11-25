package co.com.crediya.usecase.login;

import co.com.crediya.model.autenticacion.Sesion;
import co.com.crediya.model.autenticacion.exceptions.CredencialesInvalidasException;
import co.com.crediya.model.autenticacion.gateways.ClaveEncriptador;
import co.com.crediya.model.autenticacion.gateways.GeneradorToken;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * HU3: iniciar sesión con correo y clave.
 * Si son correctos devuelve un token con el rol del usuario.
 * No hay límite de intentos (criterio de aceptación): un intento fallido solo responde 401.
 */
@RequiredArgsConstructor
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final ClaveEncriptador claveEncriptador;
    private final GeneradorToken generadorToken;

    public Sesion execute(String correo, String clave) {
        if (correo == null || correo.isBlank())
            throw new ValidacionException("El campo correo_electronico es obligatorio");
        if (clave == null || clave.isBlank())
            throw new ValidacionException("El campo clave es obligatorio");

        Usuario usuario = usuarioRepository.buscarPorCorreo(correo.trim())
                .orElseThrow(CredencialesInvalidasException::new);

        // Usuarios creados antes de la HU3 no tienen clave ni rol: no pueden iniciar sesión
        if (usuario.getClave() == null || usuario.getRol() == null
                || !claveEncriptador.coincide(clave, usuario.getClave())) {
            throw new CredencialesInvalidasException();
        }
        return generadorToken.generar(usuario);
    }
}
