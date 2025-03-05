package vista;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelo.Controlador;
import modelo.Pregunta;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentanaPreguntas {
    
    // Referencia al controlador
    private final Controlador controlador;
    
    // Referencia al usuario actual
    private Usuario usuarioActual;
    
    // Componentes de UI
    private BorderPane mainLayout;
    private VBox questionContainer;
    private HBox optionsContainer;
    private Button checkButton;
    private ProgressBar lessonProgress;
    private Label progressLabel;
    
    // Datos de la lección
    private List<Pregunta> preguntas;
    private int preguntaActual = 0;
    private int aciertos = 0;
    private int totalPreguntas = 0;
    
    // Referencia al curso y lección
    private String cursoTitulo;
    private int leccionNumero;
    
    // Stage principal
    private Stage primaryStage;
    
    // Constructor
    public VentanaPreguntas(String cursoTitulo, int leccionNumero) {
        this.controlador = Controlador.getInstancia();
        this.cursoTitulo = cursoTitulo;
        this.leccionNumero = leccionNumero;
        
        // Inicializar preguntas de ejemplo (en una aplicación real, vendrían del controlador)
        inicializarPreguntasEjemplo();
    }
    
    // Método para establecer el usuario
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    // Método para iniciar la ventana
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Configurar el layout principal
        setupMainUI();
        
        // Crear y mostrar la escena
        Scene scene = new Scene(mainLayout, 800, 600);
        
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
        
        primaryStage.setTitle("LinguaLearn - Lección " + leccionNumero);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Mostrar la primera pregunta
        mostrarPregunta(0);
    }
    
    private void applyFallbackStyles(Scene scene) {
        // Estilos CSS de respaldo
        String css = 
            // Estilos generales
            ".root { -fx-font-family: 'Segoe UI', Arial, sans-serif; -fx-font-size: 14px; -fx-background-color: #f8f9fa; }" +
            
            // Estilos para la barra superior
            ".top-bar { -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; }" +
            
            // Estilos para contenedor de preguntas
            ".question-container { -fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; }" +
            
            // Estilos para opciones
            ".option { -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; " +
                      "-fx-background-radius: 8; -fx-padding: 15; -fx-cursor: hand; }" +
            ".option:hover { -fx-background-color: #f0f0f0; }" +
            ".option-selected { -fx-border-color: #4a69bd; -fx-border-width: 2; }" +
            ".option-correct { -fx-background-color: #d4edda; -fx-border-color: #28a745; }" +
            ".option-incorrect { -fx-background-color: #f8d7da; -fx-border-color: #dc3545; }" +
            
            // Estilos para botones
            ".check-button { -fx-background-color: #4a69bd; -fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-background-radius: 8; -fx-padding: 12 20; -fx-cursor: hand; }" +
            ".check-button:hover { -fx-background-color: #3c58a8; }" +
            ".check-button:disabled { -fx-background-color: #cccccc; }" +
            
            // Estilos para feedback
            ".feedback-correct { -fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-background-radius: 8; -fx-padding: 10; }" +
            ".feedback-incorrect { -fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-background-radius: 8; -fx-padding: 10; }";
        
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
        System.out.println("Se aplicaron estilos de respaldo para la ventana de preguntas");
    }
    
    private void setupMainUI() {
        // Crear el layout principal
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(0));
        
        // Configurar la barra superior
        setupTopBar();
        
        // Configurar el área de contenido
        setupContentArea();
        
        // Configurar el área inferior con el botón de verificación
        setupBottomBar();
    }
    
    private void setupTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("top-bar");
        
        // Botón para cerrar la lección
        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
        closeButton.setOnAction(e -> confirmarSalir());
        
        // Barra de progreso de la lección
        lessonProgress = new ProgressBar(0);
        lessonProgress.setPrefWidth(500);
        HBox.setHgrow(lessonProgress, Priority.ALWAYS);
        
        // Etiqueta de progreso
        progressLabel = new Label("0/" + totalPreguntas);
        
        topBar.getChildren().addAll(closeButton, lessonProgress, progressLabel);
        
        mainLayout.setTop(topBar);
    }
    
    private void setupContentArea() {
        // Contenedor principal para el área de contenido
        VBox contentArea = new VBox(30);
        contentArea.setPadding(new Insets(30, 40, 30, 40));
        contentArea.setAlignment(Pos.CENTER);
        
        // Contenedor para la pregunta
        questionContainer = new VBox(20);
        questionContainer.getStyleClass().add("question-container");
        questionContainer.setMaxWidth(700);
        questionContainer.setAlignment(Pos.CENTER);
        
        // Contenedor para las opciones
        optionsContainer = new HBox(15);
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setSpacing(15);
        
        contentArea.getChildren().addAll(questionContainer, optionsContainer);
        
        mainLayout.setCenter(contentArea);
    }
    
    private void setupBottomBar() {
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20, 40, 30, 40));
        bottomBar.setAlignment(Pos.CENTER);
        
        // Botón para verificar respuesta
        checkButton = new Button("VERIFICAR");
        checkButton.getStyleClass().add("check-button");
        checkButton.setPrefWidth(300);
        checkButton.setPrefHeight(50);
        checkButton.setDisable(true);
        checkButton.setOnAction(e -> verificarRespuesta());
        
        bottomBar.getChildren().add(checkButton);
        
        mainLayout.setBottom(bottomBar);
    }
    
    private void inicializarPreguntasEjemplo() {
        preguntas = new ArrayList<>();
        
        // Preguntas tipo selección múltiple
        preguntas.add(new Pregunta(
            "¿Cómo se dice 'hola' en inglés?",
            new String[]{"Hello", "Goodbye", "Thank you", "Please"},
            0,
            Pregunta.TipoPregunta.SELECCION_MULTIPLE
        ));
        
        preguntas.add(new Pregunta(
            "Selecciona la traducción correcta: 'The cat is black'",
            new String[]{"El perro es negro", "El gato es blanco", "El gato es negro", "La casa es negra"},
            2,
            Pregunta.TipoPregunta.SELECCION_MULTIPLE
        ));
        
        // Preguntas tipo emparejamiento
        preguntas.add(new Pregunta(
            "Empareja las palabras con su traducción",
            new String[]{"Dog - Perro", "Cat - Gato", "House - Casa", "Car - Coche"},
            -1, // No aplica para este tipo
            Pregunta.TipoPregunta.EMPAREJAMIENTO
        ));
        
        // Preguntas tipo ordenar palabras
        preguntas.add(new Pregunta(
            "Ordena las palabras para formar una frase correcta",
            new String[]{"I", "to", "school", "go", "every", "day"},
            -1, // No aplica para este tipo
            Pregunta.TipoPregunta.ORDENAR_PALABRAS
        ));
        
        // Preguntas tipo completar
        preguntas.add(new Pregunta(
            "Completa la frase: 'She ___ to the store yesterday.'",
            new String[]{"go", "goes", "went", "going"},
            2,
            Pregunta.TipoPregunta.COMPLETAR
        ));
        
        // Establecer el total de preguntas
        totalPreguntas = preguntas.size();
    }
    
    private void mostrarPregunta(int indice) {
        // Limpiar contenedores
        questionContainer.getChildren().clear();
        optionsContainer.getChildren().clear();
        
        // Actualizar progreso
        actualizarProgreso(indice);
        
        // Obtener la pregunta actual
        Pregunta pregunta = preguntas.get(indice);
        
        // Título de la pregunta
        Text questionTitle = new Text(pregunta.getEnunciado());
        questionTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        questionTitle.setWrappingWidth(650);
        
        questionContainer.getChildren().add(questionTitle);
        
        // Mostrar opciones según el tipo de pregunta
        switch (pregunta.getTipo()) {
            case SELECCION_MULTIPLE:
                mostrarOpcionesSeleccionMultiple(pregunta);
                break;
            case EMPAREJAMIENTO:
                mostrarOpcionesEmparejamiento(pregunta);
                break;
            case ORDENAR_PALABRAS:
                mostrarOpcionesOrdenarPalabras(pregunta);
                break;
            case COMPLETAR:
                mostrarOpcionesCompletar(pregunta);
                break;
        }
        
        // Deshabilitar el botón de verificar inicialmente
        checkButton.setDisable(true);
        
        // Animar la entrada de la pregunta
        animarEntradaPregunta();
    }
    
    private void mostrarOpcionesSeleccionMultiple(Pregunta pregunta) {
        // Crear un VBox para las opciones verticales
        VBox opcionesVBox = new VBox(15);
        opcionesVBox.setAlignment(Pos.CENTER);
        opcionesVBox.setPrefWidth(650);
        
        // Variable para almacenar la opción seleccionada
        final int[] seleccionada = {-1};
        
        // Crear cada opción
        for (int i = 0; i < pregunta.getOpciones().length; i++) {
            final int index = i;
            HBox opcion = new HBox(15);
            opcion.getStyleClass().add("option");
            opcion.setPadding(new Insets(15));
            opcion.setAlignment(Pos.CENTER_LEFT);
            opcion.setPrefWidth(650);
            
            // Círculo para selección
            Circle selectionCircle = new Circle(12);
            selectionCircle.setFill(Color.WHITE);
            selectionCircle.setStroke(Color.LIGHTGRAY);
            selectionCircle.setStrokeWidth(2);
            
            // Texto de la opción
            Text opcionTexto = new Text(pregunta.getOpciones()[i]);
            opcionTexto.setFont(Font.font("System", 16));
            
            opcion.getChildren().addAll(selectionCircle, opcionTexto);
            
            // Manejar clic en la opción
            opcion.setOnMouseClicked(e -> {
                // Desmarcar todas las opciones
                for (int j = 0; j < opcionesVBox.getChildren().size(); j++) {
                    HBox otherOption = (HBox) opcionesVBox.getChildren().get(j);
                    otherOption.getStyleClass().remove("option-selected");
                    
                    Circle otherCircle = (Circle) otherOption.getChildren().get(0);
                    otherCircle.setFill(Color.WHITE);
                    otherCircle.setStroke(Color.LIGHTGRAY);
                }
                
                // Marcar la opción seleccionada
                opcion.getStyleClass().add("option-selected");
                selectionCircle.setFill(Color.valueOf("#4a69bd"));
                selectionCircle.setStroke(Color.valueOf("#4a69bd"));
                
                // Actualizar la selección
                seleccionada[0] = index;
                
                // Habilitar el botón de verificar
                checkButton.setDisable(false);
            });
            
            opcionesVBox.getChildren().add(opcion);
        }
        
        // Guardar la selección para verificar después
        pregunta.setSeleccionUsuario(seleccionada);
        
        // Agregar las opciones al contenedor
        questionContainer.getChildren().add(opcionesVBox);
    }
    
    private void mostrarOpcionesEmparejamiento(Pregunta pregunta) {
        // Crear un GridPane para las opciones de emparejamiento
        GridPane emparejamientoGrid = new GridPane();
        emparejamientoGrid.setHgap(20);
        emparejamientoGrid.setVgap(15);
        emparejamientoGrid.setAlignment(Pos.CENTER);
        
        // Crear listas para las columnas izquierda y derecha
        List<String> columnLeft = new ArrayList<>();
        List<String> columnRight = new ArrayList<>();
        
        // Separar las opciones en dos columnas
        for (String opcion : pregunta.getOpciones()) {
            String[] parts = opcion.split(" - ");
            columnLeft.add(parts[0]);
            columnRight.add(parts[1]);
        }
        
        // Mezclar la columna derecha para el desafío
        List<String> shuffledRight = new ArrayList<>(columnRight);
        Collections.shuffle(shuffledRight);
        
        // Crear un array para almacenar las selecciones del usuario
        final int[] seleccionesUsuario = new int[columnLeft.size()];
        for (int i = 0; i < seleccionesUsuario.length; i++) {
            seleccionesUsuario[i] = -1;
        }
        
        // Crear las líneas de conexión
        final Line[] lineasConexion = new Line[columnLeft.size()];
        
        // Crear un Pane para las líneas
        Pane lineasPane = new Pane();
        lineasPane.setPrefSize(650, 300);
        
        // Agregar elementos de la columna izquierda
        for (int i = 0; i < columnLeft.size(); i++) {
            final int leftIndex = i;
            
            // Crear botón para elemento izquierdo
            Button leftButton = new Button(columnLeft.get(i));
            leftButton.getStyleClass().add("option");
            leftButton.setPrefWidth(200);
            
            // Agregar a la grid
            emparejamientoGrid.add(leftButton, 0, i);
            
            // Crear línea de conexión (inicialmente invisible)
            Line line = new Line();
            line.setStroke(Color.valueOf("#4a69bd"));
            line.setStrokeWidth(3);
            line.setVisible(false);
            lineasConexion[i] = line;
            lineasPane.getChildren().add(line);
            
            // Manejar clic en elemento izquierdo
            leftButton.setOnAction(e -> {
                // Marcar como seleccionado
                for (Node node : emparejamientoGrid.getChildren()) {
                    if (node instanceof Button && GridPane.getColumnIndex(node) == 0) {
                        node.getStyleClass().remove("option-selected");
                    }
                }
                leftButton.getStyleClass().add("option-selected");
                
                // Esperar selección del lado derecho
                for (Node node : emparejamientoGrid.getChildren()) {
                    if (node instanceof Button && GridPane.getColumnIndex(node) == 1) {
                        Button rightBtn = (Button) node;
                        rightBtn.setOnAction(ev -> {
                            // Obtener índice del botón derecho
                            int rightIndex = GridPane.getRowIndex(rightBtn);
                            
                            // Actualizar selección
                            seleccionesUsuario[leftIndex] = rightIndex;
                            
                            // Actualizar línea de conexión
                            double startX = leftButton.getLayoutX() + leftButton.getWidth();
                            double startY = leftButton.getLayoutY() + leftButton.getHeight() / 2;
                            double endX = rightBtn.getLayoutX();
                            double endY = rightBtn.getLayoutY() + rightBtn.getHeight() / 2;
                            
                            line.setStartX(startX);
                            line.setStartY(startY);
                            line.setEndX(endX);
                            line.setEndY(endY);
                            line.setVisible(true);
                            
                            // Verificar si todas las opciones están conectadas
                            boolean todasConectadas = true;
                            for (int sel : seleccionesUsuario) {
                                if (sel == -1) {
                                    todasConectadas = false;
                                    break;
                                }
                            }
                            
                            // Habilitar botón de verificar si todas están conectadas
                            checkButton.setDisable(!todasConectadas);
                        });
                    }
                }
            });
        }
        
        // Agregar elementos de la columna derecha
        for (int i = 0; i < shuffledRight.size(); i++) {
            Button rightButton = new Button(shuffledRight.get(i));
            rightButton.getStyleClass().add("option");
            rightButton.setPrefWidth(200);
            
            emparejamientoGrid.add(rightButton, 1, i);
        }
        
        // Guardar las selecciones para verificar después
        pregunta.setSeleccionesEmparejamiento(seleccionesUsuario);
        
        // Agregar el grid al contenedor
        questionContainer.getChildren().addAll(lineasPane, emparejamientoGrid);
    }
    
    private void mostrarOpcionesOrdenarPalabras(Pregunta pregunta) {
        // Crear contenedor para palabras desordenadas y ordenadas
        VBox ordenContainer = new VBox(20);
        ordenContainer.setAlignment(Pos.CENTER);
        
        // Contenedor para palabras desordenadas (disponibles)
        FlowPane palabrasDisponibles = new FlowPane();
        palabrasDisponibles.setHgap(10);
        palabrasDisponibles.setVgap(10);
        palabrasDisponibles.setAlignment(Pos.CENTER);
        palabrasDisponibles.setPrefWrapLength(600);
        
        // Contenedor para palabras ordenadas (seleccionadas)
        HBox palabrasOrdenadas = new HBox(10);
        palabrasOrdenadas.setAlignment(Pos.CENTER);
        palabrasOrdenadas.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 8;");
        palabrasOrdenadas.setMinHeight(60);
        
        // Lista para almacenar el orden seleccionado
        List<String> ordenSeleccionado = new ArrayList<>();
        
        // Crear botones para cada palabra y agregarlos al contenedor de disponibles
        List<String> palabras = new ArrayList<>(List.of(pregunta.getOpciones()));
        Collections.shuffle(palabras); // Mezclar palabras
        
        for (String palabra : palabras) {
            Button palabraBtn = new Button(palabra);
            palabraBtn.getStyleClass().add("option");
            
            // Manejar clic en palabra disponible
            palabraBtn.setOnAction(e -> {
                // Mover la palabra al contenedor de ordenadas
                palabrasDisponibles.getChildren().remove(palabraBtn);
                palabrasOrdenadas.getChildren().add(palabraBtn);
                
                // Actualizar el orden seleccionado
                ordenSeleccionado.add(palabra);
                
                // Cambiar el evento onClick para permitir devolver la palabra
                palabraBtn.setOnAction(ev -> {
                    // Devolver la palabra al contenedor de disponibles
                    palabrasOrdenadas.getChildren().remove(palabraBtn);
                    palabrasDisponibles.getChildren().add(palabraBtn);
                    
                    // Actualizar el orden seleccionado
                    ordenSeleccionado.remove(palabra);
                    
                    // Restaurar el evento onClick original
                    palabraBtn.setOnAction(evt -> {
                        palabrasDisponibles.getChildren().remove(palabraBtn);
                        palabrasOrdenadas.getChildren().add(palabraBtn);
                        ordenSeleccionado.add(palabra);
                        checkButton.setDisable(ordenSeleccionado.size() != pregunta.getOpciones().length);
                    });
                    
                    // Actualizar estado del botón de verificar
                    checkButton.setDisable(true);
                });
                
                // Habilitar el botón de verificar si todas las palabras están ordenadas
                checkButton.setDisable(ordenSeleccionado.size() != pregunta.getOpciones().length);
            });
            
            palabrasDisponibles.getChildren().add(palabraBtn);
        }
        
        // Guardar el orden seleccionado para verificar después
        pregunta.setOrdenSeleccionado(ordenSeleccionado);
        
        // Agregar los contenedores al layout principal
        ordenContainer.getChildren().addAll(
            new Label("Ordena las palabras para formar una frase correcta:"),
            palabrasOrdenadas,
            new Label("Palabras disponibles:"),
            palabrasDisponibles
        );
        
        questionContainer.getChildren().add(ordenContainer);
    }
    
    private void mostrarOpcionesCompletar(Pregunta pregunta) {
        // Extraer la parte de la frase antes y después del espacio a completar
        String enunciado = pregunta.getEnunciado();
        String[] partes = enunciado.split("___");
        
        // Crear contenedor para la frase y las opciones
        VBox completarContainer = new VBox(20);
        completarContainer.setAlignment(Pos.CENTER);
        
        // Crear la frase con el espacio a completar
        HBox fraseContainer = new HBox();
        fraseContainer.setAlignment(Pos.CENTER);
        
        // Parte inicial de la frase
        Text parteInicial = new Text(partes[0]);
        parteInicial.setFont(Font.font("System", 18));
        
        // Espacio a completar (inicialmente vacío)
        Label espacioCompletar = new Label("_______");
        espacioCompletar.setStyle("-fx-border-color: #4a69bd; -fx-border-width: 0 0 2 0; -fx-padding: 0 5;");
        espacioCompletar.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // Parte final de la frase
        Text parteFinal = new Text(partes[1]);
        parteFinal.setFont(Font.font("System", 18));
        
        fraseContainer.getChildren().addAll(parteInicial, espacioCompletar, parteFinal);
        
        // Contenedor para las opciones
        HBox opcionesCompletar = new HBox(15);
        opcionesCompletar.setAlignment(Pos.CENTER);
        
        // Variable para almacenar la opción seleccionada
        final int[] seleccionada = {-1};
        
        // Crear botones para cada opción
        for (int i = 0; i < pregunta.getOpciones().length; i++) {
            final int index = i;
            Button opcionBtn = new Button(pregunta.getOpciones()[i]);
            opcionBtn.getStyleClass().add("option");
            
            // Manejar clic en opción
            opcionBtn.setOnAction(e -> {
                // Actualizar todas las opciones
                for (Node node : opcionesCompletar.getChildren()) {
                    if (node instanceof Button) {
                        node.getStyleClass().remove("option-selected");
                    }
                }
                
                // Marcar la opción seleccionada
                opcionBtn.getStyleClass().add("option-selected");
                
                // Actualizar el espacio a completar
                espacioCompletar.setText(pregunta.getOpciones()[index]);
                
                // Actualizar la selección
                seleccionada[0] = index;
                
                // Habilitar el botón de verificar
                checkButton.setDisable(false);
            });
            
            opcionesCompletar.getChildren().add(opcionBtn);
        }
        
        // Guardar la selección para verificar después
        pregunta.setSeleccionUsuario(seleccionada);
        
        // Agregar los contenedores al layout principal
        completarContainer.getChildren().addAll(fraseContainer, opcionesCompletar);
        
        questionContainer.getChildren().add(completarContainer);
    }
    
    private void verificarRespuesta() {
        Pregunta pregunta = preguntas.get(preguntaActual);
        boolean esCorrecta = false;
        
        // Verificar según el tipo de pregunta
        switch (pregunta.getTipo()) {
            case SELECCION_MULTIPLE:
            case COMPLETAR:
                // Verificar si la selección coincide con la respuesta correcta
                esCorrecta = pregunta.getSeleccionUsuario()[0] == pregunta.getRespuestaCorrecta();
                mostrarFeedbackSeleccion(esCorrecta, pregunta);
                break;
                
            case EMPAREJAMIENTO:
                // Verificar si todos los emparejamientos son correctos
                esCorrecta = verificarEmparejamiento(pregunta);
                mostrarFeedbackEmparejamiento(esCorrecta, pregunta);
                break;
                
            case ORDENAR_PALABRAS:
                // Verificar si el orden es correcto
                esCorrecta = verificarOrdenPalabras(pregunta);
                mostrarFeedbackOrdenPalabras(esCorrecta, pregunta);
                break;
        }
        
        // Actualizar contador de aciertos
        if (esCorrecta) {
            aciertos++;
        }
        
        // Deshabilitar el botón de verificar
        checkButton.setDisable(true);
        
        // Cambiar el texto del botón a "Continuar"
        checkButton.setText("CONTINUAR");
        checkButton.setOnAction(e -> siguientePregunta());
    }
    
    private void mostrarFeedbackSeleccion(boolean esCorrecta, Pregunta pregunta) {
        // Obtener el contenedor de opciones
        VBox opcionesVBox = (VBox) questionContainer.getChildren().get(1);
        
        // Índice de la opción seleccionada por el usuario
        int seleccionUsuario = pregunta.getSeleccionUsuario()[0];
        
        // Índice de la respuesta correcta
        int respuestaCorrecta = pregunta.getRespuestaCorrecta();
        
        // Marcar la opción seleccionada como correcta o incorrecta
        for (int i = 0; i < opcionesVBox.getChildren().size(); i++) {
            HBox opcion = (HBox) opcionesVBox.getChildren().get(i);
            
            if (i == seleccionUsuario) {
                // Marcar la opción seleccionada
                if (esCorrecta) {
                    opcion.getStyleClass().add("option-correct");
                } else {
                    opcion.getStyleClass().add("option-incorrect");
                }
            } else if (i == respuestaCorrecta && !esCorrecta) {
                // Marcar la respuesta correcta si el usuario se equivocó
                opcion.getStyleClass().add("option-correct");
            }
        }
        
        // Mostrar mensaje de feedback
        Label feedbackLabel = new Label(esCorrecta ? "¡Correcto!" : "Incorrecto. La respuesta correcta es: " + 
                                       pregunta.getOpciones()[respuestaCorrecta]);
        feedbackLabel.getStyleClass().add(esCorrecta ? "feedback-correct" : "feedback-incorrect");
        feedbackLabel.setPadding(new Insets(10));
        
        // Agregar el feedback al contenedor
        questionContainer.getChildren().add(feedbackLabel);
        
        // Animar el feedback
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), feedbackLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private boolean verificarEmparejamiento(Pregunta pregunta) {
        int[] selecciones = pregunta.getSeleccionesEmparejamiento();
        
        // Verificar que cada elemento de la izquierda esté emparejado con el correcto de la derecha
        for (int i = 0; i < selecciones.length; i++) {
            if (selecciones[i] != i) {
                return false;
            }
        }
        
        return true;
    }
    
    private void mostrarFeedbackEmparejamiento(boolean esCorrecta, Pregunta pregunta) {
        // Mostrar mensaje de feedback
        Label feedbackLabel = new Label(esCorrecta ? "¡Correcto! Todos los emparejamientos son correctos." : 
                                      "Incorrecto. Algunos emparejamientos no son correctos.");
        feedbackLabel.getStyleClass().add(esCorrecta ? "feedback-correct" : "feedback-incorrect");
        feedbackLabel.setPadding(new Insets(10));
        
        // Agregar el feedback al contenedor
        questionContainer.getChildren().add(feedbackLabel);
        
        // Animar el feedback
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), feedbackLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private boolean verificarOrdenPalabras(Pregunta pregunta) {
        List<String> ordenSeleccionado = pregunta.getOrdenSeleccionado();
        String[] ordenCorrecto = pregunta.getOpciones();
        
        // Verificar que el orden seleccionado coincida con el orden correcto
        for (int i = 0; i < ordenCorrecto.length; i++) {
            if (!ordenSeleccionado.get(i).equals(ordenCorrecto[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    private void mostrarFeedbackOrdenPalabras(boolean esCorrecta, Pregunta pregunta) {
        // Construir la frase correcta
        StringBuilder fraseCorrecta = new StringBuilder();
        for (String palabra : pregunta.getOpciones()) {
            fraseCorrecta.append(palabra).append(" ");
        }
        
        // Mostrar mensaje de feedback
        Label feedbackLabel = new Label(esCorrecta ? "¡Correcto! El orden es correcto." : 
                                      "Incorrecto. El orden correcto es: " + fraseCorrecta.toString().trim());
        feedbackLabel.getStyleClass().add(esCorrecta ? "feedback-correct" : "feedback-incorrect");
        feedbackLabel.setPadding(new Insets(10));
        
        // Agregar el feedback al contenedor
        questionContainer.getChildren().add(feedbackLabel);
        
        // Animar el feedback
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), feedbackLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private void siguientePregunta() {
        // Incrementar el contador de pregunta actual
        preguntaActual++;
        
        // Verificar si hemos terminado todas las preguntas
        if (preguntaActual >= totalPreguntas) {
            mostrarResultadoFinal();
        } else {
            // Restaurar el botón de verificar
            checkButton.setText("VERIFICAR");
            checkButton.setOnAction(e -> verificarRespuesta());
            
            // Mostrar la siguiente pregunta
            mostrarPregunta(preguntaActual);
        }
    }
    
    private void mostrarResultadoFinal() {
        // Limpiar contenedores
        questionContainer.getChildren().clear();
        optionsContainer.getChildren().clear();
        
        // Crear contenedor para el resultado final
        VBox resultadoContainer = new VBox(20);
        resultadoContainer.setAlignment(Pos.CENTER);
        resultadoContainer.setPadding(new Insets(30));
        resultadoContainer.getStyleClass().add("question-container");
        
        // Título del resultado
        Text resultadoTitle = new Text("¡Lección Completada!");
        resultadoTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        
        // Círculo con puntuación
        StackPane scoreCircle = new StackPane();
        Circle circle = new Circle(60);
        circle.setFill(Color.valueOf("#4a69bd"));
        
        Text scoreText = new Text(aciertos + "/" + totalPreguntas);
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        scoreCircle.getChildren().addAll(circle, scoreText);
        
        // Mensaje según la puntuación
        double porcentaje = (double) aciertos / totalPreguntas;
        String mensaje;
        
        if (porcentaje >= 0.9) {
            mensaje = "¡Excelente! Has dominado esta lección.";
        } else if (porcentaje >= 0.7) {
            mensaje = "¡Muy bien! Has aprendido la mayoría de los conceptos.";
        } else if (porcentaje >= 0.5) {
            mensaje = "Buen trabajo. Sigue practicando para mejorar.";
        } else {
            mensaje = "Necesitas más práctica. ¡No te rindas!";
        }
        
        Text mensajeText = new Text(mensaje);
        mensajeText.setFont(Font.font("System", 18));
        
        // Botones de acción
        HBox botonesContainer = new HBox(20);
        botonesContainer.setAlignment(Pos.CENTER);
        
        Button repetirButton = new Button("Repetir Lección");
        repetirButton.getStyleClass().add("action-button");
        repetirButton.setOnAction(e -> reiniciarLeccion());
        
        Button continuarButton = new Button("Volver a Cursos");
        continuarButton.getStyleClass().add("action-button");
        continuarButton.setOnAction(e -> volverACursos());
        
        botonesContainer.getChildren().addAll(repetirButton, continuarButton);
        
        // Agregar todo al contenedor
        resultadoContainer.getChildren().addAll(
            resultadoTitle,
            scoreCircle,
            mensajeText,
            botonesContainer
        );
        
        // Mostrar el resultado
        questionContainer.getChildren().add(resultadoContainer);
        
        // Ocultar el botón de verificar
        checkButton.setVisible(false);
        
        // Animar la entrada del resultado
        animarEntradaPregunta();
    }
    
    private void reiniciarLeccion() {
        // Reiniciar contadores
        preguntaActual = 0;
        aciertos = 0;
        
        // Restaurar el botón de verificar
        checkButton.setText("VERIFICAR");
        checkButton.setVisible(true);
        checkButton.setOnAction(e -> verificarRespuesta());
        
        // Mostrar la primera pregunta
        mostrarPregunta(0);
    }
    
    private void volverACursos() {
        try {
            // Crear la instancia de VentanaPrincipal
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            
            
            
            // Iniciar la ventana principal
            ventanaPrincipal.start(new Stage());
            
            // Cerrar la ventana actual
            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo volver a la ventana principal: " + e.getMessage());
        }
    }
    
    private void confirmarSalir() {
        // Mostrar diálogo de confirmación
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Salir de la lección");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que quieres salir? Perderás tu progreso en esta lección.");
        
        if (alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK) {
            volverACursos();
        }
    }
    
    private void actualizarProgreso(int indice) {
        // Actualizar la barra de progreso
        double progreso = (double) (indice) / totalPreguntas;
        lessonProgress.setProgress(progreso);
        
        // Actualizar la etiqueta de progreso
        progressLabel.setText((indice + 1) + "/" + totalPreguntas);
    }
    
    private void animarEntradaPregunta() {
        // Animar la entrada de la pregunta con una transición
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), questionContainer);
        translateTransition.setFromY(50);
        translateTransition.setToY(0);
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), questionContainer);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        
        ParallelTransition parallelTransition = new ParallelTransition(translateTransition, fadeTransition);
        parallelTransition.play();
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}