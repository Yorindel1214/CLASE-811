package co.com.crediya.config;

import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

        @Bean
        public RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarioRepository) {
                return new RegistrarUsuarioUseCase(usuarioRepository);
        }
}