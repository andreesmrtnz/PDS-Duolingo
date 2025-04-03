package modelo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cursos_progreso")
public class CursoEnProgreso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
    
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    
    @Column(name = "fecha_ultima_actividad")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltimaActividad;
    
    @Column(name = "completado")
    private boolean completado;
    
    @Column(name = "preguntas_correctas")
    private int preguntasCorrectas;
    
    @Column(name = "preguntas_incorrectas")
    private int preguntasIncorrectas;
    
    @Column(name = "bloque_actual")
    private int bloqueActual;
    
    @Column(name = "pregunta_actual")
    private int preguntaActual;
    
    // Mapa para registrar el estado de cada pregunta (contestada correctamente, incorrectamente, o no contestada)
    // Esta información se guardará en una tabla separada o se serializará
    @Transient
    private Map<String, Integer> estadoPreguntas = new HashMap<>();
    
    // Constructor vacío para JPA
    public CursoEnProgreso() {
        this.fechaInicio = new Date();
        this.fechaUltimaActividad = new Date();
        this.completado = false;
        this.preguntasCorrectas = 0;
        this.preguntasIncorrectas = 0;
        this.bloqueActual = 0;
        this.preguntaActual = 0;
    }
    
    public CursoEnProgreso(Usuario usuario, Curso curso) {
        this();
        this.usuario = usuario;
        this.curso = curso;
    }
    
    // Métodos para registrar respuestas
    public void registrarRespuestaCorrecta() {
        this.preguntasCorrectas++;
        this.fechaUltimaActividad = new Date();
        // Registrar en el mapa de estado (1 = correcta)
        String clave = bloqueActual + ":" + preguntaActual;
        estadoPreguntas.put(clave, 1);
    }
    
    public void registrarRespuestaIncorrecta() {
        this.preguntasIncorrectas++;
        this.fechaUltimaActividad = new Date();
        // Registrar en el mapa de estado (2 = incorrecta)
        String clave = bloqueActual + ":" + preguntaActual;
        estadoPreguntas.put(clave, 2);
    }
    
    // Método para verificar si la pregunta actual ya fue respondida
    public boolean isPreguntaRespondida() {
        String clave = bloqueActual + ":" + preguntaActual;
        return estadoPreguntas.containsKey(clave);
    }
    
    public int getEstadoPregunta(int bloque, int pregunta) {
        String clave = bloque + ":" + pregunta;
        return estadoPreguntas.getOrDefault(clave, 0); // 0 = no contestada
    }
    
    // Método para avanzar a la siguiente pregunta
    public boolean avanzarPregunta(Curso curso) {
        int numBloques = curso.getBloques().size();
        
        if (bloqueActual >= numBloques) {
            return false; // No hay más bloques
        }
        
        int numPreguntas = curso.getBloques().get(bloqueActual).getPreguntas().size();
        
        if (preguntaActual + 1 < numPreguntas) {
            // Hay más preguntas en este bloque
            preguntaActual++;
            return true;
        } else if (bloqueActual + 1 < numBloques) {
            // Pasar al siguiente bloque
            bloqueActual++;
            preguntaActual = 0;
            return true;
        } else {
            // Curso completado
            completado = true;
            return false;
        }
    }
    
    public boolean retrocederPregunta(Curso curso) {
        if (preguntaActual > 0) {
            // Retroceder dentro del mismo bloque
            preguntaActual--;
            return true;
        } else if (bloqueActual > 0) {
            // Retroceder al bloque anterior
            bloqueActual--;
            int numPreguntas = curso.getBloques().get(bloqueActual).getPreguntas().size();
            preguntaActual = numPreguntas - 1;
            return true;
        } else {
            // Ya estamos en la primera pregunta
            return false;
        }
    }
    
    // Métodos para calcular estadísticas
    public double getPorcentajeCompletado(Curso curso) {
        int totalPreguntas = getTotalPreguntas(curso);
        int preguntasRespondidas = preguntasCorrectas + preguntasIncorrectas;
        
        if (totalPreguntas == 0) return 0;
        
        return (double) preguntasRespondidas / totalPreguntas;
    }
    
    public int getTotalPreguntas(Curso curso) {
        int total = 0;
        for (Bloque bloque : curso.getBloques()) {
            total += bloque.getPreguntas().size();
        }
        return total;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaUltimaActividad() {
        return fechaUltimaActividad;
    }

    public void setFechaUltimaActividad(Date fechaUltimaActividad) {
        this.fechaUltimaActividad = fechaUltimaActividad;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
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

    public int getBloqueActual() {
        return bloqueActual;
    }

    public void setBloqueActual(int bloqueActual) {
        this.bloqueActual = bloqueActual;
    }

    public int getPreguntaActual() {
        return preguntaActual;
    }

    public void setPreguntaActual(int preguntaActual) {
        this.preguntaActual = preguntaActual;
    }

    public Map<String, Integer> getEstadoPreguntas() {
        return estadoPreguntas;
    }

    public void setEstadoPreguntas(Map<String, Integer> estadoPreguntas) {
        this.estadoPreguntas = estadoPreguntas;
    }
}