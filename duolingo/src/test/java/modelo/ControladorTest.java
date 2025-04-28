package modelo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // <-- Falta esta importación
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import controlador.Controlador;
import persistencia.CursoDAO;
import persistencia.UsuarioDAO;

@ExtendWith(MockitoExtension.class) // Asegura que Mockito funcione con JUnit 5
class ControladorTest {
    @Mock
    private RepositorioUsuarios repoUsuarios;
    
    @Mock
    private RepositorioCursos repoCursos;
    
    @Mock
    private UsuarioDAO usuarioDAO;
    
    @Mock
    private CursoDAO cursoDAO;
    
    @InjectMocks
    private Controlador controlador;
    
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Test", "test@test.com", "pass");
        controlador = Controlador.getInstancia();
    }

    @Test
    void testLoginExitoso() {
        when(repoUsuarios.buscarPorEmail("test@test.com")).thenReturn(usuario);
        
        Usuario resultado = controlador.login("test@test.com", "pass");
        assertNotNull(resultado);
        assertEquals(usuario, controlador.getUsuarioActual());
    }

    @Test
    void testLoginFallido() {
        when(repoUsuarios.buscarPorEmail("test@test.com")).thenReturn(null);
        
        Usuario resultado = controlador.login("test@test.com", "wrong");
        assertNull(resultado);
        assertNull(controlador.getUsuarioActual());
    }

    @Test
    void testRegistroUsuario() {
        when(repoUsuarios.buscarPorEmail("nuevo@test.com")).thenReturn(null);
        
        Usuario nuevo = new Usuario("Nuevo", "nuevo@test.com", "pass");
        boolean resultado = controlador.registrar(nuevo);
        assertTrue(resultado);
        verify(usuarioDAO).registrar(nuevo);
    }

    @Test
    void testCargarCursoFallido() {
        controlador.setUsuarioActual(usuario);
        boolean resultado = controlador.cargarCursoDesdeArchivo("ruta_invalida");
        assertFalse(resultado);
    }
}
