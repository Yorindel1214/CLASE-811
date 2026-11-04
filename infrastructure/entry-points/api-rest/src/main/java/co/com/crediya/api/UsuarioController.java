package co.com.crediya.api;

import co.com.crediya.api.dto.UsuarioRequest;
import co.com.crediya.api.dto.UsuarioResponse;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody UsuarioRequest request) {
        log.info("POST /api/v1/usuarios recibido");
        Usuario usuario = Usuario.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .correoElectronico(request.correoElectronico())
                .salarioBase(request.salarioBase())
                .build();
        Usuario guardado = registrarUsuarioUseCase.execute(usuario);
        return new UsuarioResponse(
                guardado.getId(),
                guardado.getNombres(),
                guardado.getApellidos(),
                guardado.getFechaNacimiento(),
                guardado.getDireccion(),
                guardado.getTelefono(),
                guardado.getCorreoElectronico(),
                guardado.getSalarioBase());
    }
}