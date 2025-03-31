package persistencia;

import modelo.Usuario;
import org.junit.jupiter.api.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioDAOTest {
    private static UsuarioDAO usuarioDAO;
    private Usuario usuarioTest;

    @BeforeAll
    static void setUpAll() {
        // Configurar EMF para pruebas (usando H2 en memoria)
        usuarioDAO = new UsuarioDAO();
    }

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        usuarioTest = new Usuario();
        usuarioTest.setNombre("Juan Pérez");
        usuarioTest.setEmail("juan@test.com");
        usuarioTest.setPassword("secreto123");
    }

    @AfterEach
    void tearDown() {
        // Limpiar datos después de cada test
        usuarioDAO.eliminar(usuarioTest);
    }

    @AfterAll
    static void tearDownAll() {
        usuarioDAO.cerrar();
    }

    @Test
    void testRegistrarYBuscarPorId() {
        // Registrar usuario
        usuarioDAO.registrar(usuarioTest);
        
        // Buscar por ID
        Usuario encontrado = usuarioDAO.buscarPorId(usuarioTest.getId());
        
        assertNotNull(encontrado);
        assertEquals("juan@test.com", encontrado.getEmail());
    }

    @Test
    void testBuscarPorEmail() {
        usuarioDAO.registrar(usuarioTest);
        
        Usuario encontrado = usuarioDAO.buscarPorEmail("juan@test.com");
        
        assertNotNull(encontrado);
        assertEquals(usuarioTest.getNombre(), encontrado.getNombre());
    }

    @Test
    void testActualizarUsuario() {
        usuarioDAO.registrar(usuarioTest);
        
        // Modificar datos
        usuarioTest.setNombre("Nombre Actualizado");
        usuarioDAO.actualizar(usuarioTest);
        
        Usuario actualizado = usuarioDAO.buscarPorId(usuarioTest.getId());
        assertEquals("Nombre Actualizado", actualizado.getNombre());
    }

    @Test
    void testEliminarUsuario() {
        usuarioDAO.registrar(usuarioTest);
        usuarioDAO.eliminar(usuarioTest);
        
        Usuario eliminado = usuarioDAO.buscarPorId(usuarioTest.getId());
        assertNull(eliminado);
    }

    @Test
    void testListarTodos() {
        usuarioDAO.registrar(usuarioTest);
        
        int cantidad = usuarioDAO.listarTodos().size();
        assertTrue(cantidad > 0);
    }
}