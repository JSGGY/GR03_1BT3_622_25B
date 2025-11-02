package com.app.dao;

import java.util.List;

import com.app.model.Lista;
import com.app.model.Lector;
import com.app.model.ListaManga;
import com.app.model.Manga;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class ListaDAO {

    /**
     * Obtiene el EntityManagerFactory de manera lazy a través del provider.
     * Esto permite que los tests configuren una unidad de persistencia diferente.
     */
    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

    /**
     * Guarda o actualiza una lista
     */
    public boolean guardar(Lista lista) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();

            if (lista.getId() == 0) {
                em.persist(lista);
            } else {
                em.merge(lista);
            }

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.err.println("ERROR ListaDAO: Falló al guardar lista '" + lista.getNombre() + "': " + e.getMessage());
            e.printStackTrace();

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Busca una lista por su ID
     */
    public Lista buscarPorId(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(Lista.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todas las listas de un lector
     */
    public List<Lista> buscarPorLector(Lector lector) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Lista> query = em.createQuery(
                "SELECT DISTINCT l FROM Lista l LEFT JOIN FETCH l.listaMangas lm LEFT JOIN FETCH lm.manga WHERE l.lector = :lector ORDER BY l.fechaCreacion DESC",
                Lista.class
            );
            query.setParameter("lector", lector);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todas las listas de un lector por ID
     */
    public List<Lista> buscarPorLectorId(int lectorId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            TypedQuery<Lista> query = em.createQuery(
                "SELECT DISTINCT l FROM Lista l LEFT JOIN FETCH l.listaMangas lm LEFT JOIN FETCH lm.manga WHERE l.lector.id = :lectorId ORDER BY l.fechaCreacion DESC",
                Lista.class
            );
            query.setParameter("lectorId", lectorId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una lista
     */
    public boolean eliminar(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();

            Lista lista = em.find(Lista.class, id);
            if (lista == null) {
                em.getTransaction().rollback();
                System.out.println("❌ Lista con ID=" + id + " no existe");
                return false;
            }

            em.remove(lista);
            em.getTransaction().commit();

            System.out.println("✅ Lista ID=" + id + " eliminada correctamente");
            return true;

        } catch (Exception e) {
            System.err.println("ERROR al eliminar lista ID=" + id + ": " + e.getMessage());
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si un manga ya está en una lista
     */
    public boolean mangaEstaEnLista(int listaId, int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(lm) FROM ListaManga lm WHERE lm.lista.id = :listaId AND lm.manga.id = :mangaId",
                Long.class
            )
            .setParameter("listaId", listaId)
            .setParameter("mangaId", mangaId)
            .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Agrega un manga a una lista
     */
    public boolean agregarMangaALista(int listaId, int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            // Verificar si ya está en la lista
            if (mangaEstaEnLista(listaId, mangaId)) {
                System.out.println("⚠️ El manga ya está en la lista");
                return false;
            }

            em.getTransaction().begin();

            Lista lista = em.find(Lista.class, listaId);
            Manga manga = em.find(Manga.class, mangaId);

            if (lista == null || manga == null) {
                em.getTransaction().rollback();
                System.out.println("❌ Lista o Manga no existe");
                return false;
            }

            ListaManga listaManga = new ListaManga();
            listaManga.setLista(lista);
            listaManga.setManga(manga);

            em.persist(listaManga);
            em.getTransaction().commit();

            System.out.println("✅ Manga agregado a la lista correctamente");
            return true;

        } catch (Exception e) {
            System.err.println("ERROR al agregar manga a lista: " + e.getMessage());
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Remueve un manga de una lista
     */
    public boolean removerMangaDeLista(int listaId, int mangaId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<ListaManga> query = em.createQuery(
                "SELECT lm FROM ListaManga lm WHERE lm.lista.id = :listaId AND lm.manga.id = :mangaId",
                ListaManga.class
            );
            query.setParameter("listaId", listaId);
            query.setParameter("mangaId", mangaId);

            List<ListaManga> resultados = query.getResultList();
            if (resultados.isEmpty()) {
                em.getTransaction().rollback();
                System.out.println("⚠️ El manga no está en la lista");
                return false;
            }

            em.remove(resultados.get(0));
            em.getTransaction().commit();

            System.out.println("✅ Manga removido de la lista correctamente");
            return true;

        } catch (Exception e) {
            System.err.println("ERROR al remover manga de lista: " + e.getMessage());
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Verifica si un lector tiene una lista con ese nombre
     */
    public boolean existeNombreEnLector(String nombre, int lectorId) {
        EntityManager em = getEmf().createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(l) FROM Lista l WHERE l.nombre = :nombre AND l.lector.id = :lectorId",
                Long.class
            )
            .setParameter("nombre", nombre)
            .setParameter("lectorId", lectorId)
            .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }
}

