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
    private List<PreguntaDTOTest> preguntas;
    
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
    
    private List<PreguntaDTOTest> convertirPreguntasADTO(List<Pregunta> preguntas) {
        List<PreguntaDTOTest> preguntasDTO = new ArrayList<>();
        for (Pregunta pregunta : preguntas) {
            preguntasDTO.add(PreguntaDTOTest.crearDesdeObjeto(pregunta));
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
    
    public List<PreguntaDTOTest> getPreguntas() {
        return preguntas;
    }
    
    public void setPreguntas(List<PreguntaDTOTest> preguntas) {
        this.preguntas = preguntas;
    }
    
    // Convertir de DTO a objeto de dominio
    public Bloque toBloqueObjeto() {
        List<Pregunta> preguntasObjetos = new ArrayList<>();
        for (PreguntaDTOTest preguntaDTO : preguntas) {
            preguntasObjetos.add(preguntaDTO.toPreguntaObjeto());
        }
        
        return new Bloque(id, titulo, descripcion, preguntasObjetos);
    }
}