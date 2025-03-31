package persistencia;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import modelo.Bloque;
import modelo.Curso;
import modelo.Pregunta;
import java.util.List;

class PreguntaDAOTest {
    
    private PreguntaDAO preguntaDAO;
    private BloqueDAO bloqueDAO;
    private CursoDAO cursoDAO;
    private Bloque bloquePrueba;
    private Curso cursoPrueba;
    
    @BeforeEach
    void setUp() {
        preguntaDAO = new PreguntaDAO();
        bloqueDAO = new BloqueDAO();
        cursoDAO = new CursoDAO();
        
        // Crear un curso de prueba
        cursoPrueba = new Curso();
        cursoPrueba.setNombre("Curso de Prueba");
        cursoPrueba.setDescripcion("Descripción del curso de prueba");
        cursoDAO.guardar(cursoPrueba);
        
        // Crear un bloque de prueba
        bloquePrueba = new Bloque();
        bloquePrueba.setTitulo("Bloque de prueba");
        bloquePrueba.setDescripcion("Descripción del bloque de prueba");
        bloquePrueba.setCurso(cursoPrueba);
        bloqueDAO.guardar(bloquePrueba);
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar datos de prueba
        EntityManager em = preguntaDAO.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Pregunta").executeUpdate();
            em.createQuery("DELETE FROM Bloque").executeUpdate();
            em.createQuery("DELETE FROM Curso").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        preguntaDAO.cerrar();
        bloqueDAO.cerrar();
        cursoDAO.cerrar();
    }
    
    @Test
    void testGuardarPregunta() {
        // Crear una pregunta de prueba
        Pregunta pregunta = new Pregunta();
        pregunta.setTexto("¿Cuál es la capital de Francia?");
        pregunta.setOpciones("A) Londres\nB) París\nC) Madrid\nD) Roma");
        pregunta.setRespuestaCorrecta("B");
        pregunta.setExplicacion("París es la capital de Francia.");
        pregunta.setBloque(bloquePrueba);
        
        // Guardar la pregunta
        preguntaDAO.guardar(pregunta);
        
        // Verificar que el id no sea nulo
        assertNotNull(pregunta.getId(), "El ID de la pregunta no debería ser nulo después de guardarla");
        
        // Buscar la pregunta por ID
        Pregunta preguntaRecuperada = preguntaDAO.buscarPorId(pregunta.getId());
        
        // Verificar que la pregunta se recuperó correctamente
        assertNotNull(preguntaRecuperada, "La pregunta recuperada no debería ser nula");
        assertEquals(pregunta.getTexto(), preguntaRecuperada.getTexto(), "Los textos deberían coincidir");
        assertEquals(pregunta.getOpciones(), preguntaRecuperada.getOpciones(), "Las opciones deberían coincidir");
        assertEquals(pregunta.getRespuestaCorrecta(), preguntaRecuperada.getRespuestaCorrecta(), "Las respuestas correctas deberían coincidir");
        assertEquals(pregunta.getExplicacion(), preguntaRecuperada.getExplicacion(), "Las explicaciones deberían coincidir");
        assertEquals(bloquePrueba.getId(), preguntaRecuperada.getBloque().getId(), "Los IDs de bloque deberían coincidir");
    }
    
    @Test
    void testActualizarPregunta() {
        // Crear una pregunta de prueba
        Pregunta pregunta = new Pregunta();
        pregunta.setTexto("Pregunta original");
        pregunta.setOpciones("Opciones originales");
        pregunta.setRespuestaCorrecta("A");
        pregunta.setExplicacion("Explicación original");
        pregunta.setBloque(bloquePrueba);
        
        // Guardar la pregunta
        preguntaDAO.guardar(pregunta);
        
        // Modificar la pregunta
        pregunta.setTexto("Pregunta actualizada");
        pregunta.setOpciones("Opciones actualizadas");
        pregunta.setRespuestaCorrecta("B");
        pregunta.setExplicacion("Explicación actualizada");
        
        // Actualizar la pregunta
        preguntaDAO.guardar(pregunta);
        
        // Recuperar la pregunta actualizada
        Pregunta preguntaActualizada = preguntaDAO.buscarPorId(pregunta.getId());
        
        // Verificar que los cambios se guardaron
        assertEquals("Pregunta actualizada", preguntaActualizada.getTexto(), "El texto actualizado debería coincidir");
        assertEquals("Opciones actualizadas", preguntaActualizada.getOpciones(), "Las opciones actualizadas deberían coincidir");
        assertEquals("B", preguntaActualizada.getRespuestaCorrecta(), "La respuesta correcta actualizada debería coincidir");
        assertEquals("Explicación actualizada", preguntaActualizada.getExplicacion(), "La explicación actualizada debería coincidir");
    }
    
    @Test
    void testBuscarPorBloque() {
        // Crear varias preguntas para el mismo bloque
        Pregunta pregunta1 = new Pregunta();
        pregunta1.setTexto("Pregunta 1");
        pregunta1.setOpciones("Opciones 1");
        pregunta1.setRespuestaCorrecta("A");
        pregunta1.setExplicacion("Explicación 1");
        pregunta1.setBloque(bloquePrueba);
        preguntaDAO.guardar(pregunta1);
        
        Pregunta pregunta2 = new Pregunta();
        pregunta2.setTexto("Pregunta 2");
        pregunta2.setOpciones("Opciones 2");
        pregunta2.setRespuestaCorrecta("B");
        pregunta2.setExplicacion("Explicación 2");
        pregunta2.setBloque(bloquePrueba);
        preguntaDAO.guardar(pregunta2);
        
        // Crear otro bloque y pregunta
        Bloque otroBloque = new Bloque();
        otroBloque.setTitulo("Otro bloque");
        otroBloque.setDescripcion("Descripción de otro bloque");
        otroBloque.setCurso(cursoPrueba);
        bloqueDAO.guardar(otroBloque);
        
        Pregunta pregunta3 = new Pregunta();
        pregunta3.setTexto("Pregunta 3");
        pregunta3.setOpciones("Opciones 3");
        pregunta3.setRespuestaCorrecta("C");
        pregunta3.setExplicacion("Explicación 3");
        pregunta3.setBloque(otroBloque);
        preguntaDAO.guardar(pregunta3);
        
        // Buscar preguntas por bloque
        List<Pregunta> preguntas = preguntaDAO.buscarPorBloque(bloquePrueba.getId());
        
        // Verificar que se encontraron las preguntas correctas
        assertNotNull(preguntas, "La lista de preguntas no debería ser nula");
        assertEquals(2, preguntas.size(), "Deberían haber 2 preguntas para el bloque de prueba");
        
        // Verificar que las preguntas encontradas son las correctas
        boolean encontroPregunta1 = false;
        boolean encontroPregunta2 = false;
        
        for (Pregunta p : preguntas) {
            if (p.getId().equals(pregunta1.getId())) {
                encontroPregunta1 = true;
            } else if (p.getId().equals(pregunta2.getId())) {
                encontroPregunta2 = true;
            }
        }
        
        assertTrue(encontroPregunta1, "Debería encontrarse la pregunta 1");
        assertTrue(encontroPregunta2, "Debería encontrarse la pregunta 2");
    }
    
    @Test
    void testEliminarPregunta() {
        // Crear una pregunta de prueba
        Pregunta pregunta = new Pregunta();
        pregunta.setTexto("Pregunta a eliminar");
        pregunta.setOpciones("Opciones a eliminar");
        pregunta.setRespuestaCorrecta("A");
        pregunta.setExplicacion("Explicación a eliminar");
        pregunta.setBloque(bloquePrueba);
        
        // Guardar la pregunta
        preguntaDAO.guardar(pregunta);
        Long idPregunta = pregunta.getId();
        
        // Verificar que existe
        assertNotNull(preguntaDAO.buscarPorId(idPregunta), "La pregunta debería existir antes de eliminarla");
        
        // Eliminar la pregunta
        preguntaDAO.eliminar(pregunta);
        
        // Verificar que ya no existe
        assertNull(preguntaDAO.buscarPorId(idPregunta), "La pregunta no debería existir después de eliminarla");
    }
}