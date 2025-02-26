
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class DualPanelController {

    @FXML
    private HBox registerPane; // Vista de Registro
    @FXML
    private HBox loginPane;    // Vista de Login
    @FXML
    private void switchToLogin() {
        // Suponiendo que el panel verde está a la derecha en la vista actual:
        // Queremos moverlo "hacia la izquierda" (RightToLeft).
        animateRightToLeft(registerPane, loginPane);
    }

    @FXML
    private void switchToRegister() {
        // Ahora el panel verde está a la izquierda en la vista de login,
        // lo movemos "hacia la derecha" (LeftToRight).
        animateLeftToRight(loginPane, registerPane);
    }


    
    private void animateRightToLeft(Node hidePane, Node showPane) {
        double width = hidePane.getBoundsInParent().getWidth();

        // El pane que se oculta (hidePane) se mueve de x=0 a x = -width
        TranslateTransition hideTrans = new TranslateTransition(Duration.millis(500), hidePane);
        hideTrans.setFromX(0);
        hideTrans.setToX(-width);

        // El pane que se muestra (showPane) entra desde la derecha (x=+width) hasta x=0
        showPane.setTranslateX(width);
        showPane.setVisible(true);
        showPane.setManaged(true);

        TranslateTransition showTrans = new TranslateTransition(Duration.millis(500), showPane);
        showTrans.setFromX(width);
        showTrans.setToX(0);

        // Cuando termina de ocultar, marcamos el pane oculto como invisible
        hideTrans.setOnFinished(e -> {
            hidePane.setVisible(false);
            hidePane.setManaged(false);
        });

        // Ejecutamos ambas animaciones en paralelo
        new ParallelTransition(hideTrans, showTrans).play();
    }

    private void animateLeftToRight(Node hidePane, Node showPane) {
        double width = hidePane.getBoundsInParent().getWidth();

        // El pane que se oculta (hidePane) se mueve de x=0 a x=+width
        TranslateTransition hideTrans = new TranslateTransition(Duration.millis(500), hidePane);
        hideTrans.setFromX(0);
        hideTrans.setToX(width);

        // El pane que se muestra (showPane) entra desde la izquierda (x=-width) hasta x=0
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

}
