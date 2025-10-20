package com.app.dao;

import com.app.model.Lector;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;

public class LectorDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    /**
     * Busca un lector por nombre de usuario y contraseña
     */
    public Lector findByUsernameAndPassword(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT l FROM Lector l WHERE l.nombre = :username AND l.contraseña = :password",
                            Lector.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Busca un lector por su ID
     */
    public Lector buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Lector.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si ya existe un lector con el mismo nombre o correo
     */
    public boolean existePorUsernameOEmail(String username, String email) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(l) FROM Lector l WHERE l.nombre = :username OR l.correo = :email",
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
     * Guarda un lector y retorna el objeto guardado con su ID generado
     */
    public Lector guardar(Lector lector) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.persist(lector);

            em.getTransaction().commit();

            System.out.println("DEBUG: Lector guardado - ID: " + lector.getId() +
                    ", Username: " + lector.getUsername() +
                    ", Email: " + lector.getCorreo());

            return lector;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR guardando Lector: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Busca todos los lectores que siguen un manga específico.
     * Nota: Por ahora retorna una lista vacía.
     * TODO: Implementar cuando se agregue la relación ManyToMany Lector-Manga
     * 
     * @param mangaId ID del manga
     * @return Lista de lectores que siguen el manga
     */
    public java.util.List<Lector> buscarSeguidoresPorManga(int mangaId) {
        return new java.util.ArrayList<>();
    }
}

