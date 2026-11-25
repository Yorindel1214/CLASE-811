package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.autenticacion.gateways.ClaveEncriptador;
import co.com.crediya.model.usuario.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.AccesoDenegadoException;
import co.com.crediya.model.usuario.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.usuario.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * HU1: registrar usuarios.
 * HU3: solo un ADMIN o un ASESOR puede registrar; un ASESOR solo puede registrar CLIENTES.
 * La clave se guarda cifrada, nunca en texto plano.
 */
@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern DOCUMENTO = Pattern.compile("^\\d{6,15}$");
    private static final BigDecimal SALARIO_MAX = new BigDecimal("15000000");
    private static final int CLAVE_MINIMA = 8;

    private final UsuarioRepository usuarioRepository;
    private final ClaveEncriptador claveEncriptador;

    /**
     * @param usuario        datos del nuevo usuario (si no trae rol, se registra como CLIENTE)
     * @param rolSolicitante rol de quien está llamando el servicio (sale del token)
     */
    public Usuario execute(Usuario usuario, Rol rolSolicitante) {
        Rol rolNuevo = usuario.getRol() == null ? Rol.CLIENTE : usuario.getRol();
        validarPermiso(rolSolicitante, rolNuevo);
        validar(usuario);

        String correo = usuario.getCorreoElectronico().trim();
        String documento = usuario.getDocumentoIdentidad().trim();
        if (usuarioRepository.existePorCorreo(correo)) {
            throw new CorreoYaRegistradoException(correo);
        }
        if (usuarioRepository.existePorDocumento(documento)) {
            throw new DocumentoYaRegistradoException(documento);
        }

        Usuario nuevo = usuario.toBuilder()
                .correoElectronico(correo)
                .documentoIdentidad(documento)
                .clave(claveEncriptador.encriptar(usuario.getClave()))
                .rol(rolNuevo)
                .build();
        return usuarioRepository.guardar(nuevo);
    }

    private void validarPermiso(Rol rolSolicitante, Rol rolNuevo) {
        if (rolSolicitante != Rol.ADMIN && rolSolicitante != Rol.ASESOR) {
            throw new AccesoDenegadoException("Solo un administrador o un asesor puede registrar usuarios");
        }
        if (rolSolicitante == Rol.ASESOR && rolNuevo != Rol.CLIENTE) {
            throw new AccesoDenegadoException("Un asesor solo puede registrar usuarios con rol CLIENTE");
        }
    }

    private void validar(Usuario u) {
        if (esVacio(u.getNombres()))
            throw new ValidacionException("El campo nombres es obligatorio");
        if (esVacio(u.getApellidos()))
            throw new ValidacionException("El campo apellidos es obligatorio");
        if (esVacio(u.getCorreoElectronico()))
            throw new ValidacionException("El campo correo_electronico es obligatorio");
        if (esVacio(u.getDocumentoIdentidad()))
            throw new ValidacionException("El campo documento_identidad es obligatorio");
        if (esVacio(u.getClave()))
            throw new ValidacionException("El campo clave es obligatorio");
        if (u.getSalarioBase() == null)
            throw new ValidacionException("El campo salario_base es obligatorio");
        if (!EMAIL.matcher(u.getCorreoElectronico().trim()).matches())
            throw new ValidacionException("El correo electrónico no tiene un formato válido");
        if (!DOCUMENTO.matcher(u.getDocumentoIdentidad().trim()).matches())
            throw new ValidacionException("El documento de identidad debe tener solo números, entre 6 y 15 dígitos");
        if (u.getClave().length() < CLAVE_MINIMA)
            throw new ValidacionException("La clave debe tener al menos " + CLAVE_MINIMA + " caracteres");
        if (u.getSalarioBase().compareTo(BigDecimal.ZERO) < 0
                || u.getSalarioBase().compareTo(SALARIO_MAX) > 0)
            throw new ValidacionException("El salario_base debe estar entre 0 y 15.000.000");
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
