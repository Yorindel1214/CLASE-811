package co.com.crediya.usecase.crearadministrador;

import co.com.crediya.model.autenticacion.gateways.ClaveEncriptador;
import co.com.crediya.model.usuario.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/**
 * HU3: como registrar usuarios exige ser ADMIN o ASESOR, alguien tiene que existir primero.
 * Al arrancar la aplicación se crea un administrador inicial, solo si aún no existe.
 */
@RequiredArgsConstructor
public class CrearAdministradorUseCase {

    private final UsuarioRepository usuarioRepository;
    private final ClaveEncriptador claveEncriptador;

    /**
     * @return true si lo creó, false si ya existía
     */
    public boolean execute(String correo, String documento, String clave) {
        if (usuarioRepository.existePorCorreo(correo)) {
            return false;
        }
        usuarioRepository.guardar(Usuario.builder()
                .nombres("Administrador")
                .apellidos("CrediYa")
                .correoElectronico(correo)
                .documentoIdentidad(documento)
                .salarioBase(BigDecimal.ZERO)
                .clave(claveEncriptador.encriptar(clave))
                .rol(Rol.ADMIN)
                .build());
        return true;
    }
}
