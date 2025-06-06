package modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("FLASH_CARD")
public class PreguntaFlashCard extends Pregunta {
    
    // Almacena la respuesta que se mostrará en el reverso de la flashcard
    @Column(name = "respuesta_flash_card")
    private String respuestaFlashCard;
    
    // Constructor sin argumentos requerido por JPA/Hibernate
    public PreguntaFlashCard() {
        // La llamada a super() es implícita
    }
    
    public PreguntaFlashCard(String frente, String respuesta) {
        // Se usa el enunciado como el frente y se coloca la respuesta en un arreglo con un solo elemento.
        // Se usa 0 como respuestaCorrecta (ya que es el único elemento) y se asigna un tipo genérico (SELECCION_MULTIPLE) para reutilizar la estructura.
        super(frente, new String[]{respuesta}, 0, TipoPregunta.SELECCION_MULTIPLE);
        this.respuestaFlashCard = respuesta;
    }
    
    public String getRespuestaFlashCard() {
        return respuestaFlashCard;
    }
    
    public void setRespuestaFlashCard(String respuestaFlashCard) {
        this.respuestaFlashCard = respuestaFlashCard;
    }
}