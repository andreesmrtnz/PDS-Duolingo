package modelo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PruebaJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear un botón
        Button btn = new Button("¡Haz clic!");
        btn.setOnAction(e -> System.out.println("¡JavaFX funciona!"));
        

        // Usar un StackPane para centrar el botón
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // Crear la escena y asignarla al escenario principal
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Aplicación JavaFX de Prueba");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
