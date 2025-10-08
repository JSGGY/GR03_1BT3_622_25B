package com.app.dao;

import java.util.List;

import com.app.model.Scan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class ScanDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    public void guardar(Scan scan) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(scan);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Scan buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Scan.class, id);
        } finally {
            em.close();
        }
    }

    public List<Scan> buscarPorAdminScan(int adminScanId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Scan> query = em.createQuery(
                "SELECT DISTINCT s FROM Scan s LEFT JOIN FETCH s.mangas WHERE s.creadoPor.id = :adminId", Scan.class);
            query.setParameter("adminId", adminScanId);
            List<Scan> result = query.getResultList();
            System.out.println("DEBUG: Encontrados " + result.size() + " scans para adminId: " + adminScanId);
            return result;
        } finally {
            em.close();
        }
    }

    public List<Scan> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Scan> scans = em.createQuery("SELECT s FROM Scan s", Scan.class).getResultList();
            for (Scan s : scans) {
                s.getMangas().size();
            }
            return scans;
        } finally {
            em.close();
        }
    }


    public void actualizar(Scan scan) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(scan);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void eliminar(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Scan scan = em.find(Scan.class, id);
            if (scan != null) {
                em.remove(scan);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}