package modelo;

import java.util.List;

public class PreguntaMultipleChoice extends Pregunta {
	private List<String> opciones;
    private String respuestaCorrecta;
    
    public PreguntaMultipleChoice(Long id, String enunciado, List<String> opciones, String respuestaCorrecta) {
        super(id, enunciado, "MultipleChoice");
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
    }

	public List<String> getOpciones() {
		return opciones;
	}

	public String getRespuestaCorrecta() {
		return respuestaCorrecta;
	}
    
}
