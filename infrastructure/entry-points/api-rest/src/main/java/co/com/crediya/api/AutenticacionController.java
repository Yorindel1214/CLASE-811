package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.model.autenticacion.Sesion;
import co.com.crediya.usecase.login.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión")
public class AutenticacionController {

    private final LoginUseCase loginUseCase;

    @PostMapping
    @SecurityRequirements // público: no necesita token
    @Operation(
            summary = "Iniciar sesión",
            description = "Valida correo y clave. Si son correctos devuelve un JWT con el rol del usuario, "
                    + "que debe enviarse en la cabecera Authorization: Bearer <token>. No hay límite de intentos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión iniciada",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Correo o clave vacíos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Correo o clave incorrectos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public LoginResponse login(@RequestBody LoginRequest request) {
        log.info("POST /api/v1/login recibido para {}", request.correoElectronico());
        Sesion sesion = loginUseCase.execute(request.correoElectronico(), request.clave());
        log.info("Sesión iniciada para {} con rol {}", sesion.getCorreoElectronico(), sesion.getRol());
        return new LoginResponse(
                sesion.getToken(),
                "Bearer",
                sesion.getExpiraEnSegundos(),
                sesion.getCorreoElectronico(),
                sesion.getRol().name());
    }
}
