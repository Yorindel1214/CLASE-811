package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Nunca incluye la clave.
 */
@Schema(description = "Datos del usuario registrado")
public record UsuarioResponse(
        @Schema(description = "Identificador generado por el sistema", example = "1")
        Long id,
        @Schema(description = "Nombres del usuario", example = "Ana María")
        String nombres,
        @Schema(description = "Apellidos del usuario", example = "Pérez Gómez")
        String apellidos,
        @Schema(description = "Fecha de nacimiento (AAAA-MM-DD)", example = "1995-03-10")
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,
        @Schema(description = "Dirección de residencia", example = "Calle 10 # 20-30")
        String direccion,
        @Schema(description = "Teléfono de contacto", example = "3001234567")
        String telefono,
        @Schema(description = "Correo electrónico", example = "ana@correo.com")
        @JsonProperty("correo_electronico") String correoElectronico,
        @Schema(description = "Salario base", example = "3500000")
        @JsonProperty("salario_base") BigDecimal salarioBase,
        @Schema(description = "Documento de identidad", example = "1023456789")
        @JsonProperty("documento_identidad") String documentoIdentidad,
        @Schema(description = "Rol del usuario", example = "CLIENTE")
        String rol) {
}
