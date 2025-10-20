package com.app.dao;

import java.util.List;

import com.app.model.Manga;
import com.app.model.Scan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class MangaDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");
    
    public boolean guardar(Manga manga) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            if (manga.getId() == 0) {
                em.persist(manga);
            } else {
                em.merge(manga);
            }
            
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.err.println("ERROR MangaDAO: Falló al guardar manga '" + manga.getTitulo() + "': " + e.getMessage());
            
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
    
    public Manga buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Manga.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Manga> buscarPorScan(Scan scan) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Manga> query = em.createQuery(
                "SELECT DISTINCT m FROM Manga m LEFT JOIN FETCH m.capitulos WHERE m.scan = :scan ORDER BY m.titulo", 
                Manga.class
            );
            query.setParameter("scan", scan);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Manga> buscarPorScanId(int scanId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Manga> query = em.createQuery(
                "SELECT DISTINCT m FROM Manga m LEFT JOIN FETCH m.capitulos WHERE m.scan.id = :scanId ORDER BY m.titulo",
                Manga.class
            );
            query.setParameter("scanId", scanId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Manga> obtenerTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Manga m ORDER BY m.titulo", Manga.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Elimina un manga de la base de datos.
     * Gracias a CascadeType.ALL en la relación OneToMany con Capitulo,
     * todos los capítulos asociados se eliminarán automáticamente.
     * 
     * @param id ID del manga a eliminar
     * @return true si se eliminó correctamente, false si no existe o hubo error
     */
    public boolean eliminar(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Buscar el manga
            Manga manga = em.find(Manga.class, id);
            if (manga == null) {
                em.getTransaction().rollback();
                System.out.println("❌ Manga con ID=" + id + " no existe");
                return false;
            }

            // Eliminar manga (los capítulos se eliminan en cascada automáticamente)
            em.remove(manga);
            em.getTransaction().commit();

            System.out.println("✅ Manga ID=" + id + " eliminado correctamente (con capítulos en cascada)");
            return true;

        } catch (Exception e) {
            System.err.println("ERROR al eliminar manga ID=" + id + ": " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean existeTituloEnScan(String titulo, int scanId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(m) FROM Manga m WHERE m.titulo = :titulo AND m.scan.id = :scanId",
                Long.class)
                .setParameter("titulo", titulo)
                .setParameter("scanId", scanId)
                .getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    public boolean existeTituloEnScanExceptoId(String titulo, int scanId, int mangaId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(m) FROM Manga m WHERE m.titulo = :titulo AND m.scan.id = :scanId AND m.id != :mangaId",
                Long.class)
                .setParameter("titulo", titulo)
                .setParameter("scanId", scanId)
                .setParameter("mangaId", mangaId)
                .getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}