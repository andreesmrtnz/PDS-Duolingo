package modelo;

public class PreguntaFillinBlank extends Pregunta {
    
    // Almacena la respuesta correcta en texto (por ejemplo, "went")
    private String respuestaCorrectaTexto;
    
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
