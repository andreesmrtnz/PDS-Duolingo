package modelo;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

@Entity
public class Bloque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    
    @OneToMany(mappedBy = "bloque", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Pregunta> preguntas = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    
    // Constructor sin argumentos requerido por Hibernate
    public Bloque() {
        // Se deja vacío intencionalmente
    }
    
    // Constructor existente con todos los parámetros
    public Bloque(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.preguntas = new ArrayList<>();
    }
    
    // Constructor con ID incluido
    public Bloque(Long id, String titulo, String descripcion, List<Pregunta> preguntas) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.preguntas = preguntas;
        // Importante: establecer la relación bidireccional
        if (preguntas != null) {
            for (Pregunta pregunta : preguntas) {
                pregunta.setBloque(this);
            }
        }
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
    
    public List<Pregunta> getPreguntas() {
        return preguntas;
    }
    
    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas.clear();
        if (preguntas != null) {
            for (Pregunta pregunta : preguntas) {
                addPregunta(pregunta);
            }
        }
    }
    
    // Método helper para agregar una pregunta manteniendo la relación bidireccional
    public void addPregunta(Pregunta pregunta) {
        this.preguntas.add(pregunta);
        pregunta.setBloque(this);
    }
    
    // Método helper para quitar una pregunta manteniendo la relación bidireccional
    public void removePregunta(Pregunta pregunta) {
        this.preguntas.remove(pregunta);
        pregunta.setBloque(null);
    }
    
    public Curso getCurso() {
        return curso;
    }
    
    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}