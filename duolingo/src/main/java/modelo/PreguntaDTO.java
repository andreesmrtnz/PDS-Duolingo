package modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreguntaDTO {
    private String enunciado;
    private String[] opciones;
    private int respuestaCorrecta;
    private String tipo;
    private int[] seleccionUsuario;
    private int[] seleccionesEmparejamiento;
    private List<String> ordenSeleccionado;
    
    // Constructor vacío
    public PreguntaDTO() {
    }
    
    // Constructor que copia los datos de una instancia de Pregunta
    public PreguntaDTO(Pregunta pregunta) {
        this.enunciado = pregunta.getEnunciado();
        this.opciones = pregunta.getOpciones();
        this.respuestaCorrecta = pregunta.getRespuestaCorrecta();
        this.tipo = pregunta.getTipo().toString();
        this.seleccionUsuario = pregunta.getSeleccionUsuario();
        this.seleccionesEmparejamiento = pregunta.getSeleccionesEmparejamiento();
        this.ordenSeleccionado = pregunta.getOrdenSeleccionado();
    }
    
    // Getters y setters...
    public String getEnunciado() {
        return enunciado;
    }
    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }
    public String[] getOpciones() {
        return opciones;
    }
    public void setOpciones(String[] opciones) {
        this.opciones = opciones;
    }
    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }
    public void setRespuestaCorrecta(int respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public int[] getSeleccionUsuario() {
        return seleccionUsuario;
    }
    public void setSeleccionUsuario(int[] seleccionUsuario) {
        this.seleccionUsuario = seleccionUsuario;
    }
    public int[] getSeleccionesEmparejamiento() {
        return seleccionesEmparejamiento;
    }
    public void setSeleccionesEmparejamiento(int[] seleccionesEmparejamiento) {
        this.seleccionesEmparejamiento = seleccionesEmparejamiento;
    }
    public List<String> getOrdenSeleccionado() {
        return ordenSeleccionado;
    }
    public void setOrdenSeleccionado(List<String> ordenSeleccionado) {
        this.ordenSeleccionado = ordenSeleccionado;
    }
    
    /**
     * Convierte este DTO en una instancia de Pregunta (o alguna de sus subclases)
     * según el valor del campo "tipo".
     */
    public Pregunta toPreguntaObjeto() {
        // Se asume que el campo "tipo" en el JSON puede ser, por ejemplo, "MultipleChoice", "FlashCard" o "FillinBlank"
        if ("MultipleChoice".equalsIgnoreCase(tipo)) {
            return new PreguntaMultipleChoice(enunciado, opciones, respuestaCorrecta);
        } else if ("FlashCard".equalsIgnoreCase(tipo)) {
            // En este ejemplo se asume que para flashcards la respuesta está en el primer elemento de "opciones"
            String respuesta = (opciones != null && opciones.length > 0) ? opciones[0] : "";
            return new PreguntaFlashCard(enunciado, respuesta);
        } else if ("FillinBlank".equalsIgnoreCase(tipo)) {
            // Para preguntas fill in the blank podrías usar una implementación específica.
            // Aquí, como ejemplo, se reutiliza la estructura base usando el tipo COMPLETAR.
            return new Pregunta(enunciado, opciones, respuestaCorrecta, Pregunta.TipoPregunta.COMPLETAR);
        } else {
            // Valor por defecto: se crea una pregunta de selección múltiple
            return new Pregunta(enunciado, opciones, respuestaCorrecta, Pregunta.TipoPregunta.SELECCION_MULTIPLE);
        }
    }
}
