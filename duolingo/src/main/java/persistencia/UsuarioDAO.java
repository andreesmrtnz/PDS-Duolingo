package persistencia;

import jakarta.persistence.*;
import modelo.Usuario;
import java.util.List;

public class UsuarioDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");

    public void registrar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Si el usuario ya existe, lo actualizamos en lugar de crear uno nuevo
            if (usuario.getId() != null) {
                em.merge(usuario);
            } else {
                em.persist(usuario);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Relanzar la excepción para manejo superior
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Usuario usuario = null;
        try {
            usuario = em.find(Usuario.class, id);
        } finally {
            em.close();
        }
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        Usuario usuario = null;
        try {
            usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :mail", Usuario.class)
                     .setParameter("mail", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            // No se encontró usuario con ese email, retornará null
        } finally {
            em.close();
        }
        return usuario;
    }

    public List<Usuario> listarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Usuario> usuarios = null;
        try {
            usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        } finally {
            em.close();
        }
        return usuarios;
    }

    // Método para actualizar un usuario existente
    public void actualizar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Método para eliminar un usuario
    public void eliminar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(usuario)) {
                usuario = em.merge(usuario);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Cierra la EMF cuando la aplicación se cierra
    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}