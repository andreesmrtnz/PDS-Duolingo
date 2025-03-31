package modelo;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path; // <-- Falta esta importación
import java.util.List;
import org.junit.jupiter.api.Test;

class CursoParserTest {
    private final Usuario creador = new Usuario();
    
    @Test
    void testConvertirDTOaCurso() throws Exception {
        CursoDTO dto = new CursoDTO();
        dto.setId(1L);
        dto.setTitulo("Test Course");
        dto.setDominio("Testing");
        dto.setIdCreador(100L);
        dto.setPosicionActual(0);
        
        BloqueDTO bloqueDTO = new BloqueDTO();
        bloqueDTO.setDescripcion("Test Content");
        dto.setBloques(List.of(bloqueDTO));
        
        Curso curso = CursoParser.convertirDTOaCurso(dto, creador);
        
        assertEquals("Test Course", curso.getTitulo());
        assertEquals(1, curso.getBloques().size());
        assertEquals(creador, curso.getCreador());
    }

    @Test
    void testCargarYGuardarCurso() throws Exception {
        Curso curso = new Curso();
        curso.setTitulo("Temporal");
        curso.setDominio("Test");
        curso.setCreador(creador);
        
        // Test JSON
        String jsonFile = "test.json";
        CursoParser.guardarCurso(curso, jsonFile);
        assertTrue(Files.exists(Path.of(jsonFile))); // Aquí se necesita Path.of(jsonFile)
        
        Curso cargadoJson = CursoParser.cargarCurso(jsonFile, creador);
        assertEquals(curso.getTitulo(), cargadoJson.getTitulo());
        
        // Cleanup
        Files.deleteIfExists(Path.of(jsonFile));
    }
}
