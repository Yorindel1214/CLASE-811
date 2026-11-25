package co.com.crediya.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI crediyaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crediya - Microservicio de Autenticación")
                        .description("API para iniciar sesión (JWT) y registrar usuarios. "
                                + "Use el botón Authorize con el token que devuelve /api/v1/login")
                        .version("1.1.0"))
                // Botón "Authorize" de Swagger para enviar el token en cada petición
                .components(new Components().addSecuritySchemes(ESQUEMA, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
