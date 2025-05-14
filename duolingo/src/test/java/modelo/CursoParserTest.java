package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CursoParserTest {

    private Usuario creador;
    private Curso curso;
    private Path tempFile;

    @TempDir
    Path tempDir; // JUnit 5 provides a temporary directory for testing

    @BeforeEach
    void setUp() throws IOException {
        // Initialize a creator (Usuario) - assuming constructor sets id
        creador = new Usuario("Andrés", "a@gmail.com", "andres"); // Adjust constructor args based on your Usuario class
        // Note: Removed setId call since it’s undefined; use constructor instead

        // Initialize a Curso with Bloques and Preguntas
        curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Curso de Prueba");
        curso.setDominio("Educación");
        curso.setCreador(creador);
        curso.setPosicionActual(0);

        // Create a Bloque with Preguntas
        List<Pregunta> preguntas = new ArrayList<>();
        PreguntaMultipleChoice preguntaMC = new PreguntaMultipleChoice();
        preguntaMC.setId(1L);
        preguntaMC.setEnunciado("¿Cuál es la capital de Francia?");
        preguntaMC.setOpciones(Arrays.asList("Madrid", "París", "Londres").toArray(new String[0])); // Convert List to String[]
        preguntaMC.setRespuestaCorrecta(1);
        preguntas.add(preguntaMC);

        PreguntaFillinBlank preguntaFIB = new PreguntaFillinBlank(); // Added import
        preguntaFIB.setId(2L);
        preguntaFIB.setEnunciado("La capital de España es ____.");
        preguntaFIB.setRespuestaCorrectaTexto("Madrid");
        preguntas.add(preguntaFIB);

        Bloque bloque = new Bloque(1L, "Bloque 1", "Descripción del Bloque 1", preguntas);
        curso.setBloques(List.of(bloque));

        // Set bidirectional relationships
        bloque.setCurso(curso);
        for (Pregunta pregunta : preguntas) {
            pregunta.setBloque(bloque);
        }

        // Create a temporary file path for JSON
        tempFile = tempDir.resolve("testCurso.json");
    }

    @Test
    void testGuardarYcargarCurso_Valido() throws IOException {
        // Guardar el curso en un archivo JSON
        CursoParser.guardarCurso(curso, tempFile.toString());

        // Verificar que el archivo existe
        assertTrue(Files.exists(tempFile), "El archivo JSON debería existir después de guardar");

        // Cargar el curso desde el archivo JSON
        Curso cursoCargado = CursoParser.cargarCurso(tempFile.toString(), creador);

        // Verificar los datos del curso cargado
        assertNotNull(cursoCargado, "El curso cargado no debería ser nulo");
        assertEquals(curso.getId(), cursoCargado.getId(), "El ID del curso debería coincidir");
        assertEquals(curso.getTitulo(), cursoCargado.getTitulo(), "El título del curso debería coincidir");
        assertEquals(curso.getDominio(), cursoCargado.getDominio(), "El dominio del curso debería coincidir");
        assertEquals(curso.getPosicionActual(), cursoCargado.getPosicionActual(), "La posición actual debería coincidir");

        // Verificar el creador
        assertEquals(creador.getId(), cursoCargado.getCreador().getId(), "El ID del creador debería coincidir");

        // Verificar los bloques
        assertEquals(1, cursoCargado.getBloques().size(), "Debería haber un bloque");
        Bloque bloqueCargado = cursoCargado.getBloques().get(0);
        assertEquals("Bloque 1", bloqueCargado.getTitulo(), "El título del bloque debería coincidir");
        assertEquals("Descripción del Bloque 1", bloqueCargado.getDescripcion(), "La descripción del bloque debería coincidir");
        assertEquals(cursoCargado, bloqueCargado.getCurso(), "La relación bidireccional curso-bloque debería estar establecida");

        // Verificar las preguntas
        assertEquals(2, bloqueCargado.getPreguntas().size(), "Debería haber dos preguntas");
        Pregunta pregunta1 = bloqueCargado.getPreguntas().get(0);
        assertTrue(pregunta1 instanceof PreguntaMultipleChoice, "La primera pregunta debería ser de tipo PreguntaMultipleChoice");
        PreguntaMultipleChoice preguntaMC = (PreguntaMultipleChoice) pregunta1;
        assertEquals("¿Cuál es la capital de Francia?", preguntaMC.getEnunciado(), "El enunciado de la pregunta debería coincidir");
        assertArrayEquals(new String[]{"Madrid", "París", "Londres"}, preguntaMC.getOpciones(), "Las opciones deberían coincidir");
        assertEquals(1, preguntaMC.getRespuestaCorrecta(), "La respuesta correcta debería coincidir");
        assertEquals(bloqueCargado, pregunta1.getBloque(), "La relación bidireccional bloque-pregunta debería estar establecida");

        Pregunta pregunta2 = bloqueCargado.getPreguntas().get(1);
        assertTrue(pregunta2 instanceof PreguntaFillinBlank, "La segunda pregunta debería ser de tipo PreguntaFillInBlank");
        PreguntaFillinBlank preguntaFIB = (PreguntaFillinBlank) pregunta2;
        assertEquals("La capital de España es ____.", preguntaFIB.getEnunciado(), "El enunciado de la pregunta debería coincidir");
        assertEquals("Madrid", preguntaFIB.getRespuestaCorrectaTexto(), "La respuesta correcta debería coincidir");
    }

    @Test
    void testCargarCurso_ArchivoNoExistente() {
        // Intentar cargar un archivo que no existe
        Exception exception = assertThrows(IOException.class, () -> {
            CursoParser.cargarCurso(tempDir.resolve("noExiste.json").toString(), creador);
        });
        assertTrue(exception.getMessage().contains("Error al leer el archivo JSON"), "El mensaje de error debería indicar un problema al leer el archivo");
    }

    @Test
    void testCargarCurso_JSONInvalido() throws IOException {
        // Crear un archivo con JSON inválido
        Files.writeString(tempFile, "{invalid json}");

        // Intentar cargar el archivo con JSON inválido
        Exception exception = assertThrows(IOException.class, () -> {
            CursoParser.cargarCurso(tempFile.toString(), creador);
        });
        assertTrue(exception.getMessage().contains("Error al procesar los datos del curso"), "El mensaje de error debería indicar un problema al procesar los datos");
    }

    @Test
    void testGuardarCurso_IOException() throws IOException {
        // Crear un archivo de solo lectura para simular un error de escritura
        Files.createFile(tempFile);
        tempFile.toFile().setReadOnly();

        // Intentar guardar el curso en un archivo de solo lectura
        Exception exception = assertThrows(IOException.class, () -> {
            CursoParser.guardarCurso(curso, tempFile.toString());
        });
        assertTrue(exception.getMessage().contains("Error al escribir el archivo JSON"), "El mensaje de error debería indicar un problema al escribir el archivo");

        // Restaurar permisos para limpieza
        tempFile.toFile().setWritable(true);
    }

    @Test
    void testCargarCurso_UsuarioActualComoCreador() throws IOException {
        // Guardar un curso sin especificar un creador diferente
        CursoParser.guardarCurso(curso, tempFile.toString());

        // Cargar el curso con el usuario actual como creador
        Curso cursoCargado = CursoParser.cargarCurso(tempFile.toString(), creador);

        // Verificar que el creador es el usuario actual
        assertEquals(creador.getId(), cursoCargado.getCreador().getId(), "El creador debería ser el usuario actual");
    }
}