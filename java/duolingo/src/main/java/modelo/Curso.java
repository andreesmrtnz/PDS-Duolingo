package modelo;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    
    // Creador del curso
    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;
    
    // Usuario que está realizando el curso
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // Relación con Bloques (bidireccional)
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Bloque> bloques = new ArrayList<>();
    
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
        this.posicionActual = posicionActual;
        
        // Establecer la relación bidireccional
        setBloques(bloques);
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
        this.bloques.clear();
        if (bloques != null) {
            for (Bloque bloque : bloques) {
                addBloque(bloque);
            }
        }
    }
    
    // Método helper para agregar un bloque manteniendo la relación bidireccional
    public void addBloque(Bloque bloque) {
        this.bloques.add(bloque);
        bloque.setCurso(this);
    }
    
    // Método helper para quitar un bloque manteniendo la relación bidireccional
    public void removeBloque(Bloque bloque) {
        this.bloques.remove(bloque);
        bloque.setCurso(null);
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