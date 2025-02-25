import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	Parent root = FXMLLoader.load(getClass().getResource("/MiVista.fxml"));
    	Scene scene = new Scene(root, 600, 400);

    	// Añadimos la hoja de estilos
    	scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

    	primaryStage.setScene(scene);
    	primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
