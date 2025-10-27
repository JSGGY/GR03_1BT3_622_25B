package com.app.dao;

import com.app.model.CapituloImagen;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class CapituloImagenDAO {
    
    /**
     * Obtiene el EntityManagerFactory de manera lazy a través del provider.
     * Esto permite que los tests configuren una unidad de persistencia diferente.
     */
    private EntityManagerFactory getEmf() {
        return EntityManagerFactoryProvider.getEntityManagerFactory();
    }

    /**
     * Busca una imagen de capítulo por su ID
     */
    public CapituloImagen buscarPorId(int id) {
        EntityManager em = getEmf().createEntityManager();
        try {
            return em.find(CapituloImagen.class, id);
        } finally {
            em.close();
        }
    }
}

