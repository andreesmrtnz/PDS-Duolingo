# 🧠 PDS-Duolingo
**Aplicación de Aprendizaje Interactivo**

## 📚 Descripción del Proyecto

**PDS-Duolingo** es una aplicación de aprendizaje interactivo diseñada para permitir a los usuarios crear, compartir y realizar cursos con ejercicios personalizados. Los usuarios pueden registrarse como **Estudiantes** (para consumir cursos) o **Creadores** (para diseñar e importar cursos). La aplicación ofrece estrategias de aprendizaje configurables (secuencial, aleatorio o repetición espaciada), seguimiento de progreso y gestión de perfiles.

### 🎯 Objetivo
El objetivo principal es proporcionar una plataforma flexible y extensible que facilite la creación y el aprendizaje de cursos interactivos, con un enfoque en la personalización y la escalabilidad para incorporar nuevos tipos de ejercicios y funcionalidades en el futuro.

### 🛠️ Funcionalidades Implementadas
- **Autenticación y Registro**: Los usuarios pueden registrarse como Estudiantes o Creadores e iniciar sesión de forma segura.
- **Gestión de Cursos**:
  - Los Creadores pueden importar cursos desde archivos JSON/YAML, con opciones para sobreescribir o crear copias de cursos existentes.
  - Los Estudiantes pueden inscribirse manualmente en cursos o automáticamente al seguir a un Creador.
- **Aprendizaje Personalizado**:
  - Selección de estrategias de aprendizaje (secuencial, aleatorio, repetición espaciada) al iniciar un curso.
  - Progreso guardado para retomar cursos desde el último punto.
- **Gestión de Perfil**: Los usuarios pueden modificar sus datos personales y preferencias.
- **Funcionalidad Extra**:
  - **Seguir a un Creador**: Los Estudiantes pueden seguir a un Creador, lo que los inscribe automáticamente en todos sus cursos actuales y futuros. Esta funcionalidad fue añadida por el equipo y no estaba en el enunciado original.

---

## 👨‍🏫 Profesor Responsable

* **Antonio López Martínez Carrasco**

## 👥 Integrantes del Grupo

* Andrés Martínez Lorca
* Mohamed Benamrouche Zidi
* Pedro Arias Montes

---

## 🗂️ Estructura del Repositorio

* [`casos_de_uso.md`](requisitos/casos_de_uso.md) → Descripción detallada de los casos de uso.
* [`modelo_de_dominio.md`](diseño/modelo_de_dominio.md) → Estructura y componentes del modelo de dominio.
* [`manualUsuario.md`](documentacion/manualUsuario.md) → Memoria del proyecto, manual de usuario y descripción de las ventanas.
* [`README.md`](README.md) → Explicación general del proyecto y su estructura.

---

## 🐛 Issues y Navegabilidad

Para facilitar la organización y revisión del proyecto, hemos creado una sección de **[Issues](https://github.com/andreesmrtnz/PDS-Duolingo/issues)** con tareas clave de desarrollo, mejoras pendientes y seguimiento del progreso.

---

## 🚀 Cómo Usar Este Repositorio

### 1. Clonar el repositorio

```bash
git clone https://github.com/andreesmrtnz/PDS-Duolingo.git
```

### 2. Requisitos previos

- **Java**: Asegúrate de tener instalado Java (versión 17 o superior recomendada).
- **JavaFX**: La aplicación utiliza **JavaFX** para la interfaz gráfica. Descarga e instala JavaFX SDK (versión 17.0.14 recomendada) desde [este enlace](https://gluonhq.com/products/javafx/) o asegúrate de tenerlo en la siguiente ruta:
  ```
  C:\openjfx-17.0.14_windows-x64_bin-sdk\javafx-sdk-17.0.14
  ```
- **IDE**: Un entorno de desarrollo como IntelliJ IDEA o Eclipse con soporte para JavaFX.

### 3. Configuración para ejecutar la aplicación

Para lanzar la aplicación, utiliza la clase principal `VentanaLoginRegister`. Configura la ejecución con los siguientes parámetros en tu IDE:

- **Run Configuration**:
  ```
  --module-path "C:\openjfx-17.0.14_windows-x64_bin-sdk\javafx-sdk-17.0.14\lib" --add-modules javafx.controls,javafx.fxml
  ```
- Asegúrate de que la ruta al SDK de JavaFX sea correcta según tu instalación.

### 4. Pasos para ejecutar

1. Importa el proyecto en tu IDE.
2. Configura el módulo de JavaFX como se indica arriba.
3. Ejecuta la clase `VentanaLoginRegister` como aplicación Java.
4. La aplicación iniciará mostrando la ventana de login/registro.

---

## 🛠️ Tecnologías Utilizadas

- **Java**: Lenguaje principal para la lógica de la aplicación.
- **JavaFX**: Utilizado para la interfaz gráfica, proporcionando una experiencia de usuario moderna e interactiva.
- **JSON/YAML**: Formatos soportados para la importación de cursos.

---
