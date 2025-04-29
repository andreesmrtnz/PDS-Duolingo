package vista;

import java.util.ArrayList;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ScrollPane;
import java.util.List;
import java.util.Optional;

import controlador.Controlador;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;

import modelo.Bloque;
import modelo.Curso;
import modelo.Estrategia;
import modelo.Pregunta;
import modelo.PreguntaFillinBlank;
import modelo.PreguntaFlashCard;
import modelo.PreguntaMultipleChoice;

public class VentanaPreguntas extends Application {
    
	// Colores con mejor contraste para legibilidad
	private final Color COLOR_PRIMARIO = Color.rgb(0, 128, 0); // Verde más oscuro
	private final Color COLOR_SECUNDARIO = Color.rgb(220, 150, 0); // Amarillo más oscuro
	private final Color COLOR_FONDO = Color.rgb(250, 250, 250);
	private final Color COLOR_CORRECTO = Color.rgb(0, 150, 0); // Verde más oscuro para correctas
	private final Color COLOR_INCORRECTO = Color.rgb(220, 53, 53); // Rojo más intenso para incorrectas
    
    // Controlador y modelo
    private Controlador controlador;
    private Curso cursoActual;
    private List<Bloque> bloques;
    private SimpleIntegerProperty bloqueActual = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty preguntaActual = new SimpleIntegerProperty(0);
    private boolean preguntaRespondida = false;
    
    // Componentes UI principales
    private BorderPane panelPrincipal;
    private StackPane contenedorPreguntas;
    private Label labelTituloCurso;
    private ProgressBar barraProgreso;
    private Label labelProgreso;
    private Label labelCorrectas;
    private Label labelIncorrectas;
    
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
        
        
        
        // Actualizar la interfaz cuando cambia el tamaño
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> actualizarResponsividad());
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> actualizarResponsividad());
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void actualizarResponsividad() {
        double ancho = panelPrincipal.getWidth();
        
        // Ajustar la interfaz según el ancho disponible
        if (ancho < 600) {
            // Para pantallas pequeñas
            panelMultipleChoice.setPrefWidth(ancho * 0.9);
            panelFillInBlank.setPrefWidth(ancho * 0.9);
            
            if (cardFrente != null && cardReverso != null) {
                cardFrente.setMinSize(ancho * 0.8, 200);
                cardFrente.setMaxSize(ancho * 0.8, 200);
                cardReverso.setMinSize(ancho * 0.8, 200);
                cardReverso.setMaxSize(ancho * 0.8, 200);
            }
        } else {
            // Para pantallas normales
            panelMultipleChoice.setPrefWidth(600);
            panelFillInBlank.setPrefWidth(600);
            
            if (cardFrente != null && cardReverso != null) {
                cardFrente.setMinSize(400, 250);
                cardFrente.setMaxSize(400, 250);
                cardReverso.setMinSize(400, 250);
                cardReverso.setMaxSize(400, 250);
            }
        }
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
        labelTituloCurso.setFont(Font.font("System", FontWeight.BOLD, 24));
        labelTituloCurso.setStyle("-fx-text-fill: #003300;");
        
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
        
        // Añadir contador de preguntas acertadas y falladas
        HBox panelEstadisticas = new HBox(20);
        panelEstadisticas.setAlignment(Pos.CENTER);
        panelEstadisticas.setPadding(new Insets(10, 0, 0, 0));

     // Icono para correctas
        FontAwesomeIconView iconoCorrectas = new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE);
        iconoCorrectas.setFill(COLOR_CORRECTO);
        iconoCorrectas.setSize("20");
        
        labelCorrectas = new Label("Correctas: 0");
        labelCorrectas.setFont(Font.font("System", FontWeight.BOLD, 16));
        labelCorrectas.setTextFill(COLOR_CORRECTO);
        
        HBox correctasBox = new HBox(8);
        correctasBox.setAlignment(Pos.CENTER);
        correctasBox.getChildren().addAll(iconoCorrectas, labelCorrectas);

        // Icono para incorrectas
        FontAwesomeIconView iconoIncorrectas = new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE);
        iconoIncorrectas.setFill(COLOR_INCORRECTO);
        iconoIncorrectas.setSize("20");
        
        labelIncorrectas = new Label("Incorrectas: 0");
        labelIncorrectas.setFont(Font.font("System", FontWeight.BOLD, 16));
        labelIncorrectas.setTextFill(COLOR_INCORRECTO);
        
        HBox incorrectasBox = new HBox(8);
        incorrectasBox.setAlignment(Pos.CENTER);
        incorrectasBox.getChildren().addAll(iconoIncorrectas, labelIncorrectas);

        panelEstadisticas.getChildren().addAll(correctasBox, incorrectasBox);
        VBox panelInfoCompleto = new VBox(10);
        panelInfoCompleto.getChildren().addAll(panelInfo, panelEstadisticas);
        
        panelPrincipal.setTop(panelInfoCompleto);
        
        // Panel central (contenido de preguntas)
        contenedorPreguntas = new StackPane();
        contenedorPreguntas.setPadding(new Insets(20));
        
        // Añadir un ScrollPane para manejar contenido extenso
        ScrollPane scrollPane = new ScrollPane(contenedorPreguntas);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.getStyleClass().add("custom-scroll-pane");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Inicializar paneles para cada tipo de pregunta
        inicializarPanelMultipleChoice();
        inicializarPanelFlashCard();
        inicializarPanelFillInBlank();
        
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
        
        panelPrincipal.setCenter(scrollPane);
        
        // Panel inferior (botones de navegación)
        HBox panelNavegacion = new HBox(20);
        panelNavegacion.setPadding(new Insets(20, 0, 0, 0));
        panelNavegacion.setAlignment(Pos.CENTER);
        
     // Crear botones con iconos
        btnAnterior = crearBotonEstiloDuolingo("Anterior", COLOR_SECUNDARIO);
        FontAwesomeIconView iconoAnterior = new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT);
        iconoAnterior.setFill(Color.WHITE);
        iconoAnterior.setSize("18");
        btnAnterior.setGraphic(iconoAnterior);
        btnAnterior.setOnAction(e -> mostrarPreguntaAnterior());
        
        btnVerificar = crearBotonEstiloDuolingo("Verificar", COLOR_PRIMARIO);
        FontAwesomeIconView iconoVerificar = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        iconoVerificar.setFill(Color.WHITE);
        iconoVerificar.setSize("18");
        btnVerificar.setGraphic(iconoVerificar);
        btnVerificar.setOnAction(e -> verificarRespuesta());
        
        btnSiguiente = crearBotonEstiloDuolingo("Siguiente", COLOR_SECUNDARIO);
        FontAwesomeIconView iconoSiguiente = new FontAwesomeIconView(FontAwesomeIcon.ARROW_RIGHT);
        iconoSiguiente.setFill(Color.WHITE);
        iconoSiguiente.setSize("18");
        btnSiguiente.setGraphic(iconoSiguiente);
        btnSiguiente.setOnAction(e -> mostrarPreguntaSiguiente());
        
        // Posicionar el icono después del texto para el botón siguiente
        btnSiguiente.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        
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
        panelMultipleChoice.setMaxHeight(Region.USE_COMPUTED_SIZE);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 22));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        enunciado.setStyle("-fx-text-fill: #333333;");
        
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
        labelFrente.setTextFill(Color.rgb(0, 100, 0));
        
        Button btnMostrarRespuesta = crearBotonEstiloDuolingo("Mostrar", COLOR_SECUNDARIO);
        FontAwesomeIconView iconoGirar = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        iconoGirar.setFill(Color.WHITE);
        iconoGirar.setSize("16");
        btnMostrarRespuesta.setGraphic(iconoGirar);
        btnMostrarRespuesta.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
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
        labelReverso.setTextFill(Color.rgb(0, 100, 0));
        
        Button btnOcultarRespuesta = crearBotonEstiloDuolingo("Volver", COLOR_PRIMARIO);
        FontAwesomeIconView iconoVolver = new FontAwesomeIconView(FontAwesomeIcon.UNDO);
        iconoVolver.setFill(Color.WHITE);
        iconoVolver.setSize("16");
        btnOcultarRespuesta.setGraphic(iconoVolver);
        btnOcultarRespuesta.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
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
        
        // Las flashcards siempre se consideran respondidas una vez volteadas
        if (!preguntaRespondida) {
            preguntaRespondida = true;
        }
    }
    
    private void inicializarPanelFillInBlank() {
        panelFillInBlank = new VBox(30);
        panelFillInBlank.setAlignment(Pos.CENTER);
        panelFillInBlank.setPadding(new Insets(20));
        panelFillInBlank.setMaxWidth(600);
        
        Label enunciado = new Label();
        enunciado.setFont(Font.font("System", FontWeight.BOLD, 22));
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(550);
        enunciado.setAlignment(Pos.CENTER);
        enunciado.setStyle("-fx-text-fill: #333333;");
        
        VBox respuestaBox = new VBox(10);
        respuestaBox.setAlignment(Pos.CENTER);
        
        // Crear un contenedor para el campo de texto y el icono
        HBox campoContainer = new HBox(10);
        campoContainer.setAlignment(Pos.CENTER);
        campoContainer.setMaxWidth(350);
        
        // Agregar un icono al campo de texto
        FontAwesomeIconView iconoTextField = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        iconoTextField.setFill(COLOR_PRIMARIO);
        iconoTextField.setSize("20");
        
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
        
        HBox.setHgrow(textRespuesta, javafx.scene.layout.Priority.ALWAYS);
        campoContainer.getChildren().addAll(iconoTextField, textRespuesta);
        
        respuestaBox.getChildren().add(campoContainer);
        
        panelFillInBlank.getChildren().addAll(enunciado, respuestaBox);
    }
    
    private void cargarCursoActual() {
        if (controlador.getUsuarioActual() != null && controlador.getCursoActual() != null) {
            cursoActual = controlador.getCursoActual();
            bloques = cursoActual.getBloques();
            
            labelTituloCurso.setText(cursoActual.getTitulo());
            
            if (bloques != null && !bloques.isEmpty()) {
                // Recuperar el progreso actual si existe
                bloqueActual.set(controlador.getBloqueActual());
                preguntaActual.set(controlador.getPreguntaActual());
                
                actualizarContadorCorrectas();
                actualizarContadorIncorrectas();
                actualizarProgreso();
                mostrarPreguntaActual();
            } else {
                mostrarAlerta("El curso no tiene bloques.");
            }
        } else {
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

        // Verificar si la pregunta ya fue respondida usando CursoEnProgreso
        if (controlador.getEstrategia() == Estrategia.REPETICION_ESPACIADA && 
        	    controlador.isRepeticionPregunta(bloqueActual.get(), preguntaActual.get())) {
        	    // Si es una pregunta repetida en la estrategia de repetición espaciada,
        	    // permitimos que se responda de nuevo
        	    preguntaRespondida = false;
        	} else {
        	    preguntaRespondida = controlador.isPreguntaRespondida(bloqueActual.get(), preguntaActual.get());
        	}
        // Usar la enumeración TipoPregunta en lugar de instanceof
        if (pregunta instanceof PreguntaMultipleChoice) {
            mostrarPreguntaMultipleChoice((PreguntaMultipleChoice) pregunta);
        } else if (pregunta instanceof PreguntaFlashCard) {
            mostrarPreguntaFlashCard((PreguntaFlashCard) pregunta);
        } else if (pregunta instanceof PreguntaFillinBlank) {
            mostrarPreguntaFillInBlank((PreguntaFillinBlank) pregunta);
        }
        else {
        	System.out.println("pregunta sin tipo");
        }

        actualizarContadorCorrectas();
        actualizarContadorIncorrectas();
        actualizarProgreso();
        actualizarBotonSiguiente();
    }
    
    private void actualizarBotonSiguiente() {
        int totalBloques = bloques.size();
        int totalPreguntas = bloques.get(bloqueActual.get()).getPreguntas().size();
        
        boolean esUltimaPregunta = (bloqueActual.get() == totalBloques - 1) &&
                                   (preguntaActual.get() == totalPreguntas - 1);

        if (esUltimaPregunta) {
            btnSiguiente.setText("Finalizar");
            btnSiguiente.setOnAction(e -> finalizarCurso());
        } else {
            btnSiguiente.setText("Siguiente");
            btnSiguiente.setOnAction(e -> mostrarPreguntaSiguiente());
        }
    }
    
    private void mostrarPreguntaMultipleChoice(Pregunta pregunta) {
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
            HBox opcionContainer = new HBox(10);
            opcionContainer.setAlignment(Pos.CENTER_LEFT);
            opcionContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
            opcionContainer.setMaxWidth(Double.MAX_VALUE);
            
            RadioButton radio = new RadioButton(opciones[i]);
            radio.setToggleGroup(grupo);
            radio.setFont(Font.font("System", 16));
            radio.setPadding(new Insets(10));
            radio.setStyle("-fx-text-fill: #333333;");
            radio.setUserData(i); // Guardar índice como userData
            radio.setMaxWidth(Double.MAX_VALUE);
            radio.setWrapText(true);
            HBox.setHgrow(radio, javafx.scene.layout.Priority.ALWAYS);
            
            // Estilo para las opciones
            opcionContainer.setStyle(
            	    "-fx-background-color: white;" +
            	    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            	    "-fx-border-width: 2;" +
            	    "-fx-border-radius: 10;" +
            	    "-fx-background-radius: 10;" +
            	    "-fx-padding: 15;" +
            	    "-fx-cursor: hand;"
            	);
            
            // Icono para la opción (inicialmente invisible)
            FontAwesomeIconView iconoOpcion = new FontAwesomeIconView(FontAwesomeIcon.CIRCLE);
            iconoOpcion.setSize("16");
            iconoOpcion.setFill(COLOR_PRIMARIO);
            iconoOpcion.setVisible(false);
            
            opcionContainer.getChildren().addAll(radio, iconoOpcion);
            
            // Efectos hover
            opcionContainer.setOnMouseEntered(e -> 
                opcionContainer.setStyle(
                    "-fx-background-color: " + toRGBCode(COLOR_FONDO) + ";" +
                    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;" +
                    "-fx-cursor: hand;"
                )
            );
            
            opcionContainer.setOnMouseExited(e -> 
                opcionContainer.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO.deriveColor(0, 1, 1, 0.7)) + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 10;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 15;" +
                    "-fx-cursor: hand;"
                )
            );
            
            // Si la pregunta ya fue respondida, restaurar el estado
            if (preguntaRespondida) {
                int[] seleccionUsuario = pregunta.getSeleccionUsuario();
                
                if (seleccionUsuario != null && seleccionUsuario.length > 0 && seleccionUsuario[0] == i) {
                    radio.setSelected(true);
                    iconoOpcion.setVisible(true);
                    if (i == pregunta.getRespuestaCorrecta()) {
                        iconoOpcion.setIcon(FontAwesomeIcon.CHECK_CIRCLE);
                        iconoOpcion.setFill(COLOR_CORRECTO);
                        opcionContainer.setStyle("-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";");
                    } else {
                        iconoOpcion.setIcon(FontAwesomeIcon.TIMES_CIRCLE);
                        iconoOpcion.setFill(COLOR_INCORRECTO);
                        opcionContainer.setStyle("-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.2)) + ";");
                    }
                } else if (i == pregunta.getRespuestaCorrecta() && 
                        seleccionUsuario != null && seleccionUsuario.length > 0 && 
                        seleccionUsuario[0] != pregunta.getRespuestaCorrecta()) {
                    iconoOpcion.setIcon(FontAwesomeIcon.CHECK_CIRCLE);
                    iconoOpcion.setFill(COLOR_CORRECTO);
                    iconoOpcion.setVisible(true);
                    opcionContainer.setStyle("-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";");
                }
                
                // Deshabilitar si ya se respondió
                radio.setDisable(true);
            }
            
            opcionesBox.getChildren().add(opcionContainer);
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

    private void mostrarPreguntaFillInBlank(Pregunta pregunta) {
        panelFillInBlank.setVisible(true);
        
        // Configurar el contenido
        Label enunciado = (Label) panelFillInBlank.getChildren().get(0);
        enunciado.setText(pregunta.getEnunciado());
        
        VBox respuestaBox = (VBox) panelFillInBlank.getChildren().get(1);
        
        // Corrección aquí: Primero obtenemos el HBox y luego el TextField
        HBox campoContainer = (HBox) respuestaBox.getChildren().get(0);
        TextField textRespuesta = (TextField) campoContainer.getChildren().get(1);
        
        // Limpiar el campo y resetear el estilo
        textRespuesta.clear();
        textRespuesta.setStyle(
            "-fx-background-radius: 10;" + 
            "-fx-border-radius: 10;" +
            "-fx-border-color: " + toRGBCode(COLOR_PRIMARIO) + ";" +
            "-fx-border-width: 2;" +
            "-fx-padding: 10;"
        );
        textRespuesta.setEditable(true);
        
        // Si es una pregunta repetida en estrategia de repetición espaciada,
        // permitimos que se responda nuevamente
        if (controlador.getEstrategia() == Estrategia.REPETICION_ESPACIADA && 
            controlador.isRepeticionPregunta(bloqueActual.get(), preguntaActual.get())) {
            // La pregunta se puede responder de nuevo
            return;
        }
        
        // Si la pregunta ya fue respondida (y no es repetición), restaurar el estado
        if (preguntaRespondida) {
            // Obtener la respuesta del usuario de la lista ordenSeleccionado
            if (pregunta.getOrdenSeleccionado() != null && !pregunta.getOrdenSeleccionado().isEmpty()) {
                textRespuesta.setText(pregunta.getOrdenSeleccionado().get(0));
                
                // Verificar si la respuesta es correcta
                String respuestaCorrectaTexto = pregunta.getOpciones()[pregunta.getRespuestaCorrecta()];
                
                if (pregunta.getOrdenSeleccionado().get(0).equalsIgnoreCase(respuestaCorrectaTexto)) {
                    textRespuesta.setStyle(
                        "-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";" +
                        "-fx-border-color: " + toRGBCode(COLOR_CORRECTO) + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;"
                    );
                } else {
                    textRespuesta.setStyle(
                        "-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.2)) + ";" +
                        "-fx-border-color: " + toRGBCode(COLOR_INCORRECTO) + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;"
                    );
                }
                
                // Deshabilitar si ya se respondió
                textRespuesta.setEditable(false);
            }
        }
    }

    private boolean verificarRespuestaFillInBlank(PreguntaFillinBlank pregunta) {
        VBox respuestaBox = (VBox) panelFillInBlank.getChildren().get(1);
        // Primero obtenemos el HBox
        HBox campoContainer = (HBox) respuestaBox.getChildren().get(0);
        // Luego obtenemos el TextField que está en la posición 1 del HBox (después del icono)
        TextField textRespuesta = (TextField) campoContainer.getChildren().get(1);
        
        String respuestaUsuario = textRespuesta.getText().trim();
        if (respuestaUsuario.isEmpty()) {
            mostrarAlerta("Por favor, escribe una respuesta.");
            return false;
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
            
            textRespuesta.setEditable(false);
            
            mostrarResultado(true);
            return true;
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
            textRespuesta.setEditable(false);
            
            mostrarResultado(false);
            return false;
        }
    }

    // Modifica el método de verificación de Multiple Choice
    private boolean verificarRespuestaMultipleChoice(Pregunta pregunta) {
        VBox vbox = (VBox) panelMultipleChoice;
        VBox opcionesBox = (VBox) vbox.getChildren().get(1);

        int seleccionUsuario = -1;
        HBox seleccionadoContainer = null;

        // Iterar a través de los contenedores de opciones
        for (int i = 0; i < opcionesBox.getChildren().size(); i++) {
            HBox opcionContainer = (HBox) opcionesBox.getChildren().get(i);
            RadioButton radio = (RadioButton) opcionContainer.getChildren().get(0);
            
            if (radio.isSelected()) {
                seleccionUsuario = i;
                seleccionadoContainer = opcionContainer;
                break;
            }
        }

        if (seleccionUsuario == -1) {
            mostrarAlerta("Por favor, selecciona una opción.");
            return false;
        }

        boolean esCorrecto = seleccionUsuario == pregunta.getRespuestaCorrecta();
        
        // Guardar la respuesta del usuario en el modelo
        pregunta.setSeleccionUsuario(new int[]{seleccionUsuario});
        
        // Obtener el icono de la opción seleccionada
        FontAwesomeIconView iconoSeleccionado = (FontAwesomeIconView) seleccionadoContainer.getChildren().get(1);
        iconoSeleccionado.setVisible(true);
        
        if (esCorrecto) {
            seleccionadoContainer.setStyle("-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";");
            iconoSeleccionado.setIcon(FontAwesomeIcon.CHECK_CIRCLE);
            iconoSeleccionado.setFill(COLOR_CORRECTO);
        } else {
            seleccionadoContainer.setStyle("-fx-background-color: " + toRGBCode(COLOR_INCORRECTO.deriveColor(0, 1, 1, 0.2)) + ";");
            iconoSeleccionado.setIcon(FontAwesomeIcon.TIMES_CIRCLE);
            iconoSeleccionado.setFill(COLOR_INCORRECTO);
            
            // Marcar la opción correcta
            HBox opcionCorrectaContainer = (HBox) opcionesBox.getChildren().get(pregunta.getRespuestaCorrecta());
            FontAwesomeIconView iconoOpcionCorrecta = (FontAwesomeIconView) opcionCorrectaContainer.getChildren().get(1);
            iconoOpcionCorrecta.setIcon(FontAwesomeIcon.CHECK_CIRCLE);
            iconoOpcionCorrecta.setFill(COLOR_CORRECTO);
            iconoOpcionCorrecta.setVisible(true);
            opcionCorrectaContainer.setStyle("-fx-background-color: " + toRGBCode(COLOR_CORRECTO.deriveColor(0, 1, 1, 0.2)) + ";");
        }

        // Deshabilitar las opciones después de responder
        for (int i = 0; i < opcionesBox.getChildren().size(); i++) {
            HBox opcionContainer = (HBox) opcionesBox.getChildren().get(i);
            RadioButton radio = (RadioButton) opcionContainer.getChildren().get(0);
            radio.setDisable(true);
        }

        mostrarResultado(esCorrecto);
        return esCorrecto;
    }

    // Modifica el método verificarRespuesta para usar el tipo de pregunta adecuado
    private void verificarRespuesta() {
        if (preguntaRespondida) {
            return; // Evitar verificar múltiples veces
        }
        
        Pregunta pregunta = bloques.get(bloqueActual.get()).getPreguntas().get(preguntaActual.get());
        boolean respuestaCorrecta = false;

        if (pregunta instanceof PreguntaMultipleChoice) {
            respuestaCorrecta = verificarRespuestaMultipleChoice((PreguntaMultipleChoice) pregunta);
        } else if (pregunta instanceof PreguntaFillinBlank) {
            respuestaCorrecta = verificarRespuestaFillInBlank((PreguntaFillinBlank) pregunta);
        } else if (pregunta instanceof PreguntaFlashCard) {
            voltearTarjeta();
            return;
        }

        if (respuestaCorrecta) {
            controlador.registrarRespuesta(true);
            actualizarContadorCorrectas();
        } else {
            controlador.registrarRespuesta(false);
            actualizarContadorIncorrectas();
        }
        
        preguntaRespondida = true;
        actualizarProgreso();
    }
    
    
    
    private void mostrarPreguntaAnterior() {
        // Verificamos si podemos retroceder usando el controlador
        if (controlador.retrocederPregunta()) {
            // Actualizamos variables locales para reflejar el cambio en la interfaz
            bloqueActual.set(controlador.getBloqueActual());
            preguntaActual.set(controlador.getPreguntaActual());
            
            // Mostramos la pregunta actual
            mostrarPreguntaActual();
        } else {
            mostrarAlerta("Ya estás en la primera pregunta.");
        }
    }

    private void mostrarPreguntaSiguiente() {
        // Verificamos si la pregunta actual ha sido respondida (excepto para FlashCards)
        if (!preguntaRespondida && 
                !(bloques.get(bloqueActual.get()).getPreguntas().get(preguntaActual.get()) instanceof PreguntaFlashCard)) {
            mostrarAlerta("Debes responder esta pregunta antes de continuar");
            return;
        }
        
        // Utilizamos el controlador para avanzar según la estrategia configurada
        if (controlador.avanzarPregunta()) {
            // Actualizamos variables locales para reflejar el cambio en la interfaz
            bloqueActual.set(controlador.getBloqueActual());
            preguntaActual.set(controlador.getPreguntaActual());
            
            // Reiniciamos el estado para la nueva pregunta
            preguntaRespondida = false;
            
            // Mostramos la nueva pregunta
            mostrarPreguntaActual();
        } else {
            // Si no se puede avanzar más, finalizamos el curso
            finalizarCurso();
        }
    }

    private void finalizarCurso() {
        // Crear un diálogo de confirmación
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Finalizar curso");
        confirmacion.setHeaderText("¿Estás seguro de que deseas finalizar el curso?");
        confirmacion.setContentText("Se guardará tu progreso actual.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Llamar al controlador para finalizar el curso y guardar progreso
            controlador.finalizarCurso();
            
            // Mostrar resumen final
            Alert resumen = new Alert(AlertType.INFORMATION);
            resumen.setTitle("Curso finalizado");
            resumen.setHeaderText("¡Has completado el curso!");
            
            // Obtener estadísticas
            int correctas = controlador.getPreguntasCorrectas();
            int incorrectas = controlador.getPreguntasIncorrectas();
            double porcentaje = controlador.getPorcentajeAciertosCurso();
            
            resumen.setContentText(
                "Resumen:\n" +
                "Preguntas correctas: " + correctas + "\n" +
                "Preguntas incorrectas: " + incorrectas + "\n" +
                "Porcentaje de acierto: " + String.format("%.1f%%", porcentaje)
            );
            
            resumen.showAndWait();
            
            // Limpiar referencias después de mostrar estadísticas
            controlador.limpiarCursoActual();
            
            // Cerrar la ventana actual
            Stage stage = (Stage) panelPrincipal.getScene().getWindow();
            stage.close();
        }
    }

    

    private void actualizarContadorCorrectas() {
        int correctas = controlador.getPreguntasCorrectas();
        labelCorrectas.setText("Correctas: " + correctas);
    }

    private void actualizarContadorIncorrectas() {
        int incorrectas = controlador.getPreguntasIncorrectas();
        labelIncorrectas.setText("Incorrectas: " + incorrectas);
    }

    

    

    private void mostrarResultado(boolean correcto) {
        // Crear un efecto de "shake" si es incorrecto o "bounce" si es correcto
        if (correcto) {
            // Efecto de rebote para respuesta correcta
            FontAwesomeIconView iconoSuccess = new FontAwesomeIconView(FontAwesomeIcon.TROPHY);
            iconoSuccess.setFill(COLOR_CORRECTO);
            iconoSuccess.setSize("60");
            
            VBox resultadoBox = new VBox(15);
            resultadoBox.setAlignment(Pos.CENTER);
            
            Label mensaje = new Label("¡Correcto!");
            mensaje.setFont(Font.font("System", FontWeight.BOLD, 24));
            mensaje.setTextFill(COLOR_CORRECTO);
            
            resultadoBox.getChildren().addAll(iconoSuccess, mensaje);
            
            // Mostrar efecto flotante
            StackPane overlay = new StackPane(resultadoBox);
            overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
            contenedorPreguntas.getChildren().add(overlay);
            
            // Animación para aparecer y desaparecer
            FadeTransition aparecer = new FadeTransition(Duration.millis(300), overlay);
            aparecer.setFromValue(0);
            aparecer.setToValue(1);
            
            FadeTransition desaparecer = new FadeTransition(Duration.millis(300), overlay);
            desaparecer.setFromValue(1);
            desaparecer.setToValue(0);
            desaparecer.setDelay(Duration.millis(1200));
            desaparecer.setOnFinished(e -> contenedorPreguntas.getChildren().remove(overlay));
            
            aparecer.play();
            desaparecer.play();
        } else {
            // Efecto de sacudida para respuesta incorrecta
            FontAwesomeIconView iconoError = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_CIRCLE);
            iconoError.setFill(COLOR_INCORRECTO);
            iconoError.setSize("60");
            
            VBox resultadoBox = new VBox(15);
            resultadoBox.setAlignment(Pos.CENTER);
            
            Label mensaje = new Label("Incorrecto");
            mensaje.setFont(Font.font("System", FontWeight.BOLD, 24));
            mensaje.setTextFill(COLOR_INCORRECTO);
            
            resultadoBox.getChildren().addAll(iconoError, mensaje);
            
            // Mostrar efecto flotante
            StackPane overlay = new StackPane(resultadoBox);
            overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
            contenedorPreguntas.getChildren().add(overlay);
            
            // Animación para aparecer y desaparecer
            FadeTransition aparecer = new FadeTransition(Duration.millis(300), overlay);
            aparecer.setFromValue(0);
            aparecer.setToValue(1);
            
            FadeTransition desaparecer = new FadeTransition(Duration.millis(300), overlay);
            desaparecer.setFromValue(1);
            desaparecer.setToValue(0);
            desaparecer.setDelay(Duration.millis(1200));
            desaparecer.setOnFinished(e -> contenedorPreguntas.getChildren().remove(overlay));
            
            aparecer.play();
            desaparecer.play();
        }
    }


    

    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void actualizarProgreso() {
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

        double progreso = (double) preguntaActualGlobal / totalPreguntas;
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