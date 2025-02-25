package modelo;


import java.util.List;

public class RepositorioUsuarios {
    private List<Usuario> usuarios;
    
    public Usuario findById(Long id) {
        return getUsuarios().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public void save(Usuario usuario) {
        getUsuarios().add(usuario);
    }

	public List<Usuario> getUsuarios() {
		return usuarios;
	}
}
