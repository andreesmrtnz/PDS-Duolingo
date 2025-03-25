# Modelo de Dominio

El modelo de dominio elegido representa los elementos clave de la aplicación de aprendizaje de manera **modular** y **extensible**, asegurando que se diferencien correctamente los conceptos de *curso* como entidad y *curso* como experiencia de usuario.

---

## Usuario y Estadísticas

- **Usuario**  
  Representa a la persona que usa la aplicación.  
  Puede ser:
  - **Creador:** Diseña y genera cursos.
  - **Estudiante:** Realiza y consume cursos.

- **Estadísticas**  
  Cada usuario tiene asociadas estadísticas donde se almacenan datos de uso, tales como:
  - Tiempo total de estudio.
  - Racha de días consecutivos.
  - Porcentaje de aciertos.

Estas estadísticas permiten evaluar y motivar el rendimiento del estudiante.

---

## Curso y CursoEnProgreso

- **Curso:**  
  Es la especificación general de un conjunto de contenidos y ejercicios, creado por un usuario con rol de **Creador**.  
  Características:
  - Contiene *Bloques* de contenido.
  - Incluye diferentes tipos de *Preguntas*.

- **CursoEnProgreso:**  
  Se genera cuando un **Estudiante** inicia un curso.  
  Características:
  - Almacena información sobre el progreso del estudiante.
  - Registra la estrategia de aprendizaje elegida.
  - Guarda estadísticas de desempeño específicas para la experiencia en curso.

---

## Bloque y Pregunta

- **Bloque:**  
  Un curso está compuesto por múltiples bloques, que organizan el contenido en secciones.

- **Pregunta:**  
  Cada bloque contiene una o más preguntas, que pueden ser de distintos tipos, por ejemplo:
  - Opción múltiple.
  - Completar huecos.
  - Flashcards.

La **clase abstracta Pregunta** facilita la incorporación de nuevos formatos sin afectar la estructura base del sistema.

---
4. Estrategia de Aprendizaje

La estrategia de aprendizaje define la forma en que se presentan los ejercicios en un curso. Se manejan los siguientes valores:

SECUENCIAL: Los ejercicios se presentan en un orden fijo.

ALEATORIO: Los ejercicios se presentan de forma aleatoria.

REPETICIÓN_ESPACIADA: Las preguntas más difíciles se repiten con mayor frecuencia.

5. Aspecto Social y Extensibilidad

Relación entre Usuario y Curso: Permite que los usuarios puedan crear y compartir cursos.

Extensibilidad: Gracias al uso de clases abstractas (como en Pregunta), es posible incorporar nuevos tipos de ejercicios sin modificar la arquitectura principal.

Resumen del Modelo

Este modelo aborda la creación, gestión y utilización de cursos, incorpora un seguimiento del progreso y las estadísticas de uso, y permite la configuración flexible de estrategias de aprendizaje, alineándose con los objetivos y requerimientos descritos en el enunciado.

