package co.com.crediya.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JPARepository extends JpaRepository<UsuarioEntity, Long> {
    List<UsuarioEntity> findByCorreoElectronicoIn(Collection<String> correos);
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
    Optional<UsuarioEntity> findByCorreoElectronico(String correoElectronico);
}
