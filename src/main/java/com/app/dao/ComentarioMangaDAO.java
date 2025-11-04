package com.app.dao;

import java.util.List;

import com.app.model.ComentarioManga;
import com.app.model.Manga;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class ComentarioMangaDAO {

    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

    /**
     * Guarda un nuevo comentario en la base de datos
     * @param comentario ComentarioManga a guardar
     * @return true si se guardó correctamente, false en caso contrario
     */
    public boolean guardar(ComentarioManga comentario) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(comentario);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al guardar comentario: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todos los comentarios de un manga específico
     * Ordenados por fecha de comentario (más recientes primero)
     * @param manga Manga del cual obtener comentarios
     * @return Lista de comentarios
     */
    public List<ComentarioManga> obtenerComentariosPorManga(Manga manga) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<ComentarioManga> query = em.createQuery(
                "SELECT c FROM ComentarioManga c " +
                "JOIN FETCH c.lector " +
                "WHERE c.manga = :manga " +
                "ORDER BY c.fechaComentario DESC",
                ComentarioManga.class
            );
            query.setParameter("manga", manga);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todos los comentarios de un manga por su ID
     * @param mangaId ID del manga
     * @return Lista de comentarios
     */
    public List<ComentarioManga> obtenerComentariosPorMangaId(int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<ComentarioManga> query = em.createQuery(
                "SELECT c FROM ComentarioManga c " +
                "JOIN FETCH c.lector " +
                "WHERE c.manga.id = :mangaId " +
                "ORDER BY c.fechaComentario DESC",
                ComentarioManga.class
            );
            query.setParameter("mangaId", mangaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca un comentario por su ID
     * @param id ID del comentario
     * @return ComentarioManga o null si no existe
     */
    public ComentarioManga buscarPorId(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(ComentarioManga.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Elimina un comentario de la base de datos
     * @param id ID del comentario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            ComentarioManga comentario = em.find(ComentarioManga.class, id);
            if (comentario == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(comentario);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar comentario: " + e.getMessage());
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta el número de comentarios de un manga
     * @param mangaId ID del manga
     * @return Número de comentarios
     */
    public long contarComentariosPorManga(int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM ComentarioManga c WHERE c.manga.id = :mangaId",
                Long.class
            );
            query.setParameter("mangaId", mangaId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}

