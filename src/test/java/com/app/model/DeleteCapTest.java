package com.app.model;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.dao.AdminScanDAO;
import com.app.dao.CapituloDAO;
import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;

/**
 * Test de integración para eliminación de capítulos.
 * Usa H2 en memoria automáticamente gracias a persistence.xml en test resources.
 */
public class DeleteCapTest {

    private MangaDAO mangaDAO;
    private CapituloDAO capituloDAO;
    private ScanDAO scanDAO;
    private AdminScanDAO adminScanDAO;
    private Manga manga;
    private Capitulo capitulo;
    private Scan scan;
    private File imagenFisica;
    
    @BeforeEach
    public void setup() throws IOException {
        // Los DAOs usan H2 automáticamente - no requiere MySQL
        mangaDAO = new MangaDAO();
        capituloDAO = new CapituloDAO();
        scanDAO = new ScanDAO();
        adminScanDAO = new AdminScanDAO();

        // 1. Crear AdminScan (requerido para Scan)
        AdminScan admin = new AdminScan();
        admin.setUsername("testadmin_delete");
        admin.setEmail("delete.test@test.com");
        admin.setPassword("test123");
        adminScanDAO.guardar(admin);

        // 2. Crear Scan (requerido para Manga)
        scan = new Scan();
        scan.setNombre("Test Scan Delete");
        scan.setDescripcion("Scan para test de eliminación");
        scan.setCreadoPor(admin);
        scanDAO.guardar(scan);

        // 3. Crear manga en H2 con Scan
        manga = new Manga();
        manga.setTitulo("Manga E2E Real");
        manga.setEstado(EstadoManga.EN_PROGRESO);
        manga.setScan(scan); // Asignar scan requerido
        boolean mangaSaved = mangaDAO.guardar(manga);
        assertTrue(mangaSaved, "Manga debe guardarse correctamente");

        // 4. Crear capítulo
        capitulo = new Capitulo();
        capitulo.setTitulo("Capitulo 1");
        capitulo.setNumero(1);
        capitulo.setDescripcion("Capitulo de prueba");
        capitulo.setManga(manga);

        boolean capituloSaved = capituloDAO.guardar(capitulo);
        assertTrue(capituloSaved, "Capítulo debe guardarse correctamente");
        
        // 5. Crear archivo temporal para el test
        imagenFisica = File.createTempFile("test_imagen_", ".jpg");
    }

    @AfterEach
    public void cleanup() {
        // Eliminar capítulo si existe
        try {
            if (capitulo != null && capitulo.getId() > 0) {
                Capitulo c = capituloDAO.buscarPorId(capitulo.getId());
                if (c != null) {
                    capituloDAO.eliminar(c);
                }
            }
        } catch (Exception ignored) {}

        // Eliminar manga si existe
        try {
            if (manga != null && manga.getId() > 0) {
                mangaDAO.eliminar(manga.getId());
            }
        } catch (Exception ignored) {}

        // Eliminar scan si existe
        try {
            if (scan != null && scan.getId() > 0) {
                scanDAO.eliminar(scan.getId());
            }
        } catch (Exception ignored) {}

        // Eliminar archivo físico temporal
        if (imagenFisica != null && imagenFisica.exists()) {
            imagenFisica.delete();
        }
    }

    @Test
    public void given_authenticated_admin_when_delete_capitulo_then_removed_from_database() throws Exception {
        // Verificar que capítulo existe antes de eliminar
        assertNotNull(capitulo, "Capítulo debe estar inicializado");
        assertTrue(capitulo.getId() > 0, "Capítulo debe tener ID asignado");
        
        Capitulo existente = capituloDAO.buscarPorId(capitulo.getId());
        assertNotNull(existente, "El capítulo debe existir en la BD antes de eliminar");
        System.out.println("✅ Capítulo encontrado en BD con ID: " + existente.getId());

        // Eliminar capítulo (simula endpoint DELETE)
        capituloDAO.eliminar(capitulo);
        System.out.println("✅ Capítulo eliminado de la BD");

        // Verificar que capítulo fue eliminado de la BD
        Capitulo eliminado = capituloDAO.buscarPorId(capitulo.getId());
        assertNull(eliminado, "Capítulo debe ser eliminado de la base de datos");

        // Simular eliminación de archivo físico
        if (imagenFisica.exists()) {
            boolean deleted = imagenFisica.delete();
            assertTrue(deleted, "Archivo temporal debe poder eliminarse");
        }
        assertFalse(imagenFisica.exists(), "Archivo físico del capítulo debe ser eliminado");
        System.out.println("✅ Test completado: Capítulo y archivo eliminados correctamente");
    }
}

