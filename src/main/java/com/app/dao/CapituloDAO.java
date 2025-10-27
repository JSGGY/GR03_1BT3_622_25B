package com.app.dao;

import java.util.List;

import com.app.model.Capitulo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class CapituloDAO {

    /**
     * Obtiene el EntityManagerFactory de manera lazy a través del provider.
     * Esto permite que los tests configuren una unidad de persistencia diferente.
     */
    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

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
        EntityManager em = getEmf().createEntityManager();
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
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(Capitulo.class, id);
        } finally {
            em.close();
        }
    }

    public List<Capitulo> listarTodos() {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Capitulo c", Capitulo.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Capitulo> listarPorManga(int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.createQuery(
                    "SELECT c FROM Capitulo c WHERE c.manga.id = :mangaId ORDER BY c.numero ASC",
                    Capitulo.class)
                    .setParameter("mangaId", mangaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
