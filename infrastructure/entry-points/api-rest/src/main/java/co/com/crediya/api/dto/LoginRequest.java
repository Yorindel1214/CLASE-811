package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciales para iniciar sesión")
public record LoginRequest(
        @Schema(description = "Correo electrónico registrado", example = "admin@crediya.com")
        @JsonProperty("correo_electronico") String correoElectronico,
        @Schema(description = "Clave del usuario", example = "Admin12345")
        String clave) {
}
