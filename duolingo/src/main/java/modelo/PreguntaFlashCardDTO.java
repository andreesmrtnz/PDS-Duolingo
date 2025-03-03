package modelo;

import java.util.List;

//DTO para preguntas de tipo flashcard
class PreguntaFlashCardDTO extends PreguntaDTO {
 private String contenido;
 
 // Constructor vacío para Jackson
 public PreguntaFlashCardDTO() {
 }
 
 public PreguntaFlashCardDTO(Long id, String enunciado, String contenido) {
     super(id, enunciado);
     this.contenido = contenido;
 }
 
 public String getContenido() {
     return contenido;
 }
 
 public void setContenido(String contenido) {
     this.contenido = contenido;
 }
 
 @Override
 public Pregunta toPreguntaObjeto() {
     return new PreguntaFlashCard(id, enunciado, contenido);
 }
}