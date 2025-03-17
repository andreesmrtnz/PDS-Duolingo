package modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Enumeración para los tipos de pregunta
    public enum TipoPregunta {
        SELECCION_MULTIPLE,
        EMPAREJAMIENTO,
        ORDENAR_PALABRAS,
        COMPLETAR
    }

    // Atributos de la pregunta
    private String enunciado;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_opciones", 
                   joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "opcion")
    private List<String> opciones = new ArrayList<>();
    
    private int respuestaCorrecta;
    
    @Enumerated(EnumType.STRING)
    private TipoPregunta tipo;

    // Relación con Bloque
    @ManyToOne
    @JoinColumn(name = "bloque_id")
    private Bloque bloque;

    // Atributos para almacenar las respuestas del usuario
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_seleccion_usuario", 
                   joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "seleccion")
    private List<Integer> seleccionUsuario = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_emparejamientos", 
                   joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "emparejamiento")
    private List<Integer> seleccionesEmparejamiento = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pregunta_orden_seleccionado", 
                   joinColumns = @JoinColumn(name = "pregunta_id"))
    @Column(name = "palabra")
    private List<String> ordenSeleccionado = new ArrayList<>();

    // Constructor sin argumentos (obligatorio para JPA)
    public Pregunta() {}

    // Constructor parametrizado
    public Pregunta(String enunciado, List<String> opciones, int respuestaCorrecta, TipoPregunta tipo) {
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.tipo = tipo;
    }
    
    // Constructor que acepta array de strings para opciones (para compatibilidad)
    public Pregunta(String enunciado, String[] opcionesArray, int respuestaCorrecta, TipoPregunta tipo) {
        this.enunciado = enunciado;
        this.opciones = new ArrayList<>();
        if (opcionesArray != null) {
            for (String opcion : opcionesArray) {
                this.opciones.add(opcion);
            }
        }
        this.respuestaCorrecta = respuestaCorrecta;
        this.tipo = tipo;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    // Métodos para mantener compatibilidad con el código anterior
    public String[] getOpciones() {
        return opciones.toArray(new String[0]);
    }

    public void setOpciones(String[] opciones) {
        this.opciones.clear();
        if (opciones != null) {
            for (String opcion : opciones) {
                this.opciones.add(opcion);
            }
        }
    }
    
    // Métodos nuevos para trabajar con la lista
    public List<String> getOpcionesList() {
        return opciones;
    }
    
    public void setOpcionesList(List<String> opciones) {
        this.opciones = opciones;
    }

    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(int respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public TipoPregunta getTipo() {
        return tipo;
    }

    public void setTipo(TipoPregunta tipo) {
        this.tipo = tipo;
    }
    
    public Bloque getBloque() {
        return bloque;
    }
    
    public void setBloque(Bloque bloque) {
        this.bloque = bloque;
    }

    // Métodos para mantener compatibilidad con el código anterior
    public int[] getSeleccionUsuario() {
        if (seleccionUsuario == null) return null;
        return seleccionUsuario.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setSeleccionUsuario(int[] seleccionUsuarioArray) {
        this.seleccionUsuario.clear();
        if (seleccionUsuarioArray != null) {
            for (int seleccion : seleccionUsuarioArray) {
                this.seleccionUsuario.add(seleccion);
            }
        }
    }

    // Métodos nuevos para trabajar con la lista
    public List<Integer> getSeleccionUsuarioList() {
        return seleccionUsuario;
    }
    
    public void setSeleccionUsuarioList(List<Integer> seleccionUsuario) {
        this.seleccionUsuario = seleccionUsuario;
    }

    // Métodos para mantener compatibilidad con el código anterior
    public int[] getSeleccionesEmparejamiento() {
        if (seleccionesEmparejamiento == null) return null;
        return seleccionesEmparejamiento.stream().mapToInt(Integer::intValue).toArray();
    }

    public void setSeleccionesEmparejamiento(int[] seleccionesEmparejamientoArray) {
        this.seleccionesEmparejamiento.clear();
        if (seleccionesEmparejamientoArray != null) {
            for (int seleccion : seleccionesEmparejamientoArray) {
                this.seleccionesEmparejamiento.add(seleccion);
            }
        }
    }

    // Métodos nuevos para trabajar con la lista
    public List<Integer> getSeleccionesEmparejamientoList() {
        return seleccionesEmparejamiento;
    }
    
    public void setSeleccionesEmparejamientoList(List<Integer> seleccionesEmparejamiento) {
        this.seleccionesEmparejamiento = seleccionesEmparejamiento;
    }

    public List<String> getOrdenSeleccionado() {
        return ordenSeleccionado;
    }

    public void setOrdenSeleccionado(List<String> ordenSeleccionado) {
        this.ordenSeleccionado = ordenSeleccionado;
    }
}