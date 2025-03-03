package modelo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioUsuarios {
    // Instancia única del repositorio (Singleton)
    private static RepositorioUsuarios unicaInstancia;
    
    // Lista interna de usuarios
    private List<Usuario> usuarios;

    // Constructor privado que inicializa la lista
    private RepositorioUsuarios() {
        usuarios = new ArrayList<>();
    }
    
    // Método estático para obtener la única instancia del repositorio
    public static synchronized RepositorioUsuarios getUnicaInstancia() {
        if (unicaInstancia == null) {
            unicaInstancia = new RepositorioUsuarios();
        }
        return unicaInstancia;
    }
    
    // Busca un usuario por su identificador
    public Usuario findById(Long id) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Busca un usuario por su email (caso-insensible)
    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    // Guarda un nuevo usuario en el repositorio
    public void save(Usuario usuario) {
        // Opcional: comprobar si el usuario ya existe
        if (buscarPorEmail(usuario.getEmail()) == null) {
            usuarios.add(usuario);
        }
    }
    
    // Devuelve la lista completa de usuarios
    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
