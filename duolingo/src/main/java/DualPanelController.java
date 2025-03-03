

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import modelo.Controlador;
import modelo.Usuario;

public class DualPanelController {

    // Paneles de la vista (definidos en el FXML)
    @FXML
    private HBox registerPane; // Vista de Registro
    @FXML
    private HBox loginPane;    // Vista de Login

    // Campos de registro
    @FXML private TextField regNameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPassField;

    // Campos de login
    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPassField;

    // Servicio de usuario (Controlador) implementado como singleton
    private Controlador usuarioService;

    /**
     * Método de inicialización invocado al cargar el FXML.
     */
    @FXML
    private void initialize() {
        usuarioService = Controlador.getInstancia();
    }

    /**
     * Maneja el registro de un usuario a partir de los datos introducidos en la vista.
     */
    @FXML
    private void handleRegister() {
        String nombre = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String pass = regPassField.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registro", "Todos los campos deben completarse.");
            return;
        }

        // Se crea un nuevo usuario (asumiendo que el constructor adecuado es Usuario(String, String, String))
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
    @FXML
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
    @FXML
    private void switchToLogin() {
        animateRightToLeft(registerPane, loginPane);
    }

    /**
     * Cambia de la vista de login a la vista de registro con animación.
     */
    @FXML
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
}
