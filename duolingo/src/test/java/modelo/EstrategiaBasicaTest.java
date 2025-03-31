// EstrategiaBasicaTest.java
package modelo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class EstrategiaBasicaTest {
    private final Estrategia estrategia = new EstrategiaBasica();

    @Test
    void testSiguienteBloque() {
        Curso curso = new Curso();
        curso.setPosicionActual(0);
        curso.setBloques(List.of(new Bloque(), new Bloque()));
        
        assertEquals(1, estrategia.siguienteBloque(curso));
        curso.setPosicionActual(1);
        assertEquals(1, estrategia.siguienteBloque(curso)); // No debe pasar de último índice
    }

    @Test
    void testHaFinalizado() {
        Curso curso = new Curso();
        curso.setBloques(Collections.emptyList());
        assertTrue(estrategia.haFinalizado(curso));
        
        curso.setBloques(List.of(new Bloque(), new Bloque()));
        curso.setPosicionActual(1);
        assertTrue(estrategia.haFinalizado(curso));
        
        curso.setPosicionActual(0);
        assertFalse(estrategia.haFinalizado(curso));
    }
}