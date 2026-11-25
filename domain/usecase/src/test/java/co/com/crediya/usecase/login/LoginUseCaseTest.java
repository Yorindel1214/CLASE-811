package co.com.crediya.usecase.login;

import co.com.crediya.model.autenticacion.Sesion;
import co.com.crediya.model.autenticacion.exceptions.CredencialesInvalidasException;
import co.com.crediya.model.usuario.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.usecase.FakeUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginUseCaseTest {

    private FakeUsuarioRepository repositorio;
    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        repositorio = new FakeUsuarioRepository();
        useCase = new LoginUseCase(repositorio, new FakeUsuarioRepository.FakeClaveEncriptador(),
                usuario -> Sesion.builder()
                        .token("token-de-" + usuario.getCorreoElectronico())
                        .expiraEnSegundos(3600)
                        .correoElectronico(usuario.getCorreoElectronico())
                        .rol(usuario.getRol())
                        .build());
        repositorio.guardar(Usuario.builder()
                .correoElectronico("asesor@crediya.com")
                .clave("cifrada:Clave12345")
                .rol(Rol.ASESOR)
                .build());
    }

    @Test
    void credencialesCorrectasDevuelvenToken() {
        Sesion sesion = useCase.execute("asesor@crediya.com", "Clave12345");
        assertEquals("token-de-asesor@crediya.com", sesion.getToken());
        assertEquals(Rol.ASESOR, sesion.getRol());
    }

    @Test
    void claveIncorrectaEsRechazada() {
        assertThrows(CredencialesInvalidasException.class, () -> useCase.execute("asesor@crediya.com", "otra"));
    }

    @Test
    void correoInexistenteEsRechazado() {
        assertThrows(CredencialesInvalidasException.class, () -> useCase.execute("nadie@crediya.com", "Clave12345"));
    }

    @Test
    void intentosIlimitados() {
        for (int i = 0; i < 20; i++) {
            assertThrows(CredencialesInvalidasException.class, () -> useCase.execute("asesor@crediya.com", "mala"));
        }
        assertEquals(Rol.ASESOR, useCase.execute("asesor@crediya.com", "Clave12345").getRol());
    }

    @Test
    void usuarioSinClaveNoPuedeIniciarSesion() {
        repositorio.guardar(Usuario.builder().correoElectronico("viejo@correo.com").build());
        assertThrows(CredencialesInvalidasException.class, () -> useCase.execute("viejo@correo.com", "loquesea"));
    }

    @Test
    void camposVaciosSonInvalidos() {
        assertThrows(ValidacionException.class, () -> useCase.execute("", "Clave12345"));
        assertThrows(ValidacionException.class, () -> useCase.execute("asesor@crediya.com", " "));
    }
}
