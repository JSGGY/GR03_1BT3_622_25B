package com.app.dao;

import com.app.model.Capitulo;

import javax.persistence.*;
import java.util.List;

public class CapituloDAO {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    // Guardar un nuevo capítulo
    public boolean guardar(Capitulo capitulo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(capitulo);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Actualizar un capítulo existente
    public boolean actualizar(Capitulo capitulo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(capitulo);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Eliminar un capítulo por entidad
    public boolean eliminar(Capitulo capitulo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Capitulo c = em.find(Capitulo.class, capitulo.getId());
            if (c != null) {
                em.remove(c);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Buscar un capítulo por ID
    public Capitulo buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Capitulo.class, id);
        } finally {
            em.close();
        }
    }

    // Listar todos los capítulos
    public List<Capitulo> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Capitulo> query = em.createQuery("SELECT c FROM Capitulo c", Capitulo.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Listar capítulos por manga
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
