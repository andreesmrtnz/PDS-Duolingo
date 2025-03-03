package modelo;

/**
 * Interfaz para diferentes estrategias de presentación de cursos
 */
public interface Estrategia {
    /**
     * Determina el siguiente bloque a mostrar
     * @param curso El curso actual
     * @return El índice del siguiente bloque
     */
    int siguienteBloque(Curso curso);
    
    /**
     * Determina si el curso ha finalizado
     * @param curso El curso actual
     * @return true si el curso ha finalizado, false en caso contrario
     */
    boolean haFinalizado(Curso curso);
}