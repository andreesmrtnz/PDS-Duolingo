package vista;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
import modelo.Pregunta;
import modelo.Usuario;

import java.io.File;
import java.util.List;

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
    
    public VentanaPrincipal() {
        this.controlador = Controlador.getInstancia();
    }
    
    public void start(Stage primaryStage) {
        setupMainUI(primaryStage);
    }
    
    private void setupMainUI(Stage stage) {
    	mainLayout = new BorderPane();
        setupSidebar();
        mainLayout.setLeft(sidebar);
        
        // Configurar el área de contenido
        setupContentArea();
        mainLayout.setCenter(contentArea);  // Añadir esta línea
        
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
        System.out.println("Número de cursos: " + controlador.getCursosDisponibles().size());
        
        // Limpiar completamente
        contentArea.getChildren().clear();
        
        // Resetear estilos de los botones de la barra lateral
        resetSidebarButtonStyles();
        
        // Marcar el botón de "Mis Cursos" como activo
        ((Button) sidebar.getChildren().get(2)).getStyleClass().add("sidebar-button-active");
        
        // Crear el layout principal para cursos
        VBox coursesLayout = new VBox(25);
        coursesLayout.setPadding(new Insets(15));
        
        // Título de la página
        Text pageTitle = new Text("Mis Cursos");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        coursesLayout.getChildren().add(pageTitle);
        
        // Obtener la lista de cursos
        List<Curso> cursos = controlador.getCursosDisponibles();
        
        // Crear contenedor para los cursos
        if (cursos.isEmpty()) {
            Label emptyLabel = new Label("No hay cursos cargados. Utilice la opción 'Importar Curso'.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            coursesLayout.getChildren().add(emptyLabel);
        } else {
            // Usar GridPane para organizar los cursos en columnas
            GridPane coursesGrid = new GridPane();
            coursesGrid.setHgap(20);
            coursesGrid.setVgap(20);
            
            // Añadir tarjetas de cursos
            for (int i = 0; i < cursos.size(); i++) {
                Curso curso = cursos.get(i);
                Pane cursoCard = createCourseCard(curso);
                
                // Calcular posición en la cuadrícula (3 columnas)
                coursesGrid.add(cursoCard, i % 3, i / 3);
            }
            
            coursesLayout.getChildren().add(coursesGrid);
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
    private Pane createCourseCard(Curso curso) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        card.getStyleClass().add("course-card");
        
        Circle icon = new Circle(30);
        icon.setFill(getColorForCourse(curso.getTitulo()));
        
        Text title = new Text(curso.getTitulo());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Se usa el dominio como descripción (puedes ajustar según tus atributos)
        Text description = new Text(curso.getDominio());
        description.setWrappingWidth(190);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button startButton = new Button("Iniciar");
        startButton.getStyleClass().add("action-button");
        startButton.setPrefWidth(190);
        startButton.setOnAction(e -> startCourse(curso));
        
        card.getChildren().addAll(icon, title, description, spacer, startButton);
        Tooltip tooltip = new Tooltip(curso.getTitulo() + " - " + curso.getDominio());
        Tooltip.install(card, tooltip);
        
        return card;
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
    
    
 // Inicia el curso seleccionado y abre la VentanaPreguntas
    private void startCourse(Curso curso) {
        try {
            // Establecer el curso actual en el controlador
            controlador.iniciarCurso(curso);
            
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
        
        statsPane = new VBox(25);
        statsPane.setPadding(new Insets(30));
        
        Text pageTitle = new Text("Mis Estadísticas");
        pageTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        
        HBox summaryPanel = new HBox(30);
        summaryPanel.setAlignment(Pos.CENTER);
        summaryPanel.setPadding(new Insets(25));
        summaryPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        VBox dailyActivity = createStatBox("7", "Días consecutivos");
        VBox totalXP = createStatBox("1,250", "Puntos XP");
        VBox completedLessons = createStatBox("23", "Lecciones completadas");
        VBox masteredSkills = createStatBox("15", "Habilidades dominadas");
        summaryPanel.getChildren().addAll(dailyActivity, totalXP, completedLessons, masteredSkills);
        
        Text progressTitle = new Text("Progreso por Curso");
        progressTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox progressPanel = new VBox(15);
        progressPanel.setPadding(new Insets(20));
        progressPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        // Se omite el detalle de cada curso para simplificar
        
        Text recentActivityTitle = new Text("Actividad Reciente");
        recentActivityTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox activityPanel = new VBox(10);
        activityPanel.setPadding(new Insets(20));
        activityPanel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
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
            if (i < activities.length - 1) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(5, 0, 5, 0));
                activityPanel.getChildren().add(separator);
            }
        }
        
        statsPane.getChildren().addAll(pageTitle, summaryPanel, progressTitle, progressPanel, recentActivityTitle, activityPanel);
        ScrollPane scrollPane = new ScrollPane(statsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        contentArea.getChildren().add(scrollPane);
        animateContentEntry(scrollPane);
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
