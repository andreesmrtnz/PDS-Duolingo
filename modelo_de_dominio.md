Modelo de Dominio

El modelo de dominio elegido se basa en representar los elementos clave de la aplicación de aprendizaje de manera modular y extensible, cumpliendo con los requerimientos del enunciado. A continuación, se ofrece una breve explicación de cada uno de los componentes:

Componentes del Modelo de Dominio

1. ControladorAplicacion y RepositorioUsuarios

Estos elementos conforman la capa de control y persistencia:

ControladorAplicacion: Se encarga de la lógica central, como iniciar sesión, registrar usuarios o arrancar un curso.

RepositorioUsuarios: Permite acceder y almacenar la información de los usuarios.

2. Usuario y Estadísticas

Usuario: Representa a la persona que usa la aplicación.

Estadísticas: Almacena datos de uso como:

Tiempo total de uso.

Racha de aprendizaje (días consecutivos de uso).

Porcentaje de respuestas correctas.

Evolución en distintos tipos de ejercicios.

3. Curso, Bloque y Pregunta

Curso: Es el contenedor de los contenidos y ejercicios.

Bloque: Organiza el contenido en secciones dentro de un curso.

Pregunta: Cada bloque contiene una o más preguntas.

La clase abstracta Pregunta permite definir distintos tipos de ejercicios (como test, completar huecos o flashcards), facilitando la extensión del sistema sin modificar la arquitectura básica.

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
![modelo](https://github.com/user-attachments/assets/d84d53a4-859a-4119-8dc0-1af989faad00)
