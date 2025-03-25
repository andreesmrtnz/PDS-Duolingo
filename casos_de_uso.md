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
## 5. Importar Curso
- **Actor:** Usuario Creador
- **Descripción:**  
  El creador puede cargar un curso desde un fichero externo (JSON/YAML). La aplicación verifica la estructura del archivo y, si ya existe un curso con el mismo identificador, ofrece opciones para sobreescribirlo o crear una copia.
- **Precondiciones:**  
  - El usuario debe tener el rol de creador.
  - El archivo debe estar en el formato correcto.
- **Flujo Principal:**
  1. El usuario accede a la opción de importar curso.
  2. Selecciona un archivo JSON/YAML.
  3. El sistema valida la estructura del archivo.
  4. Si el curso no existe, se importa como nuevo.
  5. Si el curso ya existe, se ofrece la opción de sobreescribirlo o crear una copia.
- **Flujo Alternativo:**
  - **3a.** Si el archivo es inválido, el sistema muestra un mensaje de error.

---

## 6. Seleccionar Curso
- **Actor:** Usuario Estudiante
- **Descripción:**  
  El estudiante accede a su biblioteca personal y puede buscar o filtrar los cursos disponibles (por nombre, autor, categoría o dificultad). Al seleccionar un curso, se muestra un resumen de su contenido y estadísticas relevantes.
- **Flujo Principal:**
  1. El usuario accede a la aplicación.
  2. El sistema muestra la lista de cursos disponibles.
  3. El usuario selecciona un curso de la lista.
  4. El sistema carga el curso y lo prepara para su ejecución.
- **Flujo Alternativo:**
  - **3b.** No hay cursos disponibles y el usuario no puede seleccionar ninguno.

---

## 7. Empezar Curso
- **Actor:** Usuario Estudiante
- **Descripción:**  
  El estudiante inicia un curso seleccionado. Se carga la primera unidad o bloque de contenido y se establece la estrategia de aprendizaje por defecto o preseleccionada.
- **Precondiciones:**  
  - El usuario debe estar registrado y autenticado.
  - El curso debe estar disponible en la biblioteca del usuario.
- **Flujo Principal:**
  1. El usuario selecciona un curso de su biblioteca.
  2. El sistema carga la primera unidad o bloque de contenido.
  3. Se establece la estrategia de aprendizaje predefinida o preseleccionada.
  4. El usuario comienza a interactuar con los ejercicios o tarjetas de aprendizaje.
- **Flujo Alternativo:**
  - **2a.** Si el curso no está disponible, el sistema muestra un mensaje de error.

---

## 8. Reanudar Curso
- **Actor:** Usuario Estudiante
- **Descripción:**  
  Si el estudiante ha guardado su progreso previamente, puede reanudar el curso desde el último punto de guardado, permitiendo la continuidad en el aprendizaje sin perder avances previos.
- **Precondiciones:**  
  - El usuario debe estar registrado y autenticado.
  - El curso debe haber sido iniciado previamente y tener un progreso guardado.
- **Flujo Principal:**
  1. El usuario accede a su biblioteca de cursos.
  2. Selecciona un curso previamente iniciado.
  3. El sistema carga el último punto de guardado.
  4. El usuario continúa con el curso desde donde lo dejó.

---
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
