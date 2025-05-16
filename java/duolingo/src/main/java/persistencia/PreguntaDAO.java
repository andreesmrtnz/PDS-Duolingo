package persistencia;

import jakarta.persistence.*;
import modelo.Pregunta;
import modelo.PreguntaFillinBlank;
import modelo.PreguntaFlashCard;
import modelo.PreguntaMultipleChoice;

import java.util.List;

public class PreguntaDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void guardar(Pregunta pregunta) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            if (pregunta.getId() != null) {
                em.merge(pregunta);
            } else {
                em.persist(pregunta);
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

    public Pregunta buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Pregunta pregunta = null;
        try {
            pregunta = em.find(Pregunta.class, id);
        } finally {
            em.close();
        }
        return pregunta;
    }

    public List<Pregunta> buscarPorBloque(Long bloqueId) {
        EntityManager em = emf.createEntityManager();
        List<Pregunta> preguntas = null;
        try {
            preguntas = em.createQuery(
                    "SELECT p FROM Pregunta p WHERE p.bloque.id = :bloqueId", 
                    Pregunta.class)
                    .setParameter("bloqueId", bloqueId)
                    .getResultList();
        } finally {
            em.close();
        }
        return preguntas;
    }

    public void eliminar(Pregunta pregunta) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(pregunta)) {
                pregunta = em.merge(pregunta);
            }
            em.remove(pregunta);
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
    
 // Nuevos métodos para PreguntaDAO.java
    public List<PreguntaMultipleChoice> buscarPreguntasMultipleChoice() {
        EntityManager em = emf.createEntityManager();
        List<PreguntaMultipleChoice> preguntas = null;
        try {
            preguntas = em.createQuery(
                    "SELECT p FROM PreguntaMultipleChoice p", 
                    PreguntaMultipleChoice.class)
                    .getResultList();
        } finally {
            em.close();
        }
        return preguntas;
    }

    public List<PreguntaFlashCard> buscarPreguntasFlashCard() {
        EntityManager em = emf.createEntityManager();
        List<PreguntaFlashCard> preguntas = null;
        try {
            preguntas = em.createQuery(
                    "SELECT p FROM PreguntaFlashCard p", 
                    PreguntaFlashCard.class)
                    .getResultList();
        } finally {
            em.close();
        }
        return preguntas;
    }

    public List<PreguntaFillinBlank> buscarPreguntasFillinBlank() {
        EntityManager em = emf.createEntityManager();
        List<PreguntaFillinBlank> preguntas = null;
        try {
            preguntas = em.createQuery(
                    "SELECT p FROM PreguntaFillinBlank p", 
                    PreguntaFillinBlank.class)
                    .getResultList();
        } finally {
            em.close();
        }
        return preguntas;
    }
}