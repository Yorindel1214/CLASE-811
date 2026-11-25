package co.com.crediya.api.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HU3: reglas de acceso del microservicio de Autenticación.
 * - POST /api/v1/login es público (ahí se obtiene el token).
 * - POST /api/v1/usuarios solo para ADMIN o ASESOR.
 * - Todo lo demás exige un token válido (Authorization: Bearer ...).
 * Sin sesión en servidor (STATELESS): cada petición trae su token.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] RUTAS_PUBLICAS = {
            "/actuator/health/**", "/actuator/prometheus",
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                        .requestMatchers(RUTAS_PUBLICAS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").hasAnyRole("ADMIN", "ASESOR")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(convertidorDeRoles()))
                        .authenticationEntryPoint(noAutenticado()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(noAutenticado())
                        .accessDeniedHandler(sinPermiso()));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secreto}") String secreto) {
        SecretKeySpec llave = new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(llave).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /** El claim "rol" del token (ADMIN, ASESOR, CLIENTE) se convierte en ROLE_ADMIN, ROLE_ASESOR... */
    private JwtAuthenticationConverter convertidorDeRoles() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(roles);
        return convertidor;
    }

    /** 401: no envió token, o el token es inválido o ya expiró. */
    private AuthenticationEntryPoint noAutenticado() {
        return (request, response, excepcion) -> {
            boolean traeToken = request.getHeader(HttpHeaders.AUTHORIZATION) != null;
            log.warn("Acceso no autenticado a {} {}: {}", request.getMethod(), request.getRequestURI(),
                    excepcion.getMessage());
            escribirError(response, HttpServletResponse.SC_UNAUTHORIZED, traeToken
                    ? "El token es inválido o ha expirado. Inicie sesión nuevamente"
                    : "Debe iniciar sesión para acceder a este recurso");
        };
    }

    /** 403: el token es válido pero el rol no tiene permiso. */
    private AccessDeniedHandler sinPermiso() {
        return (request, response, excepcion) -> {
            log.warn("Acceso denegado a {} {}", request.getMethod(), request.getRequestURI());
            escribirError(response, HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para realizar esta acción");
        };
    }

    private static void escribirError(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }
}
