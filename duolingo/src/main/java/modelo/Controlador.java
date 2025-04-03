package modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import jakarta.persistence.EntityManager;
import persistencia.UsuarioDAO;
import persistencia.CursoDAO;
import persistencia.CursoEnProgresoDAO;

import java.io.File;

public class Controlador {
    // Instancia única (singleton)
    private static Controlador instancia;
    private RepositorioUsuarios repoUsuarios;
    private RepositorioCursos repoCursos;
    private UsuarioDAO usuarioDAO;
    private CursoDAO cursoDAO;
    private CursoEnProgresoDAO cursoEnProgresoDAO;
    
    // Usuario actual, que se establece durante el login
    private Usuario usuarioActual;
    private Curso cursoActual;
    private CursoEnProgreso progresoActual;
    
    private Controlador() {
        this.repoUsuarios = RepositorioUsuarios.getUnicaInstancia();
        this.repoCursos = RepositorioCursos.getInstancia();
        this.usuarioDAO = new UsuarioDAO();
        this.cursoDAO = new CursoDAO();
        this.cursoEnProgresoDAO = new CursoEnProgresoDAO();
    }
    
    public static synchronized Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }
        return instancia;
    }
    
    // Modificación en login para asignar el usuario actual
    public Usuario login(String email, String password) {
        Usuario usuario = repoUsuarios.buscarPorEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            setUsuarioActual(usuario);
            return usuario;
        }
        return null;
    }
    
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    // Métodos que exponen datos necesarios sin devolver el modelo completo
    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombre() : "";
    }
    
    public String getEmailUsuario() {
        return usuarioActual != null ? usuarioActual.getEmail() : "";
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean cargarCursoDesdeArchivo(String rutaArchivo) {
        EntityManager em = usuarioDAO.getEntityManager();
        try {
            em.getTransaction().begin();
            
            Curso curso = CursoParser.cargarCurso(rutaArchivo, this.usuarioActual);
            
            // Las relaciones bidireccionales ya se establecen en CursoParser.cargarCurso
            // Pero podemos hacerlo también aquí si fuera necesario
            
            // Eliminar duplicados en la lista de bloques del curso antes de guardarlo
            curso.setBloques(new ArrayList<>(new HashSet<>(curso.getBloques())));
            repoCursos.agregarCurso(curso);
            this.usuarioActual.addCurso(curso);
            
            // Utilizar el DAO para persistir los cambios
            cursoDAO.guardar(curso);
            usuarioDAO.actualizar(usuarioActual);
            
            em.getTransaction().commit();
            
            // Log de verificación de tipos
            for (Bloque bloque : curso.getBloques()) {
                for (Pregunta pregunta : bloque.getPreguntas()) {
                    System.out.println("Pregunta cargada: " + pregunta.getEnunciado() + 
                                       " - Tipo: " + pregunta.getClass().getSimpleName());
                }
            }
            
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al cargar curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public void finalizarCurso() {
        if (cursoActual != null && progresoActual != null) {
            // Marcar el curso como completado
            progresoActual.setCompletado(true);
            // Actualizar la fecha de última actividad
            progresoActual.setFechaUltimaActividad(new java.util.Date());
            // Guardar el progreso en la base de datos
            cursoEnProgresoDAO.actualizar(progresoActual);
            
            System.out.println("Curso finalizado: " + cursoActual.getTitulo());
            System.out.println("Preguntas correctas: " + progresoActual.getPreguntasCorrectas());
            System.out.println("Preguntas incorrectas: " + progresoActual.getPreguntasIncorrectas());
            
            // Limpiar las referencias actuales
            cursoActual = null;
            progresoActual = null;
        }
    }
    
    // Método para registrar un usuario; retorna true si se registró correctamente
    public boolean registrar(Usuario usuario) {
        if (repoUsuarios.buscarPorEmail(usuario.getEmail()) != null) {
            // El usuario ya existe
            return false;
        }
        usuarioDAO.registrar(usuario);
        return true;
    }
    
    // Método para iniciar un curso
    public void iniciarCurso(Curso curso) {
        this.cursoActual = curso;
        
        // Buscar si ya existe un progreso para este usuario y curso
        CursoEnProgreso progreso = cursoEnProgresoDAO.buscarPorUsuarioYCurso(usuarioActual, curso);
        
        // Si no existe, crear uno nuevo
        if (progreso == null) {
            progreso = new CursoEnProgreso(usuarioActual, curso);
            cursoEnProgresoDAO.guardar(progreso);
        }
        
        this.progresoActual = progreso;
    }
    
    // Método para registrar respuesta
    public void registrarRespuesta(boolean correcta) {
        if (progresoActual != null) {
            if (correcta) {
                progresoActual.registrarRespuestaCorrecta();
            } else {
                progresoActual.registrarRespuestaIncorrecta();
            }
            // Guardar el progreso actualizado
            cursoEnProgresoDAO.actualizar(progresoActual);
        }
    }
    
    // Método para verificar si la pregunta actual ya fue respondida
    public boolean isPreguntaRespondida() {
        return progresoActual != null && progresoActual.isPreguntaRespondida();
    }
    
    // Métodos para navegar entre preguntas
    public boolean avanzarPregunta() {
        if (progresoActual != null && cursoActual != null) {
            boolean resultado = progresoActual.avanzarPregunta(cursoActual);
            if (resultado) {
                cursoEnProgresoDAO.actualizar(progresoActual);
            }
            return resultado;
        }
        return false;
    }
    
    public boolean retrocederPregunta() {
        if (progresoActual != null && cursoActual != null) {
            boolean resultado = progresoActual.retrocederPregunta(cursoActual);
            if (resultado) {
                cursoEnProgresoDAO.actualizar(progresoActual);
            }
            return resultado;
        }
        return false;
    }
    
    // Acceso al repositorio (si se requiere)
    public RepositorioUsuarios getRepoUsuarios() {
        return repoUsuarios;
    }
    
    // Método para liberar recursos cuando la aplicación se cierre
    public void cerrarRecursos() {
        repoUsuarios.cerrarRecursos();
        usuarioDAO.cerrar();
        cursoDAO.cerrar();
        cursoEnProgresoDAO.cerrar();
    }

    /**
     * Obtiene todos los cursos disponibles
     * @return Lista de cursos
     */
    public List<Curso> getCursosDisponibles() {
        return cursoDAO.listarTodos();
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
    public Curso crearCurso(String titulo, String dominio, List<Bloque> bloques, String rutaArchivo) {
        try {
            // Usar el usuario actual como creador
            Usuario creador = this.usuarioActual;
            
            // Crear objeto curso con estrategia básica
            Curso curso = new Curso(null, titulo, dominio, creador, bloques, 0);
            
            // Guardar en repositorio y base de datos
            repoCursos.agregarCurso(curso);
            creador.addCurso(curso);
            
            // Persistir el curso y actualizar el usuario
            cursoDAO.guardar(curso);
            usuarioDAO.actualizar(creador);
            
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
        return cursoDAO.buscarPorDominio(dominio);
    }
    
    /**
     * Busca cursos por creador
     * @param creador Usuario creador
     * @return Lista de cursos creados por el usuario
     */
    public List<Curso> buscarCursosPorCreador(Usuario creador) {
        return cursoDAO.buscarPorCreador(creador);
    }

    public Curso getCursoActual() {
        return this.cursoActual;
    }
    
    public CursoEnProgreso getProgresoActual() {
        return this.progresoActual;
    }
    
    // Obtener estadísticas del progreso actual
    public int getPreguntasCorrectas() {
        return progresoActual != null ? progresoActual.getPreguntasCorrectas() : 0;
    }
    
    public int getPreguntasIncorrectas() {
        return progresoActual != null ? progresoActual.getPreguntasIncorrectas() : 0;
    }
    
    public double getPorcentajeCompletado() {
        if (progresoActual != null && cursoActual != null) {
            return progresoActual.getPorcentajeCompletado(cursoActual);
        }
        return 0.0;
    }
    
    public int getBloqueActual() {
        return progresoActual != null ? progresoActual.getBloqueActual() : 0;
    }
    
    public int getPreguntaActual() {
        return progresoActual != null ? progresoActual.getPreguntaActual() : 0;
    }
    
    public void setBloqueActual(int bloqueActual) {
        if (progresoActual != null) {
            progresoActual.setBloqueActual(bloqueActual);
            cursoEnProgresoDAO.actualizar(progresoActual);
        }
    }
    
    public void setPreguntaActual(int preguntaActual) {
        if (progresoActual != null) {
            progresoActual.setPreguntaActual(preguntaActual);
            cursoEnProgresoDAO.actualizar(progresoActual);
        }
    }

 // Método para verificar si una pregunta específica ya fue respondida
    public boolean isPreguntaRespondida(int bloqueIdx, int preguntaIdx) {
        if (progresoActual != null) {
            // Llamar al método correspondiente en CursoEnProgreso
            return progresoActual.getEstadoPregunta(bloqueIdx, preguntaIdx) != 0;
        }
        return false;
    }
}