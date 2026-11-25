package co.com.crediya.model.usuario.gateways;

import co.com.crediya.model.usuario.Usuario;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario guardar(Usuario usuario);
    boolean existePorCorreo(String correoElectronico);
    boolean existePorDocumento(String documentoIdentidad);
    Optional<Usuario> buscarPorCorreo(String correoElectronico);
    // HU4: el microservicio de Solicitudes pide los datos de varios clientes a la vez
    List<Usuario> buscarPorCorreos(Collection<String> correos);
}
