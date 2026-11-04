package co.com.crediya.jpa;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JPARepositoryAdapter implements UsuarioRepository {

    private final JPARepository repository;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        log.info("Guardando usuario con correo {}", usuario.getCorreoElectronico());
        try {
            UsuarioEntity guardado = repository.saveAndFlush(mapper.map(usuario, UsuarioEntity.class));
            log.info("Usuario guardado con id {}", guardado.getId());
            return mapper.map(guardado, Usuario.class);
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
}