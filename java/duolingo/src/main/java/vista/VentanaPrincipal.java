package vista;

import javafx.animation.FadeTransition;
import javafx.scene.layout.FlowPane;
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
import modelo.Curso;
import modelo.CursoEnProgreso;
import modelo.Estadistica;
import modelo.Estrategia;
import modelo.Usuario;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

import controlador.Controlador;

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
    private long sessionStartTime;
    
    public VentanaPrincipal() {
        this.controlador = Controlador.getInstancia();
    }
    
    public void start(Stage primaryStage) {
        setupMainUI(primaryStage);
        
        // Register session start time
        sessionStartTime = System.currentTimeMillis();
        
        // Add window close event handler
        primaryStage.setOnCloseRequest(e -> {
            // Calculate session duration
            long sessionEndTime = System.currentTimeMillis();
            long tiempoSesion = sessionEndTime - sessionStartTime;
            
            // Let the controller handle all the model interactions
            try {
                controlador.finalizarSesion(tiempoSesion);
            } catch (Exception ex) {
                mostrarAlerta("Error", "Error al finalizar sesión", Alert.AlertType.ERROR);
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
        
        // Establecer tamaños mínimos para asegurar todo se vea bien
        mainLayout.setMinSize(800, 600);
        
        // Usar StackPane como contenedor raíz para centrar el contenido
        StackPane root = new StackPane(mainLayout);
        root.setAlignment(Pos.CENTER);
        
        // Crear ScrollPane para toda la escena
        ScrollPane mainScrollPane = new ScrollPane(root);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setPannable(true);
        
        Scene scene = new Scene(mainScrollPane, 1000, 700);
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
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        
        // Configurar interfaz según tipo de usuario
        configurarInterfazSegunTipoUsuario();
        
        stage.show();
        
        showLandingPage();
    }
    
    private void applyFallbackStyles(Scene scene) {
        String css = 
            ".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 14px; -fx-background-color: #f8f9fa; }" +
            ".sidebar { -fx-background-color: #4a69bd; -fx-padding: 15; -fx-min-width: 180px; }" +
            ".sidebar-button { -fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-alignment: CENTER_LEFT; -fx-padding: 10 15; -fx-cursor: hand; }" +
            ".sidebar-button:hover { -fx-background-color: rgba(255, 255, 255, 0.2); }" +
            ".sidebar-button-active { -fx-background-color: rgba(255, 255, 255, 0.3); }" +
            ".course-card { -fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 3); -fx-min-width: 200px; -fx-max-width: 280px; }" +
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
        
        // Reemplazar el círculo por un icono
        FontAwesomeIconView logoIcon = new FontAwesomeIconView(FontAwesomeIcon.LANGUAGE);
        logoIcon.setSize("24");
        logoIcon.setFill(Color.WHITE);
        
        Label appName = new Label("LinguaLearn");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        
        logoBox.getChildren().addAll(logoIcon, appName);
        
        // Botones de navegación con iconos
        Button homeButton = createSidebarButton("Inicio", FontAwesomeIcon.HOME, true);
        homeButton.setOnAction(e -> showLandingPage());
        
        Button coursesButton = createSidebarButton("Mis Cursos", FontAwesomeIcon.BOOK, false);
        coursesButton.setOnAction(e -> showCoursesPage());
        
        Button profileButton = createSidebarButton("Perfil", FontAwesomeIcon.USER, false);
        profileButton.setOnAction(e -> showProfilePage());
        
        Button statsButton = createSidebarButton("Estadísticas", FontAwesomeIcon.BAR_CHART, false);
        statsButton.setOnAction(e -> showStatsPage());
        
        Button logoutButton = createSidebarButton("Cerrar Sesión", FontAwesomeIcon.SIGN_OUT, false);
        logoutButton.setOnAction(e -> handleLogout());
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(logoBox, homeButton, coursesButton, profileButton, statsButton, spacer, logoutButton);
    }
    
    private Button createSidebarButton(String text, FontAwesomeIcon icon, boolean active) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.getStyleClass().add("sidebar-button");
        
        // Crear y configurar el icono
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("18");
        iconView.setFill(Color.WHITE);
        
        // Configurar el botón con el icono
        button.setGraphic(iconView);
        button.setGraphicTextGap(10);
        
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
        
        // Configurar restricciones de tamaño para mejor responsividad
        contentArea.setMinWidth(580);
        contentArea.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
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
        
        // Botón para importar curso con icono
        Button importButton = new Button("Importar Curso");
        FontAwesomeIconView importIcon = new FontAwesomeIconView(FontAwesomeIcon.UPLOAD);
        importIcon.setSize("16");
        importIcon.setFill(Color.WHITE);
        importButton.setGraphic(importIcon);
        importButton.setGraphicTextGap(8);
        importButton.getStyleClass().add("action-button");
        importButton.setOnAction(e -> handleImportCurso());
        
        Label userLabel = new Label("¡Hola, " + controlador.getNombreUsuario() + "!");
        userLabel.setStyle("-fx-font-weight: bold;");
        
        // Reemplazar el círculo con un icono de usuario
        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_CIRCLE);
        userIcon.setSize("24");
        userIcon.setFill(Color.valueOf("#4a69bd"));
        
        topBar.getChildren().addAll(importButton, userLabel, userIcon);
        
        mainLayout.setTop(topBar);
    }
    
    // Se muestra la landing page con un banner de bienvenida
    private void showLandingPage() {
        contentArea.getChildren().clear();
        resetSidebarButtonStyles();
        ((Button) sidebar.getChildren().get(1)).getStyleClass().add("sidebar-button-active");

        VBox landingLayout = new VBox(20);
        landingLayout.setAlignment(Pos.TOP_CENTER);

        // Banner de bienvenida
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

        // Sección de creadores (solo para estudiantes)
        if (controlador.puedeRealizarCursos()) {
            // Sección de creadores seguidos
            VBox followedCreatorsSection = new VBox(15);
            followedCreatorsSection.setPadding(new Insets(20));

            Text followedCreatorsTitle = new Text("Creadores Seguidos");
            followedCreatorsTitle.setFont(Font.font("System", FontWeight.BOLD, 24));

            FlowPane followedCreatorsFlow = new FlowPane();
            followedCreatorsFlow.setHgap(20);
            followedCreatorsFlow.setVgap(20);
            followedCreatorsFlow.setPrefWrapLength(600);

            List<Usuario> creadoresSeguidos = controlador.getCreadoresSeguidos();
            if (creadoresSeguidos.isEmpty()) {
                Text noCreators = new Text("No sigues a ningún creador todavía.");
                noCreators.setStyle("-fx-fill: #666;");
                followedCreatorsFlow.getChildren().add(noCreators);
            } else {
                for (Usuario creador : creadoresSeguidos) {
                    // Crear tarjeta personalizada para creadores seguidos
                    VBox card = new VBox(12);
                    card.setPadding(new Insets(15));
                    card.setPrefWidth(220);
                    card.setPrefHeight(220); // Aumentar altura para el botón
                    card.getStyleClass().add("course-card");

                    FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.USER);
                    icon.setSize("32");
                    icon.setFill(Color.valueOf("#4a69bd"));

                    Text name = new Text(creador.getNombre());
                    name.setFont(Font.font("System", FontWeight.BOLD, 16));

                    Text email = new Text(creador.getEmail());
                    email.setWrappingWidth(190);
                    email.setFill(Color.GRAY);

                    Text followers = new Text("Seguidores: " + creador.getSeguidores().size());
                    followers.setFill(Color.GRAY);

                    Text cursosCreados = new Text("Cursos creados: " + controlador.getNumeroCursosCreados(creador));
                    cursosCreados.setFill(Color.GRAY);

                    Region spacer = new Region();
                    VBox.setVgrow(spacer, Priority.ALWAYS);

                    // Indicador de "Siguiendo"
                    HBox followingBox = new HBox(8);
                    FontAwesomeIconView checkIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
                    checkIcon.setSize("16");
                    checkIcon.setFill(Color.valueOf("#06d6a0"));
                    Text followingText = new Text("Siguiendo");
                    followingText.setFont(Font.font("System", FontWeight.BOLD, 14));
                    followingText.setFill(Color.valueOf("#06d6a0"));
                    followingBox.getChildren().addAll(checkIcon, followingText);

                    // Botón para dejar de seguir
                    Button unfollowButton = new Button("Dejar de seguir");
                    FontAwesomeIconView unfollowIcon = new FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE);
                    unfollowIcon.setSize("16");
                    unfollowIcon.setFill(Color.WHITE);
                    unfollowButton.setGraphic(unfollowIcon);
                    unfollowButton.setGraphicTextGap(8);
                    unfollowButton.getStyleClass().add("secondary-button");
                    unfollowButton.setPrefWidth(190);

                    unfollowButton.setOnAction(e -> {
                        boolean exito = controlador.dejarDeSeguirCreador(creador);
                        if (exito) {
                            mostrarAlerta("Éxito", "Has dejado de seguir a " + creador.getNombre() + ".", Alert.AlertType.INFORMATION);
                            showLandingPage(); // Refrescar la página principal
                        } else {
                            mostrarAlerta("Error", "No se pudo dejar de seguir al creador.", Alert.AlertType.ERROR);
                        }
                    });

                    card.getChildren().addAll(icon, name, email, followers, cursosCreados, spacer, followingBox, unfollowButton);

                    Tooltip tooltip = new Tooltip("Creador: " + creador.getNombre());
                    Tooltip.install(card, tooltip);

                    followedCreatorsFlow.getChildren().add(card);
                }
            }

            followedCreatorsSection.getChildren().addAll(followedCreatorsTitle, followedCreatorsFlow);

            // Sección de creadores disponibles (no seguidos)
            VBox availableCreatorsSection = new VBox(15);
            availableCreatorsSection.setPadding(new Insets(20));

            Text availableCreatorsTitle = new Text("Creadores Disponibles");
            availableCreatorsTitle.setFont(Font.font("System", FontWeight.BOLD, 24));

            FlowPane availableCreatorsFlow = new FlowPane();
            availableCreatorsFlow.setHgap(20);
            availableCreatorsFlow.setVgap(20);
            availableCreatorsFlow.setPrefWrapLength(600);

            // Obtener creadores no seguidos directamente desde getCreadoresDisponibles()
            List<Usuario> creadoresNoSeguidos = controlador.getCreadoresDisponibles();

            // Verificación adicional para evitar duplicaciones
            Set<Long> idsCreadoresSeguidos = creadoresSeguidos.stream()
                .map(Usuario::getId)
                .collect(Collectors.toSet());

            creadoresNoSeguidos = creadoresNoSeguidos.stream()
                .filter(creador -> !idsCreadoresSeguidos.contains(creador.getId()))
                .collect(Collectors.toList());

            // Mostrar mensaje si no hay creadores disponibles
            if (creadoresNoSeguidos.isEmpty()) {
                Text noCreators = new Text("No hay creadores disponibles para seguir.");
                noCreators.setStyle("-fx-fill: #666;");
                availableCreatorsFlow.getChildren().add(noCreators);
            } else {
                for (Usuario creador : creadoresNoSeguidos) {
                    Pane creatorCard = createCreatorCard(creador);
                    availableCreatorsFlow.getChildren().add(creatorCard);
                }
            }

            availableCreatorsSection.getChildren().addAll(availableCreatorsTitle, availableCreatorsFlow);

            // Añadir ambas secciones al layout
            landingLayout.getChildren().addAll(welcomeBox, followedCreatorsSection, availableCreatorsSection);
        } else {
            // Para creadores, solo mostrar el banner
            landingLayout.getChildren().add(welcomeBox);
        }

        ScrollPane scrollPane = new ScrollPane(landingLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        contentArea.getChildren().add(scrollPane);
        animateContentEntry(scrollPane);
    }
    
    private Pane createCreatorCard(Usuario creador) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        card.getStyleClass().add("course-card");

        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        icon.setSize("32");
        icon.setFill(Color.valueOf("#4a69bd"));

        Text name = new Text(creador.getNombre());
        name.setFont(Font.font("System", FontWeight.BOLD, 16));

        Text email = new Text(creador.getEmail());
        email.setWrappingWidth(190);
        email.setFill(Color.GRAY);

        Text followers = new Text("Seguidores: " + creador.getSeguidores().size());
        followers.setFill(Color.GRAY);

        Text cursosCreados = new Text("Cursos creados: " + controlador.getNumeroCursosCreados(creador));
        cursosCreados.setFill(Color.GRAY);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Verificar si el usuario actual sigue al creador
        boolean isFollowing = controlador.estaSiguiendoCreador(creador);
        Node actionNode;
        if (isFollowing) {
            // Mostrar indicador de "Siguiendo" en lugar de un botón
            HBox followingBox = new HBox(8);
            FontAwesomeIconView checkIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
            checkIcon.setSize("16");
            checkIcon.setFill(Color.valueOf("#06d6a0"));
            Text followingText = new Text("Siguiendo");
            followingText.setFont(Font.font("System", FontWeight.BOLD, 14));
            followingText.setFill(Color.valueOf("#06d6a0"));
            followingBox.getChildren().addAll(checkIcon, followingText);
            actionNode = followingBox;
        } else {
            // Mostrar botón "Seguir"
            Button followButton = new Button("Seguir");
            FontAwesomeIconView buttonIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
            buttonIcon.setSize("16");
            buttonIcon.setFill(Color.WHITE);
            followButton.setGraphic(buttonIcon);
            followButton.setGraphicTextGap(8);
            followButton.getStyleClass().add("action-button");
            followButton.setPrefWidth(190);

            followButton.setOnAction(e -> {
                boolean exito = controlador.seguirCreador(creador);
                if (exito) {
                    mostrarAlerta("Éxito", "Ahora sigues a " + creador.getNombre() + ". Te has inscrito en sus cursos.", Alert.AlertType.INFORMATION);
                    showLandingPage(); // Refrescar la página principal
                } else {
                    mostrarAlerta("Error", "No se pudo seguir al creador", Alert.AlertType.ERROR);
                }
            });

            actionNode = followButton;
        }

        card.getChildren().addAll(icon, name, email, followers, cursosCreados, spacer, actionNode);

        Tooltip tooltip = new Tooltip("Creador: " + creador.getNombre());
        Tooltip.install(card, tooltip);

        return card;
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
        
     // Dentro del método showCoursesPage(), reemplaza los GridPane por FlowPane
        if (!cursosEnProgreso.isEmpty()) {
            Text inProgressTitle = new Text("Cursos en Progreso");
            inProgressTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
            coursesLayout.getChildren().add(inProgressTitle);
            
            // Usar FlowPane en lugar de GridPane para mejor responsividad
            FlowPane inProgressFlow = new FlowPane();
            inProgressFlow.setHgap(20);
            inProgressFlow.setVgap(20);
            inProgressFlow.setPrefWrapLength(600); // Se ajustará automáticamente
            
            for (int i = 0; i < cursosEnProgreso.size(); i++) {
                Curso curso = cursosEnProgreso.get(i);
                Pane cursoCard = createCourseCard(curso);
                inProgressFlow.getChildren().add(cursoCard);
            }
            
            coursesLayout.getChildren().add(inProgressFlow);
            
            // Añadir separador si hay más cursos que mostrar
            if (!cursosDisponibles.isEmpty()) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(10, 0, 10, 0));
                coursesLayout.getChildren().add(separator);
            }
        }

        // Y lo mismo para los cursos disponibles
        if (!cursosDisponibles.isEmpty()) {
            Text availableTitle = new Text(cursosEnProgreso.isEmpty() ? "" : "Cursos Disponibles");
            availableTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
            if (!cursosEnProgreso.isEmpty()) {
                coursesLayout.getChildren().add(availableTitle);
            }
            
            // Usar FlowPane en lugar de GridPane para mejor responsividad
            FlowPane availableFlow = new FlowPane();
            availableFlow.setHgap(20);
            availableFlow.setVgap(20);
            availableFlow.setPrefWrapLength(600); // Se ajustará automáticamente
            
            for (int i = 0; i < cursosDisponibles.size(); i++) {
                Curso curso = cursosDisponibles.get(i);
                Pane cursoCard = createCourseCard(curso);
                availableFlow.getChildren().add(cursoCard);
            }
            
            coursesLayout.getChildren().add(availableFlow);
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
    private Pane createCourseCard(Curso curso) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setPrefHeight(250);
        card.getStyleClass().add("course-card");
        
        FontAwesomeIconView icon = new FontAwesomeIconView(getIconForCourse(curso.getTitulo()));
        icon.setSize("32");
        icon.setFill(getColorForCourse(curso.getTitulo()));
        
        Text title = new Text(curso.getTitulo());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Text description = new Text(curso.getDominio());
        description.setWrappingWidth(190);
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Use the controller to get the course progress
        CursoEnProgreso progreso = controlador.getProgresoDeCurso(curso);
        
        VBox progressInfo = new VBox(5);
        progressInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Check if the user is a course creator through the controller
        boolean esCreador = controlador.puedeCrearCursos();
        
        if (progreso != null) {
            // Use the controller to calculate the completion percentage
            double porcentajeCompletado = controlador.getPorcentajeCompletado(progreso, curso);
            
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
            
            // Only show buttons if not a creator
            if (!esCreador) {
                HBox buttonsBox = new HBox(10);
                buttonsBox.setAlignment(Pos.CENTER);
                
                if (progreso.getPreguntasCorrectas() > 0 || progreso.getPreguntasIncorrectas() > 0) {
                    Button resumeButton = new Button("Reanudar");
                    FontAwesomeIconView playIcon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
                    playIcon.setSize("14");
                    playIcon.setFill(Color.WHITE);
                    resumeButton.setGraphic(playIcon);
                    resumeButton.setGraphicTextGap(5);
                    resumeButton.getStyleClass().add("action-button");
                    resumeButton.setPrefWidth(100);
                    resumeButton.setOnAction(e -> resumeCourse(curso, progreso));
                    
                    Button restartButton = new Button("Reiniciar");
                    FontAwesomeIconView refreshIcon = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
                    refreshIcon.setSize("14");
                    refreshIcon.setFill(Color.valueOf("#333"));
                    restartButton.setGraphic(refreshIcon);
                    restartButton.setGraphicTextGap(5);
                    restartButton.getStyleClass().add("secondary-button");
                    restartButton.setPrefWidth(100);
                    restartButton.setOnAction(e -> confirmRestartCourse(curso));
                    
                    buttonsBox.getChildren().addAll(resumeButton, restartButton);
                } else {
                    Button startButton = new Button("Iniciar curso");
                    FontAwesomeIconView startIcon = new FontAwesomeIconView(FontAwesomeIcon.PLAY_CIRCLE);
                    startIcon.setSize("16");
                    startIcon.setFill(Color.WHITE);
                    startButton.setGraphic(startIcon);
                    startButton.setGraphicTextGap(8);
                    startButton.getStyleClass().add("action-button");
                    startButton.setPrefWidth(190);
                    startButton.setOnAction(e -> startCourse(curso));
                    
                    buttonsBox.getChildren().add(startButton);
                }
                card.getChildren().addAll(icon, title, description, spacer, progressInfo, buttonsBox);
            } else {
                card.getChildren().addAll(icon, title, description, spacer, progressInfo);
            }
        } else {
            if (esCreador) {
                card.getChildren().addAll(icon, title, description, spacer);
            } else {
                Button enrollButton = new Button("Inscribirse");
                FontAwesomeIconView enrollIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
                enrollIcon.setSize("16");
                enrollIcon.setFill(Color.WHITE);
                enrollButton.setGraphic(enrollIcon);
                enrollButton.setGraphicTextGap(8);
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
    
 // Método para obtener el icono según el tipo de curso
    private FontAwesomeIcon getIconForCourse(String courseTitle) {
        switch (courseTitle) {
            case "Programación Java":
                return FontAwesomeIcon.COFFEE;
            case "Python Básico":
                return FontAwesomeIcon.CODE;
            case "Bases de Datos SQL":
                return FontAwesomeIcon.DATABASE;
            case "HTML y CSS":
                return FontAwesomeIcon.HTML5;
            case "Algoritmos":
                return FontAwesomeIcon.SITEMAP;
            default:
                return FontAwesomeIcon.BOOK;
        }
    }
    
 // Método para inscribir al usuario en un curso
    private void inscribirEnCurso(Curso curso) {
        try {
            // Use the controller to handle enrollment logic
            boolean inscripcionExitosa = controlador.inscribirEnCurso(curso);
            
            if (inscripcionExitosa) {
                // Show success message
                mostrarAlerta("Inscripción Exitosa", 
                             "Te has inscrito al curso \"" + curso.getTitulo() + 
                             "\". Ahora puedes iniciarlo cuando quieras.", 
                             Alert.AlertType.INFORMATION);
                
                // Refresh the courses view
                showCoursesPage();
            } else {
                // Show error message for logical failure (not exception)
                mostrarAlerta("Error", 
                             "No se pudo completar la inscripción al curso.", 
                             Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Error al inscribirse en el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            mostrarAlerta("Error", 
                         "No se pudo completar la inscripción al curso: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
        }
    }

    /*
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
    }*/

 // Method to resume a course
    private void resumeCourse(Curso curso, CursoEnProgreso progreso) {
        try {
            // Use controller to handle resuming a course
            boolean resumed = controlador.reanudarCurso(curso, progreso);
            
            if (resumed) {
                // Create a new instance of VentanaPreguntas
                VentanaPreguntas ventanaPreguntas = new VentanaPreguntas();
                
                // Get the current Stage
                Stage currentStage = (Stage) mainLayout.getScene().getWindow();
                
                // Create a new Stage for the questions window
                Stage questionStage = new Stage();
                
                // Start the questions window
                ventanaPreguntas.start(questionStage);
                
                // Handle the closing of the questions window to return to the courses screen
                questionStage.setOnCloseRequest(e -> {
                    showCoursesPage();
                    currentStage.show();
                });
            } else {
                // Show error if resuming failed
                mostrarAlerta("Error", 
                             "No se pudo reanudar el curso.", 
                             Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Error al reanudar el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Show alert to the user
            mostrarAlerta("Error", 
                         "Error al reanudar el curso: " + e.getMessage(), 
                         Alert.AlertType.ERROR);
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
            // Utilizar el controlador para reiniciar el curso
            boolean reiniciado = controlador.reiniciarCurso(curso);
            
            if (reiniciado) {
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
            } else {
                // Mostrar una alerta al usuario si no se pudo reiniciar
            	mostrarAlerta("Error","No se pudo reiniciar el curso." , Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            System.err.println("Error al reiniciar el curso: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar una alerta al usuario
            mostrarAlerta("Error","No se pudo reiniciar el curso." , Alert.AlertType.ERROR);
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
            mostrarAlerta("Error", "Error al iniciar el curso", Alert.AlertType.ERROR);
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
        dailyReminder.setSelected(controlador.getUsuarioActual().isRecordatorioDiario());
        progressUpdates.setSelected(controlador.getUsuarioActual().isActualizacionesProgreso());
        newCoursesNotif.setSelected(controlador.getUsuarioActual().isNotificacionesNuevosCursos());
        notificationsBox.getChildren().addAll(dailyReminder, progressUpdates, newCoursesNotif);

        // Sección para creadores seguidos (solo para estudiantes)
        VBox followedCreatorsSection = new VBox(15);
        followedCreatorsSection.setPadding(new Insets(20));
        if (controlador.puedeRealizarCursos()) {
            Text followedCreatorsTitle = new Text("Creadores Seguidos");
            followedCreatorsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

            FlowPane followedCreatorsFlow = new FlowPane();
            followedCreatorsFlow.setHgap(20);
            followedCreatorsFlow.setVgap(20);
            followedCreatorsFlow.setPrefWrapLength(600);

            List<Usuario> creadoresSeguidos = controlador.getUsuarioActual().getCreadoresSeguidos();
            if (creadoresSeguidos.isEmpty()) {
                Text noCreators = new Text("No sigues a ningún creador todavía.");
                noCreators.setStyle("-fx-fill: #666;");
                followedCreatorsFlow.getChildren().add(noCreators);
            } else {
                for (Usuario creador : creadoresSeguidos) {
                    // Crear tarjeta personalizada para creadores seguidos
                    VBox card = new VBox(12);
                    card.setPadding(new Insets(15));
                    card.setPrefWidth(220);
                    card.setPrefHeight(220); // Aumentar altura para el botón
                    card.getStyleClass().add("course-card");

                    FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.USER);
                    icon.setSize("32");
                    icon.setFill(Color.valueOf("#4a69bd"));

                    Text name = new Text(creador.getNombre());
                    name.setFont(Font.font("System", FontWeight.BOLD, 16));

                    Text email = new Text(creador.getEmail());
                    email.setWrappingWidth(190);
                    email.setFill(Color.GRAY);

                    Text followers = new Text("Seguidores: " + creador.getSeguidores().size());
                    followers.setFill(Color.GRAY);

                    Text cursosCreados = new Text("Cursos creados: " + controlador.getNumeroCursosCreados(creador));
                    cursosCreados.setFill(Color.GRAY);

                    Region spacer = new Region();
                    VBox.setVgrow(spacer, Priority.ALWAYS);

                    // Indicador de "Siguiendo"
                    HBox followingBox = new HBox(8);
                    FontAwesomeIconView checkIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
                    checkIcon.setSize("16");
                    checkIcon.setFill(Color.valueOf("#06d6a0"));
                    Text followingText = new Text("Siguiendo");
                    followingText.setFont(Font.font("System", FontWeight.BOLD, 14));
                    followingText.setFill(Color.valueOf("#06d6a0"));
                    followingBox.getChildren().addAll(checkIcon, followingText);

                    // Botón para dejar de seguir
                    Button unfollowButton = new Button("Dejar de seguir");
                    FontAwesomeIconView unfollowIcon = new FontAwesomeIconView(FontAwesomeIcon.MINUS_CIRCLE);
                    unfollowIcon.setSize("16");
                    unfollowIcon.setFill(Color.WHITE);
                    unfollowButton.setGraphic(unfollowIcon);
                    unfollowButton.setGraphicTextGap(8);
                    unfollowButton.getStyleClass().add("secondary-button");
                    unfollowButton.setPrefWidth(190);

                    unfollowButton.setOnAction(e -> {
                        boolean exito = controlador.dejarDeSeguirCreador(creador);
                        if (exito) {
                            mostrarAlerta("Éxito", "Has dejado de seguir a " + creador.getNombre() + ".", Alert.AlertType.INFORMATION);
                            showProfilePage(); // Refrescar la página de perfil
                            showLandingPage(); // Refrescar la página principal para mostrar el creador nuevamente
                        } else {
                            mostrarAlerta("Error", "No se pudo dejar de seguir al creador.", Alert.AlertType.ERROR);
                        }
                    });

                    card.getChildren().addAll(icon, name, email, followers, cursosCreados, spacer, followingBox, unfollowButton);

                    Tooltip tooltip = new Tooltip("Creador: " + creador.getNombre());
                    Tooltip.install(card, tooltip);

                    followedCreatorsFlow.getChildren().add(card);
                }
            }

            followedCreatorsSection.getChildren().addAll(followedCreatorsTitle, followedCreatorsFlow);
        }

        profilePane.getChildren().addAll(
            profileHeader,
            separator,
            editProfileTitle,
            formGrid,
            saveButton,
            notificationsTitle,
            notificationsBox,
            followedCreatorsSection
        );
        saveButton.getStyleClass().add("action-button");
        saveButton.setOnAction(e -> {
            String nuevoNombre = nameField.getText();
            String nuevoEmail = emailField.getText();
            String nuevaPassword = passwordField.getText();
            String nuevoIdioma = languageCombo.getValue();
            boolean recordatorio = dailyReminder.isSelected();
            boolean progreso = progressUpdates.isSelected();
            boolean nuevosCursos = newCoursesNotif.isSelected();

            boolean exito = controlador.actualizarPerfil(
                nuevoNombre, nuevoEmail, nuevaPassword, nuevoIdioma,
                recordatorio, progreso, nuevosCursos
            );

            if (exito) {
                mostrarAlerta("Éxito", "Perfil actualizado correctamente.", Alert.AlertType.INFORMATION);
                // Refrescar la página para reflejar los cambios
                showProfilePage();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el perfil. Verifica los datos ingresados.", Alert.AlertType.ERROR);
            }
        });

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
        
        // Obtener las estadísticas del usuario a través del controlador
        Estadistica estadistica = controlador.getEstadisticasUsuario();
        
        // Obtener datos de progreso global de cursos a través del controlador
        List<CursoEnProgreso> cursosEnProgreso = controlador.getCursosEnProgreso();
        
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
                    // Calcular progreso real para este curso usando el controlador
                    double porcentaje = controlador.getPorcentajeCompletado(progreso, curso);
                    
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
        
        // Usando los datos de cursos en progreso para generar actividades
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
        
        // Reemplazar círculo con icono según tipo
        FontAwesomeIconView icon = new FontAwesomeIconView(getIconByType(iconType));
        icon.setSize("32");
        icon.setFill(Color.valueOf("#4a69bd"));
        
        Text valueText = new Text(value);
        valueText.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 14px; -fx-fill: #666;");
        
        box.getChildren().addAll(icon, valueText, titleText);
        return box;
    }
    
 // Método auxiliar para obtener icono según tipo
    private FontAwesomeIcon getIconByType(String iconType) {
        switch (iconType) {
            case "clock":
                return FontAwesomeIcon.CLOCK_ALT;
            case "streak":
                return FontAwesomeIcon.BOLT;
            case "calendar":
                return FontAwesomeIcon.CALENDAR;
            default:
                return FontAwesomeIcon.CIRCLE;
        }
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
