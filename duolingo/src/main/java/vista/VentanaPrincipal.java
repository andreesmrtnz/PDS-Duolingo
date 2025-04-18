package vista;

import javafx.animation.FadeTransition;
import java.text.SimpleDateFormat;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Bloque;
import modelo.Controlador;
import modelo.Curso;
import modelo.CursoEnProgreso;
import modelo.Estadistica;
import modelo.Estrategia;
import modelo.Pregunta;
import modelo.Usuario;
import persistencia.CursoEnProgresoDAO;
import persistencia.EstadisticaDAO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class VentanaPrincipal {

    // Referencia al controlador (se obtiene el usuario actual a través del controlador)
    private final Controlador controlador;
    
    // Componentes de UI
    private BorderPane mainLayout;
    private VBox sidebar;
    private StackPane contentArea;
    private ScrollPane coursesScrollPane;
    private VBox profilePane;
    private VBox statsPane;
    private CursoEnProgresoDAO cursoEnProgresoDAO;
    private long sessionStartTime;
    
    public VentanaPrincipal() {
        this.controlador = Controlador.getInstancia();
        this.cursoEnProgresoDAO = new CursoEnProgresoDAO();
    }
    
    public void start(Stage primaryStage) {
        setupMainUI(primaryStage);
        
        // Registrar el inicio de la sesión
        sessionStartTime = System.currentTimeMillis();
        
        // Agregar evento al cerrar la ventana para actualizar estadísticas
        primaryStage.setOnCloseRequest(e -> {
            long sessionEndTime = System.currentTimeMillis();
            long tiempoSesion = sessionEndTime - sessionStartTime;
            
            // Actualizar tiempo total de uso
            Usuario usuarioActual = controlador.getUsuarioActual();
            EstadisticaDAO estadisticaDAO = new EstadisticaDAO();
            try {
                estadisticaDAO.actualizarTiempoUso(usuarioActual, tiempoSesion);
            } catch (Exception ex) {
                System.err.println("Error al actualizar el tiempo de uso: " + ex.getMessage());
            }
            
            // Actualizar días consecutivos si la última conexión fue en un día distinto
            Estadistica estadistica = estadisticaDAO.buscarPorUsuario(usuarioActual);
            if (estadistica != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String fechaUltima = sdf.format(estadistica.getUltimaConexion());
                String fechaActual = sdf.format(new Date());
                if (!fechaUltima.equals(fechaActual)) {
                    try {
                        estadisticaDAO.incrementarDiasConsecutivos(usuarioActual);
                    } catch (Exception ex) {
                        System.err.println("Error al incrementar días consecutivos: " + ex.getMessage());
                    }
                }
            }
        });
        
        primaryStage.show();
    }
    
    private void setupMainUI(Stage stage) {
        mainLayout = new BorderPane();
        setupSidebar();
        mainLayout.setLeft(sidebar);
        
        // Configurar el área de contenido
        setupContentArea();
        mainLayout.setCenter(contentArea);
        
        setupTopBar();
        
        Scene scene = new Scene(mainLayout, 1000, 700);
        try {
            String cssFile = "/styles.css";
            String css = getClass().getResource(cssFile).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Error al cargar el CSS: " + e.getMessage());
            applyFallbackStyles(scene);
        }
        
        stage.setTitle("LinguaLearn - Aprende a tu ritmo");
        stage.setScene(scene);
        
        // Configurar interfaz según tipo de usuario
        configurarInterfazSegunTipoUsuario();
        
        stage.show();
        
        showLandingPage();
    }
    
    private void applyFallbackStyles(Scene scene) {
        String css = 
            ".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 14px; -fx-background-color: #f8f9fa; }" +
            ".sidebar { -fx-background-color: #4a69bd; -fx-padding: 15; }" +
            ".sidebar-button { -fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15; -fx-cursor: hand; }" +
            ".sidebar-button:hover { -fx-background-color: rgba(255, 255, 255, 0.2); }" +
            ".sidebar-button-active { -fx-background-color: rgba(255, 255, 255, 0.3); }" +
            ".course-card { -fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 3); }" +
            ".course-card:hover { -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0, 0, 5); }" +
            ".action-button { -fx-background-color: #4a69bd; -fx-text-fill: white; -fx-background-radius: 5; }" +
            ".action-button:hover { -fx-background-color: #3c58a8; }" +
            ".secondary-button { -fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-background-radius: 5; }" +
            ".secondary-button:hover { -fx-background-color: #d0d0d0; }" +
            ".section-title { -fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 15 0 10 0; }" +
            ".welcome-banner { -fx-background-color: linear-gradient(to right, #4a69bd, #7b8cde); -fx-background-radius: 10; -fx-padding: 20; }";
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
        System.out.println("Se aplicaron estilos de respaldo para la ventana principal");
    }
    
    private void setupSidebar() {
        sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setSpacing(5);
        
        // Icono y nombre de la aplicación
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(10, 0, 25, 0));
        
        Circle logoCircle = new Circle(20, Color.WHITE);
        Label appName = new Label("LinguaLearn");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        
        logoBox.getChildren().addAll(logoCircle, appName);
        
        // Botones de navegación
        Button homeButton = createSidebarButton("Inicio", true);
        homeButton.setOnAction(e -> showLandingPage());
        
        Button coursesButton = createSidebarButton("Mis Cursos", false);
        coursesButton.setOnAction(e -> showCoursesPage());
        
        Button profileButton = createSidebarButton("Perfil", false);
        profileButton.setOnAction(e -> showProfilePage());
        
        Button statsButton = createSidebarButton("Estadísticas", false);
        statsButton.setOnAction(e -> showStatsPage());
        
        Button logoutButton = createSidebarButton("Cerrar Sesión", false);
        logoutButton.setOnAction(e -> handleLogout());
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(logoBox, homeButton, coursesButton, profileButton, statsButton, spacer, logoutButton);
    }
    
    private Button createSidebarButton(String text, boolean active) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.getStyleClass().add("sidebar-button");
        if (active) {
            button.getStyleClass().add("sidebar-button-active");
        }
        return button;
    }
    
    private void setupContentArea() {
    	// Eliminar cualquier configuración previa
        // Crear un nuevo StackPane cada vez
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #f8f9fa;");
        
        // Asegurarse de que el contentArea está en el layout principal
        mainLayout.setCenter(contentArea);
    }
    
    // Se ha modificado la barra superior para incluir el botón "Importar Curso"
    private void setupTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setSpacing(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        // Botón para importar curso desde archivo
        Button importButton = new Button("Importar Curso");
        importButton.getStyleClass().add("action-button");
        importButton.setOnAction(e -> handleImportCurso());
        topBar.getChildren().add(importButton);
        
        Label userLabel = new Label("¡Hola, " + controlador.getNombreUsuario() + "!");
        userLabel.setStyle("-fx-font-weight: bold;");
        Circle userIcon = new Circle(15, Color.valueOf("#4a69bd"));
        topBar.getChildren().addAll(userLabel, userIcon);
        
        mainLayout.setTop(topBar);
    }
    
    // Se muestra la landing page con un banner de bienvenida
    private void showLandingPage() {
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        ((Button) sidebar.getChildren().get(1)).getStyleClass().add("sidebar-button-active");
        
        VBox landingLayout = new VBox(20);
        landingLayout.setAlignment(Pos.TOP_CENTER);
        
        VBox welcomeBox = new VBox(15);
        welcomeBox.getStyleClass().add("welcome-banner");
        welcomeBox.setPrefHeight(200);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        welcomeBox.setPadding(new Insets(25));
        
        Text welcomeTitle = new Text("¡Bienvenido a LinguaLearn, " + controlador.getNombreUsuario() + "!");
        welcomeTitle.setStyle("-fx-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        Text welcomeSubtitle = new Text("Continúa aprendiendo a tu ritmo. ¡Hoy es un gran día para aprender algo nuevo!");
        welcomeSubtitle.setStyle("-fx-fill: white; -fx-font-size: 16px;");
        Button continueButton = new Button("Continuar Aprendiendo");
        continueButton.getStyleClass().add("action-button");
        continueButton.setOnAction(e -> showCoursesPage());
        
        welcomeBox.getChildren().addAll(welcomeTitle, welcomeSubtitle, continueButton);
        
        landingLayout.getChildren().add(welcomeBox);
        
        ScrollPane scrollPane = new ScrollPane(landingLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
        animateContentEntry(scrollPane);
    }
    
    // Se obtiene la lista de cursos desde el controlador (en vez de usar cursos predefinidos)
    private void showCoursesPage() {
        System.out.println("Mostrando página de cursos");
        
        // Limpiar completamente
        contentArea.getChildren().clear();
        
        // Resetear estilos de los botones de la barra lateral
        resetSidebarButtonStyles();
        
        // Marcar el botón de "Mis Cursos" como activo
        ((Button) sidebar.getChildren().get(2)).getStyleClass().add("sidebar-button-active");
        
        // Crear el layout principal para cursos
        VBox coursesLayout = new VBox(25);
        coursesLayout.setPadding(new Insets(15));
        
        // Título de la página que varía según el tipo de usuario
        String tituloSeccion = controlador.puedeCrearCursos() ? "Mis Cursos Creados" : "Mis Cursos";
        Text pageTitle = new Text(tituloSeccion);
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        coursesLayout.getChildren().add(pageTitle);
        
        // Obtener la lista de cursos según el tipo de usuario
        List<Curso> cursos;
        if (controlador.puedeCrearCursos()) {
            // Para creadores, mostrar solo los cursos que han creado
            cursos = controlador.getCursosCreadosPorUsuario();
        } else {
            // Para estudiantes, mostrar todos los cursos disponibles
            cursos = controlador.getCursosDisponibles();
        }
        
        // Separar los cursos en progreso y los demás
        List<Curso> cursosEnProgreso = new ArrayList<>();
        List<Curso> cursosDisponibles = new ArrayList<>();
        
        for (Curso curso : cursos) {
            CursoEnProgreso progreso = controlador.getProgresoDeCurso(curso);
            if (progreso != null) {
                cursosEnProgreso.add(curso);
            } else {
                cursosDisponibles.add(curso);
            }
        }
        
        // Mostrar cursos en progreso si hay alguno
        if (!cursosEnProgreso.isEmpty()) {
            Text inProgressTitle = new Text("Cursos en Progreso");
            inProgressTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
            coursesLayout.getChildren().add(inProgressTitle);
            
            GridPane inProgressGrid = new GridPane();
            inProgressGrid.setHgap(20);
            inProgressGrid.setVgap(20);
            
            for (int i = 0; i < cursosEnProgreso.size(); i++) {
                Curso curso = cursosEnProgreso.get(i);
                Pane cursoCard = createCourseCard(curso);
                inProgressGrid.add(cursoCard, i % 3, i / 3);
            }
            
            coursesLayout.getChildren().add(inProgressGrid);
            
            // Añadir separador si hay más cursos que mostrar
            if (!cursosDisponibles.isEmpty()) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(10, 0, 10, 0));
                coursesLayout.getChildren().add(separator);
            }
        }
        
        // Mostrar cursos disponibles
        if (!cursosDisponibles.isEmpty()) {
            Text availableTitle = new Text(cursosEnProgreso.isEmpty() ? "" : "Cursos Disponibles");
            availableTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
            if (!cursosEnProgreso.isEmpty()) {
                coursesLayout.getChildren().add(availableTitle);
            }
            
            GridPane availableGrid = new GridPane();
            availableGrid.setHgap(20);
            availableGrid.setVgap(20);
            
            for (int i = 0; i < cursosDisponibles.size(); i++) {
                Curso curso = cursosDisponibles.get(i);
                Pane cursoCard = createCourseCard(curso);
                availableGrid.add(cursoCard, i % 3, i / 3);
            }
            
            coursesLayout.getChildren().add(availableGrid);
        }
        
        // Si no hay cursos mostrar mensaje
        if (cursos.isEmpty()) {
            Label emptyLabel = new Label("No hay cursos cargados. Utilice la opción 'Importar Curso'.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            coursesLayout.getChildren().add(emptyLabel);
        }
        
        // Crear ScrollPane para manejar muchos cursos
        coursesScrollPane = new ScrollPane(coursesLayout);
        coursesScrollPane.setFitToWidth(true);
        coursesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        coursesScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Añadir al área de contenido
        contentArea.getChildren().add(coursesScrollPane);
        
        // Animar la entrada del contenido
        animateContentEntry(coursesScrollPane);
    }
    
    // Se crea una tarjeta para cada curso cargado
 // Reemplaza el método createCourseCard existente con esta nueva implementación
    private Pane createCourseCard(Curso curso) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setPrefHeight(250);
        card.getStyleClass().add("course-card");
        
        Circle icon = new Circle(30);
        icon.setFill(getColorForCourse(curso.getTitulo()));
        
        Text title = new Text(curso.getTitulo());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Se usa el dominio como descripción
        Text description = new Text(curso.getDominio());
        description.setWrappingWidth(190);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Comprobar si existe un progreso para este curso
        CursoEnProgreso progreso = cursoEnProgresoDAO.buscarPorUsuarioYCurso(controlador.getUsuarioActual(), curso);
        
        VBox progressInfo = new VBox(5);
        progressInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Verificar si el usuario es creador de cursos
        boolean esCreador = controlador.puedeCrearCursos();
        
        if (progreso != null) {
            // Mostrar información de progreso
            double porcentajeCompletado = calcularPorcentajeCompletado(progreso, curso);
            
            Text progressText = new Text(String.format("Progreso: %.1f%%", porcentajeCompletado));
            progressText.setFont(Font.font("System", FontWeight.NORMAL, 14));
            
            ProgressBar progressBar = new ProgressBar(porcentajeCompletado / 100);
            progressBar.setPrefWidth(190);
            
            Text statsText = new Text(String.format("Correctas: %d | Incorrectas: %d", 
                                       progreso.getPreguntasCorrectas(), 
                                       progreso.getPreguntasIncorrectas()));
            statsText.setFont(Font.font("System", FontWeight.NORMAL, 12));
            statsText.setFill(Color.GRAY);
            
            progressInfo.getChildren().addAll(progressText, progressBar, statsText);
            
            // Solo mostrar botones si no es creador
            if (!esCreador) {
                HBox buttonsBox = new HBox(10);
                buttonsBox.setAlignment(Pos.CENTER);
                
                // Verificar si el curso ya ha sido iniciado (tiene preguntas respondidas)
                if (progreso.getPreguntasCorrectas() > 0 || progreso.getPreguntasIncorrectas() > 0) {
                    Button resumeButton = new Button("Reanudar");
                    resumeButton.getStyleClass().add("action-button");
                    resumeButton.setPrefWidth(90);
                    resumeButton.setOnAction(e -> resumeCourse(curso, progreso));
                    
                    Button restartButton = new Button("Reiniciar");
                    restartButton.getStyleClass().add("secondary-button");
                    restartButton.setPrefWidth(90);
                    restartButton.setOnAction(e -> confirmRestartCourse(curso));
                    
                    buttonsBox.getChildren().addAll(resumeButton, restartButton);
                } else {
                    // El curso está inscrito pero no iniciado
                    Button startButton = new Button("Iniciar curso");
                    startButton.getStyleClass().add("action-button");
                    startButton.setPrefWidth(190);
                    startButton.setOnAction(e -> startCourse(curso));
                    
                    buttonsBox.getChildren().add(startButton);
                }
                card.getChildren().addAll(icon, title, description, spacer, progressInfo, buttonsBox);
            } else {
                // Si es creador, solo mostrar la información sin botones
                card.getChildren().addAll(icon, title, description, spacer, progressInfo);
            }
        } else {
            // Si no hay progreso
            if (esCreador) {
                // Si es creador, no mostrar botones
                card.getChildren().addAll(icon, title, description, spacer);
            } else {
                // Si es estudiante y no está inscrito, mostrar botón de inscripción
                Button enrollButton = new Button("Inscribirse");
                enrollButton.getStyleClass().add("action-button");
                enrollButton.setPrefWidth(190);
                enrollButton.setOnAction(e -> inscribirEnCurso(curso));
                
                card.getChildren().addAll(icon, title, description, spacer, enrollButton);
            }
        }
        
        Tooltip tooltip = new Tooltip(curso.getTitulo() + " - " + curso.getDominio());
        Tooltip.install(card, tooltip);
        
        return card;
    }
    
 // Método para inscribir al usuario en un curso
    private void inscribirEnCurso(Curso curso) {
        try {
            // Crear nuevo registro de progreso pero sin iniciar el curso aún
            CursoEnProgreso nuevaInscripcion = new CursoEnProgreso();
            nuevaInscripcion.setUsuario(controlador.getUsuarioActual());
            nuevaInscripcion.setCurso(curso);
            nuevaInscripcion.setFechaInicio(new Date());
            nuevaInscripcion.setFechaUltimaActividad(new Date());
            nuevaInscripcion.setPreguntasCorrectas(0);
            nuevaInscripcion.setPreguntasIncorrectas(0);
            nuevaInscripcion.setBloqueActual(0);
            nuevaInscripcion.setPreguntaActual(0);
            nuevaInscripcion.setCompletado(false);
            
            // Guardar la inscripción en la base de datos
            cursoEnProgresoDAO.guardar(nuevaInscripcion);
            
            // Mostrar mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inscripción Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("Te has inscrito al curso \"" + curso.getTitulo() + "\". Ahora puedes iniciarlo cuando quieras.");
            alert.showAndWait();
            
            // Refrescar la vista de cursos
            showCoursesPage();
        } catch (Exception e) {
            System.err.println("Error al inscribirse en el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar mensaje de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al inscribirse");
            alert.setContentText("No se pudo completar la inscripción al curso. " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Método auxiliar para calcular el porcentaje completado
    private double calcularPorcentajeCompletado(CursoEnProgreso progreso, Curso curso) {
        // Si ya está completado, retornar 100%
        if (progreso.isCompletado()) {
            return 100.0;
        }
        
        // Contamos el total de preguntas en el curso
        int totalPreguntas = 0;
        for (Bloque bloque : curso.getBloques()) {
            totalPreguntas += bloque.getPreguntas().size();
        }
        
        if (totalPreguntas == 0) {
            return 0.0;
        }
        
        // Contamos las preguntas contestadas (correctas + incorrectas)
        int preguntasContestadas = progreso.getPreguntasCorrectas() + progreso.getPreguntasIncorrectas();
        
        return (preguntasContestadas * 100.0) / totalPreguntas;
    }

    // Método para reanudar un curso
    private void resumeCourse(Curso curso, CursoEnProgreso progreso) {
        try {
            // Establecer el curso y progreso actual en el controlador
            controlador.setCursoActual(curso);
            controlador.setProgresoActual(progreso);
            
            // Actualizar la fecha de última actividad
            progreso.setFechaUltimaActividad(new java.util.Date());
            cursoEnProgresoDAO.actualizar(progreso);
            
            // Crear una nueva instancia de VentanaPreguntas
            VentanaPreguntas ventanaPreguntas = new VentanaPreguntas();
            
            // Obtener el Stage actual
            Stage currentStage = (Stage) mainLayout.getScene().getWindow();
            
            // Crear un nuevo Stage para la ventana de preguntas
            Stage questionStage = new Stage();
            
            // Iniciar la ventana de preguntas
            ventanaPreguntas.start(questionStage);
            
            // Manejar el cierre de la ventana de preguntas para volver a la pantalla de cursos
            questionStage.setOnCloseRequest(e -> {
                showCoursesPage();
                currentStage.show();
            });
        } catch (Exception e) {
            System.err.println("Error al reanudar el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar una alerta al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al reanudar el curso");
            alert.setContentText("No se pudo reanudar el curso. " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Método para confirmar reinicio de curso
    private void confirmRestartCourse(Curso curso) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reiniciar Curso");
        alert.setHeaderText("¿Estás seguro de que quieres reiniciar este curso?");
        alert.setContentText("Perderás todo el progreso actual. Esta acción no se puede deshacer.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            restartCourse(curso);
        }
    }

    // Método para reiniciar un curso
    private void restartCourse(Curso curso) {
        try {
            // Eliminar el progreso anterior
            CursoEnProgreso progreso = cursoEnProgresoDAO.buscarPorUsuarioYCurso(controlador.getUsuarioActual(), curso);
            if (progreso != null) {
                cursoEnProgresoDAO.eliminar(progreso);
            }
            
            // Iniciar el curso desde el principio
            controlador.iniciarCurso(curso);
            
            // Crear una nueva instancia de VentanaPreguntas
            VentanaPreguntas ventanaPreguntas = new VentanaPreguntas();
            
            // Obtener el Stage actual
            Stage currentStage = (Stage) mainLayout.getScene().getWindow();
            
            // Crear un nuevo Stage para la ventana de preguntas
            Stage questionStage = new Stage();
            
            // Iniciar la ventana de preguntas
            ventanaPreguntas.start(questionStage);
            
            // Manejar el cierre de la ventana de preguntas para volver a la pantalla de cursos
            questionStage.setOnCloseRequest(e -> {
                showCoursesPage();
                currentStage.show();
            });
        } catch (Exception e) {
            System.err.println("Error al reiniciar el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar una alerta al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al reiniciar el curso");
            alert.setContentText("No se pudo reiniciar el curso. " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    // Método para asignar un color basado en el título del curso
    private Color getColorForCourse(String courseTitle) {
        switch (courseTitle) {
            case "Programación Java":
                return Color.valueOf("#f07b3f");
            case "Python Básico":
                return Color.valueOf("#4361ee");
            case "Bases de Datos SQL":
                return Color.valueOf("#06d6a0");
            case "HTML y CSS":
                return Color.valueOf("#ef476f");
            case "JavaScript Avanzado":
                return Color.valueOf("#ffd166");
            case "Algoritmos":
                return Color.valueOf("#118ab2");
            default:
                return Color.valueOf("#4a69bd");
        }
    }
    
    private void configurarInterfazSegunTipoUsuario() {
        // Obtener el usuario actual del controlador
        Usuario usuarioActual = controlador.getUsuarioActual();
        boolean esCreador = controlador.puedeCrearCursos();
        boolean esEstudiante = controlador.puedeRealizarCursos();
        
        // Referencia a los elementos de la interfaz que necesitan ajustarse
        Button importButton = null;
        
        // Buscar el botón de importar en la barra superior
        for (Node node : ((HBox) mainLayout.getTop()).getChildren()) {
            if (node instanceof Button && ((Button) node).getText().equals("Importar Curso")) {
                importButton = (Button) node;
                break;
            }
        }
        
        // Configurar visibilidad según tipo de usuario
        if (esCreador) {
            // Mostrar opciones de creación/importación de cursos
            if (importButton != null) {
                importButton.setVisible(true);
            }
            
            // Ocultar o deshabilitar funcionalidades específicas de estudiantes
            // (Aquí puedes ajustar otras partes de la interfaz específicas de estudiantes)
            
        } else if (esEstudiante) {
            // Ocultar opciones de creación/importación
            if (importButton != null) {
                importButton.setVisible(false);
            }
            
            // Asegurarse que las funcionalidades de aprendizaje están disponibles
        }
        
        // Actualizar la visualización de cursos en la página principal
        // para que refleje solo lo que el usuario puede ver/hacer
        showCoursesPage();
    }
    
    
    private void startCourse(Curso curso) {
        try {
            // Mostrar diálogo para elegir estrategia
            Estrategia estrategiaSeleccionada = mostrarDialogoEstrategia();
            
            if (estrategiaSeleccionada != null) {
                // Establecer el curso actual en el controlador con la estrategia seleccionada
                controlador.iniciarCurso(curso, estrategiaSeleccionada);
                
                // Crear una nueva instancia de VentanaPreguntas
                VentanaPreguntas ventanaPreguntas = new VentanaPreguntas();
                
                // Obtener el Stage actual
                Stage currentStage = (Stage) mainLayout.getScene().getWindow();
                
                // Crear un nuevo Stage para la ventana de preguntas
                Stage questionStage = new Stage();
                
                // Iniciar la ventana de preguntas
                ventanaPreguntas.start(questionStage);
                
                // Opcional: cerrar la ventana principal o mantenerla abierta
                // currentStage.close(); // Descomentar si quieres cerrar la ventana principal
                
                // Manejar el cierre de la ventana de preguntas para volver a la pantalla de cursos
                questionStage.setOnCloseRequest(e -> {
                    showCoursesPage();
                    currentStage.show(); // Solo es necesario si cerraste la ventana principal
                });
            }
        } catch (Exception e) {
            System.err.println("Error al iniciar el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar una alerta al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al iniciar el curso");
            alert.setContentText("No se pudo iniciar el curso. " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Muestra un diálogo para que el usuario elija la estrategia de aprendizaje
     * @return La estrategia seleccionada o null si se cancela
     */
    private Estrategia mostrarDialogoEstrategia() {
        // Crear un diálogo
        Dialog<Estrategia> dialog = new Dialog<>();
        dialog.setTitle("Elegir Estrategia");
        dialog.setHeaderText("Selecciona la estrategia de aprendizaje para este curso");
        
        // Configurar los botones
        ButtonType confirmarButton = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmarButton, ButtonType.CANCEL);
        
        // Crear el combo box con las estrategias
        ComboBox<Estrategia> comboEstrategias = new ComboBox<>();
        comboEstrategias.getItems().addAll(Estrategia.values());
        comboEstrategias.setValue(Estrategia.SECUENCIAL); // Valor por defecto
        
        // Crear etiquetas descriptivas para cada estrategia
        Label descripcionSecuencial = new Label("Secuencial: Las preguntas se presentan en orden uno tras otro.");
        Label descripcionAleatoria = new Label("Aleatoria: Las preguntas se presentan en orden aleatorio.");
        Label descripcionRepeticion = new Label("Repetición Espaciada: Las preguntas incorrectas se repiten periódicamente.");
        
        // Contenedor para las descripciones
        VBox descripcionBox = new VBox(5);
        descripcionBox.getChildren().addAll(descripcionSecuencial, descripcionAleatoria, descripcionRepeticion);
        
        // Contenedor principal
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Label("Estrategia:"), comboEstrategias, descripcionBox);
        dialog.getDialogPane().setContent(vbox);
        
        // Convertir el resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmarButton) {
                return comboEstrategias.getValue();
            }
            return null;
        });
        
        // Mostrar el diálogo y esperar la respuesta
        Optional<Estrategia> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }
    
    private void showProfilePage() {
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        ((Button) sidebar.getChildren().get(3)).getStyleClass().add("sidebar-button-active");
        
        profilePane = new VBox(25);
        profilePane.setPadding(new Insets(30));
        
        HBox profileHeader = new HBox(20);
        profileHeader.setAlignment(Pos.CENTER_LEFT);
        
        Circle avatar = new Circle(50);
        avatar.setFill(Color.valueOf("#4a69bd"));
        
        VBox profileInfo = new VBox(5);
        Text userName = new Text(controlador.getNombreUsuario());
        userName.setFont(Font.font("System", FontWeight.BOLD, 24));
        Text userEmail = new Text(controlador.getEmailUsuario());
        Text memberSince = new Text("Miembro desde: Marzo 2025");
        profileInfo.getChildren().addAll(userName, userEmail, memberSince);
        profileHeader.getChildren().addAll(avatar, profileInfo);
        
        Separator separator = new Separator();
        Text editProfileTitle = new Text("Editar Perfil");
        editProfileTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        
        Label nameLabel = new Label("Nombre:");
        TextField nameField = new TextField(controlador.getNombreUsuario());
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(controlador.getEmailUsuario());
        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Cambiar contraseña");
        Label languageLabel = new Label("Idioma preferido:");
        ComboBox<String> languageCombo = new ComboBox<>();
        languageCombo.setItems(javafx.collections.FXCollections.observableArrayList("Español", "English", "Français", "Deutsch"));
        languageCombo.setValue("Español");
        
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 1, 2);
        formGrid.add(languageLabel, 0, 3);
        formGrid.add(languageCombo, 1, 3);
        
        Button saveButton = new Button("Guardar Cambios");
        saveButton.getStyleClass().add("action-button");
        
        Text notificationsTitle = new Text("Preferencias de Notificación");
        notificationsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        VBox notificationsBox = new VBox(10);
        notificationsBox.setPadding(new Insets(15));
        notificationsBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        CheckBox dailyReminder = new CheckBox("Recordatorio diario");
        dailyReminder.setSelected(true);
        CheckBox progressUpdates = new CheckBox("Actualizaciones de progreso");
        progressUpdates.setSelected(true);
        CheckBox newCoursesNotif = new CheckBox("Notificación de nuevos cursos");
        newCoursesNotif.setSelected(false);
        notificationsBox.getChildren().addAll(dailyReminder, progressUpdates, newCoursesNotif);
        
        profilePane.getChildren().addAll(profileHeader, separator, editProfileTitle, formGrid, saveButton, notificationsTitle, notificationsBox);
        
        ScrollPane scrollPane = new ScrollPane(profilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
        animateContentEntry(scrollPane);
    }
    
    private void showStatsPage() {
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        ((Button) sidebar.getChildren().get(4)).getStyleClass().add("sidebar-button-active");
        
        // Obtener las estadísticas reales del usuario
        Usuario usuarioActual = controlador.getUsuarioActual();
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO();
        Estadistica estadistica = estadisticaDAO.buscarPorUsuario(usuarioActual);
        
        // Si no hay estadísticas, crear unas por defecto
        if (estadistica == null) {
            estadistica = new Estadistica(null, 0, 0, 0);
            estadistica.setUsuario(usuarioActual);
            estadisticaDAO.guardar(estadistica);
        }
        
        // Actualizar la fecha de última conexión
        estadistica.setUltimaConexion(new Date());
        estadisticaDAO.actualizar(estadistica);
        
        // Obtener datos de progreso global de cursos
        CursoEnProgresoDAO cursoEnProgresoDAO = new CursoEnProgresoDAO();
        List<CursoEnProgreso> cursosEnProgreso = cursoEnProgresoDAO.buscarPorUsuario(usuarioActual);
        
        int totalPreguntasCorrectas = 0;
        int totalPreguntasIncorrectas = 0;
        int cursosCompletados = 0;
        int totalLeccionesCompletadas = 0;
        
        for (CursoEnProgreso progreso : cursosEnProgreso) {
            totalPreguntasCorrectas += progreso.getPreguntasCorrectas();
            totalPreguntasIncorrectas += progreso.getPreguntasIncorrectas();
            
            if (progreso.isCompletado()) {
                cursosCompletados++;
            }
            
            // Contar lecciones (bloques) completados
            if (progreso.getBloqueActual() > 0) {
                totalLeccionesCompletadas += progreso.getBloqueActual();
            }
        }
        
        // Calcular puntos XP (simple: 10 puntos por pregunta correcta)
        int puntosXP = totalPreguntasCorrectas * 10;
        
        // Crear el panel de estadísticas
        statsPane = new VBox(25);
        statsPane.setPadding(new Insets(30));
        
        Text pageTitle = new Text("Mis Estadísticas");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        
        // Panel de resumen con estadísticas reales
        HBox summaryPanel = new HBox(30);
        summaryPanel.setAlignment(Pos.CENTER);
        summaryPanel.setPadding(new Insets(25));
        summaryPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        VBox diasConsecutivosBox = createStatBox(String.valueOf(estadistica.getDiasConsecutivos()), "Días consecutivos");
        VBox xpBox = createStatBox(String.format("%,d", puntosXP), "Puntos XP");
        VBox leccionesBox = createStatBox(String.valueOf(totalLeccionesCompletadas), "Lecciones completadas");
        VBox cursosCompletadosBox = createStatBox(String.valueOf(cursosCompletados), "Cursos completados");
        
        summaryPanel.getChildren().addAll(diasConsecutivosBox, xpBox, leccionesBox, cursosCompletadosBox);
        
        // Panel de precisión y estadísticas de aprendizaje
        Text learningStatsTitle = new Text("Estadísticas de Aprendizaje");
        learningStatsTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Calcular precisión
        double precision = 0;
        if (totalPreguntasCorrectas + totalPreguntasIncorrectas > 0) {
            precision = (double) totalPreguntasCorrectas / (totalPreguntasCorrectas + totalPreguntasIncorrectas) * 100;
        }
        
        VBox learningPanel = new VBox(20);
        learningPanel.setPadding(new Insets(20));
        learningPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        // Panel de precisión
        HBox precisionBox = new HBox(15);
        precisionBox.setAlignment(Pos.CENTER_LEFT);
        
        // Crear indicador de precisión circular
        double radius = 50;
        StackPane precisionIndicator = new StackPane();
        
        Circle backgroundCircle = new Circle(radius);
        backgroundCircle.setFill(Color.LIGHTGRAY);
        
        // Crear arco para mostrar precisión
        Arc precisionArc = new Arc(0, 0, radius, radius, 90, -precision * 3.6);
        precisionArc.setType(ArcType.ROUND);
        precisionArc.setFill(precision >= 70 ? Color.valueOf("#06d6a0") : 
                            precision >= 40 ? Color.valueOf("#ffd166") : 
                            Color.valueOf("#ef476f"));
        
        // Añadir texto de porcentaje en el centro
        Text precisionText = new Text(String.format("%.1f%%", precision));
        precisionText.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Añadir circulo central para mejorar diseño
        Circle innerCircle = new Circle(radius * 0.7);
        innerCircle.setFill(Color.WHITE);
        
        precisionIndicator.getChildren().addAll(backgroundCircle, precisionArc, innerCircle, precisionText);
        
        // Información de preguntas
        VBox questionsInfo = new VBox(10);
        questionsInfo.setAlignment(Pos.CENTER_LEFT);
        
        Text precisionTitle = new Text("Precisión de Respuestas");
        precisionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Text correctasText = new Text("Respuestas correctas: " + totalPreguntasCorrectas);
        correctasText.setFill(Color.valueOf("#06d6a0"));
        
        Text incorrectasText = new Text("Respuestas incorrectas: " + totalPreguntasIncorrectas);
        incorrectasText.setFill(Color.valueOf("#ef476f"));
        
        Text totalText = new Text("Total de respuestas: " + (totalPreguntasCorrectas + totalPreguntasIncorrectas));
        
        questionsInfo.getChildren().addAll(precisionTitle, correctasText, incorrectasText, totalText);
        
        precisionBox.getChildren().addAll(precisionIndicator, questionsInfo);
        
        // Información adicional
        HBox additionalInfo = new HBox(30);
        additionalInfo.setAlignment(Pos.CENTER);
        
        VBox tiempoBox = createInfoBox("Tiempo total de uso", 
                formatTime(estadistica.getTiempoTotalUso()), 
                "clock");
        
        VBox rachaBox = createInfoBox("Mejor racha", 
                estadistica.getMejorRacha() + " días", 
                "streak");
        
        VBox ultimaConexionBox = createInfoBox("Última conexión", 
                formatDate(estadistica.getUltimaConexion()), 
                "calendar");
        
        additionalInfo.getChildren().addAll(tiempoBox, rachaBox, ultimaConexionBox);
        
        learningPanel.getChildren().addAll(precisionBox, new Separator(), additionalInfo);
        
        // Panel de cursos en progreso
        Text progressTitle = new Text("Progreso por Curso");
        progressTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox progressPanel = new VBox(15);
        progressPanel.setPadding(new Insets(20));
        progressPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        // Mostrar hasta 5 cursos en progreso con sus barras de progreso
        int coursesToShow = Math.min(cursosEnProgreso.size(), 5);
        if (coursesToShow > 0) {
            for (int i = 0; i < coursesToShow; i++) {
                CursoEnProgreso progreso = cursosEnProgreso.get(i);
                Curso curso = progreso.getCurso();
                
                if (curso != null) {
                    // Calcular progreso real para este curso
                    double porcentaje = calcularPorcentajeCompletado(progreso, curso);
                    
                    HBox courseProgressBox = new HBox(15);
                    courseProgressBox.setAlignment(Pos.CENTER_LEFT);
                    
                    Circle courseIcon = new Circle(15);
                    courseIcon.setFill(getColorForCourse(curso.getTitulo()));
                    
                    VBox courseInfo = new VBox(5);
                    courseInfo.setPrefWidth(300);
                    
                    Text courseTitle = new Text(curso.getTitulo());
                    courseTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
                    
                    HBox progressBarInfo = new HBox(10);
                    progressBarInfo.setAlignment(Pos.CENTER_LEFT);
                    
                    ProgressBar progressBar = new ProgressBar(porcentaje / 100);
                    progressBar.setPrefWidth(200);
                    
                    Text percentText = new Text(String.format("%.1f%%", porcentaje));
                    
                    progressBarInfo.getChildren().addAll(progressBar, percentText);
                    courseInfo.getChildren().addAll(courseTitle, progressBarInfo);
                    
                    courseProgressBox.getChildren().addAll(courseIcon, courseInfo);
                    progressPanel.getChildren().add(courseProgressBox);
                    
                    if (i < coursesToShow - 1) {
                        Separator separator = new Separator();
                        separator.setPadding(new Insets(5, 0, 5, 0));
                        progressPanel.getChildren().add(separator);
                    }
                }
            }
        } else {
            Text noCourses = new Text("No tienes cursos en progreso actualmente");
            noCourses.setStyle("-fx-fill: #666;");
            progressPanel.getChildren().add(noCourses);
        }
        
        // Actividad reciente
        Text recentActivityTitle = new Text("Actividad Reciente");
        recentActivityTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox activityPanel = new VBox(10);
        activityPanel.setPadding(new Insets(20));
        activityPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        // Generar actividades basadas en datos reales (ultimas 5 actividades)
        List<String> actividades = new ArrayList<>();
        List<String> tiempos = new ArrayList<>();
        
        // Aquí podrías obtener las actividades reales del usuario desde una tabla de actividades
        // Por ahora usaremos actividades genéricas basadas en los cursos en progreso
        for (CursoEnProgreso progreso : cursosEnProgreso) {
            Curso curso = progreso.getCurso();
            if (curso != null) {
                actividades.add("Progreso en curso de " + curso.getTitulo());
                tiempos.add(formatTimeAgo(progreso.getFechaUltimaActividad()));
                
                if (actividades.size() >= 5) break;
            }
        }
        
        // Si no hay suficientes actividades, añadir algunas genéricas
        if (actividades.isEmpty()) {
            Text noActivity = new Text("No hay actividad reciente");
            noActivity.setStyle("-fx-fill: #666;");
            activityPanel.getChildren().add(noActivity);
        } else {
            for (int i = 0; i < actividades.size(); i++) {
                HBox activityItem = new HBox(15);
                activityItem.setAlignment(Pos.CENTER_LEFT);
                
                Circle activityDot = new Circle(5);
                activityDot.setFill(Color.valueOf("#4a69bd"));
                
                VBox activityInfo = new VBox(3);
                Text activityText = new Text(actividades.get(i));
                activityText.setStyle("-fx-font-weight: bold;");
                Text timeText = new Text(tiempos.get(i));
                timeText.setStyle("-fx-fill: #666;");
                
                activityInfo.getChildren().addAll(activityText, timeText);
                activityItem.getChildren().addAll(activityDot, activityInfo);
                activityPanel.getChildren().add(activityItem);
                
                if (i < actividades.size() - 1) {
                    Separator separator = new Separator();
                    separator.setPadding(new Insets(5, 0, 5, 0));
                    activityPanel.getChildren().add(separator);
                }
            }
        }
        
        // Añadir todo al panel principal
        statsPane.getChildren().addAll(
            pageTitle, 
            summaryPanel, 
            learningStatsTitle,
            learningPanel,
            progressTitle,
            progressPanel,
            recentActivityTitle,
            activityPanel
        );
        
        // Crear ScrollPane para el contenido
        ScrollPane scrollPane = new ScrollPane(statsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
        animateContentEntry(scrollPane);
    }

    // Método auxiliar para crear boxes de información con icono
    private VBox createInfoBox(String title, String value, String iconType) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        // En una aplicación real aquí podrías usar iconos reales
        Circle icon = new Circle(20);
        icon.setFill(Color.valueOf("#4a69bd"));
        
        Text valueText = new Text(value);
        valueText.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 14px; -fx-fill: #666;");
        
        box.getChildren().addAll(icon, valueText, titleText);
        return box;
    }

    // Método para formatear tiempo (en milisegundos) a formato legible
    private String formatTime(long timeInMillis) {
        long hours = timeInMillis / (60 * 60 * 1000);
        long minutes = (timeInMillis % (60 * 60 * 1000)) / (60 * 1000);
        
        if (hours > 0) {
            return hours + "h " + minutes + "min";
        } else {
            return minutes + " minutos";
        }
    }

    // Método para formatear fecha
    private String formatDate(Date date) {
        if (date == null) return "Nunca";
        
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }

    // Método para formatear tiempo relativo (ej: "hace 2 horas")
    private String formatTimeAgo(Date date) {
        if (date == null) return "Fecha desconocida";
        
        long diff = new Date().getTime() - date.getTime();
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        if (diffMinutes < 60) {
            return "Hace " + diffMinutes + " minutos";
        } else if (diffHours < 24) {
            return "Hace " + diffHours + " horas";
        } else {
            return "Hace " + diffDays + " días";
        }
    }
    
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres cerrar sesión?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Stage currentStage = (Stage) mainLayout.getScene().getWindow();
            currentStage.close();
            // Aquí se podría abrir la ventana de login nuevamente
        }
    }
    
    private void handleImportCurso() {
    	// Verificar si el usuario tiene permisos para importar cursos
        if (!controlador.puedeCrearCursos()) {
            mostrarAlerta("Acceso denegado", 
                         "No tienes permisos para importar cursos. Esta funcionalidad está disponible solo para creadores.", 
                         Alert.AlertType.WARNING);
            return;
        }
    	
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de curso");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File archivo = fileChooser.showOpenDialog(mainLayout.getScene().getWindow());
        if (archivo != null) {
            boolean exito = controlador.cargarCursoDesdeArchivo(archivo.getAbsolutePath());
            if (exito) {
                // Obtener el último curso importado para mostrarlo
                List<Curso> cursos = controlador.getCursosDisponibles();
                if (!cursos.isEmpty()) {
                    Curso ultimoCurso = cursos.get(cursos.size() - 1);
                    mostrarAlerta("Curso Importado", 
                        "Curso '" + ultimoCurso.getTitulo() + "' importado con éxito", 
                        Alert.AlertType.INFORMATION);
                }
                
                // Refrescar la vista de cursos
                showCoursesPage();
            } else {
                mostrarAlerta("Error", "Error al importar el curso", Alert.AlertType.ERROR);
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    
    private VBox createStatBox(String statValue, String statLabel) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        Text value = new Text(statValue);
        value.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #4a69bd;");
        Text label = new Text(statLabel);
        label.setStyle("-fx-font-size: 14px; -fx-fill: #666;");
        statBox.getChildren().addAll(value, label);
        return statBox;
    }
    
    private void resetSidebarButtonStyles() {
        for (int i = 1; i < sidebar.getChildren().size() - 1; i++) {
            Node node = sidebar.getChildren().get(i);
            if (node instanceof Button) {
                ((Button) node).getStyleClass().remove("sidebar-button-active");
            }
        }
    }
    
    private void animateContentEntry(Node content) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), content);
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        
        ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn);
        parallelTransition.play();
    }
}
