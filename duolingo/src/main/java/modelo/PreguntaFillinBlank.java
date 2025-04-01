package modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("FILL_IN_BLANK")
public class PreguntaFillinBlank extends Pregunta {
    
    @Column(name = "respuesta_correcta_texto")
    private String respuestaCorrectaTexto;
    
    // Constructor sin argumentos requerido por JPA/Hibernate
    public PreguntaFillinBlank() {
        // La llamada a super() es implícita
    }
    
    public PreguntaFillinBlank(String enunciado, String respuestaCorrectaTexto) {
        // Se usa un arreglo vacío para opciones y -1 como respuestaCorrecta ya que no aplica
        super(enunciado, new String[]{}, -1, TipoPregunta.COMPLETAR);
        this.respuestaCorrectaTexto = respuestaCorrectaTexto;
    }
    
    public String getRespuestaCorrectaTexto() {
        return respuestaCorrectaTexto;
    }
    
    public void setRespuestaCorrectaTexto(String respuestaCorrectaTexto) {
        this.respuestaCorrectaTexto = respuestaCorrectaTexto;
    }
}