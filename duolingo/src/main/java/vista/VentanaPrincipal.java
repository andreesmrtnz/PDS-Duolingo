package vista;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Controlador;
import modelo.Curso;
import modelo.Usuario;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class VentanaPrincipal {
    
    // Referencia al controlador
    private final Controlador controlador;
    
    // Referencia al usuario actual
    private Usuario usuarioActual;
    
    // Componentes de UI
    private BorderPane mainLayout;
    private VBox sidebar;
    private StackPane contentArea;
    private ScrollPane coursesScrollPane;
    private VBox profilePane;
    private VBox statsPane;
    
    // Datos de ejemplo para cursos
    private final List<CursoInfo> cursosDemostracion = Arrays.asList(
        new CursoInfo("Programación Java", "Aprende Java desde cero", "java_icon", 3, 25),
        new CursoInfo("Python Básico", "Introducción a Python", "python_icon", 5, 40),
        new CursoInfo("Bases de Datos SQL", "Domina SQL y relaciones", "sql_icon", 2, 15),
        new CursoInfo("HTML y CSS", "Crea sitios web modernos", "web_icon", 4, 30),
        new CursoInfo("JavaScript Avanzado", "Frameworks y herramientas", "js_icon", 1, 10),
        new CursoInfo("Algoritmos", "Estructuras de datos y eficiencia", "algo_icon", 0, 0)
    );
    
 // Modifica/agrega estos métodos en VentanaPrincipal

 // Agregar un constructor sin parámetros
 public VentanaPrincipal() {
     this.controlador = Controlador.getInstancia();
     this.usuarioActual = null; // Se establecerá más tarde con setUsuario
 }

 // Implementar el método setUsuario
 public void setUsuario(Usuario usuario) {
     this.usuarioActual = usuario; // 
 }

 // Implementar el método start
 public void start(Stage primaryStage) {
     setupMainUI(primaryStage);
 }
    
    private void setupMainUI(Stage stage) {
        // Crear el layout principal
        mainLayout = new BorderPane();
        
        // Configurar la barra lateral
        setupSidebar();
        mainLayout.setLeft(sidebar);
        
        // Configurar el área de contenido
        setupContentArea();
        mainLayout.setCenter(contentArea);
        
        // Configurar la barra superior
        setupTopBar();
        
        // Crear y mostrar la escena
        Scene scene = new Scene(mainLayout, 1000, 700);
        
        try {
            // Intentar cargar el CSS
            String cssFile = "/styles.css";
            String css = getClass().getResource(cssFile).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Error al cargar el CSS: " + e.getMessage());
            // Aplicar estilos de respaldo
            applyFallbackStyles(scene);
        }
        
        stage.setTitle("LinguaLearn - Aprende a tu ritmo");
        stage.setScene(scene);
        stage.show();
        
        // Mostrar la landing por defecto
        showLandingPage();
    }
    
    private void applyFallbackStyles(Scene scene) {
        // Estilos CSS de respaldo
        String css = 
            // Estilos generales
            ".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 14px; -fx-background-color: #f8f9fa; }" +
            
            // Estilos para la barra lateral
            ".sidebar { -fx-background-color: #4a69bd; -fx-padding: 15; }" +
            ".sidebar-button { -fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; " +
                              "-fx-alignment: CENTER_LEFT; -fx-padding: 10 15; -fx-cursor: hand; }" +
            ".sidebar-button:hover { -fx-background-color: rgba(255, 255, 255, 0.2); }" +
            ".sidebar-button-active { -fx-background-color: rgba(255, 255, 255, 0.3); }" +
            
            // Estilos para tarjetas de cursos
            ".course-card { -fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 3); }" +
            ".course-card:hover { -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0, 0, 5); }" +
            
            // Botones de acción
            ".action-button { -fx-background-color: #4a69bd; -fx-text-fill: white; -fx-background-radius: 5; }" +
            ".action-button:hover { -fx-background-color: #3c58a8; }" +
            
            // Estilos de temas
            ".section-title { -fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 15 0 10 0; }" +
            ".welcome-banner { -fx-background-color: linear-gradient(to right, #4a69bd, #7b8cde); " +
                              "-fx-background-radius: 10; -fx-padding: 20; }";
        
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
        
        // Logo (simulado con un círculo por ahora)
        Circle logoCircle = new Circle(20, Color.WHITE);
        
        // Nombre de la app
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
        
        // Espaciador para empujar el botón de logout al fondo
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(logoBox, homeButton, coursesButton, profileButton, 
                                    statsButton, spacer, logoutButton);
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
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #f8f9fa;");
    }
    
    private void setupTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setSpacing(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        
        // Nombre de usuario
        Label userLabel = new Label("¡Hola, " + usuarioActual.getNombre() + "!");
        userLabel.setStyle("-fx-font-weight: bold;");
        
        // Icono de usuario (simulado con círculo)
        Circle userIcon = new Circle(15, Color.valueOf("#4a69bd"));
        
        topBar.getChildren().addAll(userLabel, userIcon);
        
        mainLayout.setTop(topBar);
    }
    
    private void showLandingPage() {
        // Limpiar el contenido actual
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        
        // Configurar el botón activo
        ((Button) sidebar.getChildren().get(1)).getStyleClass().add("sidebar-button-active");
        
        // Crear layout para la landing page
        VBox landingLayout = new VBox(20);
        landingLayout.setAlignment(Pos.TOP_CENTER);
        
        // Banner de bienvenida
        VBox welcomeBox = new VBox(15);
        welcomeBox.getStyleClass().add("welcome-banner");
        welcomeBox.setPrefHeight(200);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        welcomeBox.setPadding(new Insets(25));
        
        Text welcomeTitle = new Text("¡Bienvenido a LinguaLearn, " + usuarioActual.getNombre() + "!");
        welcomeTitle.setStyle("-fx-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        Text welcomeSubtitle = new Text("Continúa aprendiendo a tu ritmo. ¡Hoy es un gran día para aprender algo nuevo!");
        welcomeSubtitle.setStyle("-fx-fill: white; -fx-font-size: 16px;");
        
        Button continueButton = new Button("Continuar Aprendiendo");
        continueButton.getStyleClass().add("action-button");
        continueButton.setOnAction(e -> showCoursesPage());
        
        welcomeBox.getChildren().addAll(welcomeTitle, welcomeSubtitle, continueButton);
        
        // Sección de cursos destacados
        Text featuredTitle = new Text("Cursos Destacados");
        featuredTitle.getStyleClass().add("section-title");
        
        // Grid de cursos destacados (3 cursos por fila)
        GridPane featuredGrid = new GridPane();
        featuredGrid.setHgap(20);
        featuredGrid.setVgap(20);
        
        // Agregar algunos cursos destacados
        for (int i = 0; i < 3; i++) {
            CursoInfo curso = cursosDemostracion.get(i);
            Pane cursoCard = createCourseCard(curso);
            featuredGrid.add(cursoCard, i % 3, i / 3);
        }
        
        // Sección de estadísticas rápidas
        Text statsTitle = new Text("Tu Progreso");
        statsTitle.getStyleClass().add("section-title");
        
        // Panel de estadísticas
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20));
        statsBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        VBox dailyStreak = createStatBox("7", "Días seguidos");
        VBox coursesProgress = createStatBox("2/6", "Cursos completados");
        VBox lessonsCompleted = createStatBox("23", "Lecciones completadas");
        
        statsBox.getChildren().addAll(dailyStreak, coursesProgress, lessonsCompleted);
        
        // Agregar todo al layout principal
        landingLayout.getChildren().addAll(welcomeBox, featuredTitle, featuredGrid, statsTitle, statsBox);
        
        // Envolver en ScrollPane para permitir desplazamiento
        ScrollPane scrollPane = new ScrollPane(landingLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        // Mostrar la landing page
        contentArea.getChildren().add(scrollPane);
        
        // Animar la entrada
        animateContentEntry(scrollPane);
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
    
    private void showCoursesPage() {
        // Limpiar el contenido actual
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        
        // Configurar el botón activo
        ((Button) sidebar.getChildren().get(2)).getStyleClass().add("sidebar-button-active");
        
        // Crear layout para la página de cursos
        VBox coursesLayout = new VBox(25);
        coursesLayout.setPadding(new Insets(15));
        
        // Título e información
        Text pageTitle = new Text("Mis Cursos");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        
        // Banner de cursos activos
        HBox activeCoursesBox = new HBox(20);
        activeCoursesBox.setPadding(new Insets(15));
        activeCoursesBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        activeCoursesBox.setPrefHeight(180);
        
        // Curso activo principal
        VBox activeCourse = new VBox(10);
        activeCourse.setPrefWidth(300);
        
        Text activeTitle = new Text("Programación Java");
        activeTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        ProgressBar progressBar = new ProgressBar(0.65);
        progressBar.setPrefWidth(280);
        
        Text progressText = new Text("65% completado - Lección 13 de 20");
        
        Button continueButton = new Button("Continuar");
        continueButton.getStyleClass().add("action-button");
        continueButton.setOnAction(e -> startCourse(cursosDemostracion.get(0)));
        
        activeCourse.getChildren().addAll(activeTitle, progressBar, progressText, continueButton);
        
        activeCoursesBox.getChildren().add(activeCourse);
        
        // Separador
        Text availableTitle = new Text("Todos los Cursos");
        availableTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        
        // Grid de todos los cursos
        GridPane coursesGrid = new GridPane();
        coursesGrid.setHgap(20);
        coursesGrid.setVgap(20);
        
        // Agregar todos los cursos disponibles
        for (int i = 0; i < cursosDemostracion.size(); i++) {
            CursoInfo curso = cursosDemostracion.get(i);
            Pane cursoCard = createCourseCard(curso);
            coursesGrid.add(cursoCard, i % 3, i / 3);
        }
        
        // Agregar todo al layout
        coursesLayout.getChildren().addAll(pageTitle, activeCoursesBox, availableTitle, coursesGrid);
        
        // Envolver en ScrollPane
        coursesScrollPane = new ScrollPane(coursesLayout);
        coursesScrollPane.setFitToWidth(true);
        coursesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        coursesScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(coursesScrollPane);
        
        // Animar la entrada
        animateContentEntry(coursesScrollPane);
    }
    
    private Pane createCourseCard(CursoInfo curso) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        card.getStyleClass().add("course-card");
        
        // Círculo de color para simular un icono
        Circle icon = new Circle(30);
        icon.setFill(getColorForCourse(curso.getTitulo()));
        
        Text title = new Text(curso.getTitulo());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Text description = new Text(curso.getDescripcion());
        description.setWrappingWidth(190);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button startButton = new Button(curso.getLeccionesCompletadas() > 0 ? "Continuar" : "Empezar");
        startButton.getStyleClass().add("action-button");
        startButton.setPrefWidth(190);
        startButton.setOnAction(e -> startCourse(curso));
        
        card.getChildren().addAll(icon, title, description, spacer, startButton);
        
        // Agregar tooltip
        Tooltip tooltip = new Tooltip(curso.getDescripcion());
        Tooltip.install(card, tooltip);
        
        return card;
    }
    
    private Color getColorForCourse(String courseTitle) {
        // Asignar colores basados en el título del curso
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
    
    private void showProfilePage() {
        // Limpiar el contenido actual
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        
        // Configurar el botón activo
        ((Button) sidebar.getChildren().get(3)).getStyleClass().add("sidebar-button-active");
        
        // Crear layout para la página de perfil
        profilePane = new VBox(25);
        profilePane.setPadding(new Insets(30));
        
        // Información del perfil
        HBox profileHeader = new HBox(20);
        profileHeader.setAlignment(Pos.CENTER_LEFT);
        
        // Avatar (simulado con círculo)
        Circle avatar = new Circle(50);
        avatar.setFill(Color.valueOf("#4a69bd"));
        
        // Información básica
        VBox profileInfo = new VBox(5);
        Text userName = new Text(usuarioActual.getNombre());
        userName.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        Text userEmail = new Text(usuarioActual.getEmail());
        
        Text memberSince = new Text("Miembro desde: Marzo 2025");
        
        profileInfo.getChildren().addAll(userName, userEmail, memberSince);
        
        profileHeader.getChildren().addAll(avatar, profileInfo);
        
        // Separador
        Separator separator = new Separator();
        
        // Formulario para editar perfil
        Text editProfileTitle = new Text("Editar Perfil");
        editProfileTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        
        // Campos de edición
        Label nameLabel = new Label("Nombre:");
        TextField nameField = new TextField(usuarioActual.getNombre());
        
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(usuarioActual.getEmail());
        
        Label passwordLabel = new Label("Contraseña:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Cambiar contraseña");
        
        Label languageLabel = new Label("Idioma preferido:");
        ComboBox<String> languageCombo = new ComboBox<>(
                FXCollections.observableArrayList("Español", "English", "Français", "Deutsch"));
        languageCombo.setValue("Español");
        
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 1, 2);
        formGrid.add(languageLabel, 0, 3);
        formGrid.add(languageCombo, 1, 3);
        
        // Botón para guardar cambios
        Button saveButton = new Button("Guardar Cambios");
        saveButton.getStyleClass().add("action-button");
        
        // Preferencias de notificación
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
        
        // Agregar todo al layout
        profilePane.getChildren().addAll(profileHeader, separator, editProfileTitle, 
                                        formGrid, saveButton, notificationsTitle, notificationsBox);
        
        // Scroll pane para el perfil
        ScrollPane scrollPane = new ScrollPane(profilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
        
        // Animar la entrada
        animateContentEntry(scrollPane);
    }
    
    private void showStatsPage() {
        // Limpiar el contenido actual
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        
        // Configurar el botón activo
        ((Button) sidebar.getChildren().get(4)).getStyleClass().add("sidebar-button-active");
        
        // Crear layout para la página de estadísticas
        statsPane = new VBox(25);
        statsPane.setPadding(new Insets(30));
        
        // Título de la página
        Text pageTitle = new Text("Mis Estadísticas");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        
        // Panel de resumen
        HBox summaryPanel = new HBox(30);
        summaryPanel.setAlignment(Pos.CENTER);
        summaryPanel.setPadding(new Insets(25));
        summaryPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        VBox dailyActivity = createStatBox("7", "Días consecutivos");
        VBox totalXP = createStatBox("1,250", "Puntos XP");
        VBox completedLessons = createStatBox("23", "Lecciones completadas");
        VBox masteredSkills = createStatBox("15", "Habilidades dominadas");
        
        summaryPanel.getChildren().addAll(dailyActivity, totalXP, completedLessons, masteredSkills);
        
        // Panel de progreso por curso
        Text progressTitle = new Text("Progreso por Curso");
        progressTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox progressPanel = new VBox(15);
        progressPanel.setPadding(new Insets(20));
        progressPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        // Para cada curso, mostrar una barra de progreso
        for (CursoInfo curso : cursosDemostracion) {
            if (curso.getLeccionesCompletadas() > 0) {
                VBox courseProgress = new VBox(5);
                
                HBox labelBox = new HBox();
                labelBox.setAlignment(Pos.CENTER_LEFT);
                
                Label courseName = new Label(curso.getTitulo());
                courseName.setStyle("-fx-font-weight: bold;");
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                Label percentage = new Label(
                        String.format("%.0f%%", (double) curso.getLeccionesCompletadas() / curso.getLeccionesCurso() * 100));
                
                labelBox.getChildren().addAll(courseName, spacer, percentage);
                
                ProgressBar progressBar = new ProgressBar(
                        (double) curso.getLeccionesCompletadas() / curso.getLeccionesCurso());
                progressBar.setPrefWidth(Double.MAX_VALUE);
                
                courseProgress.getChildren().addAll(labelBox, progressBar);
                progressPanel.getChildren().add(courseProgress);
            }
        }
        
        // Panel de actividad reciente
        Text recentActivityTitle = new Text("Actividad Reciente");
        recentActivityTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox activityPanel = new VBox(10);
        activityPanel.setPadding(new Insets(20));
        activityPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        // Entradas de actividad reciente
        String[] activities = {
            "Completaste la Lección 13 de Java",
            "Ganaste 50 XP en Algoritmos",
            "Completaste un ejercicio en Python",
            "Alcanzaste 7 días consecutivos",
            "Completaste la Lección 12 de Java"
        };
        
        String[] times = {
            "Hace 2 horas",
            "Ayer",
            "Hace 2 días",
            "Hace 2 días",
            "Hace 3 días"
        };
        
        for (int i = 0; i < activities.length; i++) {
            HBox activityItem = new HBox(15);
            activityItem.setAlignment(Pos.CENTER_LEFT);
            
            Circle activityDot = new Circle(5, Color.valueOf("#4a69bd"));
            
            VBox activityInfo = new VBox(3);
            Text activityText = new Text(activities[i]);
            activityText.setStyle("-fx-font-weight: bold;");
            
            Text timeText = new Text(times[i]);
            timeText.setStyle("-fx-fill: #666;");
            
            activityInfo.getChildren().addAll(activityText, timeText);
            
            activityItem.getChildren().addAll(activityDot, activityInfo);
            activityPanel.getChildren().add(activityItem);
            
            // Agregar separador excepto para el último elemento
            if (i < activities.length - 1) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(5, 0, 5, 0));
                activityPanel.getChildren().add(separator);
            }
        }
        
        // Agregar todos los componentes al panel principal
        statsPane.getChildren().addAll(pageTitle, summaryPanel, progressTitle, 
                                     progressPanel, recentActivityTitle, activityPanel);
        
        // Scroll pane para las estadísticas
        ScrollPane scrollPane = new ScrollPane(statsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
        
        // Animar la entrada
        animateContentEntry(scrollPane);
    }
    
    private void handleLogout() {
        // Mostrar confirmación antes de cerrar sesión
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres cerrar sesión?");
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Obtener la etapa actual
            Stage currentStage = (Stage) mainLayout.getScene().getWindow();
            
            // Cerrar la ventana actual
            currentStage.close();
            
            // Abrir la ventana de inicio de sesión
            try {
                Stage loginStage = new Stage();
                VentanaLoginRegister loginWindow = new VentanaLoginRegister();
            } catch (Exception e) {
                System.err.println("Error al abrir la ventana de inicio de sesión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void startCourse(CursoInfo curso) {
        // Limpiar el contenido actual
        contentArea.getChildren().clear();
        
        // Crear layout para la vista del curso
        VBox courseLayout = new VBox(20);
        courseLayout.setPadding(new Insets(25));
        
        // Cabecera del curso
        HBox courseHeader = new HBox(15);
        courseHeader.setAlignment(Pos.CENTER_LEFT);
        courseHeader.setPadding(new Insets(15));
        courseHeader.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Icono del curso (simulado con círculo)
        Circle courseIcon = new Circle(40);
        courseIcon.setFill(getColorForCourse(curso.getTitulo()));
        
        // Información del curso
        VBox courseInfo = new VBox(5);
        Text courseTitle = new Text(curso.getTitulo());
        courseTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        Text courseDesc = new Text(curso.getDescripcion());
        
        // Progreso actual
        HBox progressInfo = new HBox(10);
        ProgressBar courseProgress = new ProgressBar(
                (double) curso.getLeccionesCompletadas() / curso.getLeccionesCurso());
        courseProgress.setPrefWidth(200);
        
        Text progressText = new Text(String.format("%d/%d lecciones completadas", 
                curso.getLeccionesCompletadas(), curso.getLeccionesCurso()));
        
        progressInfo.getChildren().addAll(courseProgress, progressText);
        
        courseInfo.getChildren().addAll(courseTitle, courseDesc, progressInfo);
        
        // Botones de acción
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button backButton = new Button("Volver a cursos");
        backButton.setOnAction(e -> showCoursesPage());
        
        courseHeader.getChildren().addAll(courseIcon, courseInfo, spacer, backButton);
        
        // Sección de lecciones
        Text lessonsTitle = new Text("Lecciones del curso");
        lessonsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        VBox lessonsContainer = new VBox(10);
        
        // Generar lecciones de demostración
        for (int i = 1; i <= curso.getLeccionesCurso(); i++) {
            boolean completed = i <= curso.getLeccionesCompletadas();
            HBox lessonItem = createLessonItem(i, curso.getTitulo(), completed);
            lessonsContainer.getChildren().add(lessonItem);
        }
        
        // Agregar todo al layout
        courseLayout.getChildren().addAll(courseHeader, lessonsTitle, lessonsContainer);
        
        // Scroll pane para las lecciones
        ScrollPane scrollPane = new ScrollPane(courseLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
        
        // Animar la entrada
        animateContentEntry(scrollPane);
    }
    
    private HBox createLessonItem(int lessonNumber, String courseTitle, boolean completed) {
        HBox lessonItem = new HBox(15);
        lessonItem.setPadding(new Insets(15));
        lessonItem.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        lessonItem.setAlignment(Pos.CENTER_LEFT);
        
        // Indicador de finalización
        Circle statusIndicator = new Circle(12);
        if (completed) {
            statusIndicator.setFill(Color.valueOf("#4CAF50"));
            statusIndicator.setStroke(Color.TRANSPARENT);
        } else {
            statusIndicator.setFill(Color.WHITE);
            statusIndicator.setStroke(Color.LIGHTGRAY);
            statusIndicator.setStrokeWidth(2);
        }
        
        // Información de la lección
        VBox lessonInfo = new VBox(5);
        Text lessonTitle = new Text(String.format("Lección %d: %s", 
                lessonNumber, getLessonTitle(courseTitle, lessonNumber)));
        lessonTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Text lessonDesc = new Text(String.format("Duración estimada: %d minutos", 
                10 + (int)(Math.random() * 20)));
        
        lessonInfo.getChildren().addAll(lessonTitle, lessonDesc);
        
        // Botón de inicio
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button startLessonButton = new Button(completed ? "Repasar" : "Comenzar");
        startLessonButton.getStyleClass().add("action-button");
        startLessonButton.setOnAction(e -> startLesson(lessonNumber, courseTitle));
        
        lessonItem.getChildren().addAll(statusIndicator, lessonInfo, spacer, startLessonButton);
        return lessonItem;
    }
    
    private String getLessonTitle(String courseTitle, int lessonNumber) {
        // Generar títulos de lección según el curso
        if (courseTitle.contains("Java")) {
            String[] topics = {
                "Introducción a Java", "Variables y tipos", "Estructuras de control",
                "Métodos y funciones", "POO básica", "Herencia", "Interfaces",
                "Excepciones", "Colecciones", "Genéricos", "Concurrencia",
                "Entrada/Salida", "JDBC", "JavaFX", "Patrones de diseño"
            };
            return lessonNumber <= topics.length ? topics[lessonNumber - 1] : 
                   "Tema avanzado " + (lessonNumber - topics.length);
        } else if (courseTitle.contains("Python")) {
            String[] topics = {
                "Introducción a Python", "Variables y tipos básicos", "Listas y tuplas",
                "Diccionarios y sets", "Estructuras de control", "Funciones",
                "Módulos y paquetes", "Manejo de archivos", "Excepciones",
                "POO en Python", "Bibliotecas estándar", "Numpy", "Pandas"
            };
            return lessonNumber <= topics.length ? topics[lessonNumber - 1] : 
                   "Tema avanzado " + (lessonNumber - topics.length);
        } else {
            return "Tema " + lessonNumber;
        }
    }
    
    private void startLesson(int lessonNumber, String courseTitle) {
    try {
        // Crear la instancia de VentanaPreguntas
        VentanaPreguntas ventanaPreguntas = new VentanaPreguntas(courseTitle, lessonNumber);
        
        // Configurar el usuario
        ventanaPreguntas.setUsuario(usuarioActual);
        
        // Iniciar la ventana de preguntas
        ventanaPreguntas.start(new Stage());
        
        // Cerrar la ventana actual (opcional, puedes mantenerla abierta)
        // primaryStage.close();
    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error", "No se pudo abrir la lección: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}
    
    private void resetSidebarButtonStyles() {
        // Quitar la clase de activo de todos los botones
        for (int i = 1; i < sidebar.getChildren().size() - 1; i++) {
            Node node = sidebar.getChildren().get(i);
            if (node instanceof Button) {
                ((Button) node).getStyleClass().remove("sidebar-button-active");
            }
        }
    }
    
    private void animateContentEntry(Node content) {
        // Animación de entrada para el contenido
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
    
    // Clase interna para representar la información de un curso
    public static class CursoInfo {
        private final String titulo;
        private final String descripcion;
        private final String icono;
        private final int leccionesCompletadas;
        private final int leccionesCurso;
        
        public CursoInfo(String titulo, String descripcion, String icono, 
                        int leccionesCompletadas, int leccionesCurso) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icono = icono;
            this.leccionesCompletadas = leccionesCompletadas;
            this.leccionesCurso = leccionesCurso;
        }
        
        public String getTitulo() {
            return titulo;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        public String getIcono() {
            return icono;
        }
        
        public int getLeccionesCompletadas() {
            return leccionesCompletadas;
        }
        
        public int getLeccionesCurso() {
            return leccionesCurso;
        }
    }
    
    private void setupImportButton() {
        Button importButton = new Button("Importar Curso");
        importButton.getStyleClass().add("action-button");
        importButton.setOnAction(e -> handleImportCurso());
        
        HBox topBar = (HBox) mainLayout.getTop();
        topBar.getChildren().add(0, importButton);
    }

    private void handleImportCurso() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de curso");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
            new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        
        File archivo = fileChooser.showOpenDialog(mainLayout.getScene().getWindow());
        if (archivo != null) {
            boolean exito = controlador.cargarCursoDesdeArchivo(
                archivo.getAbsolutePath(),
                usuarioActual
            );
            
            if (exito) {
                mostrarAlerta("Exito","Curso importado con éxito", Alert.AlertType.INFORMATION);
                showCoursesPage(); // Actualizar la vista
            } else {
                mostrarAlerta("Error","Error al importar el curso", Alert.AlertType.ERROR);
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
}