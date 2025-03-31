package persistencia;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import modelo.Curso;
import modelo.Usuario;
import java.util.List;

class CursoDAOTest {
    
    private CursoDAO cursoDAO;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioPrueba;
    
    @BeforeEach
    void setUp() {
        cursoDAO = new CursoDAO();
        usuarioDAO = new UsuarioDAO();
        
        // Crear un usuario de prueba para ser el creador de los cursos
        usuarioPrueba = new Usuario();
        usuarioPrueba.setNombre("Usuario Test");
        usuarioPrueba.setEmail("test@example.com");
        usuarioPrueba.setPassword("password123");
        usuarioDAO.registrar(usuarioPrueba);
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar datos de prueba
        EntityManager em = cursoDAO.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Curso").executeUpdate();
            em.createQuery("DELETE FROM Usuario").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        cursoDAO.cerrar();
        usuarioDAO.cerrar();
    }
    
    @Test
    void testGuardarCurso() {
        // Crear un curso de prueba
        Curso curso = new Curso();
        curso.setNombre("Curso de Java");
        curso.setDescripcion("Aprende Java desde cero");
        curso.setDominio("Programación");
        curso.setCreador(usuarioPrueba);
        
        // Guardar el curso
        cursoDAO.guardar(curso);
        
        // Verificar que el id no sea nulo
        assertNotNull(curso.getId(), "El ID del curso no debería ser nulo después de guardarlo");
        
        // Buscar el curso por ID
        Curso cursoRecuperado = cursoDAO.buscarPorId(curso.getId());
        
        // Verificar que el curso se recuperó correctamente
        assertNotNull(cursoRecuperado, "El curso recuperado no debería ser nulo");
        assertEquals(curso.getNombre(), cursoRecuperado.getNombre(), "Los nombres deberían coincidir");
        assertEquals(curso.getDescripcion(), cursoRecuperado.getDescripcion(), "Las descripciones deberían coincidir");
        assertEquals(curso.getDominio(), cursoRecuperado.getDominio(), "Los dominios deberían coincidir");
        assertEquals(usuarioPrueba.getId(), cursoRecuperado.getCreador().getId(), "Los IDs de creador deberían coincidir");
    }
    
    @Test
    void testActualizarCurso() {
        // Crear un curso de prueba
        Curso curso = new Curso();
        curso.setNombre("Curso Original");
        curso.setDescripcion("Descripción original");
        curso.setDominio("Programación");
        curso.setCreador(usuarioPrueba);
        
        // Guardar el curso
        cursoDAO.guardar(curso);
        
        // Modificar el curso
        curso.setNombre("Curso Actualizado");
        curso.setDescripcion("Descripción actualizada");
        curso.setDominio("Desarrollo");
        
        // Actualizar el curso
        cursoDAO.actualizar(curso);
        
        // Recuperar el curso actualizado
        Curso cursoActualizado = cursoDAO.buscarPorId(curso.getId());
        
        // Verificar que los cambios se guardaron
        assertEquals("Curso Actualizado", cursoActualizado.getNombre(), "El nombre actualizado debería coincidir");
        assertEquals("Descripción actualizada", cursoActualizado.getDescripcion(), "La descripción actualizada debería coincidir");
        assertEquals("Desarrollo", cursoActualizado.getDominio(), "El dominio actualizado debería coincidir");
    }
    
    @Test
    void testListarTodos() {
        // Crear varios cursos
        Curso curso1 = new Curso();
        curso1.setNombre("Curso 1");
        curso1.setDescripcion("Descripción 1");
        curso1.setDominio("Programación");
        curso1.setCreador(usuarioPrueba);
        cursoDAO.guardar(curso1);
        
        Curso curso2 = new Curso();
        curso2.setNombre("Curso 2");
        curso2.setDescripcion("Descripción 2");
        curso2.setDominio("Base de Datos");
        curso2.setCreador(usuarioPrueba);
        cursoDAO.guardar(curso2);
        
        // Listar todos los cursos
        List<Curso> cursos = cursoDAO.listarTodos();
        
        // Verificar que se recuperaron los cursos
        assertNotNull(cursos, "La lista de cursos no debería ser nula");
        assertTrue(cursos.size() >= 2, "Deberían haber al menos 2 cursos");
        
        // Verificar que los cursos creados están en la lista
        boolean encontroCurso1 = false;
        boolean encontroCurso2 = false;
        
        for (Curso c : cursos) {
            if (c.getId().equals(curso1.getId())) {
                encontroCurso1 = true;
            } else if (c.getId().equals(curso2.getId())) {
                encontroCurso2 = true;
            }
        }
        
        assertTrue(encontroCurso1, "Debería encontrarse el curso 1");
        assertTrue(encontroCurso2, "Debería encontrarse el curso 2");
    }
    
    @Test
    void testBuscarPorDominio() {
        // Crear cursos con diferentes dominios
        Curso curso1 = new Curso();
        curso1.setNombre("Java Básico");
        curso1.setDescripcion("Aprende Java");
        curso1.setDominio("Programación");
        curso1.setCreador(usuarioPrueba);
        cursoDAO.guardar(curso1);
        
        Curso curso2 = new Curso();
        curso2.setNombre("Python Avanzado");
        curso2.setDescripcion("Domina Python");
        curso2.setDominio("Programación");
        curso2.setCreador(usuarioPrueba);
        cursoDAO.guardar(curso2);
        
        Curso curso3 = new Curso();
        curso3.setNombre("MySQL");
        curso3.setDescripcion("Base de datos MySQL");
        curso3.setDominio("Base de Datos");
        curso3.setCreador(usuarioPrueba);
        cursoDAO.guardar(curso3);
        
        // Buscar cursos por dominio
        List<Curso> cursosProgramacion = cursoDAO.buscarPorDominio("Programación");
        
        // Verificar resultados
        assertNotNull(cursosProgramacion, "La lista de cursos no debería ser nula");
        assertEquals(2, cursosProgramacion.size(), "Deberían haber 2 cursos de programación");
        
        // Verificar que los cursos encontrados son los correctos
        boolean encontroCurso1 = false;
        boolean encontroCurso2 = false;
        
        for (Curso c : cursosProgramacion) {
            if (c.getId().equals(curso1.getId())) {
                encontroCurso1 = true;
            } else if (c.getId().equals(curso2.getId())) {
                encontroCurso2 = true;
            }
        }
        
        assertTrue(encontroCurso1, "Debería encontrarse el curso 1 de programación");
        assertTrue(encontroCurso2, "Debería encontrarse el curso 2 de programación");
    }
    
    @Test
    void testBuscarPorCreador() {
        // Crear un segundo usuario
        Usuario otroUsuario = new Usuario();
        otroUsuario.setNombre("Otro Usuario");
        otroUsuario.setEmail("otro@example.com");
        otroUsuario.setPassword("password456");
        usuarioDAO.registrar(otroUsuario);
        
        // Crear cursos con diferentes creadores
        Curso curso1 = new Curso();
        curso1.setNombre("Curso Usuario 1");
        curso1.setDescripcion("Descripción curso 1");
        curso1.setDominio("Programación");
        curso1.setCreador(usuarioPrueba);
        cursoDAO.guardar(curso1);
        
        Curso curso2 = new Curso();
        curso2.setNombre("Curso Usuario 2");
        curso2.setDescripcion("Descripción curso 2");
        curso2.setDominio("Matemáticas");
        curso2.setCreador(otroUsuario);
        cursoDAO.guardar(curso2);
        
        // Buscar cursos por creador
        List<Curso> cursosUsuario1 = cursoDAO.buscarPorCreador(usuarioPrueba);
        
        // Verificar resultados
        assertNotNull(cursosUsuario1, "La lista de cursos no debería ser nula");
        assertEquals(1, cursosUsuario1.size(), "Debería haber 1 curso del usuario de prueba");
        assertEquals(curso1.getId(), cursosUsuario1.get(0).getId(), "El ID del curso debería coincidir");
    }
    
    @Test
    void testEliminarCurso() {
        // Crear un curso de prueba
        Curso curso = new Curso();
        curso.setNombre("Curso a eliminar");
        curso.setDescripcion("Este curso será eliminado");
        curso.setDominio("Pruebas");
        curso.setCreador(usuarioPrueba);
        
        // Guardar el curso
        cursoDAO.guardar(curso);
        Long idCurso = curso.getId();
        
        // Verificar que existe
        assertNotNull(cursoDAO.buscarPorId(idCurso), "El curso debería existir antes de eliminarlo");
        
        // Eliminar el curso
        cursoDAO.eliminar(curso);
        
        // Verificar que ya no existe
        assertNull(cursoDAO.buscarPorId(idCurso), "El curso no debería existir después de eliminarlo");
    }
}