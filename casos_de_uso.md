Casos de Uso

1. Crear un Curso

Actor: Usuario registradoDescripción: El usuario puede crear un nuevo curso definiendo su estructura general, incluyendo bloques de contenido y distintos tipos de ejercicios (preguntas de opción múltiple, completar huecos, traducir, flashcards, etc.). Además, podrá configurar el título del curso, descripción y nivel de dificultad. El curso se guarda en formato JSON/YAML para su posterior uso o modificación.

2. Cargar un Curso

Actor: Usuario registradoDescripción: El usuario puede importar un curso previamente creado desde un archivo JSON/YAML. La aplicación verifica la estructura del archivo antes de importarlo. Si el curso ya existe en la biblioteca del usuario, se le ofrece la opción de sobrescribir o crear una copia.

3. Seleccionar un Curso

Actor: Usuario registradoDescripción: El usuario puede visualizar una lista de cursos disponibles en su biblioteca personal, filtrarlos por nombre, categoría o dificultad y seleccionar uno mostrando un resumen de lo que ofrece y su estructura.

4. Realizar un Curso

Actor: Usuario registradoDescripción: El usuario interactúa con los ejercicios del curso siguiendo una estrategia de aprendizaje predefinida. Se registra cada respuesta y se proporciona retroalimentación inmediata. Las respuestas correctas e incorrectas se guardan con el objetivo de actualizar las estadísticas de rendimiento.

5. Guardar el Progreso

Actor: Usuario registradoDescripción: El usuario puede guardar su progreso en cualquier momento, almacenando el ejercicio actual, la estrategia de aprendizaje y las respuestas dadas. La próxima vez que inicie sesión, podrá continuar desde donde lo dejó.

6. Configurar Estrategia de Aprendizaje

Actor: Usuario registradoDescripción: El usuario puede elegir entre distintas estrategias de aprendizaje:

Secuencial: Los ejercicios se presentan en un orden fijo.

Aleatorio: Los ejercicios se presentan de forma aleatoria.

Repetición Espaciada: Las preguntas más difíciles se repiten con mayor frecuencia.

7. Registrar Estadísticas

Actor: Usuario registradoDescripción: Se almacenan datos del usuario para evaluar su progreso, incluyendo:

Tiempo total de uso.

Racha de aprendizaje (días consecutivos de uso).

Porcentaje de respuestas correctas.

Evolución en distintos tipos de ejercicios.

8. Compartir un Curso

Actor: Usuario registradoDescripción: Un usuario puede compartir un curso exportándolo en formato JSON/YAML, generando un enlace de descarga o enviándolo por correo. Otros usuarios pueden importarlo a su biblioteca personal.

9. Gestionar Usuarios

Actor: UsuarioDescripción: Los usuarios pueden registrarse, iniciar sesión y gestionar su perfil. Se permite modificar datos personales y establecer preferencias de aprendizaje, como idioma o nivel de dificultad.

10. Añadir Nuevos Tipos de Ejercicios

Actor: Administrador o usuario avanzadoDescripción: La aplicación permite ampliar la variedad de ejercicios sin afectar los cursos existentes. Los desarrolladores pueden definir nuevos formatos de preguntas y modificar la interfaz para soportarlos. Los usuarios también pueden utilizar estos nuevos ejercicios al crear cursos.

Ejemplo de Casos de Uso Detallados

Caso de Uso 1: Seleccionar un Curso

Actor: Usuario registradoDescripción: El usuario selecciona un curso disponible en la aplicación para comenzar a realizar los ejercicios.

Flujo Principal:

El usuario accede a la aplicación.

El sistema muestra la lista de cursos disponibles.

El usuario selecciona un curso de la lista.

El sistema carga el curso y lo prepara para su ejecución.

Se muestra la primera actividad según la estrategia de aprendizaje configurada.

Flujo Alternativo:

3a. Si el usuario no ha iniciado sesión, se le solicita que lo haga antes de continuar.

4a. Si el curso seleccionado no está disponible, se muestra un mensaje de error.

Caso de Uso 2: Realizar un Curso

Actor: Usuario registradoDescripción: El usuario interactúa con los ejercicios del curso según la estrategia de aprendizaje seleccionada.

Flujo Principal:

El usuario ha seleccionado un curso.

El sistema presenta la primera actividad según la estrategia de aprendizaje.

El usuario responde a la actividad.

El sistema evalúa la respuesta y proporciona retroalimentación.

Se registra el avance del usuario.

Se presenta la siguiente actividad según la estrategia configurada.

El proceso se repite hasta completar el curso.

Flujo Alternativo:

3a. Si el usuario abandona el curso, el sistema guarda el progreso para su reanudación posterior.

4a. Si el usuario responde incorrectamente, el sistema puede ofrecer pistas o repetir la pregunta más adelante según la estrategia de aprendizaje.
