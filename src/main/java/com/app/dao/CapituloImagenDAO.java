package com.app.dao;

import com.app.model.CapituloImagen;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class CapituloImagenDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("AdminScanPU");

    /**
     * Busca una imagen de capítulo por su ID
     */
    public CapituloImagen buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(CapituloImagen.class, id);
        } finally {
            em.close();
        }
    }
}

