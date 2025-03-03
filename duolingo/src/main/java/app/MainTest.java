package app;

import persistencia.UsuarioDAO;
import modelo.Usuario;
import java.util.Date;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Registrar un nuevo usuario
        Usuario usuario = new Usuario("Juan Pérez", "juan@example.com", "password123");
        usuarioDAO.registrar(usuario);
        System.out.println("Usuario registrado con id: " + usuario.getId());

        // Listar todos los usuarios
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        usuarios.forEach(u -> System.out.println(u.getNombre() + " - " + u.getEmail()));

        // Cerrar la fábrica de EntityManager cuando la aplicación finalice
        usuarioDAO.cerrar();
    }
}
