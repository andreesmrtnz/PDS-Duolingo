package modelo;

public class Controlador {
    private RepositorioUsuarios repoUsuarios;
    
    public Controlador(RepositorioUsuarios repoUsuarios) {
        this.repoUsuarios = repoUsuarios;
    }
    
    public Usuario login(String email, String password) {
        return repoUsuarios.getUsuarios().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    
    public void registrar(Usuario usuario) {
        repoUsuarios.save(usuario);
    }
    
    public void iniciarCurso(Curso curso) {
        System.out.println("Iniciando curso: " + curso.getTitulo());
    }

	public RepositorioUsuarios getRepoUsuarios() {
		return repoUsuarios;
	}
    
}
