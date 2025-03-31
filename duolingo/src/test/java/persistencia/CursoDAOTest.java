package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.*;

import modelo.Curso;
import modelo.Usuario;

public class CursoDAOTest {
    
    private static CursoDAO cursoDAO;
    private static UsuarioDAO usuarioDAO;
    private static Usuario creador;
    
    @BeforeAll
    public static void setUp() {
        cursoDAO = new CursoDAO();
        usuarioDAO = new UsuarioDAO();
        
        // Crear un usuario para usar como creador de los cursos
        creador = new Usuario();
        usuarioDAO.registrar(creador);
    }
    
    @AfterAll
    public static void tearDown() {
        cursoDAO.cerrar();
        usuarioDAO.cerrar();
    }
    
    @Test
    public void testCrearYRecuperarCurso() {
        // Crear un curso de prueba
        Curso curso = new Curso();
        curso.setTitulo("Curso de Java");
        curso.setDominio("Programación");
        curso.setCreador(creador);
        curso.setPosicionActual(0);
        
        cursoDAO.guardar(curso);
        
        // Verificar que se asignó un ID
        assertNotNull(curso.getId(), "El ID del curso debe ser asignado");
        
        // Recuperar el curso y verificar sus datos
        Curso recuperado = cursoDAO.buscarPorId(curso.getId());
        assertNotNull(recuperado, "El curso debe ser recuperado de la base de datos");
        assertEquals("Curso de Java", recuperado.getTitulo(), "El título debe coincidir");
        assertEquals("Programación", recuperado.getDominio(), "El dominio debe coincidir");
        assertEquals(creador.getId(), recuperado.getCreador().getId(), "El creador debe coincidir");
        assertEquals(0, recuperado.getPosicionActual(), "La posición actual debe coincidir");
    }
    
    @Test
    public void testListarTodos() {
        // Crear varios cursos
        Curso curso1 = new Curso();
        curso1.setTitulo("Curso de Python");
        curso1.setDominio("Programación");
        curso1.setCreador(creador);
        
        Curso curso2 = new Curso();
        curso2.setTitulo("Curso de HTML/CSS");
        curso2.setDominio("Web");
        curso2.setCreador(creador);
        
        cursoDAO.guardar(curso1);
        cursoDAO.guardar(curso2);
        
        // Obtener la lista de todos los cursos
        List<Curso> cursos = cursoDAO.listarTodos();
        
        // Verificar
        assertNotNull(cursos, "La lista de cursos no debe ser null");
        assertFalse(cursos.isEmpty(), "La lista de cursos no debe estar vacía");
        assertTrue(cursos.stream().anyMatch(c -> c.getId().equals(curso1.getId())), 
                "La lista debe contener el primer curso creado");
        assertTrue(cursos.stream().anyMatch(c -> c.getId().equals(curso2.getId())), 
                "La lista debe contener el segundo curso creado");
    }
    
    @Test
    public void testBuscarPorDominio() {
        // Crear cursos con diferentes dominios
        String dominioUnico = "Matemáticas" + System.currentTimeMillis();
        
        Curso curso1 = new Curso();
        curso1.setTitulo("Álgebra");
        curso1.setDominio(dominioUnico);
        curso1.setCreador(creador);
        
        Curso curso2 = new Curso();
        curso2.setTitulo("Geometría");
        curso2.setDominio(dominioUnico);
        curso2.setCreador(creador);
        
        Curso curso3 = new Curso();
        curso3.setTitulo("Otro curso");
        curso3.setDominio("Otro dominio");
        curso3.setCreador(creador);
        
        cursoDAO.guardar(curso1);
        cursoDAO.guardar(curso2);
        cursoDAO.guardar(curso3);
        
        // Buscar por dominio
        List<Curso> cursosPorDominio = cursoDAO.buscarPorDominio(dominioUnico);
        
        // Verificar
        assertNotNull(cursosPorDominio, "La lista de cursos por dominio no debe ser null");
        assertEquals(2, cursosPorDominio.size(), "Deben haber exactamente 2 cursos con el dominio específico");
        assertTrue(cursosPorDominio.stream().anyMatch(c -> c.getId().equals(curso1.getId())), 
                "La lista debe contener el primer curso del dominio");
        assertTrue(cursosPorDominio.stream().anyMatch(c -> c.getId().equals(curso2.getId())), 
                "La lista debe contener el segundo curso del dominio");
        assertFalse(cursosPorDominio.stream().anyMatch(c -> c.getId().equals(curso3.getId())), 
                "La lista no debe contener el curso de otro dominio");
    }
    
    @Test
    public void testBuscarPorCreador() {
        // Crear un nuevo usuario para este test
        Usuario otroCreador = new Usuario();
        usuarioDAO.registrar(otroCreador);
        
        // Crear cursos con diferentes creadores
        Curso curso1 = new Curso();
        curso1.setTitulo("Curso del creador original");
        curso1.setDominio("Dominio test");
        curso1.setCreador(creador);
        
        Curso curso2 = new Curso();
        curso2.setTitulo("Curso del otro creador");
        curso2.setDominio("Dominio test");
        curso2.setCreador(otroCreador);
        
        cursoDAO.guardar(curso1);
        cursoDAO.guardar(curso2);
        
        // Buscar por creador original
        List<Curso> cursosCreadorOriginal = cursoDAO.buscarPorCreador(creador);
        
        // Verificar
        assertNotNull(cursosCreadorOriginal, "La lista de cursos por creador no debe ser null");
        assertTrue(cursosCreadorOriginal.stream().anyMatch(c -> c.getId().equals(curso1.getId())), 
                "La lista debe contener el curso del creador original");
        
        // Buscar por otro creador
        List<Curso> cursosOtroCreador = cursoDAO.buscarPorCreador(otroCreador);
        
        // Verificar
        assertNotNull(cursosOtroCreador, "La lista de cursos por otro creador no debe ser null");
        assertTrue(cursosOtroCreador.stream().anyMatch(c -> c.getId().equals(curso2.getId())), 
                "La lista debe contener el curso del otro creador");
        assertFalse(cursosOtroCreador.stream().anyMatch(c -> c.getId().equals(curso1.getId())), 
                "La lista no debe contener el curso del creador original");
    }
    
    @Test
    public void testActualizarCurso() {
        // Crear un curso
        Curso curso = new Curso();
        curso.setTitulo("Curso Original");
        curso.setDominio("Dominio Original");
        curso.setCreador(creador);
        curso.setPosicionActual(0);
        
        cursoDAO.guardar(curso);
        Long id = curso.getId();
        
        // Modificar y actualizar
        curso.setTitulo("Curso Actualizado");
        curso.setDominio("Dominio Actualizado");
        curso.setPosicionActual(5);
        
        cursoDAO.actualizar(curso);
        
        // Recuperar y verificar cambios
        Curso actualizado = cursoDAO.buscarPorId(id);
        assertEquals("Curso Actualizado", actualizado.getTitulo(), "El título actualizado debe coincidir");
        assertEquals("Dominio Actualizado", actualizado.getDominio(), "El dominio actualizado debe coincidir");
        assertEquals(5, actualizado.getPosicionActual(), "La posición actual actualizada debe coincidir");
    }
    
    @Test
    public void testEliminarCurso() {
        // Crear un curso para eliminarlo
        Curso curso = new Curso();
        curso.setTitulo("Curso para eliminar");
        curso.setDominio("Dominio test");
        curso.setCreador(creador);
        
        cursoDAO.guardar(curso);
        Long id = curso.getId();
        
        // Verificar que existe
        assertNotNull(cursoDAO.buscarPorId(id), "El curso debe existir antes de eliminarlo");
        
        // Eliminar
        cursoDAO.eliminar(curso);
        
        // Verificar que ya no existe
        assertNull(cursoDAO.buscarPorId(id), "El curso no debe existir después de eliminarlo");
    }
}