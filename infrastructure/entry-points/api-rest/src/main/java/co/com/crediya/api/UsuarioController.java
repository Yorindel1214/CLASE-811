package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.dto.UsuarioRequest;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.model.usuario.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.AccesoDenegadoException;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.usecase.buscarusuarios.BuscarUsuariosUseCase;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios")
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final BuscarUsuariosUseCase buscarUsuariosUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar un usuario (ADMIN o ASESOR)",
            description = "Crea un nuevo usuario. Requiere token de un ADMIN o un ASESOR; un ASESOR solo puede crear "
                    + "CLIENTES. Valida campos obligatorios, formato de correo y documento, que no estén registrados, "
                    + "clave de mínimo 8 caracteres y salario entre 0 y 15.000.000.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o campos obligatorios vacíos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No envió token o el token no es válido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso para registrar ese usuario",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El correo o el documento ya está registrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public UsuarioResponse registrar(@RequestBody UsuarioRequest request, @AuthenticationPrincipal Jwt token) {
        Rol rolSolicitante = rolDelToken(token);
        log.info("POST /api/v1/usuarios recibido de {} ({})", token.getSubject(), rolSolicitante);
        Usuario usuario = Usuario.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .correoElectronico(request.correoElectronico())
                .salarioBase(request.salarioBase())
                .documentoIdentidad(request.documentoIdentidad())
                .clave(request.clave())
                .rol(rolDelTexto(request.rol()))
                .build();
        Usuario guardado = registrarUsuarioUseCase.execute(usuario, rolSolicitante);
        return aRespuesta(guardado);
    }

    @GetMapping
    @Operation(
            summary = "Consultar usuarios por correo (ADMIN o ASESOR)",
            description = "HU4: devuelve los usuarios cuyos correos se envían separados por coma. "
                    + "Lo usa el microservicio de Solicitudes para conocer nombre y salario base de los clientes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados (los correos que no existan se omiten)"),
            @ApiResponse(responseCode = "400", description = "No se envió ningún correo",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No envió token o el token no es válido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "El rol no tiene permiso",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<UsuarioResponse> buscarPorCorreos(
            @Parameter(description = "Correos separados por coma", example = "ana@correo.com,luis@correo.com")
            @RequestParam(name = "correos", required = false) List<String> correos,
            @AuthenticationPrincipal Jwt token) {
        log.info("GET /api/v1/usuarios recibido de {} para {} correos", token.getSubject(),
                correos == null ? 0 : correos.size());
        return buscarUsuariosUseCase.execute(correos).stream().map(UsuarioController::aRespuesta).toList();
    }

    static UsuarioResponse aRespuesta(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombres(),
                u.getApellidos(),
                u.getFechaNacimiento(),
                u.getDireccion(),
                u.getTelefono(),
                u.getCorreoElectronico(),
                u.getSalarioBase(),
                u.getDocumentoIdentidad(),
                u.getRol() == null ? null : u.getRol().name());
    }

    private static Rol rolDelToken(Jwt token) {
        try {
            return Rol.valueOf(token.getClaimAsString("rol"));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AccesoDenegadoException("El token no tiene un rol válido");
        }
    }

    private static Rol rolDelTexto(String rol) {
        if (rol == null || rol.isBlank()) {
            return null; // el caso de uso lo registra como CLIENTE
        }
        try {
            return Rol.valueOf(rol.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("El rol debe ser ADMIN, ASESOR o CLIENTE");
        }
    }
}
