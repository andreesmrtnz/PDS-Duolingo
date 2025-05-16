package modelo;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "estadisticas")
public class Estadistica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private long tiempoTotalUso;
    private int mejorRacha;
    private int diasConsecutivos;
    private int preguntasCorrectas;
    private int preguntasIncorrectas;
    private int cursosTotales;
    private int cursosCompletados;
    private Date ultimaConexion;
    
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    public Estadistica() {
        // Constructor vacío requerido por JPA
    }
    
    public Estadistica(Long id, long tiempoTotalUso, int mejorRacha, int diasConsecutivos) {
        this.id = id;
        this.tiempoTotalUso = tiempoTotalUso;
        this.mejorRacha = mejorRacha;
        this.diasConsecutivos = diasConsecutivos;
        this.preguntasCorrectas = 0;
        this.preguntasIncorrectas = 0;
        this.cursosTotales = 0;
        this.cursosCompletados = 0;
        this.ultimaConexion = new Date();
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public long getTiempoTotalUso() {
        return tiempoTotalUso;
    }
    
    public void setTiempoTotalUso(long tiempoTotalUso) {
        this.tiempoTotalUso = tiempoTotalUso;
    }

    public int getMejorRacha() {
        return mejorRacha;
    }
    
    public void setMejorRacha(int mejorRacha) {
        this.mejorRacha = mejorRacha;
    }

    public int getDiasConsecutivos() {
        return diasConsecutivos;
    }
    
    public void setDiasConsecutivos(int diasConsecutivos) {
        this.diasConsecutivos = diasConsecutivos;
    }
    
    public int getPreguntasCorrectas() {
        return preguntasCorrectas;
    }
    
    public void setPreguntasCorrectas(int preguntasCorrectas) {
        this.preguntasCorrectas = preguntasCorrectas;
    }
    
    public int getPreguntasIncorrectas() {
        return preguntasIncorrectas;
    }
    
    public void setPreguntasIncorrectas(int preguntasIncorrectas) {
        this.preguntasIncorrectas = preguntasIncorrectas;
    }
    
    public int getCursosTotales() {
        return cursosTotales;
    }
    
    public void setCursosTotales(int cursosTotales) {
        this.cursosTotales = cursosTotales;
    }
    
    public int getCursosCompletados() {
        return cursosCompletados;
    }
    
    public void setCursosCompletados(int cursosCompletados) {
        this.cursosCompletados = cursosCompletados;
    }
    
    public Date getUltimaConexion() {
        return ultimaConexion;
    }
    
    public void setUltimaConexion(Date ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    // Métodos útiles
    public void incrementarPreguntasCorrectas() {
        this.preguntasCorrectas++;
    }
    
    public void incrementarPreguntasIncorrectas() {
        this.preguntasIncorrectas++;
    }
    
    public void registrarActividad() {
        this.ultimaConexion = new Date();
    }
    
    public double getPrecision() {
        if (preguntasCorrectas + preguntasIncorrectas == 0) {
            return 0;
        }
        return (double) preguntasCorrectas / (preguntasCorrectas + preguntasIncorrectas) * 100;
    }
    
    public double getPrecision(CursoEnProgreso progreso) {
        if (progreso != null) {
            int total = progreso.getPreguntasCorrectas() + progreso.getPreguntasIncorrectas();
            if (total == 0) {
                return 0.0;
            }
            return (double) progreso.getPreguntasCorrectas() / total * 100;
        } else {
            // Cálculo global (para estadísticas generales del usuario)
            if (preguntasCorrectas + preguntasIncorrectas == 0) {
                return 0.0;
            }
            return (double) preguntasCorrectas / (preguntasCorrectas + preguntasIncorrectas) * 100;
        }
    }
}