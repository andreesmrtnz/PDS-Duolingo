package modelo;

import modelo.Pregunta;
import modelo.PreguntaDTO;
import modelo.Pregunta.TipoPregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PreguntaDTOTest {
    private Pregunta pregunta;
    private PreguntaDTO preguntaDTO;

    @BeforeEach
    void setUp() {
        List<String> opciones = Arrays.asList("Opción 1", "Opción 2", "Opción 3");
        pregunta = new Pregunta("¿Cuál es la respuesta correcta?", opciones, 1, TipoPregunta.SELECCION_MULTIPLE);
        preguntaDTO = new PreguntaDTO(pregunta);
    }

    @Test
    void testPreguntaDTOConversion() {
        assertEquals(pregunta.getEnunciado(), preguntaDTO.getEnunciado());
        assertArrayEquals(pregunta.getOpciones(), preguntaDTO.getOpciones());
        assertEquals(pregunta.getRespuestaCorrecta(), preguntaDTO.getRespuestaCorrecta());
        assertEquals(pregunta.getTipo().toString(), preguntaDTO.getTipo());
    }
}
