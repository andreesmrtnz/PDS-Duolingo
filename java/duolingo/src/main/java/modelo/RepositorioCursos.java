package modelo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioCursos {
    private static RepositorioCursos instancia;
    private List<Curso> cursos;

    private RepositorioCursos() {
        cursos = new ArrayList<>();
    }

    public static synchronized RepositorioCursos getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioCursos();
        }
        return instancia;
    }

    public void agregarCurso(Curso curso) {
        cursos.add(curso);
    }

    public List<Curso> getTodosCursos() {
        return new ArrayList<>(cursos);
    }
}