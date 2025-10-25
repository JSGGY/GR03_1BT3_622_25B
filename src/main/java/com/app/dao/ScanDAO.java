package com.app.dao;

import java.util.List;
import java.util.function.Consumer;

import com.app.model.Scan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ScanDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");


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
            List<Scan> result = em.createQuery(
                "SELECT DISTINCT s FROM Scan s LEFT JOIN FETCH s.mangas WHERE s.creadoPor.id = :adminId", Scan.class)
                .setParameter("adminId", adminScanId)
                .getResultList();
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

    public void guardar(Scan scan) {
        executeTransaction(em -> em.persist(scan));
    }

    public void actualizar(Scan scan) {
        executeTransaction(em -> em.merge(scan));
    }

    public void eliminar(int id) {
        executeTransaction(em -> {
            Scan scan = em.find(Scan.class, id);
            if (scan != null) {
                em.remove(scan);
            }
        });
    }

    private void executeTransaction(Consumer<EntityManager> action) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            action.accept(em);
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