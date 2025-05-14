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
  Permite a un usuario interesado en consumir cursos registrarse en la plataforma como estudiante. Se recopilan datos básicos, se selecciona el rol de estudiante y se asigna un rol con permisos limitados a consumir contenido.
- **Precondiciones:**  
  - El sistema debe permitir registros nuevos.
- **Flujo Principal:**
  1. El usuario accede al formulario de registro.
  2. Introduce su nombre, correo electrónico, contraseña y selecciona el rol de estudiante.
  3. El sistema valida los datos y crea la cuenta con el rol de estudiante.
- **Flujo Alternativo:**
  - **2a.** Si el correo ya está registrado, el sistema muestra un mensaje de error.

---

## 3. Registrar Usuario Creador
- **Actor:** Usuario Creador
- **Descripción:**  
  Permite a un usuario registrarse con un perfil de creador, lo que le habilita la creación e importación de cursos. Se recopilan datos básicos y se selecciona el rol de creador.
- **Precondiciones:**  
  - El usuario debe proporcionar información válida.
- **Flujo Principal:**
  1. El usuario accede al formulario de registro.
  2. Introduce su nombre, correo electrónico, contraseña y selecciona el rol de creador.
  3. El sistema valida los datos y crea la cuenta con el rol de creador.
- **Flujo Alternativo:**
  - **2a.** Si el correo ya está registrado, el sistema muestra un mensaje de error.

---

## 4. Seguir a un Creador (añadido por nosotros)
- **Actor:** Usuario Estudiante
- **Descripción:**  
  El estudiante puede seguir a un creador de cursos. Al seguir a un creador, el estudiante se inscribe automáticamente en todos los cursos actuales del creador y en cualquier curso futuro que el creador publique. Esta funcionalidad no estaba incluida en el enunciado original y fue añadida por nosotros.
- **Precondiciones:**  
  - El usuario debe tener el rol de estudiante y estar autenticado.
  - El creador debe existir en el sistema.
- **Flujo Principal:**
  1. El usuario accede al perfil del creador.
  2. Selecciona la opción de "Seguir" al creador.
  3. El sistema registra al estudiante como seguidor del creador.
  4. El sistema inscribe automáticamente al estudiante en todos los cursos actuales del creador y configura la inscripción automática para cursos futuros.
- **Flujo Alternativo:**
  - **2a.** Si el estudiante ya sigue al creador, el sistema muestra un mensaje indicando que ya está suscrito.

---

## 5. Importar Curso
- **Actor:** Usuario Creador
- **Descripción:**  
  El creador puede cargar un curso desde un fichero externo (JSON/YAML). La aplicación verifica la estructura del archivo y, si ya existe un curso con el mismo identificador, ofrece opciones para sobreescribirlo o crear una copia. El curso importado se añade automáticamente a los cursos inscritos de los estudiantes que siguen al creador.
- **Precondiciones:**  
  - El usuario debe tener el rol de creador.
  - El archivo debe estar en el formato correcto.
- **Flujo Principal:**
  1. El usuario accede a la opción de importar curso.
  2. Selecciona un archivo JSON/YAML.
  3. El sistema valida la estructura del archivo.
  4. Si el curso no existe, se importa como nuevo y se añade automáticamente a los cursos inscritos de los estudiantes que siguen al creador.
  5. Si el curso ya existe, se ofrece la opción de sobreescribirlo o crear una copia. Si se sobreescribe o se crea una copia, la versión final se añade a los cursos inscritos de los estudiantes que siguen al creador.
- **Flujo Alternativo:**
  - **3a.** Si el archivo es inválido, el sistema muestra un mensaje de error.

---

## 6. Inscribirse a un Curso
- **Actor:** Usuario Estudiante
- **Descripción:**  
  El estudiante accede a una lista de cursos disponibles, selecciona uno y se inscribe en él. Una vez inscrito, el curso aparece en su lista de cursos en progreso como listo para iniciar.
- **Precondiciones:**  
  - El usuario debe estar registrado y autenticado.
  - Debe haber cursos disponibles en el sistema.
- **Flujo Principal:**
  1. El usuario accede a la lista de cursos disponibles.
  2. El usuario selecciona un curso y confirma su inscripción.
  3. El sistema registra la inscripción y añade el curso a la lista de cursos en progreso del usuario, marcado como listo para iniciar.
- **Flujo Alternativo:**
  - **2a.** Si no hay cursos disponibles, el sistema muestra un mensaje indicando que no hay cursos.

---

## 7. Empezar Curso
- **Actor:** Usuario Estudiante
- **Descripción:**  
  El estudiante inicia un curso al que se ha inscrito previamente. Si el curso se inicia desde cero, el sistema solicita la selección de una estrategia de aprendizaje. If the course already has progress, it resumes with the saved learning strategy.
- **Precondiciones:**  
  - El usuario debe estar registrado y autenticado.
  - El usuario debe estar inscrito en el curso.
  - El curso debe estar disponible en la biblioteca del usuario.
- **Flujo Principal:**
  1. El usuario selecciona un curso de su lista de cursos en progreso.
  2. El sistema verifica si el curso tiene progreso previo.
  3. Si el curso se inicia desde cero, el sistema solicita al usuario que seleccione una estrategia de aprendizaje.
  4. Si el curso tiene progreso, se reanuda con la estrategia de aprendizaje guardada.
  5. El sistema carga la primera unidad o bloque de contenido (o el punto donde se quedó).
  6. El usuario comienza a interactuar con los ejercicios o tarjetas de aprendizaje.
- **Flujo Alternativo:**
  - **2a.** Si el usuario no está inscrito en el curso, el sistema muestra un mensaje de error.
  - **2b.** Si el curso no está disponible, el sistema muestra un mensaje de error.

---

## 8. Configurar Estrategia de Aprendizaje
- **Actor:** Usuario Estudiante
- **Descripción:**  
  Cuando un curso se inicia desde cero, el estudiante debe seleccionar una estrategia de aprendizaje (por ejemplo, secuencial, aleatorio o repetición espaciada). La estrategia seleccionada se guarda para ese curso en progreso y determina el orden y la frecuencia de los ejercicios presentados.
- **Precondiciones:**  
  - El usuario debe haber iniciado sesión en la aplicación.
  - El sistema debe contar con una lista de estrategias de aprendizaje predefinidas.
  - El curso debe iniciarse desde cero.
- **Flujo Principal:**
  1. El usuario inicia un curso desde cero.
  2. El sistema muestra una lista de estrategias de aprendizaje disponibles:
     - **Secuencial:** Los ejercicios se presentan en el orden definido por el curso.
     - **Aleatorio:** Los ejercicios aparecen en orden aleatorio.
     - **Repetición Espaciada:** Los ejercicios más difíciles se repiten con mayor frecuencia.
  3. El usuario selecciona la estrategia de aprendizaje deseada.
  4. El sistema guarda la configuración y la asocia al curso en progreso del usuario.
  5. El usuario comienza el curso utilizando la estrategia seleccionada.
- **Flujo Alternativo:**
  - **3a.** Si el usuario no selecciona una estrategia, el sistema asigna una por defecto (por ejemplo, secuencial).

---

## 9. Gestionar Perfil de Usuario
- **Actor:** Usuario (Estudiante y Creador)
- **Descripción:**  
  Permite a cualquier usuario modificar sus datos personales (incluida la contraseña), preferencias de aprendizaje y gestionar la configuración de su cuenta.
- **Precondiciones:**  
  - El usuario debe haber iniciado sesión.
- **Flujo Principal:**
  1. El usuario accede a la configuración de su perfil.
  2. Puede modificar datos como nombre, contraseña y preferencias.
  3. El sistema valida y guarda los cambios.
- **Flujo Alternativo:**
  - **2a.** Si los datos ingresados son inválidos (por ejemplo, nueva contraseña no cumple requisitos), el sistema muestra un mensaje de error.
