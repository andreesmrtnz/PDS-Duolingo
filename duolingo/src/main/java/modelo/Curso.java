package modelo;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;

@Entity
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    private String dominio;
    
    // This is the creator relationship
    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;
    
    // This is the bidirectional relationship with Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bloque> bloques;

    
    private int posicionActual;
       
    // Constructor sin argumentos es obligatorio para JPA
    public Curso() {}
    
    // Constructor existente y getters
    public Curso(Long id, String titulo, String dominio, Usuario creador, List<Bloque> bloques, int posicionActual) {
        this.id = id;
        this.titulo = titulo;
        this.dominio = dominio;
        this.creador = creador;
        this.usuario = creador; // Initialize the user with the creator by default
        this.bloques = bloques;
        this.posicionActual = posicionActual;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getDominio() {
        return dominio;
    }
    
    public Usuario getCreador() {
        return creador;
    }
    
    public List<Bloque> getBloques() {
        return bloques;
    }
    
    public int getPosicionActual() {
        return posicionActual;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
    
    public void setCreador(Usuario creador) {
        this.creador = creador;
    }
    
    public void setBloques(List<Bloque> bloques) {
        this.bloques = bloques;
    }
    
    public void setPosicionActual(int posicionActual) {
        this.posicionActual = posicionActual;
    }
    
    // Add these methods for the bidirectional relationship
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}