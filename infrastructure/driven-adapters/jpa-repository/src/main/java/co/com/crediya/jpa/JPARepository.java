package co.com.crediya.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JPARepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByCorreoElectronico(String correoElectronico);
}