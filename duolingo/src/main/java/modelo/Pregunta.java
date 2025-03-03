package modelo;

import java.util.List;

public class Pregunta {
    
    // Enumeración para los tipos de pregunta
    public enum TipoPregunta {
        SELECCION_MULTIPLE,
        EMPAREJAMIENTO,
        ORDENAR_PALABRAS,
        COMPLETAR
    }
    
    // Atributos de la pregunta
    private String enunciado;
    private String[] opciones;
    private int respuestaCorrecta;
    private TipoPregunta tipo;
    
    // Atributos para almacenar las respuestas del usuario
    private int[] seleccionUsuario;
    private int[] seleccionesEmparejamiento;
    private List<String> ordenSeleccionado;
    
    // Constructor
    public Pregunta(String enunciado, String[] opciones, int respuestaCorrecta, TipoPregunta tipo) {
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.tipo = tipo;
    }
    
    // Getters y setters
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
    
    public TipoPregunta getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoPregunta tipo) {
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
}