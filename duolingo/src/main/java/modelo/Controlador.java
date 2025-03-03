package modelo;

public class Controlador {
    // Instancia única (singleton)
    private static Controlador instancia;
    private RepositorioUsuarios repoUsuarios;
    
    // Constructor privado para evitar instanciación directa
    private Controlador() {
        // Suponemos que RepositorioUsuarios también es singleton
        this.repoUsuarios = RepositorioUsuarios.getUnicaInstancia();
    }
    
    // Método estático para obtener la única instancia (singleton)
    public static synchronized Controlador getInstancia() {
        if (instancia == null) {
            instancia = new Controlador();
        }
        return instancia;
    }
    
    // Método para realizar login (agregamos validación de password)
    public Usuario login(String email, String password) {
        Usuario usuario = repoUsuarios.buscarPorEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        return null;
    }
    
    // Método para registrar un usuario; retorna true si se registró correctamente
    public boolean registrar(Usuario usuario) {
        if (repoUsuarios.buscarPorEmail(usuario.getEmail()) != null) {
            // El usuario ya existe
            return false;
        }
        repoUsuarios.save(usuario);
        return true;
    }
    
    // Método para iniciar un curso
    public void iniciarCurso(Curso curso) {
        // Aquí se podría invocar lógica adicional
        System.out.println("Iniciando curso: " + curso.getTitulo());
    }
    
    // Acceso al repositorio (si se requiere)
    public RepositorioUsuarios getRepoUsuarios() {
        return repoUsuarios;
    }
}
