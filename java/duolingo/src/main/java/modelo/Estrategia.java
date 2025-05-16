package modelo;

/**
 * Enum para diferentes estrategias de presentación de cursos
 */
public enum Estrategia {
    SECUENCIAL,        // Presenta las preguntas en orden secuencial
    ALEATORIO,         // Presenta las preguntas en orden aleatorio
    REPETICION_ESPACIADA  // Repite preguntas periódicamente (ej: cada 3 preguntas)
}