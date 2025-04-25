package modelo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cursos_progreso")
public class CursoEnProgreso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
    
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;
    
    @Column(name = "fecha_ultima_actividad")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltimaActividad;
    
    @Column(name = "completado")
    private boolean completado;
    
    @Column(name = "preguntas_correctas")
    private int preguntasCorrectas;
    
    @Column(name = "preguntas_incorrectas")
    private int preguntasIncorrectas;
    
    @Column(name = "bloque_actual")
    private int bloqueActual;
    
    @Column(name = "pregunta_actual")
    private int preguntaActual;
    
    // Estrategia de aprendizaje seleccionada
    @Enumerated(EnumType.STRING)
    @Column(name = "estrategia")
    private Estrategia estrategia;
    
    // Para la estrategia de repetición espaciada
    @Column(name = "contador_repeticion")
    private int contadorRepeticion;
    
    // Lista de preguntas a repetir (para REPETICION_ESPACIADA)
    @Transient
    private Map<String, Integer> preguntasARepetir = new HashMap<>();
    
    // Mapa para registrar el estado de cada pregunta (contestada correctamente, incorrectamente, o no contestada)
    // Esta información se guardará en una tabla separada o se serializará
    @ElementCollection
    @CollectionTable(
        name = "estado_preguntas",
        joinColumns = @JoinColumn(name = "curso_progreso_id")
    )
    @MapKeyColumn(name = "clave_pregunta")
    @Column(name = "estado")
    private Map<String, Integer> estadoPreguntas = new HashMap<>();
    
    
    // Variables para recordar la posición antes de hacer una repetición
    @Transient
    private int bloqueAnterior = 0;
    
    @Transient
    private int preguntaAnterior = 0;
    
    @Transient
    private boolean enModoRepeticion = false;
    
    // Constructor vacío para JPA
    public CursoEnProgreso() {
        this.fechaInicio = new Date();
        this.fechaUltimaActividad = new Date();
        this.completado = false;
        this.preguntasCorrectas = 0;
        this.preguntasIncorrectas = 0;
        this.bloqueActual = 0;
        this.preguntaActual = 0;
        this.estrategia = Estrategia.SECUENCIAL; // Por defecto es secuencial
        this.contadorRepeticion = 0;
    }
    
    public CursoEnProgreso(Usuario usuario, Curso curso) {
        this();
        this.usuario = usuario;
        this.curso = curso;
    }
    
    public CursoEnProgreso(Usuario usuario, Curso curso, Estrategia estrategia) {
        this(usuario, curso);
        this.estrategia = estrategia;
    }
    
    // Métodos para registrar respuestas
    public void registrarRespuestaCorrecta() {
        this.preguntasCorrectas++;
        this.fechaUltimaActividad = new Date();
        // Registrar en el mapa de estado (1 = correcta)
        String clave = bloqueActual + ":" + preguntaActual;
        estadoPreguntas.put(clave, 1);
        
        // Si es repetición espaciada, eliminamos esta pregunta de las que hay que repetir
        if (estrategia == Estrategia.REPETICION_ESPACIADA) {
            preguntasARepetir.remove(clave);
            
            // Si estábamos en modo repetición, volver a la pregunta donde estábamos
            if (enModoRepeticion) {
                bloqueActual = bloqueAnterior;
                preguntaActual = preguntaAnterior;
                enModoRepeticion = false;
            }
        }
    }
    
    public void registrarRespuestaIncorrecta() {
        this.preguntasIncorrectas++;
        this.fechaUltimaActividad = new Date();
        // Registrar en el mapa de estado (2 = incorrecta)
        String clave = bloqueActual + ":" + preguntaActual;
        estadoPreguntas.put(clave, 2);
        
        // Si es repetición espaciada, añadimos esta pregunta a las que hay que repetir
        if (estrategia == Estrategia.REPETICION_ESPACIADA) {
            preguntasARepetir.put(clave, 3); // Repetir después de 3 preguntas
            
            // Si estábamos en modo repetición, volver a la pregunta donde estábamos
            if (enModoRepeticion) {
                bloqueActual = bloqueAnterior;
                preguntaActual = preguntaAnterior;
                enModoRepeticion = false;
            }
        }
    }
    
    // Método para verificar si la pregunta actual ya fue respondida
    public boolean isPreguntaRespondida() {
        String clave = bloqueActual + ":" + preguntaActual;
        return estadoPreguntas.containsKey(clave);
    }
    
    public int getEstadoPregunta(int bloque, int pregunta) {
        String clave = bloque + ":" + pregunta;
        return estadoPreguntas.getOrDefault(clave, 0); // 0 = no contestada
    }
    
    // Método para avanzar a la siguiente pregunta según la estrategia
    public boolean avanzarPregunta(Curso curso) {
        switch (estrategia) {
            case SECUENCIAL:
                return avanzarPreguntaSecuencial(curso);
            case ALEATORIO:
                return avanzarPreguntaAleatoria(curso);
            case REPETICION_ESPACIADA:
                return avanzarPreguntaRepeticionEspaciada(curso);
            default:
                return avanzarPreguntaSecuencial(curso); // Por defecto
        }
    }
    
    // Implementación para estrategia secuencial
    private boolean avanzarPreguntaSecuencial(Curso curso) {
        System.out.println("avanzando sec");
        int numBloques = curso.getBloques().size();
        
        if (bloqueActual >= numBloques) {
            return false; // No hay más bloques
        }
        
        int numPreguntas = curso.getBloques().get(bloqueActual).getPreguntas().size();
        
        if (preguntaActual + 1 < numPreguntas) {
            // Hay más preguntas en este bloque
            preguntaActual++;
            return true;
        } else if (bloqueActual + 1 < numBloques) {
            // Pasar al siguiente bloque
            bloqueActual++;
            preguntaActual = 0;
            return true;
        } else {
            // Curso completado
            completado = true;
            return false;
        }
    }
    
    // Implementación para estrategia aleatoria
    private boolean avanzarPreguntaAleatoria(Curso curso) {
        System.out.println("avanzando random");
        
        int numBloques = curso.getBloques().size();
        int totalPreguntas = getTotalPreguntas(curso);
        int preguntasRespondidas = preguntasCorrectas + preguntasIncorrectas;
        
        if (preguntasRespondidas >= totalPreguntas) {
            // Todas las preguntas respondidas
            completado = true;
            return false;
        }
        
        // Generar índices aleatorios hasta encontrar una pregunta no respondida
        java.util.Random random = new java.util.Random();
        int intentos = 0;
        int maxIntentos = totalPreguntas * 2; // Para evitar bucles infinitos
        
        while (intentos < maxIntentos) {
            int randomBloque = random.nextInt(numBloques);
            int numPreguntas = curso.getBloques().get(randomBloque).getPreguntas().size();
            if (numPreguntas > 0) {
                int randomPregunta = random.nextInt(numPreguntas);
                
                String clave = randomBloque + ":" + randomPregunta;
                if (!estadoPreguntas.containsKey(clave)) {
                    // Encontramos una pregunta no respondida
                    bloqueActual = randomBloque;
                    preguntaActual = randomPregunta;
                    return true;
                }
            }
            intentos++;
        }
        
        // Si llegamos aquí, no encontramos preguntas sin responder
        completado = true;
        return false;
    }
    
    // Implementación para estrategia de repetición espaciada
    private boolean avanzarPreguntaRepeticionEspaciada(Curso curso) {
        // Incrementar el contador de repetición
        contadorRepeticion++;
        System.out.println("avanzando rep. espaciada, contador: " + contadorRepeticion);
        
        // Si no estamos en modo repetición, guardar la posición actual como punto de retorno
        if (!enModoRepeticion) {
            bloqueAnterior = bloqueActual;
            preguntaAnterior = preguntaActual;
        }
        
        // Verificar si hay alguna pregunta que deba repetirse
        for (Map.Entry<String, Integer> entrada : new HashMap<>(preguntasARepetir).entrySet()) {
            int intervalo = entrada.getValue();
            if (intervalo <= 1) {
                // Esta pregunta debe repetirse ahora
                String[] partes = entrada.getKey().split(":");
                int bloqueRepetir = Integer.parseInt(partes[0]);
                int preguntaRepetir = Integer.parseInt(partes[1]);
                
                System.out.println("Repitiendo pregunta: " + bloqueRepetir + ":" + preguntaRepetir);
                
                // Si no estamos ya en modo repetición, guardar la posición actual
                if (!enModoRepeticion) {
                    bloqueAnterior = bloqueActual;
                    preguntaAnterior = preguntaActual;
                    enModoRepeticion = true;
                }
                
                // Cambiar a la pregunta a repetir
                bloqueActual = bloqueRepetir;
                preguntaActual = preguntaRepetir;
                
                // Actualizar el intervalo para la próxima repetición
                preguntasARepetir.put(entrada.getKey(), 5); // Repetir de nuevo después de 5 preguntas
                return true;
            } else {
                // Decrementar el intervalo
                System.out.println("Decrementando intervalo para " + entrada.getKey() + " de " + intervalo + " a " + (intervalo - 1));
                preguntasARepetir.put(entrada.getKey(), intervalo - 1);
            }
        }
        
        // Si estábamos en modo repetición y no hay más preguntas a repetir,
        // volver a la posición donde estábamos antes de la repetición
        if (enModoRepeticion) {
            System.out.println("Volviendo de repetición a: " + bloqueAnterior + ":" + preguntaAnterior);
            bloqueActual = bloqueAnterior;
            preguntaActual = preguntaAnterior;
            enModoRepeticion = false;
            // Ahora avanzar normalmente desde esa posición
            return avanzarPreguntaSecuencial(curso);
        }
        
        // Si no hay preguntas a repetir, seguir con la secuencia normal
        System.out.println("Continuando secuencia normal desde: " + bloqueActual + ":" + preguntaActual);
        return avanzarPreguntaSecuencial(curso);
    }
    
    public boolean retrocederPregunta(Curso curso) {
        // La funcionalidad de retroceder puede ser igual para todas las estrategias
        // o personalizarse según cada una
        if (preguntaActual > 0) {
            // Retroceder dentro del mismo bloque
            preguntaActual--;
            return true;
        } else if (bloqueActual > 0) {
            // Retroceder al bloque anterior
            bloqueActual--;
            int numPreguntas = curso.getBloques().get(bloqueActual).getPreguntas().size();
            preguntaActual = numPreguntas - 1;
            return true;
        } else {
            // Ya estamos en la primera pregunta
            return false;
        }
    }
    
    // Métodos para calcular estadísticas
    public double getPorcentajeCompletado(Curso curso) {
        int totalPreguntas = getTotalPreguntas(curso);
        int preguntasRespondidas = preguntasCorrectas + preguntasIncorrectas;
        
        if (totalPreguntas == 0) return 0;
        
        return (double) preguntasRespondidas / totalPreguntas;
    }
    
    public int getTotalPreguntas(Curso curso) {
        int total = 0;
        for (Bloque bloque : curso.getBloques()) {
            total += bloque.getPreguntas().size();
        }
        return total;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaUltimaActividad() {
        return fechaUltimaActividad;
    }

    public void setFechaUltimaActividad(Date fechaUltimaActividad) {
        this.fechaUltimaActividad = fechaUltimaActividad;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public int getPreguntasCorrectas() {
        return preguntasCorrectas;
    }

    public void setPreguntasCorrectas(int preguntasCorrectas) {
        this.preguntasCorrectas = preguntasCorrectas;
    }

    public int getPreguntasIncorrectas() {
        return preguntasIncorrectas;
    }

    public void setPreguntasIncorrectas(int preguntasIncorrectas) {
        this.preguntasIncorrectas = preguntasIncorrectas;
    }

    public int getBloqueActual() {
        return bloqueActual;
    }

    public void setBloqueActual(int bloqueActual) {
        this.bloqueActual = bloqueActual;
    }

    public int getPreguntaActual() {
        return preguntaActual;
    }

    public void setPreguntaActual(int preguntaActual) {
        this.preguntaActual = preguntaActual;
    }

    public Map<String, Integer> getEstadoPreguntas() {
        return estadoPreguntas;
    }

    public void setEstadoPreguntas(Map<String, Integer> estadoPreguntas) {
        this.estadoPreguntas = estadoPreguntas;
    }
    
    public Estrategia getEstrategia() {
        return estrategia;
    }
    
    public void setEstrategia(Estrategia estrategia) {
        this.estrategia = estrategia;
    }
    
    public int getContadorRepeticion() {
        return contadorRepeticion;
    }
    
    public void setContadorRepeticion(int contadorRepeticion) {
        this.contadorRepeticion = contadorRepeticion;
    }
    
    public Map<String, Integer> getPreguntasARepetir() {
        return preguntasARepetir;
    }
    
    public void setPreguntasARepetir(Map<String, Integer> preguntasARepetir) {
        this.preguntasARepetir = preguntasARepetir;
    }
    
    public boolean isEnModoRepeticion() {
        return enModoRepeticion;
    }
    
    public void setEnModoRepeticion(boolean enModoRepeticion) {
        this.enModoRepeticion = enModoRepeticion;
    }
}