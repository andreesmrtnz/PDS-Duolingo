package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PreguntaMultipleChoiceTest {

    @Test
    public void testConstructorYGetters() {
        String enunciado = "¿Cuál es el planeta más grande del sistema solar?";
        String[] opciones = {"Tierra", "Júpiter", "Marte", "Venus"};
        int respuestaCorrecta = 1; // índice de "Júpiter"
        PreguntaMultipleChoice pregunta = new PreguntaMultipleChoice(enunciado, opciones, respuestaCorrecta);
        
        // Se verifica que el enunciado se establezca correctamente
        assertEquals(enunciado, pregunta.getEnunciado());
        // Se verifica que las opciones se establezcan correctamente
        assertArrayEquals(opciones, pregunta.getOpciones());
        // Se verifica que la respuesta correcta se obtenga correctamente
        assertEquals(respuestaCorrecta, pregunta.getRespuestaCorrecta());
        // Se verifica que el tipo de pregunta sea SELECCION_MULTIPLE
        assertEquals(Pregunta.TipoPregunta.SELECCION_MULTIPLE, pregunta.getTipo());
    }
}
