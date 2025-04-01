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
## Estrategia de Aprendizaje

La aplicación permite configurar diferentes estrategias de aprendizaje mediante una enumeración llamada **EstrategiaAprendizaje**, que incluye opciones como:
- **Secuencial:** Los ejercicios se presentan en orden predefinido.
- **Aleatoria:** Los ejercicios se muestran en orden aleatorio.
- **Repetición Espaciada:** Se priorizan los ejercicios más difíciles, repitiéndolos con mayor frecuencia.

Esto permite a los estudiantes adaptar su experiencia de aprendizaje según sus preferencias.

---

## Compartición y Ampliación de Cursos

- **Compartición:**  
  La relación entre **Usuario** y **Curso** permite que los usuarios no solo consuman contenido, sino que también lo generen y compartan con otros.

- **Ampliación:**  
  La arquitectura basada en clases abstractas y modularidad garantiza que el sistema pueda ampliarse con nuevos tipos de ejercicios y funcionalidades sin afectar la estructura existente.

---


## Resumen

Este modelo aborda:
- La diferenciación entre la **definición** y la **ejecución** de un curso.
- La gestión del progreso y las estadísticas del usuario.
- La configuración flexible de estrategias de aprendizaje.

Todo ello se alinea con los requisitos descritos en el enunciado, ofreciendo una base sólida para el desarrollo y evolución futura de la aplicación.

---

# Modelo de dominio UML
![modelo-v2](https://github.com/user-attachments/assets/2f668b27-db30-49cf-92b6-c54244cf7f3f)

