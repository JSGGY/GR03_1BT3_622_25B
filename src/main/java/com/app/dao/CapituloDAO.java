package com.app.dao;

import java.util.List;

import com.app.model.Capitulo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class CapituloDAO {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");


    public boolean guardar(Capitulo capitulo) {
        return ejecutarTransaccion(em -> em.persist(capitulo));
    }

    public boolean actualizar(Capitulo capitulo) {
        return ejecutarTransaccion(em -> em.merge(capitulo));
    }

    public boolean eliminar(Capitulo capitulo) {
        return ejecutarTransaccion(em -> {
            Capitulo c = em.find(Capitulo.class, capitulo.getId());
            if (c != null) em.remove(c);
        });
    }

    private boolean ejecutarTransaccion(java.util.function.Consumer<EntityManager> accion) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            accion.accept(em);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            // Usa logging en vez de printStackTrace
            java.util.logging.Logger.getLogger(CapituloDAO.class.getName()).severe(e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public Capitulo buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Capitulo.class, id);
        } finally {
            em.close();
        }
    }

    public List<Capitulo> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Capitulo> query = em.createQuery("SELECT c FROM Capitulo c", Capitulo.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Capitulo> listarPorManga(int mangaId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Capitulo> query = em.createQuery(
                    "SELECT c FROM Capitulo c WHERE c.manga.id = :mangaId ORDER BY c.numero ASC",
                    Capitulo.class
            );
            query.setParameter("mangaId", mangaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
