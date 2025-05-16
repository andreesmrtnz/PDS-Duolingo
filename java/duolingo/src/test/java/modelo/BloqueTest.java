package modelo;

import modelo.Bloque;
import modelo.Pregunta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de prueba para la entidad Bloque.
 */
public class BloqueTest {

    /**
     * DummyPregunta simula la clase Pregunta para efectos de prueba.
     */
    public static class DummyPregunta extends Pregunta {
        private Bloque bloque;

        @Override
        public void setBloque(Bloque bloque) {
            this.bloque = bloque;
        }

        @Override
        public Bloque getBloque() {
            return this.bloque;
        }
    }

    @Test
    public void testConstructorSinPreguntas() {
        Bloque bloque = new Bloque("Título de prueba", "Descripción de prueba");
        assertEquals("Título de prueba", bloque.getTitulo());
        assertEquals("Descripción de prueba", bloque.getDescripcion());
        assertNotNull(bloque.getPreguntas());
        assertTrue(bloque.getPreguntas().isEmpty());
    }

    @Test
    public void testAddPregunta() {
        Bloque bloque = new Bloque("Título", "Descripción");
        DummyPregunta pregunta = new DummyPregunta();
        bloque.addPregunta(pregunta);

        // Verifica que la pregunta se haya agregado a la lista
        assertTrue(bloque.getPreguntas().contains(pregunta));
        // Verifica que la relación bidireccional se haya establecido
        assertEquals(bloque, pregunta.getBloque());
    }

    @Test
    public void testRemovePregunta() {
        Bloque bloque = new Bloque("Título", "Descripción");
        DummyPregunta pregunta = new DummyPregunta();
        bloque.addPregunta(pregunta);
        // Ahora removemos la pregunta
        bloque.removePregunta(pregunta);

        // Se verifica que la pregunta ya no esté en la lista
        assertFalse(bloque.getPreguntas().contains(pregunta));
        // Se verifica que la relación bidireccional se haya anulado
        assertNull(pregunta.getBloque());
    }

    @Test
    public void testSetPreguntas() {
        Bloque bloque = new Bloque("Título", "Descripción");
        DummyPregunta pregunta = new DummyPregunta();
        List<Pregunta> listaPreguntas = new ArrayList<>();
        listaPreguntas.add(pregunta);

        // Se establece la lista de preguntas
        bloque.setPreguntas(listaPreguntas);

        // Se verifica que la lista se haya establecido correctamente
        assertEquals(1, bloque.getPreguntas().size());
        // Además se comprueba que se haya establecido la relación bidireccional en cada pregunta
        assertEquals(bloque, pregunta.getBloque());
    }
}
