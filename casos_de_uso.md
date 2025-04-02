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

## 8. Configurar Estrategia de Aprendizaje
- **Actor:** Usuario Estudiante
- **Descripción:**  
  Antes o durante el curso, el estudiante puede elegir entre diferentes estrategias de aprendizaje (por ejemplo, secuencial, aleatorio o repetición espaciada) que determinan el orden y la frecuencia de los ejercicios presentados.
- **Precondiciones:**  
  - El usuario debe haber iniciado sesión en la aplicación.
  - El sistema debe contar con una lista de estrategias de aprendizaje predefinidas.
- **Flujo Principal:**
  1. El usuario accede a la configuración de su perfil o al menú de ajustes del curso.
  2. El sistema muestra una lista de estrategias de aprendizaje disponibles:
     - **Secuencial:** Los ejercicios se presentan en el orden definido por el curso.
     - **Aleatorio:** Los ejercicios aparecen en orden aleatorio.
     - **Repetición Espaciada:** Los ejercicios más difíciles se repiten con mayor frecuencia.
  3. El usuario selecciona la estrategia de aprendizaje deseada.
  4. El sistema guarda la configuración y la asocia al curso actual del usuario.
  5. El usuario puede continuar con el curso utilizando la estrategia seleccionada.

---

## 9. Gestionar Perfil de Usuario
- **Actor:** Usuario (Estudiante y Creador)
- **Descripción:**  
  Permite a cualquier usuario modificar sus datos personales, preferencias de aprendizaje y gestionar la configuración de su cuenta.
- **Precondiciones:**  
  - El usuario debe haber iniciado sesión.
- **Flujo Principal:**
  1. El usuario accede a la configuración de su perfil.
  2. Puede modificar datos como nombre y preferencias.
  3. El sistema valida y guarda los cambios.
- **Flujo Alternativo:**
  - Si los datos ingresados son inválidos, el sistema muestra un mensaje de error.
