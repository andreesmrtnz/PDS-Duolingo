package modelo;

import java.util.List;

public class Curso {
	 private Long id;
	    private String titulo;
	    private String dominio;
	    private Usuario creador;
	    private List<Bloque> bloques;
	    private int posicionActual;
	    private Estrategia estrategia;
	    
	    public Curso(Long id, String titulo, String dominio, Usuario creador, List<Bloque> bloques, int posicionActual, Estrategia estrategia) {
	        this.id = id;
	        this.titulo = titulo;
	        this.dominio = dominio;
	        this.creador = creador;
	        this.bloques = bloques;
	        this.posicionActual = posicionActual;
	        this.estrategia = estrategia;
	    }

		public Long getId() {
			return id;
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

		public Estrategia getEstrategia() {
			return estrategia;
		}
	    
}
