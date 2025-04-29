package controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import modelo.Bloque;
import modelo.Creador;
import modelo.Curso;
import modelo.CursoEnProgreso;
import modelo.CursoParser;
import modelo.Estadistica;
import modelo.Estrategia;
import modelo.Estudiante;
import modelo.Pregunta;
import modelo.RepositorioCursos;
import modelo.RepositorioUsuarios;
import modelo.Usuario;
import persistencia.UsuarioDAO;
import persistencia.CursoDAO;
import persistencia.CursoEnProgresoDAO;
import persistencia.EstadisticaDAO;


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
            curso.setBloques(new ArrayList<>(new HashSet<>(curso.getBloques())));
            repoCursos.agregarCurso(curso);
            this.usuarioActual.addCurso(curso);
            
            
            cursoDAO.guardar(curso);
            usuarioDAO.actualizar(usuarioActual);
            
            // Inscribir automáticamente a los seguidores
            inscribirSeguidoresEnCurso(curso);
            
            em.getTransaction().commit();
            
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
    
    public int getNumeroCursosCreados(Usuario creador) {
        if (creador == null || !creador.esCreador()) {
            return 0;
        }
        return cursoDAO.buscarPorCreador(creador).size();
    }
    
    public void finalizarCurso() {
        if (cursoActual != null && progresoActual != null) {
            // Marcar el curso como completado
            progresoActual.setCompletado(true);
            // Actualizar la fecha de última actividad
            progresoActual.setFechaUltimaActividad(new java.util.Date());
            // Guardar el progreso en la base de datos
            cursoEnProgresoDAO.actualizar(progresoActual);
            
            // Actualizar estadísticas globales del usuario
            Estadistica estadistica = getEstadisticasUsuario();
            if (estadistica != null) {
                estadistica.setCursosCompletados(estadistica.getCursosCompletados() + 1);
                EstadisticaDAO estadisticaDAO = new EstadisticaDAO();
                estadisticaDAO.actualizar(estadistica);
            }
            
            System.out.println("Curso finalizado: " + cursoActual.getTitulo());
            System.out.println("Preguntas correctas: " + progresoActual.getPreguntasCorrectas());
            System.out.println("Preguntas incorrectas: " + progresoActual.getPreguntasIncorrectas());
        }
    }
    
    public double getPorcentajeAciertosCurso() {
        if (progresoActual == null) {
            return 0.0;
        }
        Estadistica estadistica = getEstadisticasUsuario();
        if (estadistica != null) {
            return estadistica.getPrecision(progresoActual);
        }
        return 0.0;
    }
    
 // Dentro de Controlador.java, agregar el siguiente método después de los métodos existentes

    /**
     * Actualiza los datos del perfil del usuario actual
     * @param nombre Nuevo nombre del usuario
     * @param email Nuevo email del usuario
     * @param password Nueva contraseña (puede ser null si no se cambia)
     * @param idiomaPreferido Idioma preferido
     * @param recordatorioDiario Preferencia de recordatorio diario
     * @param actualizacionesProgreso Preferencia de actualizaciones de progreso
     * @param notificacionesNuevosCursos Preferencia de notificaciones de nuevos cursos
     * @return true si la actualización fue exitosa, false si los datos son inválidos o hay un error
     */
    public boolean actualizarPerfil(String nombre, String email, String password, String idiomaPreferido,
                                    boolean recordatorioDiario, boolean actualizacionesProgreso, 
                                    boolean notificacionesNuevosCursos) {
        if (usuarioActual == null) {
            System.err.println("Error: No hay usuario autenticado");
            return false;
        }

        EntityManager em = usuarioDAO.getEntityManager();
        try {
            em.getTransaction().begin();

            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                System.err.println("Error: El nombre no puede estar vacío");
                return false;
            }

            if (email == null) {
                System.err.println("Error: El email no es válido");
                return false;
            }

            // Verificar si el email ya está en uso por otro usuario
            Usuario usuarioExistente = repoUsuarios.buscarPorEmail(email);
            if (usuarioExistente != null && !usuarioExistente.getId().equals(usuarioActual.getId())) {
                System.err.println("Error: El email ya está en uso");
                return false;
            }

            if (password != null && !password.isEmpty() && password.length() < 6) {
                System.err.println("Error: La contraseña debe tener al menos 6 caracteres");
                return false;
            }

            // Actualizar datos del usuario
            usuarioActual.setNombre(nombre.trim());
            usuarioActual.setEmail(email.trim());
            if (password != null && !password.isEmpty()) {
                usuarioActual.setPassword(password);
            }
            usuarioActual.setIdiomaPreferido(idiomaPreferido);
            usuarioActual.setRecordatorioDiario(recordatorioDiario);
            usuarioActual.setActualizacionesProgreso(actualizacionesProgreso);
            usuarioActual.setNotificacionesNuevosCursos(notificacionesNuevosCursos);

            // Persistir los cambios
            usuarioDAO.actualizar(usuarioActual);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
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
        
        // Si no existe, pedir al estudiante que lo cree
        if (progreso == null) {
            // Asegurarse de que el usuario es un estudiante
            if (usuarioActual instanceof Estudiante) {
                Estudiante estudiante = (Estudiante) usuarioActual;
                progreso = estudiante.iniciarCurso(curso, estrategia);
                cursoEnProgresoDAO.guardar(progreso);
            } else {
                return false;
            }
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
    
    public void limpiarCursoActual() {
        cursoActual = null;
        progresoActual = null;
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
    
    /**
     * Calcula el porcentaje completado para un curso específico
     * @param progreso El objeto CursoEnProgreso
     * @param curso El curso asociado
     * @return Porcentaje de completado (0-100)
     */
    public double getPorcentajeCompletado(CursoEnProgreso progreso, Curso curso) {
        if (progreso == null || curso == null) {
            return 0.0;
        }
        
        int totalPreguntas = 0;
        for (Bloque bloque : curso.getBloques()) {
            totalPreguntas += bloque.getPreguntas().size();
        }
        
        if (totalPreguntas == 0) {
            return 0.0;
        }
        
        // Total de preguntas respondidas (correctas + incorrectas)
        int preguntasRespondidas = progreso.getPreguntasCorrectas() + progreso.getPreguntasIncorrectas();
        
        return (preguntasRespondidas * 100.0) / totalPreguntas;
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
        if (usuarioActual == null || !(usuarioActual instanceof Estudiante) || curso == null) {
            System.err.println("Error: Usuario no válido o curso nulo");
            return false;
        }

        EntityManager em = cursoEnProgresoDAO.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Buscar si ya existe un progreso para este usuario y curso
            CursoEnProgreso progresoExistente = cursoEnProgresoDAO.buscarPorUsuarioYCurso(usuarioActual, curso);
            
            // Si existe, eliminarlo
            if (progresoExistente != null) {
                cursoEnProgresoDAO.eliminar(progresoExistente);
            }
            
            // Crear nuevo progreso usando el método de Estudiante
            Estudiante estudiante = (Estudiante) usuarioActual;
            CursoEnProgreso nuevoProgreso = estudiante.inscribirEnCurso(curso);
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
    
    public void finalizarSesion(long tiempoSesion) {
        if (usuarioActual == null) return;
        
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO();
        
        // Update total usage time
        estadisticaDAO.actualizarTiempoUso(usuarioActual, tiempoSesion);
        
        // Update consecutive days logic
        Estadistica estadistica = estadisticaDAO.buscarPorUsuario(usuarioActual);
        if (estadistica != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fechaUltima = sdf.format(estadistica.getUltimaConexion());
            String fechaActual = sdf.format(new Date());
            if (!fechaUltima.equals(fechaActual)) {
                estadisticaDAO.incrementarDiasConsecutivos(usuarioActual);
            }
        }
    }
    
    /**
     * Inscribe al usuario actual en un curso
     * @param curso El curso en el que inscribirse
     * @return true si la inscripción fue exitosa, false en caso contrario
     */
    public boolean inscribirEnCurso(Curso curso) {
        if (usuarioActual == null || curso == null || !(usuarioActual instanceof Estudiante)) {
            return false;
        }
        
        try {
            // Check if the user is already enrolled
            CursoEnProgreso existente = cursoEnProgresoDAO.buscarPorUsuarioYCurso(usuarioActual, curso);
            if (existente != null) {
                // Already enrolled
                return false;
            }
            
            // Delegar la creación de la inscripción al estudiante
            Estudiante estudiante = (Estudiante) usuarioActual;
            CursoEnProgreso nuevaInscripcion = estudiante.inscribirEnCurso(curso);
            
            // Save the enrollment to the database
            cursoEnProgresoDAO.guardar(nuevaInscripcion);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error en Controlador.inscribirEnCurso: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception to be handled by the view
        }
    }
    
    /**
     * Reanuda un curso previamente iniciado
     * @param curso El curso a reanudar
     * @param progreso El progreso existente del curso
     * @return true si se reanudó correctamente, false en caso contrario
     */
    public boolean reanudarCurso(Curso curso, CursoEnProgreso progreso) {
        if (curso == null || progreso == null) {
            return false;
        }
        
        try {
            // Set the current course and progress
            this.cursoActual = curso;
            this.progresoActual = progreso;
            
            // Update the last activity date
            progreso.setFechaUltimaActividad(new java.util.Date());
            cursoEnProgresoDAO.actualizar(progreso);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error en Controlador.resumirCurso: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to be handled by the view
        }
    }
    
    /**
     * Obtiene las estadísticas del usuario actual
     * @return Objeto Estadistica con los datos del usuario
     */
    public Estadistica getEstadisticasUsuario() {
        if (usuarioActual == null) {
            return null;
        }
        
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO();
        Estadistica estadistica = estadisticaDAO.buscarPorUsuario(usuarioActual);
        
        // Si no hay estadísticas, pedirle al usuario que las cree
        if (estadistica == null) {
            estadistica = usuarioActual.crearEstadistica();
            estadisticaDAO.guardar(estadistica);
        }
        
        // Actualizar la fecha de última conexión
        estadistica.setUltimaConexion(new Date());
        estadisticaDAO.actualizar(estadistica);
        
        return estadistica;
    }
    
    
    /**
     * Obtiene la lista de todos los creadores disponibles
     * @return Lista de usuarios que son creadores
     */
    public List<Usuario> getCreadoresDisponibles() {
        // Obtener todos los creadores
        List<Usuario> todosLosCreadores = usuarioDAO.listarCreadoresConSeguidores();
        
        // Si no hay usuario actual, devolver todos los creadores (caso defensivo)
        if (usuarioActual == null) {
            return todosLosCreadores;
        }
        
        // Obtener los IDs de los creadores seguidos por el usuario actual
        List<Long> idsCreadoresSeguidos = usuarioActual.getCreadoresSeguidos()
            .stream()
            .map(Usuario::getId)
            .collect(Collectors.toList());
        
        // Filtrar los creadores no seguidos
        List<Usuario> creadoresNoSeguidos = todosLosCreadores.stream()
            .filter(creador -> !idsCreadoresSeguidos.contains(creador.getId()))
            .collect(Collectors.toList());
        
        return creadoresNoSeguidos;
    }

    /**
     * Permite a un estudiante seguir a un creador
     * @param creador El creador a seguir
     * @return true si se siguió correctamente, false en caso contrario
     */
    public boolean seguirCreador(Usuario creador) {
        if (usuarioActual == null || !usuarioActual.esEstudiante() || !creador.esCreador()) {
            return false;
        }

        EntityManager em = usuarioDAO.getEntityManager();
        try {
            em.getTransaction().begin();

            // Añadir el creador a la lista de creadores seguidos
            usuarioActual.addCreadorSeguido(creador);
            usuarioDAO.actualizar(usuarioActual);

            // Inscribir automáticamente en todos los cursos del creador
            List<Curso> cursosCreador = cursoDAO.buscarPorCreador(creador);
            for (Curso curso : cursosCreador) {
                inscribirEnCurso(curso);
            }

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al seguir creador: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Permite a un estudiante dejar de seguir a un creador
     * @param creador El creador a dejar de seguir
     * @return true si se dejó de seguir correctamente, false en caso contrario
     */
    public boolean dejarDeSeguirCreador(Usuario creador) {
        if (usuarioActual == null || !usuarioActual.esEstudiante() || !creador.esCreador()) {
            return false;
        }

        EntityManager em = usuarioDAO.getEntityManager();
        try {
            em.getTransaction().begin();

            // Eliminar el creador de la lista de creadores seguidos
            usuarioActual.removeCreadorSeguido(creador);
            usuarioDAO.actualizar(usuarioActual);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al dejar de seguir creador: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si el usuario actual sigue a un creador específico
     * @param creador El creador a verificar
     * @return true si el usuario lo sigue, false en caso contrario
     */
    public boolean estaSiguiendoCreador(Usuario creador) {
        if (usuarioActual == null || creador == null || !creador.esCreador()) {
            return false;
        }
        List<Usuario> creadoresSeguidos = usuarioActual.getCreadoresSeguidos();
        return creadoresSeguidos != null && creadoresSeguidos.contains(creador);
    }
    
    public List<Usuario> getCreadoresSeguidos(){
    	return this.usuarioActual.getCreadoresSeguidos();
    } 

    /**
     * Inscribe automáticamente a los seguidores de un creador en un nuevo curso
     * @param curso El nuevo curso creado
     */
    private void inscribirSeguidoresEnCurso(Curso curso) {
        if (curso == null || curso.getCreador() == null) {
            return;
        }

        EntityManager em = cursoEnProgresoDAO.getEntityManager();
        try {
            em.getTransaction().begin();

            // Obtener los seguidores del creador
            List<Usuario> seguidores = curso.getCreador().getSeguidores();
            for (Usuario seguidor : seguidores) {
                if (seguidor.esEstudiante()) {
                    // Verificar si el seguidor ya está inscrito
                    CursoEnProgreso existente = cursoEnProgresoDAO.buscarPorUsuarioYCurso(seguidor, curso);
                    if (existente == null) {
                        // Inscribir al seguidor
                        Estudiante estudiante = (Estudiante) seguidor;
                        CursoEnProgreso nuevaInscripcion = estudiante.inscribirEnCurso(curso);
                        cursoEnProgresoDAO.guardar(nuevaInscripcion);
                    }
                }
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al inscribir seguidores en curso: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    
}