package modelo;

import java.util.List;

//DTO para preguntas de opción múltiple
class PreguntaMultipleChoiceDTO extends PreguntaDTO {
 private List<String> opciones;
 private String respuestaCorrecta;
 
 // Constructor vacío para Jackson
 public PreguntaMultipleChoiceDTO() {
 }
 
 public PreguntaMultipleChoiceDTO(Long id, String enunciado, List<String> opciones, String respuestaCorrecta) {
     super(id, enunciado);
     this.opciones = opciones;
     this.respuestaCorrecta = respuestaCorrecta;
 }
 
 public List<String> getOpciones() {
     return opciones;
 }
 
 public void setOpciones(List<String> opciones) {
     this.opciones = opciones;
 }
 
 public String getRespuestaCorrecta() {
     return respuestaCorrecta;
 }
 
 public void setRespuestaCorrecta(String respuestaCorrecta) {
     this.respuestaCorrecta = respuestaCorrecta;
 }
 
 @Override
 public Pregunta toPreguntaObjeto() {
     return new PreguntaMultipleChoice(id, enunciado, opciones, respuestaCorrecta);
 }
}
