package co.com.crediya.config;

import co.com.crediya.model.autenticacion.gateways.ClaveEncriptador;
import co.com.crediya.model.autenticacion.gateways.GeneradorToken;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.usecase.crearadministrador.CrearAdministradorUseCase;
import co.com.crediya.usecase.login.LoginUseCase;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

        @Bean
        public RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarioRepository,
                                                               ClaveEncriptador claveEncriptador) {
                return new RegistrarUsuarioUseCase(usuarioRepository, claveEncriptador);
        }

        @Bean
        public LoginUseCase loginUseCase(UsuarioRepository usuarioRepository,
                                         ClaveEncriptador claveEncriptador,
                                         GeneradorToken generadorToken) {
                return new LoginUseCase(usuarioRepository, claveEncriptador, generadorToken);
        }

        @Bean
        public CrearAdministradorUseCase crearAdministradorUseCase(UsuarioRepository usuarioRepository,
                                                                   ClaveEncriptador claveEncriptador) {
                return new CrearAdministradorUseCase(usuarioRepository, claveEncriptador);
        }
}
