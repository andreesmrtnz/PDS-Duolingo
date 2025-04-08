package modelo;

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
}