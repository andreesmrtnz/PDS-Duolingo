package modelo;

import modelo.Pregunta;
import modelo.Pregunta.TipoPregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PreguntaTest {
    private Pregunta pregunta;

    @BeforeEach
    void setUp() {
        List<String> opciones = Arrays.asList("Opción 1", "Opción 2", "Opción 3");
        pregunta = new Pregunta("¿Cuál es la respuesta correcta?", opciones, 1, TipoPregunta.SELECCION_MULTIPLE);
    }

    @Test
    void testGetEnunciado() {
        assertEquals("¿Cuál es la respuesta correcta?", pregunta.getEnunciado());
    }

    @Test
    void testGetOpciones() {
        assertArrayEquals(new String[]{"Opción 1", "Opción 2", "Opción 3"}, pregunta.getOpciones());
    }

    @Test
    void testGetRespuestaCorrecta() {
        assertEquals(1, pregunta.getRespuestaCorrecta());
    }

    @Test
    void testSetEnunciado() {
        pregunta.setEnunciado("Nuevo enunciado");
        assertEquals("Nuevo enunciado", pregunta.getEnunciado());
    }
}
