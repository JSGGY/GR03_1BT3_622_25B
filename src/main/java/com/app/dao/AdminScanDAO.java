package com.app.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.app.model.AdminScan;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;

public class AdminScanDAO {
    
    /**
     * Obtiene el EntityManagerFactory de manera lazy a través del provider.
     * Esto permite que los tests configuren una unidad de persistencia diferente.
     */
    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

    public AdminScan findByUsernameAndPassword(String username, String password) {
        EntityManager em = getEmf().createEntityManager();
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

    public boolean eliminar(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AdminScan admin = session.get(AdminScan.class, id);
            if (admin == null) return false;
            tx = session.beginTransaction();
            session.delete(admin);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public AdminScan buscarPorId(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(AdminScan.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Busca un AdminScan por su email/correo
     */
    public AdminScan buscarPorEmail(String email) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.createQuery(
                "SELECT a FROM AdminScan a WHERE a.correo = :email", 
                AdminScan.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si existe un AdminScan con el username o email dados
     */
    public boolean existePorUsernameOEmail(String username, String email) {
        EntityManager em = getEmf().createEntityManager();
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
        EntityManager em = getEmf().createEntityManager();
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




