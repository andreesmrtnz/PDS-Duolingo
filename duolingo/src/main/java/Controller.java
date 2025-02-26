

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller {

    @FXML
    private Button miBoton;

    @FXML
    private void initialize() {
        // Se llama al cargar la vista FXML
        System.out.println("Controlador inicializado");
    }

    @FXML
    private void onBotonClick() {
        System.out.println("¡Botón pulsado!");
    }
}
