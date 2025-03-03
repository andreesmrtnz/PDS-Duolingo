package modelo;

import java.util.List;

public class Bloque {
    private Long id;
    private String titulo;
    private String descripcion;
    private List<Pregunta> preguntas;
    
    public Bloque(Long id, String titulo, String descripcion, List<Pregunta> preguntas) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.preguntas = preguntas;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public List<Pregunta> getPreguntas() {
        return preguntas;
    }
}