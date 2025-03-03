package modelo;

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
    // Podrías añadir un campo password
    private String password;

    // Ejemplo de relación con "Curso" si es bidireccional:
    // @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    // private List<Curso> cursosInstalados;

    // @OneToOne (cascade = CascadeType.ALL)
    // private Estadistica estadisticas;

    public Usuario() { /* Constructor vacío requerido por JPA */ }

    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
