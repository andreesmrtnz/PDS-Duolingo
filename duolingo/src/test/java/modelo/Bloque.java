package modelo;

import java.util.List;

public class Bloque {
	private Long id;
    private String titulo;
    private int orden;
    private List<Pregunta> preguntas;
    
    public Bloque(Long id, String titulo, int orden, List<Pregunta> preguntas) {
        this.id = id;
        this.titulo = titulo;
        this.orden = orden;
        this.preguntas = preguntas;
    }

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public int getOrden() {
		return orden;
	}

	public List<Pregunta> getPreguntas() {
		return preguntas;
	}
    
}
