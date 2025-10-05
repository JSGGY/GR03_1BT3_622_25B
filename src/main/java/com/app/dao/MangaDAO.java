package com.app.dao;

import java.util.List;

import com.app.model.Manga;
import com.app.model.Scan;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

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
            em.getTransaction().rollback();
            e.printStackTrace();
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
            TypedQuery<Manga> query = em.createQuery("SELECT m FROM Manga m ORDER BY m.titulo", Manga.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public boolean eliminar(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Manga manga = em.find(Manga.class, id);
            if (manga != null) {
                em.remove(manga);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean existeTituloEnScan(String titulo, int scanId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(m) FROM Manga m WHERE m.titulo = :titulo AND m.scan.id = :scanId", 
                Long.class
            );
            query.setParameter("titulo", titulo);
            query.setParameter("scanId", scanId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    public boolean existeTituloEnScanExceptoId(String titulo, int scanId, int mangaId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(m) FROM Manga m WHERE m.titulo = :titulo AND m.scan.id = :scanId AND m.id != :mangaId", 
                Long.class
            );
            query.setParameter("titulo", titulo);
            query.setParameter("scanId", scanId);
            query.setParameter("mangaId", mangaId);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}