package persistencia;

import jakarta.persistence.*;
import modelo.Bloque;
import java.util.List;

public class BloqueDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void guardar(Bloque bloque) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            if (bloque.getId() != null) {
                em.merge(bloque);
            } else {
                em.persist(bloque);
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

    public Bloque buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Bloque bloque = null;
        try {
            bloque = em.find(Bloque.class, id);
        } finally {
            em.close();
        }
        return bloque;
    }

    public List<Bloque> buscarPorCurso(Long cursoId) {
        EntityManager em = emf.createEntityManager();
        List<Bloque> bloques = null;
        try {
            bloques = em.createQuery("SELECT b FROM Bloque b WHERE b.curso.id = :cursoId", Bloque.class)
                     .setParameter("cursoId", cursoId)
                     .getResultList();
        } finally {
            em.close();
        }
        return bloques;
    }

    public void eliminar(Bloque bloque) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(bloque)) {
                bloque = em.merge(bloque);
            }
            em.remove(bloque);
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
}