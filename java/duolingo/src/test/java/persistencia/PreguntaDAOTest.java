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
        cursoPrueba.setTitulo("Curso de Prueba");
        cursoDAO.guardar(cursoPrueba);
        
        // Crear un bloque de prueba
        bloquePrueba = new Bloque("Bloque de prueba", "Descripción del bloque de prueba");
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
        List<String> opciones = new ArrayList<>();
        opciones.add("Londres");
        opciones.add("París");
        opciones.add("Madrid");
        opciones.add("Roma");
        
        Pregunta pregunta = new Pregunta("¿Cuál es la capital de Francia?", 
                                         opciones, 
                                         1, 
                                         TipoPregunta.SELECCION_MULTIPLE);
        pregunta.setBloque(bloquePrueba);
        
        // Guardar la pregunta
        preguntaDAO.guardar(pregunta);
        
        // Verificar que el id no sea nulo
        assertNotNull(pregunta.getId(), "El ID de la pregunta no debería ser nulo después de guardarla");
        
        // Buscar la pregunta por ID
        Pregunta preguntaRecuperada = preguntaDAO.buscarPorId(pregunta.getId());
        
        // Verificar que la pregunta se recuperó correctamente
        assertNotNull(preguntaRecuperada, "La pregunta recuperada no debería ser nula");
        assertEquals(pregunta.getEnunciado(), preguntaRecuperada.getEnunciado(), "Los enunciados deberían coincidir");
        assertArrayEquals(pregunta.getOpciones(), preguntaRecuperada.getOpciones(), "Las opciones deberían coincidir");
        assertEquals(pregunta.getRespuestaCorrecta(), preguntaRecuperada.getRespuestaCorrecta(), "Las respuestas correctas deberían coincidir");
        assertEquals(bloquePrueba.getId(), preguntaRecuperada.getBloque().getId(), "Los IDs de bloque deberían coincidir");
    }
    
    @Test
    void testActualizarPregunta() {
        // Crear una pregunta de prueba
        List<String> opcionesOriginales = new ArrayList<>();
        opcionesOriginales.add("Opción A");
        opcionesOriginales.add("Opción B");
        
        Pregunta pregunta = new Pregunta("Pregunta original", 
                                         opcionesOriginales, 
                                         0, 
                                         TipoPregunta.SELECCION_MULTIPLE);
        pregunta.setBloque(bloquePrueba);
        
        // Guardar la pregunta
        preguntaDAO.guardar(pregunta);
        
        // Modificar la pregunta
        pregunta.setEnunciado("Pregunta actualizada");
        List<String> opcionesActualizadas = new ArrayList<>();
        opcionesActualizadas.add("Opción C");
        opcionesActualizadas.add("Opción D");
        
        pregunta.setOpcionesList(opcionesActualizadas);
        pregunta.setRespuestaCorrecta(1);
        
        // Actualizar la pregunta
        preguntaDAO.guardar(pregunta);
        
        // Recuperar la pregunta actualizada
        Pregunta preguntaActualizada = preguntaDAO.buscarPorId(pregunta.getId());
        
        // Verificar que los cambios se guardaron
        assertEquals("Pregunta actualizada", preguntaActualizada.getEnunciado(), "El enunciado actualizado debería coincidir");
        assertEquals("Opción C", preguntaActualizada.getOpciones()[0], "La primera opción actualizada debería coincidir");
        assertEquals("Opción D", preguntaActualizada.getOpciones()[1], "La segunda opción actualizada debería coincidir");
        assertEquals(1, preguntaActualizada.getRespuestaCorrecta(), "La respuesta correcta actualizada debería coincidir");
    }
    
    @Test
    void testBuscarPorBloque() {
        // Crear varias preguntas para el mismo bloque
        Pregunta pregunta1 = new Pregunta("Pregunta 1", 
                                          List.of("Opción 1", "Opción 2"), 
                                          0, 
                                          TipoPregunta.SELECCION_MULTIPLE);
        pregunta1.setBloque(bloquePrueba);
        preguntaDAO.guardar(pregunta1);
        
        Pregunta pregunta2 = new Pregunta("Pregunta 2", 
                                          List.of("Opción A", "Opción B"), 
                                          1, 
                                          TipoPregunta.SELECCION_MULTIPLE);
        pregunta2.setBloque(bloquePrueba);
        preguntaDAO.guardar(pregunta2);
        
        // Crear otro bloque y pregunta
        Bloque otroBloque = new Bloque("Otro bloque", "Descripción de otro bloque");
        otroBloque.setCurso(cursoPrueba);
        bloqueDAO.guardar(otroBloque);
        
        Pregunta pregunta3 = new Pregunta("Pregunta 3", 
                                          List.of("Opción X", "Opción Y", "Opción Z"), 
                                          2, 
                                          TipoPregunta.SELECCION_MULTIPLE);
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
        Pregunta pregunta = new Pregunta("Pregunta a eliminar", 
                                         List.of("Opción X", "Opción Y"), 
                                         0, 
                                         TipoPregunta.SELECCION_MULTIPLE);
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
    
    @Test
    void testTiposPregunta() {
        // Probar diferentes tipos de preguntas
        
        // Pregunta de selección múltiple
        Pregunta preguntaSeleccion = new Pregunta("Pregunta selección múltiple", 
                                                 List.of("Opción 1", "Opción 2", "Opción 3"), 
                                                 1, 
                                                 TipoPregunta.SELECCION_MULTIPLE);
        preguntaSeleccion.setBloque(bloquePrueba);
        preguntaDAO.guardar(preguntaSeleccion);
        
        // Pregunta de emparejamiento
        Pregunta preguntaEmparejamiento = new Pregunta("Pregunta emparejamiento", 
                                                      List.of("Elemento A", "Elemento B", "Coincide con A", "Coincide con B"), 
                                                      0, 
                                                      TipoPregunta.EMPAREJAMIENTO);
        preguntaEmparejamiento.setBloque(bloquePrueba);
        preguntaDAO.guardar(preguntaEmparejamiento);
        
        // Recuperar preguntas
        Pregunta seleccionRecuperada = preguntaDAO.buscarPorId(preguntaSeleccion.getId());
        Pregunta emparejamientoRecuperada = preguntaDAO.buscarPorId(preguntaEmparejamiento.getId());
        
        // Verificar tipos
        assertEquals(TipoPregunta.SELECCION_MULTIPLE, seleccionRecuperada.getTipo(), 
                   "El tipo de pregunta de selección múltiple debería conservarse");
        assertEquals(TipoPregunta.EMPAREJAMIENTO, emparejamientoRecuperada.getTipo(), 
                   "El tipo de pregunta de emparejamiento debería conservarse");
    }
}