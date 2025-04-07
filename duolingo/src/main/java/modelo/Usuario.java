package modelo;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String password;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Curso> cursos = new LinkedList<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Estadistica estadistica;

    public Usuario() { /* Constructor vacío requerido por JPA */ }

    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        // Inicializar estadísticas para el usuario
        this.estadistica = new Estadistica(null, 0, 0, 0);
        this.estadistica.setUsuario(this);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Curso> getCursos() {
    	return new LinkedList<Curso>(this.cursos);
    }
    public Estadistica getEstadistica() { return estadistica; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void addCurso(Curso c) {
        this.cursos.add(c);
        c.setCreador(this); // Set the back-reference
    }
    public void setEstadistica(Estadistica estadistica) { 
        this.estadistica = estadistica;
        if (estadistica != null) {
            estadistica.setUsuario(this);
        }
    }
    
    public void inicializarEstadistica() {
        if (this.estadistica == null) {
            this.estadistica = new Estadistica(null, 0, 0, 0);
            this.estadistica.setUsuario(this);
        }
    }
}