package modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class PreguntaMultipleChoice extends Pregunta {
    
    // Constructor sin argumentos requerido por JPA/Hibernate
    public PreguntaMultipleChoice() {
        // La llamada a super() es implícita
    }
    
    public PreguntaMultipleChoice(String enunciado, String[] opciones, int respuestaCorrecta) {
        // Se utiliza el tipo SELECCION_MULTIPLE definido en la clase base.
        super(enunciado, opciones, respuestaCorrecta, TipoPregunta.SELECCION_MULTIPLE);
    }
    
    // Aquí se pueden agregar métodos específicos para preguntas de selección múltiple, si fuera necesario.
}