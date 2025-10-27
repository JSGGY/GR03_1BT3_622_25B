package com.app.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Helper para tests con H2 en memoria.
 * 
 * NOTA: Ya no es necesario llamar setupTestDatabase() manualmente.
 * Maven automáticamente usa el persistence.xml de src/test/resources
 * que configura H2 en memoria.
 * 
 * Esta clase se mantiene por compatibilidad pero ya no hace nada.
 */
public class TestHelper implements BeforeAllCallback {
    
    /**
     * Ya no hace nada - Maven maneja la configuración automáticamente.
     * Se mantiene por compatibilidad con tests existentes.
     * 
     * @deprecated Ya no es necesario, eliminar las llamadas a este método
     */
    @Deprecated
    public static void setupTestDatabase() {
        // No hace nada - Maven usa automáticamente el persistence.xml de test
    }
    
    /**
     * Ya no hace nada - Maven maneja la configuración automáticamente.
     * 
     * @deprecated Ya no es necesario
     */
    @Deprecated
    public static void resetDatabase() {
        // No hace nada
    }
    
    /**
     * Implementación de la extensión JUnit (ya no hace nada).
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        // No hace nada - Maven configura todo automáticamente
    }
}

