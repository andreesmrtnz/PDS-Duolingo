package vista;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Controlador;
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
    
    // Campos de login
    private TextField loginEmailField;
    private PasswordField loginPassField;
    
    // Servicio de usuario (Controlador) implementado como singleton
    private Controlador usuarioService;
    
    @Override
    public void start(Stage primaryStage) {
        usuarioService = Controlador.getInstancia();
        
        // Crear el contenedor principal
        StackPane root = new StackPane();
        root.setPrefSize(800, 450);
        
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
        
        // Crear escena y mostrar
        Scene scene = new Scene(root);
        
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
        
        primaryStage.setTitle("Login/Register");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void applyFallbackStyles(Scene scene) {
        // Definir estilos CSS directamente como una cadena (respaldo)
        String css = 
            // Estilos generales
            ".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 14px; }" +
            
            // Estilo para el panel coloreado
            ".colored-panel { -fx-background-color: #4a69bd; -fx-text-fill: white; }" +
            ".colored-panel .label { -fx-text-fill: white; }" +
            
            // Estilo para etiquetas de encabezado
            ".header-label { -fx-font-size: 24px; -fx-font-weight: bold; }" +
            
            // Estilos para campos de texto
            ".text-field, .password-field { -fx-pref-height: 40px; -fx-background-radius: 5; }" +
            
            // Estilos para botones
            ".button { -fx-cursor: hand; -fx-pref-height: 40px; -fx-font-weight: bold; }" +
            ".action-button { -fx-background-color: #4a69bd; -fx-text-fill: white; -fx-background-radius: 5; }" +
            ".action-button:hover { -fx-background-color: #3c58a8; }" +
            ".switch-button { -fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 5; " +
                             "-fx-text-fill: white; -fx-border-width: 2; }" +
            ".switch-button:hover { -fx-background-color: rgba(255, 255, 255, 0.2); }";
        
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
        System.out.println("Se aplicaron estilos de respaldo");
    }
    
    private void setupRegisterPane() {
        // Crear panel de registro
        registerPane = new HBox();
        registerPane.setSpacing(0);
        StackPane.setAlignment(registerPane, Pos.CENTER);
        
        // Panel izquierdo (formulario de registro)
        registerLeft = new VBox();
        registerLeft.setSpacing(20);
        registerLeft.setPrefSize(400, 450);
        registerLeft.setAlignment(Pos.CENTER);
        registerLeft.setPadding(new Insets(50, 30, 50, 30));
        
        Label headerRegister = new Label("Create Account");
        headerRegister.getStyleClass().add("header-label");
        
        regNameField = new TextField();
        regNameField.setPromptText("Name");
        
        regEmailField = new TextField();
        regEmailField.setPromptText("Email");
        
        regPassField = new PasswordField();
        regPassField.setPromptText("Password");
        
        Button signUpButton = new Button("SIGN UP");
        signUpButton.getStyleClass().add("action-button");
        signUpButton.setOnAction(e -> handleRegister());
        
        registerLeft.getChildren().addAll(
                headerRegister, 
                regNameField, 
                regEmailField, 
                regPassField, 
                signUpButton
        );
        
        // Panel derecho (bienvenida y cambio a login)
        registerRight = new VBox();
        registerRight.setSpacing(20);
        registerRight.setPrefSize(400, 450);
        registerRight.setAlignment(Pos.CENTER);
        registerRight.setPadding(new Insets(50, 30, 50, 30));
        registerRight.setId("registerRight");
        
        Label welcomeBack = new Label("Welcome Back!");
        welcomeBack.getStyleClass().add("header-label");
        
        Label infoLabel = new Label("To keep connected with us,\nplease login with your personal info");
        
        Button switchToLoginButton = new Button("SIGN IN");
        switchToLoginButton.getStyleClass().add("switch-button");
        switchToLoginButton.setOnAction(e -> switchToLogin());
        
        registerRight.getChildren().addAll(
                welcomeBack, 
                infoLabel, 
                switchToLoginButton
        );
        
        // Añadir paneles al HBox principal
        registerPane.getChildren().addAll(registerLeft, registerRight);
    }
    
    private void setupLoginPane() {
        // Crear panel de login
        loginPane = new HBox();
        loginPane.setSpacing(0);
        StackPane.setAlignment(loginPane, Pos.CENTER);
        
        // Panel izquierdo (bienvenida y cambio a registro)
        loginLeft = new VBox();
        loginLeft.setSpacing(20);
        loginLeft.setPrefSize(400, 450);
        loginLeft.setAlignment(Pos.CENTER);
        loginLeft.setPadding(new Insets(50, 30, 50, 30));
        loginLeft.setId("loginLeft");
        
        Label helloFriend = new Label("Hello, Friend!");
        helloFriend.getStyleClass().add("header-label");
        
        Label infoLogin = new Label("Enter your personal details\nand start your journey with us");
        
        Button switchToRegisterButton = new Button("SIGN UP");
        switchToRegisterButton.getStyleClass().add("switch-button");
        switchToRegisterButton.setOnAction(e -> switchToRegister());
        
        loginLeft.getChildren().addAll(
                helloFriend, 
                infoLogin, 
                switchToRegisterButton
        );
        
        // Panel derecho (formulario de login)
        loginRight = new VBox();
        loginRight.setSpacing(20);
        loginRight.setPrefSize(400, 450);
        loginRight.setAlignment(Pos.CENTER);
        loginRight.setPadding(new Insets(50, 30, 50, 30));
        
        Label signInLabel = new Label("Sign In");
        signInLabel.getStyleClass().add("header-label");
        
        loginEmailField = new TextField();
        loginEmailField.setPromptText("Email");
        
        loginPassField = new PasswordField();
        loginPassField.setPromptText("Password");
        
        Button loginButton = new Button("SIGN IN");
        loginButton.getStyleClass().add("action-button");
        loginButton.setOnAction(e -> handleLogin());
        
        loginRight.getChildren().addAll(
                signInLabel, 
                loginEmailField, 
                loginPassField, 
                loginButton
        );
        
        // Añadir paneles al HBox principal
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

        // Se crea un nuevo usuario
        Usuario nuevoUsuario = new Usuario(nombre, email, pass);
        boolean registrado = usuarioService.registrar(nuevoUsuario);
        if (registrado) {
            showAlert(Alert.AlertType.INFORMATION, "Registro", "Usuario registrado con éxito.");
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
            showAlert(Alert.AlertType.ERROR, "Login", "Por favor, complete todos los campos.");
            return;
        }

        Usuario u = usuarioService.login(email, pass);
        if (u != null) {
            showAlert(Alert.AlertType.INFORMATION, "Login", "Login correcto. Bienvenido " + u.getNombre() + ".");
            // Aquí podrías cambiar de escena o proceder según tu flujo de la aplicación.
        } else {
            showAlert(Alert.AlertType.ERROR, "Login", "Login fallido. Revisa tus credenciales.");
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