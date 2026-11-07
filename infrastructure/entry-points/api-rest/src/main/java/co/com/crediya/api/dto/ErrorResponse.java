package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta cuando ocurre un error")
public record ErrorResponse(
        @Schema(description = "Mensaje que explica el error", example = "El campo nombres es obligatorio")
        String error) {
}