package modelo;

/**
 * Implementación básica de estrategia que muestra los bloques en orden secuencial
 */
public class EstrategiaBasica implements Estrategia {
    
    @Override
    public int siguienteBloque(Curso curso) {
        int posActual = curso.getPosicionActual();
        if (posActual < curso.getBloques().size() - 1) {
            return posActual + 1;
        }
        return posActual; // Si estamos en el último, no avanzamos más
    }
    
    @Override
    public boolean haFinalizado(Curso curso) {
        return curso.getPosicionActual() >= curso.getBloques().size() - 1;
    }
}