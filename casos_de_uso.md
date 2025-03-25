# Desarrollo de Casos de Uso

A continuación se presenta el detalle de los casos de uso propuestos, con sus respectivos actores, descripciones, precondiciones, flujos principales, flujos alternativos y postcondiciones cuando corresponda.

---

## 1. Iniciar Sesión
- **Actor:** Usuario (Estudiante o Creador)
- **Descripción:**  
  El usuario ya registrado introduce sus credenciales para acceder a la aplicación. Se valida su identidad y se inicia una sesión segura.
- **Precondiciones:**  
  - El usuario debe estar registrado en el sistema.
- **Flujo Principal:**
  1. El usuario accede a la pantalla de inicio de sesión.
  2. El usuario introduce su correo electrónico y contraseña.
  3. El sistema valida las credenciales.
  4. Si la validación es exitosa, se inicia una sesión y se redirige al usuario a su perfil.
- **Flujo Alternativo:**
  - **3a.** Si las credenciales son incorrectas, el sistema muestra un mensaje de error e invita a reintentar.

---

## 2. Registrar Usuario Estudiante
- **Actor:** Usuario Estudiante
- **Descripción:**  
  Permite a un usuario interesado en consumir cursos registrarse en la plataforma como estudiante. Se recopilan datos básicos y se asigna un rol con permisos limitados a consumir contenido.
- **Precondiciones:**  
  - El sistema debe permitir registros nuevos.
- **Flujo Principal:**
  1. El usuario accede al formulario de registro.
  2. Introduce su nombre, correo electrónico y contraseña.
  3. El sistema valida los datos y crea la cuenta con el rol de estudiante.
- **Flujo Alternativo:**
  - **2a.** Si el correo ya está registrado, el sistema muestra un mensaje de error.

---

## 3. Registrar Usuario Creador
- **Actor:** Usuario Creador
- **Descripción:**  
  Permite a un usuario registrarse con un perfil de creador, lo que le habilita la creación e importación de cursos.
- **Precondiciones:**  
  - El usuario debe proporcionar información válida.
- **Flujo Principal:**
  1. El usuario accede al formulario de registro.
  2. Introduce su nombre, correo electrónico y contraseña.
  3. El sistema valida los datos y crea la cuenta con el rol de creador.
- **Flujo Alternativo:**
  - **2a.** Si el correo ya está registrado, el sistema muestra un mensaje de error.

---

## 4. Crear un Curso (añadido por nosotros)
- **Actor:** Usuario Creador
- **Descripción:**  
  El creador de cursos genera un nuevo curso desde la aplicación, definiendo su estructura, título, descripción, bloques de contenido y los diferentes tipos de ejercicios (por ejemplo, opción múltiple, completar huecos, traducción, flashcards, etc.). El curso se guarda en formato JSON/YAML para permitir futuras ediciones y publicación.
- **Precondiciones:**  
  - El usuario debe tener el rol de creador.
  - Debe haber un mecanismo para definir y almacenar cursos.
- **Flujo Principal:**
  1. El usuario accede a la opción de crear cursos.
  2. Introduce los datos del curso (título, descripción, estructura).
  3. Agrega bloques de contenido y ejercicios.
  4. El curso queda disponible para edición y publicación.
- **Flujo Alternativo:**
  - **4a.** Si el curso ya existe, el sistema rechaza el curso.

---
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
