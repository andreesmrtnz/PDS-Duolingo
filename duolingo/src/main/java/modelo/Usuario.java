package modelo;

import java.util.List;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private List<Curso> cursosInstalados;
    private Estadistica estadisticas;
    
    public Usuario(Long id, String nombre, String email, List<Curso> cursosInstalados, Estadistica estadisticas) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.cursosInstalados = cursosInstalados;
        this.estadisticas = estadisticas;
    }

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getEmail() {
		return email;
	}

	public List<Curso> getCursosInstalados() {
		return cursosInstalados;
	}

	public Estadistica getEstadisticas() {
		return estadisticas;
	}
    
}
