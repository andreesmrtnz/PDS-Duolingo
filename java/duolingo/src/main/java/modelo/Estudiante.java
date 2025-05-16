package modelo;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ESTUDIANTE")
public class Estudiante extends Usuario {
    
    // Atributos específicos para estudiantes (si los hay)
    private int cursosCompletados;
    
    public Estudiante() {
        // Constructor vacío para JPA
    }
    
    public Estudiante(String nombre, String email, String password) {
        super(nombre, email, password);
        this.cursosCompletados = 0;
    }
    
    public int getCursosCompletados() {
        return cursosCompletados;
    }
    
    public void setCursosCompletados(int cursosCompletados) {
        this.cursosCompletados = cursosCompletados;
    }
    
    public void incrementarCursosCompletados() {
        this.cursosCompletados++;
    }
    
    public CursoEnProgreso iniciarCurso(Curso curso, Estrategia estrategia) {
        CursoEnProgreso progreso = new CursoEnProgreso(this, curso);
        progreso.setEstrategia(estrategia);
        return progreso;
    }

    public CursoEnProgreso inscribirEnCurso(Curso curso) {
        CursoEnProgreso nuevaInscripcion = new CursoEnProgreso();
        nuevaInscripcion.setUsuario(this);
        nuevaInscripcion.setCurso(curso);
        nuevaInscripcion.setFechaInicio(new Date());
        nuevaInscripcion.setFechaUltimaActividad(new Date());
        nuevaInscripcion.setPreguntasCorrectas(0);
        nuevaInscripcion.setPreguntasIncorrectas(0);
        nuevaInscripcion.setBloqueActual(0);
        nuevaInscripcion.setPreguntaActual(0);
        nuevaInscripcion.setCompletado(false);
        return nuevaInscripcion;
    }
}