package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final BigDecimal SALARIO_MAX = new BigDecimal("15000000");

    private final UsuarioRepository usuarioRepository;

    public Usuario execute(Usuario usuario) {
        validar(usuario);
        if (usuarioRepository.existePorCorreo(usuario.getCorreoElectronico().trim())) {
            throw new CorreoYaRegistradoException(usuario.getCorreoElectronico());
        }
        return usuarioRepository.guardar(usuario);
    }

    private void validar(Usuario u) {
        if (esVacio(u.getNombres()))
            throw new ValidacionException("El campo nombres es obligatorio");
        if (esVacio(u.getApellidos()))
            throw new ValidacionException("El campo apellidos es obligatorio");
        if (esVacio(u.getCorreoElectronico()))
            throw new ValidacionException("El campo correo_electronico es obligatorio");
        if (u.getSalarioBase() == null)
            throw new ValidacionException("El campo salario_base es obligatorio");
        if (!EMAIL.matcher(u.getCorreoElectronico().trim()).matches())
            throw new ValidacionException("El correo electrónico no tiene un formato válido");
        if (u.getSalarioBase().compareTo(BigDecimal.ZERO) < 0
                || u.getSalarioBase().compareTo(SALARIO_MAX) > 0)
            throw new ValidacionException("El salario_base debe estar entre 0 y 15.000.000");
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}