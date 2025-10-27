package com.app.dao;

import com.app.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UsuarioDAO {

    /**
     * Obtiene el EntityManagerFactory de manera lazy a través del provider.
     * Esto permite que los tests configuren una unidad de persistencia diferente.
     */
    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

    public void guardar(Usuario usuario) {
        EntityManager em = getEmf().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }
}
