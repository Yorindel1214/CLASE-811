package co.com.crediya.usecase.buscarusuarios;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.usecase.FakeUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuscarUsuariosUseCaseTest {

    private BuscarUsuariosUseCase useCase;

    @BeforeEach
    void setUp() {
        FakeUsuarioRepository repositorio = new FakeUsuarioRepository();
        repositorio.guardar(Usuario.builder().correoElectronico("ana@correo.com").nombres("Ana").build());
        repositorio.guardar(Usuario.builder().correoElectronico("luis@correo.com").nombres("Luis").build());
        useCase = new BuscarUsuariosUseCase(repositorio);
    }

    @Test
    void devuelveSoloLosCorreosQueExisten() {
        List<Usuario> encontrados = useCase.execute(List.of(" ana@correo.com ", "nadie@correo.com"));
        assertEquals(1, encontrados.size());
        assertEquals("Ana", encontrados.get(0).getNombres());
    }

    @Test
    void sinCorreosEsInvalido() {
        assertThrows(ValidacionException.class, () -> useCase.execute(null));
        assertThrows(ValidacionException.class, () -> useCase.execute(List.of(" ")));
    }
}
