package co.com.crediya.usecase.registrarusuario;

import co.com.crediya.model.usuario.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.exceptions.AccesoDenegadoException;
import co.com.crediya.model.usuario.exceptions.CorreoYaRegistradoException;
import co.com.crediya.model.usuario.exceptions.DocumentoYaRegistradoException;
import co.com.crediya.model.usuario.exceptions.ValidacionException;
import co.com.crediya.usecase.FakeUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistrarUsuarioUseCaseTest {

    private FakeUsuarioRepository repositorio;
    private RegistrarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        repositorio = new FakeUsuarioRepository();
        useCase = new RegistrarUsuarioUseCase(repositorio, new FakeUsuarioRepository.FakeClaveEncriptador());
    }

    private Usuario.UsuarioBuilder valido() {
        return Usuario.builder()
                .nombres("Ana").apellidos("Pérez")
                .correoElectronico("ana@correo.com")
                .documentoIdentidad("1023456789")
                .clave("Clave12345")
                .salarioBase(new BigDecimal("3500000"));
    }

    @Test
    void adminRegistraUsuarioConClaveCifradaYRolClientePorDefecto() {
        Usuario guardado = useCase.execute(valido().build(), Rol.ADMIN);

        assertNotNull(guardado.getId());
        assertEquals(Rol.CLIENTE, guardado.getRol());
        assertEquals("cifrada:Clave12345", guardado.getClave());
    }

    @Test
    void adminPuedeRegistrarAsesor() {
        Usuario guardado = useCase.execute(valido().rol(Rol.ASESOR).build(), Rol.ADMIN);
        assertEquals(Rol.ASESOR, guardado.getRol());
    }

    @Test
    void asesorPuedeRegistrarCliente() {
        Usuario guardado = useCase.execute(valido().rol(Rol.CLIENTE).build(), Rol.ASESOR);
        assertEquals(Rol.CLIENTE, guardado.getRol());
    }

    @Test
    void asesorNoPuedeRegistrarAdmin() {
        Usuario usuario = valido().rol(Rol.ADMIN).build();
        assertThrows(AccesoDenegadoException.class, () -> useCase.execute(usuario, Rol.ASESOR));
    }

    @Test
    void clienteNoPuedeRegistrarUsuarios() {
        Usuario usuario = valido().build();
        assertThrows(AccesoDenegadoException.class, () -> useCase.execute(usuario, Rol.CLIENTE));
    }

    @Test
    void correoRepetidoLanzaConflicto() {
        useCase.execute(valido().build(), Rol.ADMIN);
        Usuario otro = valido().documentoIdentidad("99999999").build();
        assertThrows(CorreoYaRegistradoException.class, () -> useCase.execute(otro, Rol.ADMIN));
    }

    @Test
    void documentoRepetidoLanzaConflicto() {
        useCase.execute(valido().build(), Rol.ADMIN);
        Usuario otro = valido().correoElectronico("otra@correo.com").build();
        assertThrows(DocumentoYaRegistradoException.class, () -> useCase.execute(otro, Rol.ADMIN));
    }

    @Test
    void claveCortaEsInvalida() {
        Usuario usuario = valido().clave("123").build();
        assertThrows(ValidacionException.class, () -> useCase.execute(usuario, Rol.ADMIN));
    }

    @Test
    void documentoConLetrasEsInvalido() {
        Usuario usuario = valido().documentoIdentidad("ABC123").build();
        assertThrows(ValidacionException.class, () -> useCase.execute(usuario, Rol.ADMIN));
    }

    @Test
    void salarioFueraDeRangoEsInvalido() {
        Usuario usuario = valido().salarioBase(new BigDecimal("20000000")).build();
        assertThrows(ValidacionException.class, () -> useCase.execute(usuario, Rol.ADMIN));
    }
}
