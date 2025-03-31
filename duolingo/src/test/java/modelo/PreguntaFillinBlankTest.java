package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PreguntaFillinBlankTest {

    @Test
    public void testConstructorYGetter() {
        String enunciado = "Complete la frase: Yo ____ a la tienda.";
        String respuestaCorrecta = "fui";
        PreguntaFillinBlank pregunta = new PreguntaFillinBlank(enunciado, respuestaCorrecta);
        
        // Se verifica que el enunciado se establezca correctamente (método heredado de Pregunta)
        assertEquals(enunciado, pregunta.getEnunciado());
        // Se verifica que la respuesta correcta se obtenga correctamente
        assertEquals(respuestaCorrecta, pregunta.getRespuestaCorrectaTexto());
        // Se verifica que el tipo de pregunta sea COMPLETAR
        assertEquals(Pregunta.TipoPregunta.COMPLETAR, pregunta.getTipo());
    }

    @Test
    public void testSetRespuestaCorrectaTexto() {
        String enunciado = "Complete la frase: Ella ____ contenta.";
        PreguntaFillinBlank pregunta = new PreguntaFillinBlank(enunciado, "estaba");
        
        // Cambiamos la respuesta correcta
        String nuevaRespuesta = "está";
        pregunta.setRespuestaCorrectaTexto(nuevaRespuesta);
        
        // Se verifica que el setter actualice correctamente la respuesta
        assertEquals(nuevaRespuesta, pregunta.getRespuestaCorrectaTexto());
    }
}
