package persistencia;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import modelo.Curso;
import modelo.CursoEnProgreso;
import modelo.Usuario;

public class CursoEnProgresoDAO {
    
    private EntityManagerFactory emf;
    
    public CursoEnProgresoDAO() {
    	emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    }
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void guardar(CursoEnProgreso cursoEnProgreso) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cursoEnProgreso);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public void actualizar(CursoEnProgreso cursoEnProgreso) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cursoEnProgreso);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public CursoEnProgreso buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CursoEnProgreso.class, id);
        } finally {
            em.close();
        }
    }
    
    public CursoEnProgreso buscarPorUsuarioYCurso(Usuario usuario, Curso curso) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<CursoEnProgreso> query = em.createQuery(
                "SELECT cp FROM CursoEnProgreso cp " +
                "LEFT JOIN FETCH cp.estadoPreguntas " +
                "WHERE cp.usuario = :usuario AND cp.curso = :curso", 
                CursoEnProgreso.class);
            query.setParameter("usuario", usuario);
            query.setParameter("curso", curso);
            
            List<CursoEnProgreso> resultados = query.getResultList();
            if (resultados.isEmpty()) {
                return null;
            }
            return resultados.get(0);
        } finally {
            em.close();
        }
    }
    
    public List<CursoEnProgreso> buscarPorUsuario(Usuario usuario) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<CursoEnProgreso> query = em.createQuery(
                "SELECT cep FROM CursoEnProgreso cep WHERE cep.usuario = :usuario", 
                CursoEnProgreso.class);
            query.setParameter("usuario", usuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public void eliminar(CursoEnProgreso cursoEnProgreso) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(cursoEnProgreso)) {
                cursoEnProgreso = em.merge(cursoEnProgreso);
            }
            em.remove(cursoEnProgreso);
            em.getTransaction().commit();
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