package modelo;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * DTO base para las preguntas
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo")
@JsonSubTypes({
    @Type(value = PreguntaMultipleChoiceDTO.class, name = "MultipleChoice"),
    @Type(value = PreguntaFillinBlankDTO.class, name = "FillInBlank"),
    @Type(value = PreguntaFlashCardDTO.class, name = "Flashcard")
})
public abstract class PreguntaDTO {
    protected Long id;
    protected String enunciado;
    
    // Constructor vacío para Jackson
    public PreguntaDTO() {
    }
    
    public PreguntaDTO(Long id, String enunciado) {
        this.id = id;
        this.enunciado = enunciado;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEnunciado() {
        return enunciado;
    }
    
    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }
    
    // Método para convertir a objeto de dominio
    public abstract Pregunta toPreguntaObjeto();
    
    // Método de fábrica para crear el DTO adecuado
    public static PreguntaDTO crearDesdeObjeto(Pregunta pregunta) {
        if (pregunta instanceof PreguntaMultipleChoice) {
            PreguntaMultipleChoice pmc = (PreguntaMultipleChoice) pregunta;
            return new PreguntaMultipleChoiceDTO(
                pmc.getId(), 
                pmc.getEnunciado(), 
                pmc.getOpciones(), 
                pmc.getRespuestaCorrecta()
            );
        } else if (pregunta instanceof PreguntaFillinBlank) {
            PreguntaFillinBlank pfb = (PreguntaFillinBlank) pregunta;
            return new PreguntaFillinBlankDTO(
                pfb.getId(), 
                pfb.getEnunciado(), 
                pfb.getTextoConHuecos(), 
                pfb.getRespuestas()
            );
        } else if (pregunta instanceof PreguntaFlashCard) {
            PreguntaFlashCard pfc = (PreguntaFlashCard) pregunta;
            return new PreguntaFlashCardDTO(
                pfc.getId(), 
                pfc.getEnunciado(), 
                pfc.getContenido()
            );
        }
        
        throw new IllegalArgumentException("Tipo de pregunta desconocido: " + pregunta.getClass().getName());
    }
}