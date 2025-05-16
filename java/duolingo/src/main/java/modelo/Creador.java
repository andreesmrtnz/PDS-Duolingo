package modelo;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CREADOR")
public class Creador extends Usuario {
    
    // Atributos específicos para creadores (si los hay)
    private int cursosCreados;
    
    public Creador() {
        // Constructor vacío para JPA
    }
    
    public Creador(String nombre, String email, String password) {
        super(nombre, email, password);
        this.cursosCreados = 0;
    }
    
    public int getCursosCreados() {
        return cursosCreados;
    }
    
    public void setCursosCreados(int cursosCreados) {
        this.cursosCreados = cursosCreados;
    }
    
    public void incrementarCursosCreados() {
        this.cursosCreados++;
    }
}