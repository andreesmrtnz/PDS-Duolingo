package modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import jakarta.persistence.EntityManager;
import persistencia.UsuarioDAO;
import persistencia.CursoDAO;
import persistencia.CursoEnProgresoDAO;


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
            
            Usuario creador = em.find(Usuario.class, usuarioActual.getId());
            if (creador == null) {
                System.err.println("Error: El usuario actual no existe en la base de datos");
                return false;
            }
           
            Curso curso = CursoParser.cargarCurso(rutaArchivo, creador);
            
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
    
 // Método modificado para registrar diferentes tipos de usuarios
    public boolean registrar(Usuario usuario) {
        if (repoUsuarios.buscarPorEmail(usuario.getEmail()) != null) {
            // El usuario ya existe
            return false;
        }
        usuarioDAO.registrar(usuario);
        return true;
    }
    
    // Método nuevo para crear estudiantes
    public Estudiante crearEstudiante(String nombre, String email, String password) {
        Estudiante estudiante = new Estudiante(nombre, email, password);
        if (registrar(estudiante)) {
            return estudiante;
        }
        return null;
    }
    
    // Método nuevo para crear creadores
    public Creador crearCreador(String nombre, String email, String password) {
        Creador creador = new Creador(nombre, email, password);
        if (registrar(creador)) {
            return creador;
        }
        return null;
    }
    
    // Validaciones para restringir acciones según el tipo de usuario
    public boolean puedeCrearCursos() {
        return usuarioActual != null && usuarioActual.esCreador();
    }
    
    public boolean puedeRealizarCursos() {
        return usuarioActual != null && usuarioActual.esEstudiante();
    }
    
    // Método modificado para iniciar un curso (solo para estudiantes)
    public boolean iniciarCurso(Curso curso, Estrategia estrategia) {
        if (!puedeRealizarCursos()) {
            System.err.println("Error: Solo los estudiantes pueden realizar cursos");
            return false;
        }
        
        this.cursoActual = curso;
        
        // Buscar si ya existe un progreso para este usuario y curso
        CursoEnProgreso progreso = cursoEnProgresoDAO.buscarPorUsuarioYCurso(usuarioActual, curso);
        
        // Si no existe, crear uno nuevo con la estrategia seleccionada
        if (progreso == null) {
            progreso = new CursoEnProgreso(usuarioActual, curso);
            progreso.setEstrategia(estrategia);
            cursoEnProgresoDAO.guardar(progreso);
        } else {
            // Si ya existe un progreso, actualizamos la estrategia si es diferente
            if (progreso.getEstrategia() != estrategia) {
                progreso.setEstrategia(estrategia);
                cursoEnProgresoDAO.actualizar(progreso);
            }
        }
        
        this.progresoActual = progreso;
        return true;
    }
    
    // Método modificado para crear cursos (solo para creadores)
    public Curso crearCurso(String titulo, String dominio, List<Bloque> bloques, String rutaArchivo) {
        if (!puedeCrearCursos()) {
            System.err.println("Error: Solo los creadores pueden crear cursos");
            return null;
        }
        
        try {
            // Usar el usuario actual como creador
            Usuario creador = this.usuarioActual;
            
            // Incrementar contador de cursos creados si es un Creador
            if (creador instanceof Creador) {
                ((Creador) creador).incrementarCursosCreados();
            }
            
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
     * Obtiene todos los cursos creados por el usuario actual
     * @return Lista de cursos creados por el usuario actual
     */
    public List<Curso> getCursosCreadosPorUsuario() {
        if (usuarioActual == null) {
            return new ArrayList<>();
        }
        
        // Utilizar el DAO de cursos para buscar por creador
        return cursoDAO.buscarPorCreador(usuarioActual);
    }
    
    // Mantener el método original para compatibilidad con código existente
    public void iniciarCurso(Curso curso) {
        // Por defecto usar estrategia SECUENCIAL
        iniciarCurso(curso, Estrategia.SECUENCIAL);
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
    
    public boolean isRepeticionPregunta(int bloque, int pregunta) {
        if (progresoActual != null && progresoActual.getEstrategia() == Estrategia.REPETICION_ESPACIADA) {
            String clave = bloque + ":" + pregunta;
            return progresoActual.getPreguntasARepetir().containsKey(clave);
        }
        return false;
    }

    public Estrategia getEstrategia() {
        if (progresoActual != null) {
            return progresoActual.getEstrategia();
        }
        return Estrategia.SECUENCIAL; // Por defecto
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
    
 // Añadir estos métodos al Controlador.java

    /**
     * Establece el progreso actual directamente
     * @param progreso El objeto CursoEnProgreso a establecer
     */
    public void setProgresoActual(CursoEnProgreso progreso) {
        this.progresoActual = progreso;
    }

    /**
     * Establece el curso actual directamente
     * @param curso El objeto Curso a establecer
     */
    public void setCursoActual(Curso curso) {
        this.cursoActual = curso;
    }

    /**
     * Reinicia un curso, borrando el progreso anterior y creando uno nuevo
     * @param curso El curso a reiniciar
     * @return true si se reinició correctamente, false en caso contrario
     */
    public boolean reiniciarCurso(Curso curso) {
        EntityManager em = cursoEnProgresoDAO.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Buscar si ya existe un progreso para este usuario y curso
            CursoEnProgreso progresoExistente = cursoEnProgresoDAO.buscarPorUsuarioYCurso(usuarioActual, curso);
            
            // Si existe, eliminarlo
            if (progresoExistente != null) {
                cursoEnProgresoDAO.eliminar(progresoExistente);
            }
            
            // Crear nuevo progreso
            CursoEnProgreso nuevoProgreso = new CursoEnProgreso(usuarioActual, curso);
            cursoEnProgresoDAO.guardar(nuevoProgreso);
            
            // Establecer el curso y progreso actual
            this.cursoActual = curso;
            this.progresoActual = nuevoProgreso;
            
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al reiniciar curso: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene la lista de cursos en progreso del usuario actual
     * @return Lista de CursoEnProgreso del usuario
     */
    public List<CursoEnProgreso> getCursosEnProgreso() {
        if (usuarioActual == null) {
            return new ArrayList<>();
        }
        return cursoEnProgresoDAO.buscarPorUsuario(usuarioActual);
    }

    /**
     * Verifica si un curso tiene progreso para el usuario actual
     * @param curso El curso a verificar
     * @return El objeto CursoEnProgreso si existe, null en caso contrario
     */
    public CursoEnProgreso getProgresoDeCurso(Curso curso) {
        if (usuarioActual == null || curso == null) {
            return null;
        }
        return cursoEnProgresoDAO.buscarPorUsuarioYCurso(usuarioActual, curso);
    }
}