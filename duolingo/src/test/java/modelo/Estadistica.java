package modelo;

public class Estadistica {
	 private Long id;
	    private long tiempoTotalUso;
	    private int mejorRacha;
	    private int diasConsecutivos;
	    
	    public Estadistica(Long id, long tiempoTotalUso, int mejorRacha, int diasConsecutivos) {
	        this.id = id;
	        this.tiempoTotalUso = tiempoTotalUso;
	        this.mejorRacha = mejorRacha;
	        this.diasConsecutivos = diasConsecutivos;
	    }

		public Long getId() {
			return id;
		}

		public long getTiempoTotalUso() {
			return tiempoTotalUso;
		}

		public int getMejorRacha() {
			return mejorRacha;
		}

		public int getDiasConsecutivos() {
			return diasConsecutivos;
		}
	    
}
