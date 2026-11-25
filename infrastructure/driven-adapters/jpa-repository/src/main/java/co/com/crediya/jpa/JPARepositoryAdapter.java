package co.com.crediya.jpa;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements UsuarioRepository {

    private final JPARepository repository;

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        log.info("Guardando usuario con correo {} y rol {}", usuario.getCorreoElectronico(), usuario.getRol());
        try {
            UsuarioEntity guardado = repository.saveAndFlush(aEntidad(usuario));
            log.info("Usuario guardado con id {}", guardado.getId());
            return aDominio(guardado);
        } catch (DataIntegrityViolationException e) {
            log.warn("Violación de integridad al guardar usuario: {}", e.getMessage());
            throw new CorreoYaRegistradoException(usuario.getCorreoElectronico());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCorreo(String correoElectronico) {
        return repository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDocumento(String documentoIdentidad) {
        return repository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correoElectronico) {
        log.info("Buscando usuario con correo {}", correoElectronico);
        return repository.findByCorreoElectronico(correoElectronico).map(this::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorCorreos(Collection<String> correos) {
        log.info("Buscando {} usuarios por correo", correos.size());
        return repository.findByCorreoElectronicoIn(correos).stream().map(this::aDominio).toList();
    }

    private UsuarioEntity aEntidad(Usuario u) {
        return new UsuarioEntity(
                u.getId(),
                u.getNombres(),
                u.getApellidos(),
                u.getFechaNacimiento(),
                u.getDireccion(),
                u.getTelefono(),
                u.getCorreoElectronico(),
                u.getSalarioBase(),
                u.getDocumentoIdentidad(),
                u.getClave(),
                u.getRol());
    }

    private Usuario aDominio(UsuarioEntity e) {
        return Usuario.builder()
                .id(e.getId())
                .nombres(e.getNombres())
                .apellidos(e.getApellidos())
                .fechaNacimiento(e.getFechaNacimiento())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .correoElectronico(e.getCorreoElectronico())
                .salarioBase(e.getSalarioBase())
                .documentoIdentidad(e.getDocumentoIdentidad())
                .clave(e.getClave())
                .rol(e.getRol())
                .build();
    }
}
