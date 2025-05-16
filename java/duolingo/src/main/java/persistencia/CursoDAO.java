package persistencia;

import jakarta.persistence.*;
import modelo.Curso;
import modelo.Usuario;
import java.util.List;

public class CursoDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void guardar(Curso curso) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Si el curso ya existe, lo actualizamos en lugar de crear uno nuevo
            if (curso.getId() != null) {
                em.merge(curso);
            } else {
                em.persist(curso);
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

    public Curso buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Curso curso = null;
        try {
            curso = em.find(Curso.class, id);
        } finally {
            em.close();
        }
        return curso;
    }

    public List<Curso> listarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Curso> cursos = null;
        try {
            cursos = em.createQuery("SELECT c FROM Curso c", Curso.class).getResultList();
        } finally {
            em.close();
        }
        return cursos;
    }
    
    public List<Curso> buscarPorDominio(String dominio) {
        EntityManager em = emf.createEntityManager();
        List<Curso> cursos = null;
        try {
            cursos = em.createQuery("SELECT c FROM Curso c WHERE c.dominio = :dominio", Curso.class)
                     .setParameter("dominio", dominio)
                     .getResultList();
        } finally {
            em.close();
        }
        return cursos;
    }
    
    public List<Curso> buscarPorCreador(Usuario creador) {
        EntityManager em = emf.createEntityManager();
        List<Curso> cursos = null;
        try {
            cursos = em.createQuery("SELECT c FROM Curso c WHERE c.creador.id = :creadorId", Curso.class)
                     .setParameter("creadorId", creador.getId())
                     .getResultList();
        } finally {
            em.close();
        }
        return cursos;
    }

    public void actualizar(Curso curso) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(curso);
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

    public void eliminar(Curso curso) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(curso)) {
                curso = em.merge(curso);
            }
            em.remove(curso);
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