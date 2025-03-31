package modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioTest {
    private Usuario usuario;
    private Curso curso;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan", "juan@test.com", "pass123");
        curso = new Curso();
    }

    @Test
    void testAddCurso() {
        usuario.addCurso(curso);
        assertEquals(1, usuario.getCursos().size());
        assertEquals(usuario, curso.getCreador());
    }

    @Test
    void testGetCursosDevuelveCopia() {
        usuario.addCurso(curso);
        List<Curso> copia = usuario.getCursos();
        copia.clear();
        assertEquals(1, usuario.getCursos().size());
    }

    @Test
    void testSetters() {
        usuario.setNombre("Pedro");
        usuario.setEmail("pedro@test.com");
        usuario.setPassword("nuevaPass");
        
        assertEquals("Pedro", usuario.getNombre());
        assertEquals("pedro@test.com", usuario.getEmail());
        assertEquals("nuevaPass", usuario.getPassword());
    }
}
