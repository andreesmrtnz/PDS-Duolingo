# Modelo de Dominio

El modelo de dominio representa los elementos clave de una aplicación de aprendizaje, diseñada para ser **modular**, **extensible** y centrada en la experiencia del usuario. Se distingue claramente entre un *curso* como entidad conceptual y un *curso* como experiencia activa del estudiante, garantizando una estructura flexible para futuras ampliaciones.

---

## Entidades Principales

### Usuario y sus Roles
- **Usuario**  
  Representa a una persona que interactúa con la aplicación. Atributos clave:  
  - `id`, `nombre`, `email`, `password`.  
  - Preferencias: `idiomaPreferido`, `recordatorioDiario`, `actualizacionesProgreso`, `notificacionesNuevosCursos`.  
  Un usuario puede especializarse en dos roles:  
  - **Estudiante**: Consume cursos y realiza ejercicios. Atributo específico: `cursosCompletados`.  
  - **Creador**: Diseña y publica cursos. Atributo específico: `cursosCreados`.  

- **Estadistica**  
  Cada usuario tiene asociada una estadística (opcional) que registra su actividad:  
  - `tiempoTotalUso`, `mejorRacha`, `diasConsecutivos`.  
  - Desempeño: `preguntasCorrectas`, `preguntasIncorrectas`.  
  - Progreso: `cursosTotales`, `cursosCompletados`, `ultimaConexion`.  
  Esto permite evaluar el rendimiento y motivar al estudiante.

---

### Curso y Progreso del Estudiante
- **Curso**  
  Entidad que encapsula el contenido educativo creado por un **Creador**. Características:  
  - Atributos: `id`, `titulo`, `dominio`.  
  - Contiene múltiples **Bloques** que organizan el contenido.  
  - Un curso puede ser consumido por varios **Estudiantes**.  

- **CursoEnProgreso**  
  Representa la experiencia activa de un **Estudiante** en un curso específico. Atributos:  
  - `id`, `fechaInicio`, `fechaUltimaActividad`, `completado`.  
  - Progreso: `bloqueActual`, `preguntaActual`, `contadorRepeticion`.  
  - Desempeño: `preguntasCorrectas`, `preguntasIncorrectas`.  
  Está vinculado a una estrategia de aprendizaje definida por el usuario.

---

### Contenido y Ejercicios
- **Bloque**  
  Unidad de organización dentro de un **Curso**, con atributos:  
  - `id`, `titulo`, `descripcion`.  
  Cada bloque incluye una o más **Preguntas**, formando la estructura del contenido educativo.

- **Pregunta**  
  Clase abstracta que representa un ejercicio dentro de un **Bloque**. Atributos:  
  - `id`, `enunciado`.  
  Tipos específicos:  
  - **PreguntaMultipleChoice**: `opciones`, `respuestaCorrecta`.  
  - **PreguntaFillInBlank**: `respuestaCorrectaTexto`.  
  - **PreguntaFlashCard**: `respuestaFlashCard`.  
  La abstracción permite incorporar nuevos formatos de preguntas sin modificar la estructura base.

---

### Estrategia de Aprendizaje
- **Estrategia**  
  Enumeración que define el enfoque de aprendizaje en un **CursoEnProgreso**:  
  - **Secuencial**: Preguntas en orden predefinido.  
  - **Aleatorio**: Preguntas en orden aleatorio.  
  - **Repeticion_Espaciada**: Prioriza preguntas difíciles con repeticiones frecuentes.  
  Esto permite personalizar la experiencia de aprendizaje según las necesidades del estudiante.

---

## Relaciones y Funcionalidades

- **Creación y Consumo de Contenido**  
  - Un **Creador** puede crear múltiples **Cursos**, mientras que un **Curso** tiene un único creador.  
  - Un **Estudiante** se inscribe en uno o más **Cursos**, y un **Curso** puede tener varios estudiantes inscritos.  
  - Los **Estudiantes** pueden seguir a **Creadores** para descubrir nuevos cursos.

- **Progreso y Evaluación**  
  - Cada **Usuario** puede tener varios **CursosEnProgreso**, que registran su avance en un **Curso** específico.  
  - Las **Estadisticas** proporcionan una visión general del desempeño del usuario en la aplicación.

- **Extensibilidad**  
  La arquitectura basada en clases abstractas (e.g., **Pregunta**) y relaciones bien definidas asegura que el sistema pueda incorporar nuevos tipos de ejercicios y funcionalidades sin afectar la estructura existente.

---

## Resumen

Este modelo de dominio:  
- Diferencia claramente entre la definición estática de un **Curso** y su ejecución dinámica en **CursoEnProgreso**.  
- Gestiona el progreso, estadísticas y preferencias del usuario de forma estructurada.  
- Ofrece flexibilidad para personalizar estrategias de aprendizaje y ampliar el sistema.  

La estructura presentada se alinea con los requisitos del enunciado, proporcionando una base sólida para el desarrollo y evolución futura de la aplicación.

---

## Modelo de Dominio UML
![modelo1](https://github.com/user-attachments/assets/3f558a4d-cccd-40f0-9d87-2fe527df73b7)
