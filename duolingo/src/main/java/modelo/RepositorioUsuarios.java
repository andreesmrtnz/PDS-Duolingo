package modelo;

import java.util.List;
import persistencia.UsuarioDAO;

public class RepositorioUsuarios {
    // Instancia única del repositorio (Singleton)
    private static RepositorioUsuarios unicaInstancia;
    
    // DAO para la persistencia con JPA
    private UsuarioDAO usuarioDAO;

    // Constructor privado que inicializa el DAO
    private RepositorioUsuarios() {
        usuarioDAO = new UsuarioDAO();
        // Inicializar otras dependencias si es necesario
    }
    
    // Método estático para obtener la única instancia del repositorio
    public static synchronized RepositorioUsuarios getUnicaInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new RepositorioUsuarios();
        }
        return unicaInstancia;
    }
    
    // Busca un usuario por su identificador, delegando al DAO
    public Usuario findById(Long id) {
        return usuarioDAO.buscarPorId(id);
    }
    
    // Busca un usuario por su email, delegando al DAO
    public Usuario buscarPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }
    
    // Guarda un nuevo usuario en la base de datos
    public void save(Usuario usuario) {
        usuarioDAO.registrar(usuario);
    }
    
    // Devuelve la lista completa de usuarios desde la base de datos
    public List<Usuario> getUsuarios() {
        return usuarioDAO.listarTodos();
    }
    
    // Método para cerrar recursos cuando la aplicación se cierre
    public void cerrarRecursos() {
        usuarioDAO.cerrar();
    }
}