package vista;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.RotateTransition;

import modelo.Bloque;
import modelo.Controlador;
import modelo.Curso;
import modelo.Pregunta;
import modelo.PreguntaFillinBlank;
import modelo.PreguntaFlashCard;
import modelo.PreguntaMultipleChoice;

public class VentanaPreguntas extends Application {
    
    // Colores estilo Duolingo
    private final Color COLOR_PRIMARIO = Color.rgb(88, 204, 2); // Verde Duolingo
    private final Color COLOR_SECUNDARIO = Color.rgb(255, 200, 0); // Amarillo Duolingo
    private final Color COLOR_FONDO = Color.rgb(245, 245, 245);
    private final Color COLOR_CORRECTO = Color.rgb(88, 204, 2);
    private final Color COLOR_INCORRECTO = Color.rgb(255, 75, 75);
    
    // Controlador y modelo
    private Controlador controlador;
    private Curso cursoActual;
    private List<Bloque> bloques;
    private SimpleIntegerProperty bloqueActual = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty preguntaActual = new SimpleIntegerProperty(0);
    
    // Componentes UI principales
    private BorderPane panelPrincipal;
    private StackPane contenedorPreguntas;
    private Label labelTituloCurso;
    private ProgressBar barraProgreso;
    private Label labelProgreso;
    
    // Paneles específicos para cada tipo de pregunta
    private VBox panelMultipleChoice;
    private StackPane panelFlashCard;
    private VBox panelFillInBlank;
    
    // Componentes para FlashCard
    private boolean mostrandoFrente = true;
    private StackPane cardFrente;
    private StackPane cardReverso;
    
    // Navegación
    private Button btnAnterior;
    private Button btnSiguiente;
    private Button btnVerificar;
    
 // Añadir estos atributos a VentanaPreguntas
    private VBox panelEmparejamiento;
    private VBox panelOrdenarPalabras;
    private VBox panelCompletarTexto;
    
    @Override
    public void start(Stage primaryStage) {
        controlador = Controlador.getInstancia();
        
        // Configurar la ventana principal
        primaryStage.setTitle("Curso de Aprendizaje");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        
        inicializarUI();
        cargarCursoActual();
        
        Scene scene = new Scene(panelPrincipal);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void inicializarUI() {
        panelPrincipal = new BorderPane();
        panelPrincipal.setStyle("-fx-background-color: " + toRGBCode(COLOR_FONDO) + ";");
        panelPrincipal.setPadding(new Insets(20));
        
        // Panel superior (información del curso)
        HBox panelInfo = new HBox(20);
        panelInfo.setPadding(new Insets(0, 0, 20, 0));
        panelInfo.setAlignment(Pos.CENTER_LEFT);
        
        labelTituloCurso = new Label();
        labelTituloCurso.setFont(Font.font("System", FontWeight.BOLD, 22));
        labelTituloCurso.setTextFill(COLOR_PRIMARIO);
        
        VBox progressBox = new VBox(5);
        progressBox.setAlignment(Pos.CENTER_RIGHT);
        
        barraProgreso = new ProgressBar(0);
        barraProgreso.setPrefWidth(200);
        barraProgreso.setStyle("-fx-accent: " + toRGBCode(COLOR_PRIMARIO) + ";");
        
        labelProgreso = new Label();
        labelProgreso.setFont(Font.font("System", 14));
        
        progressBox.getChildren().addAll(barraProgreso, labelProgreso);
        panelInfo.getChildren().addAll(labelTituloCurso, progressBox);
        
        // Agregar espacio flexible entre el título y la barra de progreso
        HBox.setHgrow(progressBox, javafx.scene.layout.Priority.ALWAYS);
        progressBox.setAlignment(Pos.CENTER_RIGHT);
        
        panelPrincipal.setTop(panelInfo);
        
        // Panel central (contenido de preguntas)
        contenedorPreguntas = new StackPane();
        contenedorPreguntas.setPadding(new Insets(20));
        
        // Inicializar paneles para cada tipo de pregunta
        inicializarPanelMultipleChoice();
        inicializarPanelFlashCard();
        inicializarPanelFillInBlank();
        inicializarPanelEmparejamiento();
        inicializarPanelOrdenarPalabras();
        inicializarPanelCompletarTexto();
        
        // Ocultar todos los paneles inicialmente
        panelMultipleChoice.setVisible(false);
        panelFlashCard.setVisible(false);
        panelFillInBlank.setVisible(false);
        
        contenedorPreguntas.getChildren().addAll(panelMultipleChoice, panelFlashCard, panelFillInBlank);
        
        // Agregar efecto de sombra al contenedor
        DropShadow sombra = new DropShadow();
        sombra.setRadius(10.0);
        sombra.setOffsetX(3.0);
        sombra.setOffsetY(3.0);
        sombra.setColor(Color.rgb(0, 0, 0, 0.3));
        contenedorPreguntas.setEffect(sombra);
        
        // Establecer borde redondeado para el contenedor
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(contenedorPreguntas.widthProperty());
        clip.heightProperty().bind(contenedorPreguntas.heightProperty());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        contenedorPreguntas.setClip(clip);
        contenedorPreguntas.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        panelPrincipal.setCenter(contenedorPreguntas);
        
        // Panel inferior (botones de navegación)
        HBox panelNavegacion = new HBox(20);
        panelNavegacion.setPadding(new Insets(20, 0, 0, 0));
        panelNavegacion.setAlignment(Pos.CENTER);
        
        btnAnterior = crearBotonEstiloDuolingo("Anterior", COLOR_SECUNDARIO);
        btnAnterior.setOnAction(e -> mostrarPreguntaAnterior());
        
        btnVerificar = crearBotonEstiloDuolingo("Verificar", COLOR_PRIMARIO);
        btnVerificar.setOnAction(e -> verificarRespuesta());
        
        btnSiguiente = crearBotonEstiloDuolingo("Siguiente", COLOR_SECUNDARIO);
        btnSiguiente.setOnAction(e -> mostrarPreguntaSiguiente());
        
        panelNavegacion.getChildren().addAll(btnAnterior, btnVerificar, btnSiguiente);
        
        panelPrincipal.setBottom(panelNavegacion);
    }
    
    private Button crearBotonEstiloDuolingo(String texto, Color color) {
        Button btn = new Button(texto);
        btn.setPrefWidth(120);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Estilo para el botón
        String colorHex = toRGBCode(color);
        String colorHoverHex = toRGBCode(color.darker());
        
        btn.setStyle(
            "-fx-background-color: " + colorHex + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        
        // Efectos hover
        btn.setOnMouseEntered(e -> 
            btn.setStyle(
                "-fx-background-color: " + colorHoverHex + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: hand;"
            )
        );
        
        btn.setOnMouseExited(e -> 
            btn.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: hand;"
            )
        );
        
        return btn;
    }
    
    private void inicializarPanelMultipleChoice() {
        panelMultipleChoice = new VBox(20);
        panelMultipleChoice.setAlignment(Pos.CENTER);
        panelMultipleChoice.setPadding(new Insets(20));
        panelMultipleChoice.setMaxWidth(600);
        panelMultipleChoice.setMaxHeight(400);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 20));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        
        VBox opciones = new VBox(15);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(20, 0, 0, 0));
        
        panelMultipleChoice.getChildren().addAll(enunciado, opciones);
    }
    
    private void inicializarPanelFlashCard() {
        panelFlashCard = new StackPane();
        panelFlashCard.setAlignment(Pos.CENTER);
        
        // Tarjeta frontal
        cardFrente = new StackPane();
        cardFrente.setMinSize(400, 250);
        cardFrente.setMaxSize(400, 250);
        cardFrente.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        VBox frenteBox = new VBox(10);
        frenteBox.setAlignment(Pos.CENTER);
        
        Label labelFrente = new Label();
        labelFrente.setWrapText(true);
        labelFrente.setMaxWidth(350);
        labelFrente.setFont(Font.font("System", FontWeight.BOLD, 22));
        labelFrente.setTextFill(COLOR_PRIMARIO);
        
        Button btnMostrarRespuesta = crearBotonEstiloDuolingo("Mostrar Respuesta", COLOR_SECUNDARIO);
        btnMostrarRespuesta.setOnAction(e -> voltearTarjeta());
        
        frenteBox.getChildren().addAll(labelFrente, btnMostrarRespuesta);
        cardFrente.getChildren().add(frenteBox);
        
        // Tarjeta trasera
        cardReverso = new StackPane();
        cardReverso.setMinSize(400, 250);
        cardReverso.setMaxSize(400, 250);
        cardReverso.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + toRGBCode(COLOR_SECUNDARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        VBox reversoBox = new VBox(10);
        reversoBox.setAlignment(Pos.CENTER);
        
        Label labelReverso = new Label();
        labelReverso.setWrapText(true);
        labelReverso.setMaxWidth(350);
        labelReverso.setFont(Font.font("System", FontWeight.BOLD, 22));
        labelReverso.setTextFill(COLOR_SECUNDARIO);
        
        Button btnOcultarRespuesta = crearBotonEstiloDuolingo("Volver", COLOR_PRIMARIO);
        btnOcultarRespuesta.setOnAction(e -> voltearTarjeta());
        
        reversoBox.getChildren().addAll(labelReverso, btnOcultarRespuesta);
        cardReverso.getChildren().add(reversoBox);
        
        // Inicialmente solo mostrar la parte frontal
        cardReverso.setVisible(false);
        
        panelFlashCard.getChildren().addAll(cardFrente, cardReverso);
    }
    
    private void voltearTarjeta() {
        RotateTransition rotacion = new RotateTransition(Duration.millis(600), panelFlashCard);
        
        if (mostrandoFrente) {
            rotacion.setFromAngle(0);
            rotacion.setToAngle(90);
            rotacion.setOnFinished(e -> {
                cardFrente.setVisible(false);
                cardReverso.setVisible(true);
                
                RotateTransition rotacion2 = new RotateTransition(Duration.millis(600), panelFlashCard);
                rotacion2.setFromAngle(270);
                rotacion2.setToAngle(360);
                rotacion2.play();
            });
        } else {
            rotacion.setFromAngle(0);
            rotacion.setToAngle(90);
            rotacion.setOnFinished(e -> {
                cardReverso.setVisible(false);
                cardFrente.setVisible(true);
                
                RotateTransition rotacion2 = new RotateTransition(Duration.millis(600), panelFlashCard);
                rotacion2.setFromAngle(270);
                rotacion2.setToAngle(360);
                rotacion2.play();
            });
        }
        
        rotacion.play();
        mostrandoFrente = !mostrandoFrente;
    }
    
    private void inicializarPanelFillInBlank() {
        panelFillInBlank = new VBox(30);
        panelFillInBlank.setAlignment(Pos.CENTER);
        panelFillInBlank.setPadding(new Insets(20));
        panelFillInBlank.setMaxWidth(600);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 20));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        
        VBox respuestaBox = new VBox(10);
        respuestaBox.setAlignment(Pos.CENTER);
        
        TextField textRespuesta = new TextField();
        textRespuesta.setMaxWidth(300);
        textRespuesta.setMinHeight(40);
        textRespuesta.setFont(Font.font("System", 16));
        textRespuesta.setAlignment(Pos.CENTER);
        textRespuesta.setPromptText("Escribe tu respuesta aquí");
        textRespuesta.setStyle(
            "-fx-background-radius: 10;" + 
            "-fx-border-radius: 10;" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-padding: 10;"
        );
        
        respuestaBox.getChildren().add(textRespuesta);
        
        panelFillInBlank.getChildren().addAll(enunciado, respuestaBox);
    }
    
    private void cargarCursoActual() {
        if (controlador.getUsuarioActual() != null && controlador.getCursoActual() != null) {
            cursoActual = controlador.getCursoActual();
            bloques = cursoActual.getBloques();
            
            labelTituloCurso.setText(cursoActual.getTitulo());
            
            if (bloques != null && !bloques.isEmpty()) {
                actualizarProgreso();
                mostrarPreguntaActual();
            } else {
                mostrarAlerta("El curso no tiene bloques.");
            }
        }
        else {
        	System.out.println("curso no cargado");
        }
    }
    
    private void mostrarPreguntaActual() {
        if (bloques == null || bloques.isEmpty() || bloqueActual.get() >= bloques.size()) {
            mostrarAlerta("No hay bloques o preguntas disponibles.");
            return;
        }
        
        Bloque bloque = bloques.get(bloqueActual.get());
        List<Pregunta> preguntas = bloque.getPreguntas();
        
        if (preguntas == null || preguntas.isEmpty() || preguntaActual.get() >= preguntas.size()) {
            mostrarAlerta("No hay preguntas en este bloque.");
            return;
        }
        
        Pregunta pregunta = preguntas.get(preguntaActual.get());
        
        // Ocultar todos los paneles primero
        panelMultipleChoice.setVisible(false);
        panelFlashCard.setVisible(false);
        panelFillInBlank.setVisible(false);
        if (panelEmparejamiento != null) panelEmparejamiento.setVisible(false);
        if (panelOrdenarPalabras != null) panelOrdenarPalabras.setVisible(false);
        if (panelCompletarTexto != null) panelCompletarTexto.setVisible(false);
        
        // Primero comprobamos si es alguno de los tipos específicos
        if (pregunta instanceof PreguntaMultipleChoice) {
            mostrarPreguntaMultipleChoice((PreguntaMultipleChoice) pregunta);
        } else if (pregunta instanceof PreguntaFlashCard) {
            mostrarPreguntaFlashCard((PreguntaFlashCard) pregunta);
        } else if (pregunta instanceof PreguntaFillinBlank) {
            mostrarPreguntaFillInBlank((PreguntaFillinBlank) pregunta);
        } else {
            // Si no es ninguno de los tipos específicos, tratamos la pregunta normal
            mostrarPreguntaNormal(pregunta);
        }
        
        actualizarProgreso();
    }
    
    private void mostrarPreguntaNormal(Pregunta pregunta) {
        Pregunta.TipoPregunta tipo = pregunta.getTipo();
        
        switch (tipo) {
            case SELECCION_MULTIPLE:
                mostrarPreguntaSeleccionMultiple(pregunta);
                break;
            case EMPAREJAMIENTO:
                mostrarPreguntaEmparejamiento(pregunta);
                break;
            case ORDENAR_PALABRAS:
                mostrarPreguntaOrdenarPalabras(pregunta);
                break;
            case COMPLETAR:
                mostrarPreguntaCompletar(pregunta);
                break;
            default:
                mostrarAlerta("Tipo de pregunta no implementado: " + tipo);
        }
    }
    
    private void mostrarPreguntaSeleccionMultiple(Pregunta pregunta) {
        // Podemos reutilizar el panel de múltiple choice ya existente
        panelMultipleChoice.setVisible(true);
        
        // Limpiar y configurar
        VBox vbox = (VBox) panelMultipleChoice;
        Label enunciado = (Label) vbox.getChildren().get(0);
        VBox opcionesBox = (VBox) vbox.getChildren().get(1);
        
        enunciado.setText(pregunta.getEnunciado());
        opcionesBox.getChildren().clear();
        
        ToggleGroup grupo = new ToggleGroup();
        String[] opciones = pregunta.getOpciones();
        
        for (int i = 0; i < opciones.length; i++) {
            RadioButton radio = new RadioButton(opciones[i]);
            radio.setToggleGroup(grupo);
            radio.setFont(Font.font("System", 16));
            radio.setPadding(new Insets(10));
            radio.setUserData(i); // Guardar índice como userData
            
            // Estilo para las opciones
            radio.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO.deriveColor(0, 1, 1, 0.7)) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;" +
                "-fx-cursor: hand;"
            );
            
            radio.setPrefWidth(500);
            
            // Efectos hover
            radio.setOnMouseEntered(e -> 
                radio.setStyle(
                    "-fx-background-color: " + toRGBCode(COLOR_FONDO) + ";" +
                    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;" +
                    "-fx-cursor: hand;"
                )
            );
            
            radio.setOnMouseExited(e -> 
                radio.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO.deriveColor(0, 1, 1, 0.7)) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;" +
                    "-fx-cursor: hand;"
                )
            );
            
            opcionesBox.getChildren().add(radio);
        }
    }

    private void inicializarPanelEmparejamiento() {
        panelEmparejamiento = new VBox(20);
        panelEmparejamiento.setAlignment(Pos.CENTER);
        panelEmparejamiento.setPadding(new Insets(20));
        panelEmparejamiento.setMaxWidth(600);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 20));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        
        FlowPane parejas = new FlowPane(10, 10);
        parejas.setAlignment(Pos.CENTER);
        parejas.setPadding(new Insets(20));
        
        panelEmparejamiento.getChildren().addAll(enunciado, parejas);
        contenedorPreguntas.getChildren().add(panelEmparejamiento);
        panelEmparejamiento.setVisible(false);
    }

    private void mostrarPreguntaEmparejamiento(Pregunta pregunta) {
        // Inicializar panel si no existe
        if (panelEmparejamiento == null) {
            inicializarPanelEmparejamiento();
        }
        
        panelEmparejamiento.setVisible(true);
        
        VBox vbox = panelEmparejamiento;
        Label enunciado = (Label) vbox.getChildren().get(0);
        FlowPane parejas = (FlowPane) vbox.getChildren().get(1);
        
        enunciado.setText(pregunta.getEnunciado());
        parejas.getChildren().clear();
        
        // Implementar lógica para mostrar las opciones de emparejamiento
        // Aquí puedes usar la lista seleccionesEmparejamiento de la pregunta
        // para mostrar las opciones y almacenar las selecciones del usuario
        
        String[] opciones = pregunta.getOpciones();
        // Separar opciones en dos columnas (ítems y coincidencias)
        int mitad = opciones.length / 2;
        
        // Crear dos columnas para elementos a emparejar
        VBox columnaIzquierda = new VBox(10);
        VBox columnaDerecha = new VBox(10);
        
        for (int i = 0; i < mitad; i++) {
            Button btnIzq = crearBotonEmparejamiento(opciones[i], i);
            columnaIzquierda.getChildren().add(btnIzq);
            
            Button btnDer = crearBotonEmparejamiento(opciones[i + mitad], i + mitad);
            columnaDerecha.getChildren().add(btnDer);
        }
        
        HBox contenedor = new HBox(30);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.getChildren().addAll(columnaIzquierda, columnaDerecha);
        
        parejas.getChildren().add(contenedor);
    }

    private Button crearBotonEmparejamiento(String texto, int indice) {
        Button btn = new Button(texto);
        btn.setPrefWidth(200);
        btn.setPrefHeight(60);
        btn.setWrapText(true);
        btn.setFont(Font.font("System", 14));
        
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10;"
        );
        
        btn.setUserData(indice);
        
        // Implementar lógica para selección
        btn.setOnAction(e -> seleccionarEmparejamiento(btn));
        
        return btn;
    }

    private void seleccionarEmparejamiento(Button botonSeleccionado) {
        // Implementar lógica para guardar la selección del usuario
        // y mostrar visualmente la conexión entre elementos
    }

    private void inicializarPanelOrdenarPalabras() {
        panelOrdenarPalabras = new VBox(20);
        panelOrdenarPalabras.setAlignment(Pos.CENTER);
        panelOrdenarPalabras.setPadding(new Insets(20));
        panelOrdenarPalabras.setMaxWidth(600);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 20));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        
        FlowPane palabrasDisponibles = new FlowPane(10, 10);
        palabrasDisponibles.setAlignment(Pos.CENTER);
        palabrasDisponibles.setPadding(new Insets(20));
        
        FlowPane palabrasSeleccionadas = new FlowPane(10, 10);
        palabrasSeleccionadas.setAlignment(Pos.CENTER);
        palabrasSeleccionadas.setPadding(new Insets(20));
        palabrasSeleccionadas.setStyle(
            "-fx-background-color: " + toRGBCode(COLOR_FONDO) + ";" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        panelOrdenarPalabras.getChildren().addAll(enunciado, palabrasDisponibles, palabrasSeleccionadas);
        contenedorPreguntas.getChildren().add(panelOrdenarPalabras);
        panelOrdenarPalabras.setVisible(false);
    }

    private void mostrarPreguntaOrdenarPalabras(Pregunta pregunta) {
        // Inicializar panel si no existe
        if (panelOrdenarPalabras == null) {
            inicializarPanelOrdenarPalabras();
        }
        
        panelOrdenarPalabras.setVisible(true);
        
        VBox vbox = panelOrdenarPalabras;
        Label enunciado = (Label) vbox.getChildren().get(0);
        FlowPane palabrasDisponibles = (FlowPane) vbox.getChildren().get(1);
        FlowPane palabrasSeleccionadas = (FlowPane) vbox.getChildren().get(2);
        
        enunciado.setText(pregunta.getEnunciado());
        palabrasDisponibles.getChildren().clear();
        palabrasSeleccionadas.getChildren().clear();
        
        // Obtener las palabras y mostrarlas como botones
        String[] opciones = pregunta.getOpciones();
        
        for (int i = 0; i < opciones.length; i++) {
            Button btnPalabra = crearBotonPalabra(opciones[i]);
            palabrasDisponibles.getChildren().add(btnPalabra);
            
            // Agregar lógica para mover palabras entre los contenedores
            btnPalabra.setOnAction(e -> moverPalabra((Button)e.getSource(), 
                                                    palabrasDisponibles, 
                                                    palabrasSeleccionadas));
        }
    }

    private Button crearBotonPalabra(String texto) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("System", 16));
        
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10;"
        );
        
        return btn;
    }

    private void moverPalabra(Button boton, FlowPane origen, FlowPane destino) {
        origen.getChildren().remove(boton);
        destino.getChildren().add(boton);
        
        // Cambiar el comportamiento al hacer clic si está en el destino
        if (destino.getChildren().contains(boton)) {
            boton.setOnAction(e -> moverPalabra(boton, destino, origen));
        } else {
            boton.setOnAction(e -> moverPalabra(boton, origen, destino));
        }
    }

    private void inicializarPanelCompletarTexto() {
        panelCompletarTexto = new VBox(20);
        panelCompletarTexto.setAlignment(Pos.CENTER);
        panelCompletarTexto.setPadding(new Insets(20));
        panelCompletarTexto.setMaxWidth(600);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 20));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        
        VBox camposTexto = new VBox(15);
        camposTexto.setAlignment(Pos.CENTER);
        
        panelCompletarTexto.getChildren().addAll(enunciado, camposTexto);
        contenedorPreguntas.getChildren().add(panelCompletarTexto);
        panelCompletarTexto.setVisible(false);
    }

    private void mostrarPreguntaCompletar(Pregunta pregunta) {
        // Inicializar panel si no existe
        if (panelCompletarTexto == null) {
            inicializarPanelCompletarTexto();
        }
        
        panelCompletarTexto.setVisible(true);
        
        VBox vbox = panelCompletarTexto;
        Label enunciado = (Label) vbox.getChildren().get(0);
        VBox camposTexto = (VBox) vbox.getChildren().get(1);
        
        enunciado.setText(pregunta.getEnunciado());
        camposTexto.getChildren().clear();
        
        // Aquí procesaremos el enunciado para mostrar campos de texto
        // donde sea necesario completar
        
        TextField textRespuesta = new TextField();
        textRespuesta.setMaxWidth(300);
        textRespuesta.setMinHeight(40);
        textRespuesta.setFont(Font.font("System", 16));
        textRespuesta.setAlignment(Pos.CENTER);
        textRespuesta.setPromptText("Completa aquí");
        textRespuesta.setStyle(
            "-fx-background-radius: 10;" + 
            "-fx-border-radius: 10;" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-padding: 10;"
        );
        
        camposTexto.getChildren().add(textRespuesta);
    }
    
    private void mostrarPreguntaMultipleChoice(PreguntaMultipleChoice pregunta) {
        panelMultipleChoice.setVisible(true);
        
        // Limpiar y configurar
        VBox vbox = (VBox) panelMultipleChoice;
        Label enunciado = (Label) vbox.getChildren().get(0);
        VBox opcionesBox = (VBox) vbox.getChildren().get(1);
        
        enunciado.setText(pregunta.getEnunciado());
        opcionesBox.getChildren().clear();
        
        ToggleGroup grupo = new ToggleGroup();
        String[] opciones = pregunta.getOpciones();
        
        for (int i = 0; i < opciones.length; i++) {
            RadioButton radio = new RadioButton(opciones[i]);
            radio.setToggleGroup(grupo);
            radio.setFont(Font.font("System", 16));
            radio.setPadding(new Insets(10));
            radio.setUserData(i); // Guardar índice como userData
            
            // Estilo para las opciones
            radio.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO.deriveColor(0, 1, 1, 0.7)) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;" +
                "-fx-cursor: hand;"
            );
            
            radio.setPrefWidth(500);
            
            // Efectos hover
            radio.setOnMouseEntered(e -> 
                radio.setStyle(
                    "-fx-background-color: " + toRGBCode(COLOR_FONDO) + ";" +
                    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;" +
                    "-fx-cursor: hand;"
                )
            );
            
            radio.setOnMouseExited(e -> 
                radio.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO.deriveColor(0, 1, 1, 0.7)) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;" +
                    "-fx-cursor: hand;"
                )
            );
            
            opcionesBox.getChildren().add(radio);
        }
    }
    
    private void mostrarPreguntaFlashCard(PreguntaFlashCard pregunta) {
        panelFlashCard.setVisible(true);
        mostrandoFrente = true;
        
        // Actualizar el contenido de las tarjetas
        VBox frenteBox = (VBox) cardFrente.getChildren().get(0);
        Label labelFrente = (Label) frenteBox.getChildren().get(0);
        labelFrente.setText(pregunta.getEnunciado());
        
        VBox reversoBox = (VBox) cardReverso.getChildren().get(0);
        Label labelReverso = (Label) reversoBox.getChildren().get(0);
        labelReverso.setText(pregunta.getRespuestaFlashCard());
        
        // Asegurarse de que la tarjeta frontal es visible
        cardFrente.setVisible(true);
        cardReverso.setVisible(false);
    }
    
    private void mostrarPreguntaFillInBlank(PreguntaFillinBlank pregunta) {
        panelFillInBlank.setVisible(true);
        
        // Configurar el contenido
        Label enunciado = (Label) panelFillInBlank.getChildren().get(0);
        enunciado.setText(pregunta.getEnunciado());
        
        VBox respuestaBox = (VBox) panelFillInBlank.getChildren().get(1);
        TextField textRespuesta = (TextField) respuestaBox.getChildren().get(0);
        textRespuesta.clear();
    }
    
    private void mostrarPreguntaAnterior() {
        int actual = preguntaActual.get() - 1;
        
        if (actual < 0) {
            int bloqueAnt = bloqueActual.get() - 1;
            
            if (bloqueAnt < 0) {
                // Ya estamos en la primera pregunta
                return;
            } else {
                bloqueActual.set(bloqueAnt);
                preguntaActual.set(bloques.get(bloqueAnt).getPreguntas().size() - 1);
            }
        } else {
            preguntaActual.set(actual);
        }
        
        mostrarPreguntaActual();
    }
    
    private void mostrarPreguntaSiguiente() {
        if (bloques == null || bloques.isEmpty()) return;
        
        int actual = preguntaActual.get() + 1;
        Bloque bloqueActualObj = bloques.get(bloqueActual.get());
        
        if (actual >= bloqueActualObj.getPreguntas().size()) {
            int bloqueSig = bloqueActual.get() + 1;
            if (bloqueSig >= bloques.size()) return;
            bloqueActual.set(bloqueSig);
            preguntaActual.set(0);
        } else {
            preguntaActual.set(actual);
        }
        
        mostrarPreguntaActual();
    }
    
    private void verificarRespuesta() {
        Pregunta pregunta = bloques.get(bloqueActual.get()).getPreguntas().get(preguntaActual.get());
        
        if (pregunta instanceof PreguntaMultipleChoice) {
            verificarRespuestaMultipleChoice((PreguntaMultipleChoice) pregunta);
        } else if (pregunta instanceof PreguntaFillinBlank) {
            verificarRespuestaFillInBlank((PreguntaFillinBlank) pregunta);
        } else if (pregunta instanceof PreguntaFlashCard) {
            // Para FlashCard, solo voltear la tarjeta
            voltearTarjeta();
        } else {
            // Verificar según el tipo de pregunta normal
            verificarRespuestaNormal(pregunta);
        }
    }

    private void verificarRespuestaNormal(Pregunta pregunta) {
        Pregunta.TipoPregunta tipo = pregunta.getTipo();
        
        switch (tipo) {
            case SELECCION_MULTIPLE:
                verificarRespuestaSeleccionMultiple(pregunta);
                break;
            case EMPAREJAMIENTO:
                verificarRespuestaEmparejamiento(pregunta);
                break;
            case ORDENAR_PALABRAS:
                verificarRespuestaOrdenarPalabras(pregunta);
                break;
            case COMPLETAR:
                verificarRespuestaCompletar(pregunta);
                break;
            default:
                mostrarAlerta("Verificación para este tipo no implementada: " + tipo);
        }
    }

    private void verificarRespuestaSeleccionMultiple(Pregunta pregunta) {
        // Reutilizamos la lógica de verificación existente
        VBox vbox = (VBox) panelMultipleChoice;
        VBox opcionesBox = (VBox) vbox.getChildren().get(1);
        
        int seleccionUsuario = -1;
        RadioButton seleccionado = null;
        
        for (int i = 0; i < opcionesBox.getChildren().size(); i++) {
            RadioButton radio = (RadioButton) opcionesBox.getChildren().get(i);
            if (radio.isSelected()) {
                seleccionUsuario = i;
                seleccionado = radio;
                break;
            }
        }
        
        if (seleccionUsuario == -1) {
            mostrarAlerta("Por favor, selecciona una opción.");
            return;
        }
        
        // Actualizar la selección en la pregunta
        pregunta.setSeleccionUsuario(new int[]{seleccionUsuario});
        
        if (seleccionUsuario == pregunta.getRespuestaCorrecta()) {
            // Respuesta correcta
            seleccionado.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.3)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;"
            );
            mostrarResultado(true);
        } else {
            // Respuesta incorrecta
            seleccionado.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.3)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_INCORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;"
            );
            
            // Mostrar la respuesta correcta
            RadioButton opcionCorrecta = (RadioButton) opcionesBox.getChildren().get(pregunta.getRespuestaCorrecta());
            opcionCorrecta.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.3)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;"
            );
            
            mostrarResultado(false);
        }
    }

    private void verificarRespuestaEmparejamiento(Pregunta pregunta) {
        // Implementar lógica para verificar las respuestas de emparejamiento
        // usando pregunta.getSeleccionesEmparejamiento() y comparando con
        // los valores esperados
        
        // Para obtener las selecciones actuales del usuario
        FlowPane contenedorParejas = (FlowPane) panelEmparejamiento.getChildren().get(1);
        // Lógica para obtener el estado actual de las parejas y verificar
        
        // Ejemplo de verificación (simplificado)
        boolean todasCorrectas = true;
        // Verificar con la lógica específica para emparejamiento
        
        mostrarResultado(todasCorrectas);
    }

    private void verificarRespuestaOrdenarPalabras(Pregunta pregunta) {
        // Obtener el orden actual de las palabras seleccionadas
        FlowPane contenedorSeleccionadas = (FlowPane) panelOrdenarPalabras.getChildren().get(2);
        
        // Obtener palabras en el orden actual
        List<String> ordenActual = new ArrayList<>();
        for (javafx.scene.Node node : contenedorSeleccionadas.getChildren()) {
            if (node instanceof Button) {
                ordenActual.add(((Button) node).getText());
            }
        }
        
        // Guardar la selección del usuario
        pregunta.setOrdenSeleccionado(ordenActual);
        
        // Verificar si el orden es correcto (aquí necesitas implementar la lógica específica)
        boolean ordenCorrecto = verificarOrdenPalabras(ordenActual, pregunta);
        
        mostrarResultado(ordenCorrecto);
    }

    private boolean verificarOrdenPalabras(List<String> ordenActual, Pregunta pregunta) {
        // Implementar lógica para verificar si el orden es correcto
        // Esta es una implementación simplificada
        
        // Ejemplo: verificar si coincide con el orden esperado
        String[] opcionesCorrectas = pregunta.getOpciones();
        
        if (ordenActual.size() != opcionesCorrectas.length) {
            return false;
        }
        
        // Compara el orden seleccionado con el esperado
        // (En una implementación real, necesitarías tener el orden correcto 
        // guardado en la pregunta)
        return true; // Simplificado para este ejemplo
    }

    private void verificarRespuestaCompletar(Pregunta pregunta) {
        VBox camposTexto = (VBox) panelCompletarTexto.getChildren().get(1);
        
        // Obtener texto de los campos
        List<String> respuestas = new ArrayList<>();
        for (javafx.scene.Node node : camposTexto.getChildren()) {
            if (node instanceof TextField) {
                respuestas.add(((TextField) node).getText().trim());
            }
        }
        
        // Simplificación: verificamos solo un campo
        if (respuestas.size() > 0) {
            String respuestaUsuario = respuestas.get(0);
            
            // Comparar con la respuesta correcta (simplificado)
            boolean esCorrecta = respuestaUsuario.equalsIgnoreCase(pregunta.getOpciones()[0]);
            
            // Estilo para la respuesta
            TextField campo = (TextField) camposTexto.getChildren().get(0);
            if (esCorrecta) {
                campo.setStyle(
                    "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";" +
                    "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 10;"
                );
            } else {
                campo.setStyle(
                    "-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.2)) + ";" +
                    "-fx-border-color: " + toRGBCode(COLOR_INCORRECTO) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 10;"
                );
            }
            
            mostrarResultado(esCorrecta);
        } else {
            mostrarAlerta("Por favor, completa todos los campos.");
        }
    }
    
    private void verificarRespuestaMultipleChoice(PreguntaMultipleChoice pregunta) {
        VBox vbox = (VBox) panelMultipleChoice;
        VBox opcionesBox = (VBox) vbox.getChildren().get(1);
        
        int seleccionUsuario = -1;
        RadioButton seleccionado = null;
        
        for (int i = 0; i < opcionesBox.getChildren().size(); i++) {
            RadioButton radio = (RadioButton) opcionesBox.getChildren().get(i);
            if (radio.isSelected()) {
                seleccionUsuario = i;
                seleccionado = radio;
                break;
            }
        }
        
        if (seleccionUsuario == -1) {
            mostrarAlerta("Por favor, selecciona una opción.");
            return;
        }
        
        if (seleccionUsuario == pregunta.getRespuestaCorrecta()) {
            // Respuesta correcta
            seleccionado.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.3)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;"
            );
            mostrarResultado(true);
        } else {
            // Respuesta incorrecta
            seleccionado.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.3)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_INCORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;"
            );
            
            // Mostrar la respuesta correcta
            RadioButton opcionCorrecta = (RadioButton) opcionesBox.getChildren().get(pregunta.getRespuestaCorrecta());
            opcionCorrecta.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.3)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 15;"
            );
            
            mostrarResultado(false);
        }
    }
    
    private void verificarRespuestaFillInBlank(PreguntaFillinBlank pregunta) {
        VBox respuestaBox = (VBox) panelFillInBlank.getChildren().get(1);
        TextField textRespuesta = (TextField) respuestaBox.getChildren().get(0);
        
        String respuestaUsuario = textRespuesta.getText().trim();
        
        if (respuestaUsuario.isEmpty()) {
            mostrarAlerta("Por favor, escribe una respuesta.");
            return;
        }
        
        if (respuestaUsuario.equalsIgnoreCase(pregunta.getRespuestaCorrectaTexto())) {
            // Respuesta correcta
            textRespuesta.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;"
            );
            
            mostrarResultado(true);
        } else {
            // Respuesta incorrecta
            textRespuesta.setStyle(
                "-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.2)) + ";" +
                "-fx-border-color: " + toRGBCode(COLOR_INCORRECTO) + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;"
            );
            
            mostrarResultado(false);
        }
    }
    
    private void mostrarResultado(boolean correcto) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Resultado");
        alerta.setHeaderText(null);
        
        if (correcto) {
            alerta.setContentText("¡Correcto! ¡Muy bien!");
            alerta.getDialogPane().getStyleClass().add("alert-success");
        } else {
            alerta.setContentText("Incorrecto. Intenta de nuevo.");
            alerta.getDialogPane().getStyleClass().add("alert-error");
        }
        
        alerta.showAndWait();
    }
    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void actualizarProgreso() {
        // Calcular el total de preguntas y la pregunta actual global
        int totalPreguntas = 0;
        int preguntaActualGlobal = 0;
        
        for (int i = 0; i < bloques.size(); i++) {
            int preguntasEnBloque = bloques.get(i).getPreguntas().size();
            totalPreguntas += preguntasEnBloque;
            
            if (i < bloqueActual.get()) {
                preguntaActualGlobal += preguntasEnBloque;
            }
        }
        
        preguntaActualGlobal += preguntaActual.get() + 1;
        
        double progreso = (double) (preguntaActualGlobal - 1) / totalPreguntas;
        barraProgreso.setProgress(progreso);
        labelProgreso.setText(preguntaActualGlobal + " de " + totalPreguntas);
    }

    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}