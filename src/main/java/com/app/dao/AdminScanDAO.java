package com.app.dao;

import com.app.model.AdminScan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;

public class AdminScanDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    public AdminScan findByUsernameAndPassword(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                "SELECT a FROM AdminScan a WHERE a.username = :username AND a.contraseña = :password", 
                AdminScan.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public AdminScan buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(AdminScan.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si existe un AdminScan con el username o email dados
     */
    public boolean existePorUsernameOEmail(String username, String email) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(a) FROM AdminScan a WHERE a.username = :username OR a.correo = :email",
                Long.class)
                .setParameter("username", username)
                .setParameter("email", email)
                .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Guarda un AdminScan y retorna el objeto guardado con su ID generado
     */
    public AdminScan guardar(AdminScan adminScan) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(adminScan);
            em.getTransaction().commit();

            System.out.println("DEBUG: AdminScan guardado - ID: " + adminScan.getId() +
                             ", Username: " + adminScan.getUsername() +
                             ", Email: " + adminScan.getCorreo());

            return adminScan;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR guardando AdminScan: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }
}
