package persistencia;

import jakarta.persistence.*;
import modelo.Usuario;
import java.util.List;

public class UsuarioDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");

    public void registrar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();
        em.close();
    }

    public Usuario buscarPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        Usuario user = null;
        try {
            user = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :mail", Usuario.class)
                     .setParameter("mail", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            // si no existe usuario
        } finally {
            em.close();
        }
        return user;
    }

    public List<Usuario> listarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        em.close();
        return usuarios;
    }

    // Cierra la EMF si quieres, en el shutdown de la app
    public void cerrar() {
        emf.close();
    }
}
