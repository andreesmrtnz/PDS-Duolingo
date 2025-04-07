package persistencia;

import jakarta.persistence.*;
import modelo.Estadistica;
import modelo.Usuario;

import java.util.List;

public class EstadisticaDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void guardar(Estadistica estadistica) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Si la estadística ya existe, la actualizamos en lugar de crear una nueva
            if (estadistica.getId() != null) {
                em.merge(estadistica);
            } else {
                em.persist(estadistica);
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
    
    public Estadistica buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        Estadistica estadistica = null;
        try {
            estadistica = em.find(Estadistica.class, id);
        } finally {
            em.close();
        }
        return estadistica;
    }
    
    public Estadistica buscarPorUsuario(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        Estadistica estadistica = null;
        try {
            // Asumiendo que hay una relación usuario -> estadística
            List<Estadistica> resultados = em.createQuery("SELECT e FROM Estadistica e WHERE e.usuario.id = :usuarioId", Estadistica.class)
                     .setParameter("usuarioId", usuario.getId())
                     .getResultList();
            
            if (!resultados.isEmpty()) {
                estadistica = resultados.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar estadísticas del usuario: " + e.getMessage());
        } finally {
            em.close();
        }
        return estadistica;
    }
    
    public void actualizar(Estadistica estadistica) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(estadistica);
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
    
    public void eliminar(Estadistica estadistica) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(estadistica)) {
                estadistica = em.merge(estadistica);
            }
            em.remove(estadistica);
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
    
    public void actualizarTiempoUso(Usuario usuario, long tiempoAdicional) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Estadistica estadistica = buscarPorUsuario(usuario);
            if (estadistica != null) {
                // Actualizar el tiempo de uso
                em.createQuery("UPDATE Estadistica e SET e.tiempoTotalUso = e.tiempoTotalUso + :tiempo WHERE e.usuario.id = :usuarioId")
                  .setParameter("tiempo", tiempoAdicional)
                  .setParameter("usuarioId", usuario.getId())
                  .executeUpdate();
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
    
    public void incrementarDiasConsecutivos(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Estadistica estadistica = buscarPorUsuario(usuario);
            if (estadistica != null) {
                int nuevoDias = estadistica.getDiasConsecutivos() + 1;
                int mejorRacha = estadistica.getMejorRacha();
                
                // Actualizar mejor racha si es necesario
                if (nuevoDias > mejorRacha) {
                    em.createQuery("UPDATE Estadistica e SET e.diasConsecutivos = :nuevoDias, e.mejorRacha = :nuevoDias WHERE e.usuario.id = :usuarioId")
                      .setParameter("nuevoDias", nuevoDias)
                      .setParameter("usuarioId", usuario.getId())
                      .executeUpdate();
                } else {
                    em.createQuery("UPDATE Estadistica e SET e.diasConsecutivos = :nuevoDias WHERE e.usuario.id = :usuarioId")
                      .setParameter("nuevoDias", nuevoDias)
                      .setParameter("usuarioId", usuario.getId())
                      .executeUpdate();
                }
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
    
    public void resetearDiasConsecutivos(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            em.createQuery("UPDATE Estadistica e SET e.diasConsecutivos = 0 WHERE e.usuario.id = :usuarioId")
              .setParameter("usuarioId", usuario.getId())
              .executeUpdate();
            
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