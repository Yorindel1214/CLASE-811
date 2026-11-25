package co.com.crediya.usecase.buscarusuarios;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * HU4: consultar varios usuarios por correo. Lo usa el microservicio de Solicitudes para
 * mostrarle al asesor el nombre y el salario base de cada cliente.
 */
@RequiredArgsConstructor
public class BuscarUsuariosUseCase {

    private static final int MAXIMO_CORREOS = 100;

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> execute(Collection<String> correos) {
        if (correos == null || correos.isEmpty())
            throw new ValidacionException("Debe enviar al menos un correo en el parámetro correos");
        List<String> limpios = correos.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .distinct()
                .toList();
        if (limpios.isEmpty())
            throw new ValidacionException("Debe enviar al menos un correo en el parámetro correos");
        if (limpios.size() > MAXIMO_CORREOS)
            throw new ValidacionException("Puede consultar máximo " + MAXIMO_CORREOS + " correos a la vez");
        return usuarioRepository.buscarPorCorreos(limpios);
    }
}
