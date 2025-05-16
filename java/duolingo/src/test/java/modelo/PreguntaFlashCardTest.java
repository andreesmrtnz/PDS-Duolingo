package modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PreguntaFlashCardTest {
    
    private PreguntaFlashCard pregunta;
    private final String FRENTE = "¿Capital de Francia?";
    private final String RESPUESTA = "París";

    @BeforeEach
    void setUp() {
        pregunta = new PreguntaFlashCard(FRENTE, RESPUESTA);
    }

    @Test
    void testConstructor() {
        // Verificar herencia de Pregunta
        assertEquals(FRENTE, pregunta.getEnunciado());
        assertArrayEquals(new String[]{RESPUESTA}, pregunta.getOpciones());
        assertEquals(0, pregunta.getRespuestaCorrecta());
        assertEquals(Pregunta.TipoPregunta.SELECCION_MULTIPLE, pregunta.getTipo());
        
        // Verificar atributo específico
        assertEquals(RESPUESTA, pregunta.getRespuestaFlashCard());
    }

    @Test
    void testGetRespuestaFlashCard() {
        assertEquals(RESPUESTA, pregunta.getRespuestaFlashCard());
    }

    @Test
    void testSetRespuestaFlashCard() {
        String nuevaRespuesta = "Lyon";
        pregunta.setRespuestaFlashCard(nuevaRespuesta);
        
        assertEquals(nuevaRespuesta, pregunta.getRespuestaFlashCard());
        // Verificar que también actualiza el array de respuestas
        assertArrayEquals(new String[]{nuevaRespuesta}, pregunta.getOpciones());
    }
}