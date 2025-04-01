package modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreguntaDTO {
    private Long id;
    private String enunciado;
    private String[] opciones;
    private int respuestaCorrecta;
    private String tipo;
    private int[] seleccionUsuario;
    private int[] seleccionesEmparejamiento;
    private List<String> ordenSeleccionado;
    private String respuestaCorrectaTexto; // Para preguntas de completar
    private String respuestaFlashCard;     // Para flashcards
    
    // Constructor vacío
    public PreguntaDTO() {
    }
    
    // Constructor que copia los datos de una instancia de Pregunta
    public PreguntaDTO(Pregunta pregunta) {
        this.id = pregunta.getId();
        this.enunciado = pregunta.getEnunciado();
        this.opciones = pregunta.getOpciones();
        this.respuestaCorrecta = pregunta.getRespuestaCorrecta();
        
        // Determinar el tipo más específico de la pregunta
        if (pregunta instanceof PreguntaMultipleChoice) {
            this.tipo = "SELECCION_MULTIPLE";
        } else if (pregunta instanceof PreguntaFlashCard) {
            this.tipo = "FLASHCARD";
            this.respuestaFlashCard = ((PreguntaFlashCard) pregunta).getRespuestaFlashCard();
        } else if (pregunta instanceof PreguntaFillinBlank) {
            this.tipo = "COMPLETAR";
            this.respuestaCorrectaTexto = ((PreguntaFillinBlank) pregunta).getRespuestaCorrectaTexto();
        } else {
            // Usar el tipo enumerado como respaldo
            this.tipo = pregunta.getTipo().toString();
        }
        
        this.seleccionUsuario = pregunta.getSeleccionUsuario();
        this.seleccionesEmparejamiento = pregunta.getSeleccionesEmparejamiento();
        this.ordenSeleccionado = pregunta.getOrdenSeleccionado();
    }
    
    // Getters y setters...
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
    
    public String getRespuestaCorrectaTexto() {
        return respuestaCorrectaTexto;
    }
    
    public void setRespuestaCorrectaTexto(String respuestaCorrectaTexto) {
        this.respuestaCorrectaTexto = respuestaCorrectaTexto;
    }
    
    public String getRespuestaFlashCard() {
        return respuestaFlashCard;
    }
    
    public void setRespuestaFlashCard(String respuestaFlashCard) {
        this.respuestaFlashCard = respuestaFlashCard;
    }
    
    /**
     * Convierte este DTO en una instancia de Pregunta (o alguna de sus subclases)
     * según el valor del campo "tipo".
     */
    public Pregunta toPreguntaObjeto() {
        Pregunta pregunta = null;
        
        // Comparamos con los valores para determinar el tipo específico
        if ("SELECCION_MULTIPLE".equals(tipo)) {
            pregunta = new PreguntaMultipleChoice(enunciado, opciones, respuestaCorrecta);
        } else if ("FLASHCARD".equals(tipo)) {
            // Para flashcards, usamos respuestaFlashCard o el primer elemento de opciones como respuesta
            String respuesta = respuestaFlashCard;
            if (respuesta == null && opciones != null && opciones.length > 0) {
                respuesta = opciones[0];
            }
            pregunta = new PreguntaFlashCard(enunciado, respuesta);
        } else if ("COMPLETAR".equals(tipo)) {
            // Para preguntas de completar, usamos respuestaCorrectaTexto o opciones[0]
            String respuestaTexto = respuestaCorrectaTexto;
            if (respuestaTexto == null && opciones != null && opciones.length > 0) {
                respuestaTexto = opciones[0];
            }
            pregunta = new PreguntaFillinBlank(enunciado, respuestaTexto);
        } else if ("EMPAREJAMIENTO".equals(tipo)) {
            pregunta = new Pregunta(enunciado, opciones, respuestaCorrecta, Pregunta.TipoPregunta.EMPAREJAMIENTO);
        } else if ("ORDENAR_PALABRAS".equals(tipo)) {
            pregunta = new Pregunta(enunciado, opciones, respuestaCorrecta, Pregunta.TipoPregunta.ORDENAR_PALABRAS);
        } else {
            // Retrocompatibilidad con valores antiguos
            try {
                // Intentamos convertir a un valor de la enumeración
                Pregunta.TipoPregunta tipoPregunta = Pregunta.TipoPregunta.valueOf(tipo);
                pregunta = new Pregunta(enunciado, opciones, respuestaCorrecta, tipoPregunta);
            } catch (IllegalArgumentException e) {
                // Si no es un valor válido de la enumeración, usamos el valor por defecto
                System.out.println("Tipo de pregunta desconocido: " + tipo + ", usando SELECCION_MULTIPLE como predeterminado");
                pregunta = new PreguntaMultipleChoice(enunciado, opciones, respuestaCorrecta);
            }
        }
        
        // Establecer el ID si existe
        if (id != null) {
            pregunta.setId(id);
        }
        
        return pregunta;
    }
}