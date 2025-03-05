package prueba;

import modelo.*;

import java.util.List;
import java.util.Scanner;

public class PruebaTerminal {
    private static Controlador controlador = Controlador.getInstancia();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarMenuPrincipal();
    }

    private static void mostrarMenuPrincipal() {
        while (true) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Login");
            System.out.println("3. Cargar curso desde archivo");
            System.out.println("4. Listar cursos disponibles");
            System.out.println("5. Ver detalles de un curso");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    hacerLogin();
                    break;
                case 3:
                    cargarCurso();
                    break;
                case 4:
                    listarCursos();
                    break;
                case 5:
                    verDetallesCurso();
                    break;
                case 6:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private static void registrarUsuario() {
        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();
        System.out.print("Ingrese nombre: ");
        String nombre = scanner.nextLine();

        Usuario nuevoUsuario = new Usuario(nombre, email, password);
        if (controlador.registrar(nuevoUsuario)) {
            System.out.println("Usuario registrado exitosamente");
        } else {
            System.out.println("Error al registrar usuario");
        }
    }

    private static void hacerLogin() {
        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();

        Usuario usuario = controlador.login(email, password);
        if (usuario != null) {
            System.out.println("Login exitoso. Bienvenido: " + usuario.getNombre());
        } else {
            System.out.println("Credenciales inválidas");
        }
    }

    private static void cargarCurso() {
        if (controlador.getUsuarioActual() == null) {
            System.out.println("Debe hacer login primero");
            return;
        }

        System.out.print("Ingrese la ruta completa del archivo del curso: ");
        String ruta = scanner.nextLine();

        if (controlador.cargarCursoDesdeArchivo(ruta)) {
            System.out.println("Curso cargado exitosamente");
        } else {
            System.out.println("Error al cargar el curso");
        }
    }

    private static void listarCursos() {
        List<Curso> cursos = controlador.getCursosDisponibles();
        if (cursos.isEmpty()) {
            System.out.println("No hay cursos disponibles");
            return;
        }

        System.out.println("\n=== CURSOS DISPONIBLES ===");
        for (int i = 0; i < cursos.size(); i++) {
            Curso curso = cursos.get(i);
            System.out.println((i + 1) + ". " + curso.getTitulo() + " - " + curso.getDominio());
        }
    }

    private static void verDetallesCurso() {
        listarCursos();
        System.out.print("Seleccione el número del curso: ");
        int numCurso = scanner.nextInt() - 1;
        scanner.nextLine(); // Limpiar buffer

        List<Curso> cursos = controlador.getCursosDisponibles();
        if (numCurso < 0 || numCurso >= cursos.size()) {
            System.out.println("Selección inválida");
            return;
        }

        Curso curso = cursos.get(numCurso);
        System.out.println("\n=== DETALLES DEL CURSO ===");
        System.out.println("Título: " + curso.getTitulo());
        System.out.println("Dominio: " + curso.getDominio());
        System.out.println("Creador: " + curso.getCreador().getNombre());
        System.out.println("Bloques: " + curso.getBloques().size());

        for (Bloque bloque : curso.getBloques()) {
            System.out.println("\nBloque: " + bloque.getTitulo());
            System.out.println("Descripción: " + bloque.getDescripcion());
            System.out.println("Preguntas: " + bloque.getPreguntas().size());
            for (Pregunta p : bloque.getPreguntas()) {
            	System.out.println("Pregunta: " + p.getEnunciado());
            	System.out.println("Respuestas: " +p.getOpciones());
            }
        }
    }
}