

//RepositorioCursosTest.java
package modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioCursosTest {
 private RepositorioCursos repositorio;
 private Curso curso;

 @BeforeEach
 void setUp() {
     repositorio = RepositorioCursos.getInstancia();
     repositorio.getTodosCursos().clear(); // Limpiar instancia singleton
     curso = new Curso();
 }

 @Test
 void testSingleton() {
     RepositorioCursos otraInstancia = RepositorioCursos.getInstancia();
     assertSame(repositorio, otraInstancia);
 }

 @Test
 void testAgregarYRecuperarCurso() {
     repositorio.agregarCurso(curso);
     assertEquals(1, repositorio.getTodosCursos().size());
     assertTrue(repositorio.getTodosCursos().contains(curso));
 }

 @Test
 void testGetTodosCursosDevuelveCopia() {
     repositorio.agregarCurso(curso);
     List<Curso> copia = repositorio.getTodosCursos();
     copia.clear();
     assertEquals(1, repositorio.getTodosCursos().size());
 }
}