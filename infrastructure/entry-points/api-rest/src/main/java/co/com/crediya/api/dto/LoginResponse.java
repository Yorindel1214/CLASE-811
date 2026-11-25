package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sesión iniciada")
public record LoginResponse(
        @Schema(description = "JWT para enviar en la cabecera Authorization: Bearer <token>")
        String token,
        @Schema(description = "Tipo de token", example = "Bearer")
        @JsonProperty("tipo_token") String tipoToken,
        @Schema(description = "Segundos que dura el token", example = "3600")
        @JsonProperty("expira_en") long expiraEn,
        @Schema(description = "Correo del usuario", example = "admin@crediya.com")
        @JsonProperty("correo_electronico") String correoElectronico,
        @Schema(description = "Rol del usuario", example = "ADMIN")
        String rol) {
}
