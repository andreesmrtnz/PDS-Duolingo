package modelo;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
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

    // Nueva relación muchos a muchos para creadores seguidos
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "seguidores",
        joinColumns = @JoinColumn(name = "seguidor_id"),
        inverseJoinColumns = @JoinColumn(name = "creador_id")
    )
    private List<Usuario> creadoresSeguidos = new LinkedList<>();

    // Nueva relación inversa para los seguidores de un creador
    @ManyToMany(mappedBy = "creadoresSeguidos", fetch = FetchType.EAGER)
    private List<Usuario> seguidores = new LinkedList<>();

    public Usuario() { /* Constructor vacío requerido por JPA */ }

    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.estadistica = new Estadistica(null, 0, 0, 0);
        this.estadistica.setUsuario(this);
    }

    // Getters y setters existentes
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Curso> getCursos() { return new LinkedList<>(this.cursos); }
    public Estadistica getEstadistica() { return estadistica; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void addCurso(Curso c) {
        this.cursos.add(c);
        c.setCreador(this);
    }
    public void setEstadistica(Estadistica estadistica) { 
        this.estadistica = estadistica;
        if (estadistica != null) {
            estadistica.setUsuario(this);
        }
    }

    // Nuevos getters y setters para creadores seguidos y seguidores
    public List<Usuario> getCreadoresSeguidos() {
        return new LinkedList<>(this.creadoresSeguidos);
    }

    public void setCreadoresSeguidos(List<Usuario> creadoresSeguidos) {
        this.creadoresSeguidos = new LinkedList<>(creadoresSeguidos);
    }

    public void addCreadorSeguido(Usuario creador) {
        if (creador.esCreador() && !this.creadoresSeguidos.contains(creador)) {
            this.creadoresSeguidos.add(creador);
            creador.addSeguidor(this);
        }
    }

    public void removeCreadorSeguido(Usuario creador) {
        if (this.creadoresSeguidos.contains(creador)) {
            this.creadoresSeguidos.remove(creador);
            creador.removeSeguidor(this);
        }
    }

    public List<Usuario> getSeguidores() {
        return new LinkedList<>(this.seguidores);
    }

    public void setSeguidores(List<Usuario> seguidores) {
        this.seguidores = new LinkedList<>(seguidores);
    }

    private void addSeguidor(Usuario seguidor) {
        if (!this.seguidores.contains(seguidor)) {
            this.seguidores.add(seguidor);
        }
    }

    private void removeSeguidor(Usuario seguidor) {
        this.seguidores.remove(seguidor);
    }

    // Métodos existentes
    public void inicializarEstadistica() {
        if (this.estadistica == null) {
            this.estadistica = new Estadistica(null, 0, 0, 0);
            this.estadistica.setUsuario(this);
        }
    }

    public boolean esCreador() {
        return this instanceof Creador;
    }

    public boolean esEstudiante() {
        return this instanceof Estudiante;
    }

    public Estadistica crearEstadistica() {
        Estadistica estadistica = new Estadistica(null, 0, 0, 0);
        estadistica.setUsuario(this);
        return estadistica;
    }
}