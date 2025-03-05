package modelo;

public class PreguntaMultipleChoice extends Pregunta {
    
    public PreguntaMultipleChoice(String enunciado, String[] opciones, int respuestaCorrecta) {
        // Se utiliza el tipo SELECCION_MULTIPLE definido en la clase base.
        super(enunciado, opciones, respuestaCorrecta, TipoPregunta.SELECCION_MULTIPLE);
    }
    
    // Aquí se pueden agregar métodos específicos para preguntas de selección múltiple, si fuera necesario.
}
