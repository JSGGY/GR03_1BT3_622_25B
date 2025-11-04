package com.app.dao;

import java.util.List;
import com.app.model.Favoritos;
import com.app.model.Manga;
import com.app.model.Lector;
import jakarta.persistence.*;

public class FavoritosDAO {

    private EntityManagerFactory emf;

    public FavoritosDAO() {
        this.emf = Persistence.createEntityManagerFactory("AdminScanPU");
    }

    public FavoritosDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public boolean guardar(Favoritos favorito) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(favorito);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al guardar favorito: " + e.getMessage());
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean eliminarFavorito(Lector lector, Manga manga) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Favoritos> query = em.createQuery(
                    "SELECT f FROM Favoritos f WHERE f.lector.id = :lectorId AND f.manga.id = :mangaId",
                    Favoritos.class);
            query.setParameter("lectorId", lector.getId());
            query.setParameter("mangaId", manga.getId());
            List<Favoritos> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                em.getTransaction().rollback();
                return false;
            }

            em.remove(resultados.get(0));
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar favorito: " + e.getMessage());
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public List<Manga> obtenerFavoritosPorLector(Lector lector) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Manga> query = em.createQuery(
                    "SELECT f.manga FROM Favoritos f " +
                            "JOIN FETCH f.manga.scan " +
                            "WHERE f.lector.id = :lectorId",
                    Manga.class);
            query.setParameter("lectorId", lector.getId());

            List<Manga> mangas = query.getResultList();

            // Inicializar el Scan para evitar LazyInitializationException
            for (Manga manga : mangas) {
                if (manga.getScan() != null) {
                    manga.getScan().getNombre();
                }
            }

            return mangas;
        } catch (Exception e) {
            System.err.println("❌ Error al obtener favoritos: " + e.getMessage());
            return new java.util.ArrayList<>();
        } finally {
            em.close();
        }
    }

    public boolean existeFavorito(Lector lector, Manga manga) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(f) FROM Favoritos f WHERE f.lector.id = :lectorId AND f.manga.id = :mangaId",
                    Long.class);
            query.setParameter("lectorId", lector.getId());
            query.setParameter("mangaId", manga.getId());
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}