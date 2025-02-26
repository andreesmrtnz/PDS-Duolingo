package modelo;

public class Pregunta {
	 protected Long id;
	    protected String enunciado;
	    protected String tipo;
	    
	    public Pregunta(Long id, String enunciado, String tipo) {
	        this.id = id;
	        this.enunciado = enunciado;
	        this.tipo = tipo;
	    }
	    
	    public Long getId() { 
	    	return id; 
	    	}
	    
	    public String getEnunciado() { 
	    	return enunciado; 
	    	}
	    
	    public String getTipo() { 
	    	return tipo; 
	    	}
}
