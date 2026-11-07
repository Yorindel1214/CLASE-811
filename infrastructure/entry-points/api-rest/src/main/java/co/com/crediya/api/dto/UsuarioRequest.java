package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos para registrar un solicitante")
public record UsuarioRequest(
        @Schema(description = "Nombres del solicitante", example = "Ana María")
        String nombres,
        @Schema(description = "Apellidos del solicitante", example = "Pérez Gómez")
        String apellidos,
        @Schema(description = "Fecha de nacimiento (AAAA-MM-DD)", example = "1995-03-10")
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,
        @Schema(description = "Dirección de residencia", example = "Calle 10 # 20-30")
        String direccion,
        @Schema(description = "Teléfono de contacto", example = "3001234567")
        String telefono,
        @Schema(description = "Correo electrónico (único en el sistema)", example = "ana@correo.com")
        @JsonProperty("correo_electronico") String correoElectronico,
        @Schema(description = "Salario base, entre 0 y 15.000.000", example = "3500000")
        @JsonProperty("salario_base") BigDecimal salarioBase) {
}