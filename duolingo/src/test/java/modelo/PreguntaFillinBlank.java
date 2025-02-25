package modelo;

import java.util.List;

public class PreguntaFillinBlank extends Pregunta{
	
	private String textoConHuecos;
	private List<String> respuestas;
	    
	public PreguntaFillinBlank(Long id, String enunciado, String textoConHuecos, List<String> respuestas) {
	      super(id, enunciado, "FillInBlank");	
	      this.textoConHuecos = textoConHuecos;
	      this.respuestas = respuestas;
	    }

	public String getTextoConHuecos() {
		return textoConHuecos;
	}

	public List<String> getRespuestas() {
		return respuestas;
	}
	
}
