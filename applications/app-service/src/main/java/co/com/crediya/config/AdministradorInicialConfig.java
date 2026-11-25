package co.com.crediya.config;

import co.com.crediya.usecase.crearadministrador.CrearAdministradorUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HU3: al arrancar crea el administrador inicial (si no existe) para poder iniciar sesión
 * y registrar a los demás usuarios. Los datos salen de application.yaml (crediya.admin.*).
 */
@Slf4j
@Configuration
public class AdministradorInicialConfig {

    @Bean
    public ApplicationRunner crearAdministradorInicial(CrearAdministradorUseCase useCase,
                                                       @Value("${crediya.admin.correo}") String correo,
                                                       @Value("${crediya.admin.documento}") String documento,
                                                       @Value("${crediya.admin.clave}") String clave) {
        return args -> {
            if (useCase.execute(correo, documento, clave)) {
                log.info("Administrador inicial creado: {}", correo);
            } else {
                log.info("El administrador inicial {} ya existe", correo);
            }
        };
    }
}
