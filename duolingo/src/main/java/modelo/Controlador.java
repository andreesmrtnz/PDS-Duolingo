package modelo;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Controlador {
    // Instancia única (singleton)
    private static Controlador instancia;
    private RepositorioUsuarios repoUsuarios;
    private RepositorioCursos repoCursos;
    
    // Constructor privado para evitar instanciación directa
    private Controlador() {
        this.repoUsuarios = RepositorioUsuarios.getUnicaInstancia();
        this.repoCursos = RepositorioCursos.getInstancia();
    }
    
    // Método estático para obtener la única instancia (singleton)
    public static synchronized Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }
        return instancia;
    }
    
    // Método para realizar login (con validación de password)
    public Usuario login(String email, String password) {
        Usuario usuario = repoUsuarios.buscarPorEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null;
    }
    
    // Método para registrar un usuario; retorna true si se registró correctamente
    public boolean registrar(Usuario usuario) {
        if (repoUsuarios.buscarPorEmail(usuario.getEmail()) != null) {
            // El usuario ya existe
            return false;
        }
        // Guardamos el usuario en la base de datos a través del repositorio
        repoUsuarios.save(usuario);
        return true;
    }
    
    // Método para iniciar un curso
    public void iniciarCurso(Curso curso) {
        // Aquí se podría invocar lógica adicional
        System.out.println("Iniciando curso: " + curso.getTitulo());
    }
    
    // Acceso al repositorio (si se requiere)
    public RepositorioUsuarios getRepoUsuarios() {
        return repoUsuarios;
    }
    
    // Método para liberar recursos cuando la aplicación se cierre
    public void cerrarRecursos() {
        repoUsuarios.cerrarRecursos();
    }
    
    /**
     * Carga un curso desde un archivo y lo agrega al repositorio
     * @param rutaArchivo Ruta al archivo JSON o YAML
     * @param creador Usuario que carga el curso
     * @return true si se cargó correctamente, false en caso contrario
     */
    public boolean cargarCursoDesdeArchivo(String rutaArchivo, Usuario creador) {
        try {
            Curso curso = CursoParser.cargarCurso(rutaArchivo, creador);
            repoCursos.agregarCurso(curso);
            return true;
        } catch (Exception e) {
            System.err.println("Error al cargar curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todos los cursos disponibles
     * @return Lista de cursos
     */
    public List<Curso> getCursosDisponibles() {
        return repoCursos.getTodosCursos();
    }
    
    /**
     * Crea un nuevo curso y lo guarda en un archivo
     * @param titulo Título del curso
     * @param dominio Dominio o categoría del curso
     * @param creador Usuario creador
     * @param bloques Lista de bloques del curso
     * @param rutaArchivo Ruta donde guardar el archivo (opcional)
     * @return El curso creado o null si hubo un error
     */
    public Curso crearCurso(String titulo, String dominio, Usuario creador, 
                            List<Bloque> bloques, String rutaArchivo) {
        try {
            // Generar ID único para el curso
            Long id = generarIdUnico();
            
            // Crear objeto curso con estrategia básica
            Curso curso = new Curso(id, titulo, dominio, creador, bloques, 0, new EstrategiaBasica());
            
            // Guardar en repositorio
            repoCursos.agregarCurso(curso);
            
            // Si se proporcionó una ruta, guardamos el curso en archivo
            if (rutaArchivo != null && !rutaArchivo.isEmpty()) {
                CursoParser.guardarCurso(curso, rutaArchivo);
            }
            
            return curso;
        } catch (Exception e) {
            System.err.println("Error al crear curso: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Comparte un curso exportándolo a un archivo
     * @param curso Curso a compartir
     * @param rutaArchivo Ruta donde guardar el archivo
     * @return true si se exportó correctamente, false en caso contrario
     */
    public boolean compartirCurso(Curso curso, String rutaArchivo) {
        try {
            CursoParser.guardarCurso(curso, rutaArchivo);
            return true;
        } catch (Exception e) {
            System.err.println("Error al compartir curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca cursos por dominio o categoría
     * @param dominio Dominio o categoría a buscar
     * @return Lista de cursos que coinciden con el dominio
     */
    public List<Curso> buscarCursosPorDominio(String dominio) {
        List<Curso> resultado = new ArrayList<>();
        for (Curso curso : repoCursos.getTodosCursos()) {
            if (curso.getDominio().equalsIgnoreCase(dominio)) {
                resultado.add(curso);
            }
        }
        return resultado;
    }
    
    /**
     * Busca cursos por creador
     * @param creador Usuario creador
     * @return Lista de cursos creados por el usuario
     */
    public List<Curso> buscarCursosPorCreador(Usuario creador) {
        List<Curso> resultado = new ArrayList<>();
        for (Curso curso : repoCursos.getTodosCursos()) {
            if (curso.getCreador().getId().equals(creador.getId())) {
                resultado.add(curso);
            }
        }
        return resultado;
    }
    
    /**
     * Genera un ID único para nuevos cursos
     * @return ID único
     */
    private Long generarIdUnico() {
        // Implementación simple: usar timestamp
        return System.currentTimeMillis();
    }
}