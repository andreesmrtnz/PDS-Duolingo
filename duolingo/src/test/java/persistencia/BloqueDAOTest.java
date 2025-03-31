package persistencia;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import modelo.Bloque;
import modelo.Curso;
import modelo.Pregunta;
import modelo.Pregunta.TipoPregunta;
import java.util.List;
import java.util.ArrayList;

class BloqueDAOTest {
    
    private BloqueDAO bloqueDAO;
    private CursoDAO cursoDAO;
    private Curso cursoPrueba;
    
    @BeforeEach
    void setUp() {
        bloqueDAO = new BloqueDAO();
        cursoDAO = new CursoDAO();
        
        // Crear un curso de prueba
        cursoPrueba = new Curso();
        cursoPrueba.setTitulo("Curso de Prueba");
        cursoDAO.guardar(cursoPrueba);
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar datos de prueba
        EntityManager em = bloqueDAO.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Pregunta").executeUpdate();
            em.createQuery("DELETE FROM Bloque").executeUpdate();
            em.createQuery("DELETE FROM Curso").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        bloqueDAO.cerrar();
        cursoDAO.cerrar();
    }
    
    @Test
    void testGuardarBloque() {
        // Crear un bloque de prueba
        Bloque bloque = new Bloque("Bloque de prueba", "Descripción del bloque de prueba");
        bloque.setCurso(cursoPrueba);
        
        // Guardar el bloque
        bloqueDAO.guardar(bloque);
        
        // Verificar que el id no sea nulo
        assertNotNull(bloque.getId(), "El ID del bloque no debería ser nulo después de guardarlo");
        
        // Buscar el bloque por ID
        Bloque bloqueRecuperado = bloqueDAO.buscarPorId(bloque.getId());
        
        // Verificar que el bloque se recuperó correctamente
        assertNotNull(bloqueRecuperado, "El bloque recuperado no debería ser nulo");
        assertEquals(bloque.getTitulo(), bloqueRecuperado.getTitulo(), "Los títulos deberían coincidir");
        assertEquals(bloque.getDescripcion(), bloqueRecuperado.getDescripcion(), "Las descripciones deberían coincidir");
        assertEquals(cursoPrueba.getId(), bloqueRecuperado.getCurso().getId(), "Los IDs de curso deberían coincidir");
    }
    
    @Test
    void testActualizarBloque() {
        // Crear un bloque de prueba
        Bloque bloque = new Bloque("Título original", "Descripción original");
        bloque.setCurso(cursoPrueba);
        
        // Guardar el bloque
        bloqueDAO.guardar(bloque);
        
        // Modificar el bloque
        bloque.setTitulo("Título actualizado");
        bloque.setDescripcion("Descripción actualizada");
        
        // Actualizar el bloque
        bloqueDAO.guardar(bloque);
        
        // Recuperar el bloque actualizado
        Bloque bloqueActualizado = bloqueDAO.buscarPorId(bloque.getId());
        
        // Verificar que los cambios se guardaron
        assertEquals("Título actualizado", bloqueActualizado.getTitulo(), "El título actualizado debería coincidir");
        assertEquals("Descripción actualizada", bloqueActualizado.getDescripcion(), "La descripción actualizada debería coincidir");
    }
    
    @Test
    void testBuscarPorCurso() {
        // Crear varios bloques para el mismo curso
        Bloque bloque1 = new Bloque("Bloque 1", "Descripción 1");
        bloque1.setCurso(cursoPrueba);
        bloqueDAO.guardar(bloque1);
        
        Bloque bloque2 = new Bloque("Bloque 2", "Descripción 2");
        bloque2.setCurso(cursoPrueba);
        bloqueDAO.guardar(bloque2);
        
        // Crear otro curso y bloque
        Curso otroCurso = new Curso();
        otroCurso.setTitulo("Otro curso");
        cursoDAO.guardar(otroCurso);
        
        Bloque bloque3 = new Bloque("Bloque 3", "Descripción 3");
        bloque3.setCurso(otroCurso);
        bloqueDAO.guardar(bloque3);
        
        // Buscar bloques por curso
        List<Bloque> bloques = bloqueDAO.buscarPorCurso(cursoPrueba.getId());
        
        // Verificar que se encontraron los bloques correctos
        assertNotNull(bloques, "La lista de bloques no debería ser nula");
        assertEquals(2, bloques.size(), "Deberían haber 2 bloques para el curso de prueba");
        
        // Verificar que los bloques encontrados son los correctos
        boolean encontroBloque1 = false;
        boolean encontroBloque2 = false;
        
        for (Bloque b : bloques) {
            if (b.getId().equals(bloque1.getId())) {
                encontroBloque1 = true;
            } else if (b.getId().equals(bloque2.getId())) {
                encontroBloque2 = true;
            }
        }
        
        assertTrue(encontroBloque1, "Debería encontrarse el bloque 1");
        assertTrue(encontroBloque2, "Debería encontrarse el bloque 2");
    }
    
    @Test
    void testEliminarBloque() {
        // Crear un bloque de prueba
        Bloque bloque = new Bloque("Bloque a eliminar", "Descripción a eliminar");
        bloque.setCurso(cursoPrueba);
        
        // Guardar el bloque
        bloqueDAO.guardar(bloque);
        Long idBloque = bloque.getId();
        
        // Verificar que existe
        assertNotNull(bloqueDAO.buscarPorId(idBloque), "El bloque debería existir antes de eliminarlo");
        
        // Eliminar el bloque
        bloqueDAO.eliminar(bloque);
        
        // Verificar que ya no existe
        assertNull(bloqueDAO.buscarPorId(idBloque), "El bloque no debería existir después de eliminarlo");
    }
    
    @Test
    void testRelacionBidireccionalConPreguntas() {
        // Crear un bloque
        Bloque bloque = new Bloque("Bloque con preguntas", "Contiene preguntas");
        bloque.setCurso(cursoPrueba);
        
        // Crear preguntas
        Pregunta pregunta1 = new Pregunta("¿Pregunta 1?", 
                                          List.of("Opción 1", "Opción 2", "Opción 3"), 
                                          1, 
                                          TipoPregunta.SELECCION_MULTIPLE);
        
        Pregunta pregunta2 = new Pregunta("¿Pregunta 2?", 
                                          List.of("Opción A", "Opción B"), 
                                          0, 
                                          TipoPregunta.SELECCION_MULTIPLE);
        
        // Agregar preguntas al bloque
        bloque.addPregunta(pregunta1);
        bloque.addPregunta(pregunta2);
        
        // Guardar el bloque (debería guardar también las preguntas por cascada)
        bloqueDAO.guardar(bloque);
        
        // Recuperar el bloque
        Bloque bloqueRecuperado = bloqueDAO.buscarPorId(bloque.getId());
        
        // Verificar que las preguntas se guardaron
        assertNotNull(bloqueRecuperado, "El bloque recuperado no debería ser nulo");
        assertEquals(2, bloqueRecuperado.getPreguntas().size(), "El bloque debería tener 2 preguntas");
        
        // Verificar que la relación bidireccional se mantiene
        for (Pregunta p : bloqueRecuperado.getPreguntas()) {
            assertEquals(bloqueRecuperado, p.getBloque(), "La relación bidireccional debería mantenerse");
        }
    }
}