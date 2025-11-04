package com.app.dao;

import com.app.model.Lector;
import com.app.model.Manga;
import com.app.model.MangaLike;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class MangaLikeDAO {

    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

    public boolean existeLike(int mangaId, int lectorId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(ml) FROM MangaLike ml WHERE ml.manga.id = :mangaId AND ml.lector.id = :lectorId",
                            Long.class)
                    .setParameter("mangaId", mangaId)
                    .setParameter("lectorId", lectorId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean guardarLike(Manga manga, Lector lector) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();

            MangaLike like = new MangaLike(manga, lector);
            em.persist(like);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    public int contarLikes(int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(ml) FROM MangaLike ml WHERE ml.manga.id = :mangaId",
                            Long.class)
                    .setParameter("mangaId", mangaId)
                    .getSingleResult();
            return count.intValue();
        } finally {
            em.close();
        }
    }
}