package com.app.dao;

import com.app.model.HistorialVisitas;
import com.app.model.Lector;
import com.app.model.Manga;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

public class HistorialVisitasDAO {

    /**
     * Registra una nueva visita o actualiza la fecha de visita
     * si el lector ya ha visitado previamente el manga.
     */
    public void registrarOActualizarVisita(Lector lector, Manga manga) {
        EntityManager em = EntityManagerFactoryProvider.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            // Buscar si ya existe una visita del lector a ese manga
            String jpql = "SELECT h FROM HistorialVisitas h WHERE h.lector = :lector AND h.manga = :manga";
            TypedQuery<HistorialVisitas> query = em.createQuery(jpql, HistorialVisitas.class);
            query.setParameter("lector", lector);
            query.setParameter("manga", manga);

            List<HistorialVisitas> resultados = query.getResultList();

            HistorialVisitas historial;
            if (resultados.isEmpty()) {
                // No existe → crear nueva visita
                historial = new HistorialVisitas(manga, lector);
                em.persist(historial);
            } else {
                // Ya existe → actualizar fecha de visita
                historial = resultados.get(0);
                historial.setFechaVisita(LocalDateTime.now());
                em.merge(historial);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene los últimos 5 mangas visitados por el lector,
     * ordenados por la fecha de visita más reciente.
     */
    public List<HistorialVisitas> obtenerUltimasVisitas(Lector lector) {
        EntityManager em = EntityManagerFactoryProvider.getEntityManagerFactory().createEntityManager();
        List<HistorialVisitas> resultados = null;

        try {
            String jpql = "SELECT h FROM HistorialVisitas h " +
                    "JOIN FETCH h.manga " +
                    "WHERE h.lector = :lector " +
                    "ORDER BY h.fechaVisita DESC";

            TypedQuery<HistorialVisitas> query = em.createQuery(jpql, HistorialVisitas.class);
            query.setParameter("lector", lector);
            query.setMaxResults(5);

            resultados = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return resultados;
    }
}
