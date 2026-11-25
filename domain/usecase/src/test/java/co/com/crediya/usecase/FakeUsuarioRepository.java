package co.com.crediya.usecase;

import co.com.crediya.model.autenticacion.gateways.ClaveEncriptador;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria para probar los casos de uso sin base de datos.
 */
public class FakeUsuarioRepository implements UsuarioRepository {

    public final List<Usuario> guardados = new ArrayList<>();

    @Override
    public Usuario guardar(Usuario usuario) {
        Usuario conId = usuario.toBuilder().id((long) guardados.size() + 1).build();
        guardados.add(conId);
        return conId;
    }

    @Override
    public boolean existePorCorreo(String correoElectronico) {
        return guardados.stream().anyMatch(u -> correoElectronico.equals(u.getCorreoElectronico()));
    }

    @Override
    public boolean existePorDocumento(String documentoIdentidad) {
        return guardados.stream().anyMatch(u -> documentoIdentidad.equals(u.getDocumentoIdentidad()));
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correoElectronico) {
        return guardados.stream().filter(u -> correoElectronico.equals(u.getCorreoElectronico())).findFirst();
    }

    @Override
    public List<Usuario> buscarPorCorreos(Collection<String> correos) {
        return guardados.stream().filter(u -> correos.contains(u.getCorreoElectronico())).toList();
    }

    /** "Cifra" anteponiendo un prefijo: suficiente para verificar que no se guarda en texto plano. */
    public static class FakeClaveEncriptador implements ClaveEncriptador {
        @Override
        public String encriptar(String claveEnTextoPlano) {
            return "cifrada:" + claveEnTextoPlano;
        }

        @Override
        public boolean coincide(String claveEnTextoPlano, String claveEncriptada) {
            return ("cifrada:" + claveEnTextoPlano).equals(claveEncriptada);
        }
    }
}
