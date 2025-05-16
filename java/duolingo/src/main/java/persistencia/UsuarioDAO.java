package persistencia;

import jakarta.persistence.*;
import modelo.Usuario;
import java.util.List;

public class UsuarioDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void registrar(Usuario usuario) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            
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
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        Usuario usuario = null;
        try {
            usuario = em.createQuery(
                "SELECT u FROM Usuario u LEFT JOIN FETCH u.seguidores LEFT JOIN FETCH u.creadoresSeguidos WHERE u.id = :id",
                Usuario.class
            )
            .setParameter("id", id)
            .getSingleResult();
        } catch (NoResultException e) {
            // Retornar null si no se encuentra el usuario
        } finally {
            em.close();
        }
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        EntityManager em = getEntityManager();
        Usuario usuario = null;
        try {
            usuario = em.createQuery(
                "SELECT u FROM Usuario u LEFT JOIN FETCH u.creadoresSeguidos WHERE u.email = :mail",
                Usuario.class
            )
            .setParameter("mail", email)
            .getSingleResult();
        } catch (NoResultException e) {
        } finally {
            em.close();
        }
        return usuario;
    }

    public List<Usuario> listarTodos() {
        EntityManager em = getEntityManager();
        List<Usuario> usuarios = null;
        try {
            usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        } finally {
            em.close();
        }
        return usuarios;
    }

    public void actualizar(Usuario usuario) {
        EntityManager em = getEntityManager();
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

    public void eliminar(Usuario usuario) {
        EntityManager em = getEntityManager();
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

    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public List<Usuario> listarCreadoresConSeguidores() {
        EntityManager em = getEntityManager();
        List<Usuario> creadores = null;
        try {
            creadores = em.createQuery(
                "SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.seguidores WHERE TYPE(u) = Creador",
                Usuario.class
            ).getResultList();
        } finally {
            em.close();
        }
        return creadores;
    }
}