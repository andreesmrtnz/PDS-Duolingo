// CursoTest.java
package modelo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CursoTest {
    private Curso curso;
    private Usuario creador;
    private Bloque bloque1;
    private Bloque bloque2;

    @BeforeEach
    void setUp() {
        creador = new Usuario();
        bloque1 = new Bloque();
        bloque2 = new Bloque();
        List<Bloque> bloques = new ArrayList<>(List.of(bloque1, bloque2));
        
        curso = new Curso(1L, "Java Basics", "Programación", creador, bloques, 0);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1L, curso.getId());
        assertEquals("Java Basics", curso.getTitulo());
        assertEquals("Programación", curso.getDominio());
        assertEquals(creador, curso.getCreador());
        assertEquals(2, curso.getBloques().size());
        assertEquals(0, curso.getPosicionActual());
        assertEquals(creador, curso.getUsuario());
    }

    @Test
    void testAddAndRemoveBloque() {
        Bloque nuevoBloque = new Bloque();
        curso.addBloque(nuevoBloque);
        
        assertEquals(3, curso.getBloques().size());
        assertEquals(curso, nuevoBloque.getCurso());
        
        curso.removeBloque(nuevoBloque);
        assertEquals(2, curso.getBloques().size());
        assertNull(nuevoBloque.getCurso());
    }

    @Test
    void testSetBloques() {
        Bloque nuevoBloque = new Bloque();
        curso.setBloques(List.of(nuevoBloque));
        
        assertEquals(1, curso.getBloques().size());
        assertEquals(curso, nuevoBloque.getCurso());
    }

    @Test
    void testSetters() {
        curso.setTitulo("Java Advanced");
        curso.setDominio("POO");
        curso.setPosicionActual(1);
        
        assertEquals("Java Advanced", curso.getTitulo());
        assertEquals("POO", curso.getDominio());
        assertEquals(1, curso.getPosicionActual());
    }
}