package modelo;

public class PreguntaFlashCard extends Pregunta{
	 private String contenido;
	    
	 public PreguntaFlashCard(Long id, String enunciado, String contenido) {
	     super(id, enunciado, "Flashcard");
	     this.contenido = contenido;
	    }

	public String getContenido() {
		return contenido;
	}
	 
}
