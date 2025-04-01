package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para los bloques de un curso
 */
public class BloqueDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private List<PreguntaDTO> preguntas;
    
    // Constructor vacío para Jackson
    public BloqueDTO() {
    }
    
    // Constructor para crear DTO desde un objeto Bloque
    public BloqueDTO(Bloque bloque) {
        this.id = bloque.getId();
        this.titulo = bloque.getTitulo();
        this.descripcion = bloque.getDescripcion();
        this.preguntas = convertirPreguntasADTO(bloque.getPreguntas());
    }
    
    private List<PreguntaDTO> convertirPreguntasADTO(List<Pregunta> preguntas) {
        List<PreguntaDTO> preguntasDTO = new ArrayList<>();
        for (Pregunta pregunta : preguntas) {
            preguntasDTO.add(new PreguntaDTO(pregunta));
        }
        return preguntasDTO;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<PreguntaDTO> getPreguntas() {
        return preguntas;
    }
    
    public void setPreguntas(List<PreguntaDTO> preguntas) {
        this.preguntas = preguntas;
    }
    
    // Convertir de DTO a objeto de dominio
    public Bloque toBloqueObjeto() {
        List<Pregunta> preguntasObjetos = new ArrayList<>();
        for (PreguntaDTO preguntaDTO : preguntas) {
            preguntasObjetos.add(preguntaDTO.toPreguntaObjeto());
        }
        
        return new Bloque(id, titulo, descripcion, preguntasObjetos);
    }
}