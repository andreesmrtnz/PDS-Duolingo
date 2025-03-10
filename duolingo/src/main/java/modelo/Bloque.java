package modelo;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Bloque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Pregunta> preguntas;
    @ManyToOne
    private Curso curso;

    
    // Constructor sin argumentos requerido por Hibernate
    public Bloque() {
        // Se deja vacío intencionalmente
    }
    
    // Constructor existente con todos los parámetros
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