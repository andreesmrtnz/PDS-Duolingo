package vista;

import controlador.Controlador;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Creador;
import modelo.Estudiante;
import modelo.Usuario;

public class VentanaLoginRegister extends Application {
    
    // Paneles principales
    private HBox registerPane;
    private HBox loginPane;
    
    // Paneles secundarios
    private VBox registerLeft;
    private VBox registerRight;
    private VBox loginLeft;
    private VBox loginRight;
    
    // Campos de registro
    private TextField regNameField;
    private TextField regEmailField;
    private PasswordField regPassField;
    private RadioButton radioEstudiante;
    private RadioButton radioCreador;
    
    // Campos de login
    private TextField loginEmailField;
    private PasswordField loginPassField;
    
    // Servicio de usuario (Controlador) implementado como singleton
    private Controlador usuarioService;
    
    // Stage principal para cambiar escenas
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        usuarioService = Controlador.getInstancia();
        
        // Crear el contenedor principal con StackPane para centrar todo
        StackPane root = new StackPane();
        // Eliminar tamaño prefijado para que sea responsive
        root.getStyleClass().add("root");
        
        // Configurar paneles
        setupRegisterPane();
        setupLoginPane();
        
        // Agregar paneles al contenedor principal
        root.getChildren().addAll(registerPane, loginPane);
        
        // Configurar visibilidad inicial
        registerPane.setVisible(true);
        registerPane.setManaged(true);
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        
        // Crear escena con tamaño mínimo pero que permita crecer
        Scene scene = new Scene(root, 800, 500);
        
        try {
            // Intenta cargar el CSS desde diferentes ubicaciones posibles
            String cssFile = "/styles.css";
            String css = getClass().getResource(cssFile).toExternalForm();
            scene.getStylesheets().add(css);
            System.out.println("CSS cargado con éxito: " + css);
        } catch (Exception e) {
            System.err.println("Error al cargar el CSS: " + e.getMessage());
            // Si falla, aplicamos estilos en línea como respaldo
            applyFallbackStyles(scene);
        }
        
        primaryStage.setTitle("Sistema de Acceso");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(820);
        primaryStage.setMinHeight(520);
        
        // Hacer la ventana responsive al cambiar de tamaño
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustPanelSizes();
        });
        
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            adjustPanelSizes();
        });
        
        primaryStage.show();
    }
    
 // Ajuste mejorado para los tamaños de los paneles según el tamaño de la ventana
    private void adjustPanelSizes() {
        double sceneWidth = primaryStage.getWidth();
        double panelWidth = Math.max(350, Math.min(500, sceneWidth/2 - 20));
        
        // Ajustar los paneles de registro
        registerLeft.setPrefWidth(panelWidth);
        registerRight.setPrefWidth(panelWidth);
        
        // Ajustar los paneles de login
        loginLeft.setPrefWidth(panelWidth);
        loginRight.setPrefWidth(panelWidth);
        
        // Asegurar que los campos de texto tengan un ancho adecuado
        adjustTextFieldSizes(panelWidth);
    }
    
    private void adjustTextFieldSizes(double panelWidth) {
        // Ajuste para los campos de texto en el panel de registro
        double fieldWidth = Math.max(250, panelWidth - 80);
        
        // Ajustar tamaño de los campos de registro
        regNameField.setPrefWidth(fieldWidth);
        regEmailField.setPrefWidth(fieldWidth);
        regPassField.setPrefWidth(fieldWidth);
        
        // Ajustar tamaño de los campos de login
        loginEmailField.setPrefWidth(fieldWidth);
        loginPassField.setPrefWidth(fieldWidth);
    }
    
    private void applyFallbackStyles(Scene scene) {
        // Definir estilos CSS directamente como una cadena (respaldo)
        String css = 
            // Estilos generales
            ".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 14px; }" +
            
            // Paneles principales
            ".register-pane, .login-pane { -fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5); -fx-background-radius: 8; -fx-max-width: 800px; -fx-alignment: center; }" +
            
            // Contenedores específicos
            "#registerLeft, #loginRight { -fx-background-color: white; -fx-padding: 30; -fx-background-radius: 8 0 0 8; -fx-min-width: 200px; -fx-alignment: center; }" +
            "#registerRight, #loginLeft { -fx-background-color: linear-gradient(to bottom right, #4a69bd, #3742fa); -fx-padding: 30; -fx-background-radius: 0 8 8 0; -fx-min-width: 200px; -fx-alignment: center; }" +
            
            // Estilo para el panel coloreado
            ".colored-panel { -fx-background-color: linear-gradient(to bottom right, #4a69bd, #3742fa); -fx-text-fill: white; }" +
            ".colored-panel .label { -fx-text-fill: white; }" +
            
            // Estilo para etiquetas de encabezado
            ".header-label { -fx-font-size: 28px; -fx-font-weight: bold; -fx-padding: 0 0 10 0; -fx-text-alignment: center; }" +
            ".sub-header { -fx-font-size: 16px; -fx-text-alignment: center; }" +
            
            // Estilos para campos de texto
            ".text-field, .password-field { -fx-pref-height: 40px; -fx-background-radius: 5; -fx-prompt-text-fill: #a0a0a0; -fx-padding: 10px; -fx-max-width: 300px; }" +
            ".field-container { -fx-alignment: center-left; -fx-spacing: 10; -fx-padding: 5 0; }" +
            ".field-icon { -fx-fill: #4a69bd; -fx-size: 18px; -fx-padding: 0 0 0 10; }" +
            
            // Estilos para botones
            ".button { -fx-cursor: hand; -fx-pref-height: 40px; -fx-font-weight: bold; }" +
            ".action-button { -fx-background-color: #4a69bd; -fx-text-fill: white; -fx-background-radius: 20; -fx-max-width: 250px; -fx-min-width: 180px; -fx-padding: 10 20; }" +
            ".switch-button { -fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 20; -fx-text-fill: white; -fx-border-width: 2; -fx-max-width: 250px; -fx-min-width: 180px; -fx-padding: 10 20; }";
        
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
        System.out.println("Se aplicaron estilos de respaldo");
    }
    
    private void setupRegisterPane() {
    	// Crear panel de registro
        registerPane = new HBox();
        registerPane.setMaxSize(800, 450);
        registerPane.setSpacing(0);
        registerPane.getStyleClass().add("register-pane");
        StackPane.setAlignment(registerPane, Pos.CENTER);
        
        // Panel izquierdo (formulario de registro)
        registerLeft = new VBox();
        registerLeft.setSpacing(20);
        registerLeft.setPrefWidth(400);
        registerLeft.setPrefHeight(450);
        registerLeft.setId("registerLeft");
        registerLeft.setAlignment(Pos.CENTER);
        registerLeft.setPadding(new Insets(50, 30, 50, 30));
        
        Label headerRegister = new Label("Crear Cuenta");
        headerRegister.getStyleClass().add("header-label");
        
        // Campo Nombre con icono
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.setPrefWidth(300);
        nameBox.setMaxWidth(350);
        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        userIcon.setGlyphSize(16);
        userIcon.getStyleClass().add("field-icon");
        regNameField = new TextField();
        regNameField.setPromptText("Nombre completo");
        regNameField.setPrefWidth(280);
        regNameField.setMinWidth(250);
        HBox.setHgrow(regNameField, Priority.ALWAYS);
        nameBox.getChildren().addAll(userIcon, regNameField);
        
     // Campo Email con icono
        HBox emailBox = new HBox(10);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.setPrefWidth(300);
        emailBox.setMaxWidth(350);
        FontAwesomeIconView emailIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE);
        emailIcon.setGlyphSize(16);
        emailIcon.getStyleClass().add("field-icon");
        regEmailField = new TextField();
        regEmailField.setPromptText("Correo electrónico");
        regEmailField.setPrefWidth(280);
        regEmailField.setMinWidth(250);
        HBox.setHgrow(regEmailField, Priority.ALWAYS);
        emailBox.getChildren().addAll(emailIcon, regEmailField);

        // Campo Contraseña con icono
        HBox passBox = new HBox(10);
        passBox.setAlignment(Pos.CENTER_LEFT);
        passBox.setPrefWidth(300);
        passBox.setMaxWidth(350);
        FontAwesomeIconView passIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        passIcon.setGlyphSize(16);
        passIcon.getStyleClass().add("field-icon");
        regPassField = new PasswordField();
        regPassField.setPromptText("Contraseña");
        regPassField.setPrefWidth(280);
        regPassField.setMinWidth(250);
        HBox.setHgrow(regPassField, Priority.ALWAYS);
        passBox.getChildren().addAll(passIcon, regPassField);
        
        // Agregar selector de tipo de usuario
        Label tipoUsuarioLabel = new Label("Tipo de usuario:");
        
        // Crear grupo de botones de radio
        ToggleGroup grupoTipoUsuario = new ToggleGroup();
        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        
        // Radio button Estudiante con icono
        HBox estudianteBox = new HBox(5);
        estudianteBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView studentIcon = new FontAwesomeIconView(FontAwesomeIcon.GRADUATION_CAP);
        studentIcon.setGlyphSize(14);
        radioEstudiante = new RadioButton("Estudiante");
        estudianteBox.getChildren().addAll(studentIcon, radioEstudiante);
        
        // Radio button Creador con icono
        HBox creadorBox = new HBox(5);
        creadorBox.setAlignment(Pos.CENTER_LEFT);
        FontAwesomeIconView creatorIcon = new FontAwesomeIconView(FontAwesomeIcon.PAINT_BRUSH);
        creatorIcon.setGlyphSize(14);
        radioCreador = new RadioButton("Creador");
        creadorBox.getChildren().addAll(creatorIcon, radioCreador);
        
        radioEstudiante.setToggleGroup(grupoTipoUsuario);
        radioCreador.setToggleGroup(grupoTipoUsuario);
        radioEstudiante.setSelected(true); // Por defecto, seleccionar estudiante
        
        radioBox.getChildren().addAll(estudianteBox, creadorBox);
        
        // Botón de registro con icono
        Button signUpButton = new Button();
        signUpButton.getStyleClass().add("action-button");
        
        FontAwesomeIconView signUpIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_PLUS);
        signUpIcon.setGlyphSize(16);
        HBox buttonContent = new HBox(10);
        buttonContent.setAlignment(Pos.CENTER);
        buttonContent.getChildren().addAll(signUpIcon, new Label("REGISTRARSE"));
        signUpButton.setGraphic(buttonContent);
        
        signUpButton.setOnAction(e -> handleRegister());
        
        registerLeft.getChildren().addAll(
                headerRegister, 
                nameBox, 
                emailBox, 
                passBox,
                tipoUsuarioLabel,
                radioBox,
                signUpButton
        );
        
        // Panel derecho (bienvenida y cambio a login)
        registerRight = new VBox();
        registerRight.setSpacing(20);
        registerRight.setPrefWidth(400);
        registerRight.setPrefHeight(450);
        registerRight.setId("registerRight");
        registerRight.getStyleClass().add("colored-panel");
        registerRight.setAlignment(Pos.CENTER);
        registerRight.setPadding(new Insets(50, 30, 50, 30));
        
        
        Label welcomeBack = new Label("¡Bienvenido de nuevo!");
        welcomeBack.getStyleClass().add("header-label");
        
        TextFlow infoTextFlow = new TextFlow();
        infoTextFlow.setTextAlignment(TextAlignment.CENTER);
        Text infoLabel = new Text("Para mantenerte conectado con nosotros,\ninicia sesión con tus datos personales");
        infoLabel.getStyleClass().add("sub-header");
        infoTextFlow.getChildren().add(infoLabel);
        
        Button switchToLoginButton = new Button();
        switchToLoginButton.getStyleClass().add("switch-button");
        
        FontAwesomeIconView loginIcon = new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN);
        loginIcon.setGlyphSize(16);
        HBox loginButtonContent = new HBox(10);
        loginButtonContent.setAlignment(Pos.CENTER);
        loginButtonContent.getChildren().addAll(loginIcon, new Label("INICIAR SESIÓN"));
        switchToLoginButton.setGraphic(loginButtonContent);
        
        switchToLoginButton.setOnAction(e -> switchToLogin());
        
        registerRight.getChildren().addAll(
                welcomeBack, 
                infoTextFlow, 
                switchToLoginButton
        );
        
        HBox.setHgrow(registerLeft, Priority.ALWAYS);
        HBox.setHgrow(registerRight, Priority.ALWAYS);
        registerPane.getChildren().addAll(registerLeft, registerRight);
        
    }
    
    private void setupLoginPane() {
    	// Crear panel de login
        loginPane = new HBox();
        loginPane.setMaxSize(800, 450);
        loginPane.setSpacing(0);
        loginPane.getStyleClass().add("login-pane");
        StackPane.setAlignment(loginPane, Pos.CENTER);
        
        // Panel izquierdo (bienvenida y cambio a registro)
        loginLeft = new VBox();
        loginLeft.setSpacing(20);
        loginLeft.setPrefWidth(400);
        loginLeft.setPrefHeight(450);
        loginLeft.setId("loginLeft");
        loginLeft.getStyleClass().add("colored-panel");
        loginLeft.setAlignment(Pos.CENTER);
        loginLeft.setPadding(new Insets(50, 30, 50, 30));
        
        Label helloFriend = new Label("¡Hola, amigo!");
        helloFriend.getStyleClass().add("header-label");
        
        TextFlow infoTextFlow = new TextFlow();
        infoTextFlow.setTextAlignment(TextAlignment.CENTER);
        Text infoLogin = new Text("Ingresa tus datos personales\ny comienza tu viaje con nosotros");
        infoLogin.getStyleClass().add("sub-header");
        infoTextFlow.getChildren().add(infoLogin);
        
        Button switchToRegisterButton = new Button();
        switchToRegisterButton.getStyleClass().add("switch-button");
        
        FontAwesomeIconView registerIcon = new FontAwesomeIconView(FontAwesomeIcon.USER_PLUS);
        registerIcon.setGlyphSize(16);
        HBox registerButtonContent = new HBox(10);
        registerButtonContent.setAlignment(Pos.CENTER);
        registerButtonContent.getChildren().addAll(registerIcon, new Label("REGISTRARSE"));
        switchToRegisterButton.setGraphic(registerButtonContent);
        
        switchToRegisterButton.setOnAction(e -> switchToRegister());
        
        loginLeft.getChildren().addAll(
                helloFriend, 
                infoTextFlow, 
                switchToRegisterButton
        );
        
        // Panel derecho (formulario de login)
        loginRight = new VBox();
        loginRight.setSpacing(20);
        loginRight.setPrefWidth(400);
        loginRight.setPrefHeight(450);
        loginRight.setId("loginRight");
        loginRight.setAlignment(Pos.CENTER);
        loginRight.setPadding(new Insets(50, 30, 50, 30));
        
        Label signInLabel = new Label("Iniciar Sesión");
        signInLabel.getStyleClass().add("header-label");
        
     // Campo Email con icono - AQUÍ ESTABA EL ERROR
        HBox loginEmailBox = new HBox(10);
        loginEmailBox.setAlignment(Pos.CENTER_LEFT);
        loginEmailBox.setPrefWidth(300);
        loginEmailBox.setMaxWidth(350);
        FontAwesomeIconView emailIcon = new FontAwesomeIconView(FontAwesomeIcon.ENVELOPE);
        emailIcon.setGlyphSize(16);
        emailIcon.getStyleClass().add("field-icon");
        loginEmailField = new TextField();  // CORRECCIÓN: Inicializar loginEmailField en lugar de reutilizar regEmailField
        loginEmailField.setPromptText("Correo electrónico");
        loginEmailField.setPrefWidth(280);
        loginEmailField.setMinWidth(250);
        HBox.setHgrow(loginEmailField, Priority.ALWAYS);
        loginEmailBox.getChildren().addAll(emailIcon, loginEmailField);

        // Campo Contraseña con icono - AQUÍ ESTABA EL ERROR
        HBox loginPassBox = new HBox(10);
        loginPassBox.setAlignment(Pos.CENTER_LEFT);
        loginPassBox.setPrefWidth(300);
        loginPassBox.setMaxWidth(350);
        FontAwesomeIconView passIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        passIcon.setGlyphSize(16);
        passIcon.getStyleClass().add("field-icon");
        loginPassField = new PasswordField();  // CORRECCIÓN: Inicializar loginPassField en lugar de reutilizar regPassField
        loginPassField.setPromptText("Contraseña");
        loginPassField.setPrefWidth(280);
        loginPassField.setMinWidth(250);
        HBox.setHgrow(loginPassField, Priority.ALWAYS);
        loginPassBox.getChildren().addAll(passIcon, loginPassField);
        
        // Botón de inicio de sesión con icono
        Button loginButton = new Button();
        loginButton.getStyleClass().add("action-button");
        
        FontAwesomeIconView signInIcon = new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN);
        signInIcon.setGlyphSize(16);
        HBox signInContent = new HBox(10);
        signInContent.setAlignment(Pos.CENTER);
        signInContent.getChildren().addAll(signInIcon, new Label("INICIAR SESIÓN"));
        loginButton.setGraphic(signInContent);
        
        loginButton.setOnAction(e -> handleLogin());
        
        loginRight.getChildren().addAll(
                signInLabel, 
                loginEmailBox, 
                loginPassBox, 
                loginButton
        );
        
        // Añadir paneles al HBox principal
        HBox.setHgrow(loginLeft, Priority.ALWAYS);
        HBox.setHgrow(loginRight, Priority.ALWAYS);
        loginPane.getChildren().addAll(loginLeft, loginRight);
    }
    
    /**
     * Maneja el registro de un usuario a partir de los datos introducidos en la vista.
     */
    private void handleRegister() {
        String nombre = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String pass = regPassField.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registro", "Todos los campos deben completarse.");
            return;
        }

        Usuario nuevoUsuario = null;
        boolean registrado = false;
        
        // Determinar el tipo de usuario según la selección
        if (radioEstudiante.isSelected()) {
            // Crear un estudiante
            Estudiante estudiante = usuarioService.crearEstudiante(nombre, email, pass);
            registrado = (estudiante != null);
            nuevoUsuario = estudiante;
        } else if (radioCreador.isSelected()) {
            // Crear un creador
            Creador creador = usuarioService.crearCreador(nombre, email, pass);
            registrado = (creador != null);
            nuevoUsuario = creador;
        }

        if (registrado) {
            String tipoUsuario = radioEstudiante.isSelected() ? "Estudiante" : "Creador";
            showAlert(Alert.AlertType.INFORMATION, "Registro", 
                    "Usuario registrado con éxito como " + tipoUsuario + ".");
            switchToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registro", "Error al registrar. El usuario ya existe o datos inválidos.");
        }
    }

    /**
     * Maneja el inicio de sesión, validando las credenciales introducidas.
     */
    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String pass = loginPassField.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Iniciar Sesión", "Por favor, complete todos los campos.");
            return;
        }

        Usuario u = usuarioService.login(email, pass);
        if (u != null) {
            String tipoUsuario = u.esEstudiante() ? "Estudiante" : "Creador";
            showAlert(Alert.AlertType.INFORMATION, "Iniciar Sesión", 
                    "Login correcto. Bienvenido " + u.getNombre() + " (" + tipoUsuario + ").");
            // Abrir la ventana principal
            abrirVentanaPrincipal(u);
        } else {
            showAlert(Alert.AlertType.ERROR, "Iniciar Sesión", "Login fallido. Revisa tus credenciales.");
        }
    }
    
    /**
     * Abre la ventana principal con los datos del usuario logueado.
     * @param usuario El usuario que ha iniciado sesión.
     */
    private void abrirVentanaPrincipal(Usuario usuario) {
        try {
            // Crear la instancia de VentanaPrincipal con el usuario y el stage
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            
            // Iniciar la ventana principal
            ventanaPrincipal.start(new Stage());
            
            // Cerrar la ventana de login
            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana principal: " + e.getMessage());
        }
    }

    /**
     * Cambia de la vista de registro a la vista de login con animación.
     */
    private void switchToLogin() {
        animateRightToLeft(registerPane, loginPane);
    }

    /**
     * Cambia de la vista de login a la vista de registro con animación.
     */
    private void switchToRegister() {
        animateLeftToRight(loginPane, registerPane);
    }

    /**
     * Realiza una animación de desplazamiento de derecha a izquierda para ocultar un panel y mostrar otro.
     * @param hidePane El panel a ocultar.
     * @param showPane El panel a mostrar.
     */
    private void animateRightToLeft(Node hidePane, Node showPane) {
        double width = hidePane.getBoundsInParent().getWidth();

        TranslateTransition hideTrans = new TranslateTransition(Duration.millis(500), hidePane);
        hideTrans.setFromX(0);
        hideTrans.setToX(-width);

        showPane.setTranslateX(width);
        showPane.setVisible(true);
        showPane.setManaged(true);

        TranslateTransition showTrans = new TranslateTransition(Duration.millis(500), showPane);
        showTrans.setFromX(width);
        showTrans.setToX(0);

        hideTrans.setOnFinished(e -> {
            hidePane.setVisible(false);
            hidePane.setManaged(false);
        });

        new ParallelTransition(hideTrans, showTrans).play();
    }

    /**
     * Realiza una animación de desplazamiento de izquierda a derecha para ocultar un panel y mostrar otro.
     * @param hidePane El panel a ocultar.
     * @param showPane El panel a mostrar.
     */
    private void animateLeftToRight(Node hidePane, Node showPane) {
        double width = hidePane.getBoundsInParent().getWidth();

        TranslateTransition hideTrans = new TranslateTransition(Duration.millis(500), hidePane);
        hideTrans.setFromX(0);
        hideTrans.setToX(width);

        showPane.setTranslateX(-width);
        showPane.setVisible(true);
        showPane.setManaged(true);

        TranslateTransition showTrans = new TranslateTransition(Duration.millis(500), showPane);
        showTrans.setFromX(-width);
        showTrans.setToX(0);

        hideTrans.setOnFinished(e -> {
            hidePane.setVisible(false);
            hidePane.setManaged(false);
        });

        new ParallelTransition(hideTrans, showTrans).play();
    }

    /**
     * Muestra un diálogo de alerta con el título y mensaje especificados.
     * @param type Tipo de alerta (ERROR, INFORMATION, etc.)
     * @param title Título del diálogo.
     * @param message Mensaje a mostrar.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}