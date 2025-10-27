package com.app.dao;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Provider singleton para EntityManagerFactory que permite configuración flexible
 * para producción y tests.
 * 
 * En tests, el archivo persistence.xml en src/test/resources tiene prioridad
 * sobre el de src/main/resources, por lo que automáticamente usa H2 en memoria.
 */
public class EntityManagerFactoryProvider {
    
    private static EntityManagerFactory emf;
    private static final String PERSISTENCE_UNIT_NAME = "AdminScanPU";
    
    /**
     * Obtiene la instancia singleton del EntityManagerFactory.
     * Se inicializa lazy (solo cuando se necesita).
     * 
     * En tests usa automáticamente H2 gracias a la prioridad del classpath.
     */
    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return emf;
    }
    
    /**
     * Inyecta un EntityManagerFactory específico (deprecated - ya no necesario).
     * Se mantiene por compatibilidad pero ya no se usa.
     * 
     * @param unitName nombre de la unidad de persistencia (ignorado)
     * @deprecated Maven maneja esto automáticamente con classpath priority
     */
    @Deprecated
    public static synchronized void setPersistenceUnitName(String unitName) {
        // Ya no es necesario - Maven usa el persistence.xml correcto automáticamente
        // Se mantiene para no romper código existente
    }
    
    /**
     * Inyecta un EntityManagerFactory específico.
     * Útil para tests que quieren usar mocks.
     * 
     * @param customEmf EntityManagerFactory personalizado
     */
    public static synchronized void setEntityManagerFactory(EntityManagerFactory customEmf) {
        if (emf != null && emf.isOpen() && emf != customEmf) {
            emf.close();
        }
        emf = customEmf;
    }
    
    /**
     * Cierra el EntityManagerFactory si está abierto.
     */
    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
    
    /**
     * Resetea el provider a su estado inicial.
     * Cierra el EntityManagerFactory actual para que se cree uno nuevo en el próximo uso.
     * 
     * @deprecated Ya no es necesario - Maven maneja la configuración automáticamente
     */
    @Deprecated
    public static synchronized void reset() {
        close();
        // Ya no es necesario cambiar el nombre de la PU - es constante
        // Maven usa automáticamente el persistence.xml correcto según el classpath
    }
}

