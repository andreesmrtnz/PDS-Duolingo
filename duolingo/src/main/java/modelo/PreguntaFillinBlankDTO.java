package modelo;

import java.util.List;

class PreguntaFillinBlankDTO extends PreguntaDTO {
    private String textoConHuecos;
    private List<String> respuestas;
    
    // Constructor vacío para Jackson
    public PreguntaFillinBlankDTO() {
    }
    
    public PreguntaFillinBlankDTO(Long id, String enunciado, String textoConHuecos, List<String> respuestas) {
        super(id, enunciado);
        this.textoConHuecos = textoConHuecos;
        this.respuestas = respuestas;
    }
    
    public String getTextoConHuecos() {
        return textoConHuecos;
    }
    
    public void setTextoConHuecos(String textoConHuecos) {
        this.textoConHuecos = textoConHuecos;
    }
    
    public List<String> getRespuestas() {
        return respuestas;
    }
    
    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }
    
    @Override
    public Pregunta toPreguntaObjeto() {
        return new PreguntaFillinBlank(id, enunciado, textoConHuecos, respuestas);
    }
}
