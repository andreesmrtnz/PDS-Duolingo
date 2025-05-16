// CursoDTOTest.java
package modelo;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CursoDTOTest {
    @Test
    void testConversionFromCurso() {
        Usuario creador = new Usuario();
        Bloque bloque = new Bloque();
        bloque.setDescripcion("Hola Mundo");
        List<Bloque> bloques = new ArrayList<>(List.of(bloque));
        
        Curso curso = new Curso(1L, "Java", "Programación", creador, bloques, 0);
        CursoDTO dto = new CursoDTO(curso);
        
        assertEquals(1L, dto.getId());
        assertEquals("Java", dto.getTitulo());
        assertEquals("Programación", dto.getDominio());
        assertEquals(100L, dto.getIdCreador());
        assertEquals(1, dto.getBloques().size());
        assertEquals(0, dto.getPosicionActual());
    }

    @Test
    void testSettersAndGetters() {
        CursoDTO dto = new CursoDTO();
        dto.setId(2L);
        dto.setTitulo("Python");
        dto.setDominio("Data Science");
        dto.setIdCreador(200L);
        dto.setPosicionActual(5);
        
        assertEquals(2L, dto.getId());
        assertEquals("Python", dto.getTitulo());
        assertEquals("Data Science", dto.getDominio());
        assertEquals(200L, dto.getIdCreador());
        assertEquals(5, dto.getPosicionActual());
    }
}