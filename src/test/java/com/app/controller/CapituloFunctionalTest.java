package com.app.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.app.dao.AdminScanDAO;
import com.app.dao.CapituloDAO;
import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.AdminScan;
import com.app.model.Capitulo;
import com.app.model.CapituloImagen;
import com.app.model.EstadoManga;
import com.app.model.Manga;
import com.app.model.Scan;
import com.app.test.TestHelper;

@DisplayName("Test Funcional - Editar Capítulo Completo con H2 en Memoria")
class CapituloFunctionalTest {

    private CapituloDAO capituloDAO;
    private MangaDAO mangaDAO;
    private ScanDAO scanDAO;
    private AdminScanDAO adminScanDAO;
    
    private List<Integer> capitulosALimpiar = new ArrayList<>();
    private List<Integer> mangasALimpiar = new ArrayList<>();
    private List<Integer> scansALimpiar = new ArrayList<>();
    
    @BeforeAll
    static void setUpDatabase() {
        // Configurar H2 en memoria para todos los tests
        TestHelper.setupTestDatabase();
    }
    
    @BeforeEach
    void setUp() {
        capituloDAO = new CapituloDAO();
        mangaDAO = new MangaDAO();
        scanDAO = new ScanDAO();
        adminScanDAO = new AdminScanDAO();
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar en orden inverso de dependencias
        for (Integer id : capitulosALimpiar) {
            try {
                Capitulo cap = capituloDAO.buscarPorId(id);
                if (cap != null) capituloDAO.eliminar(cap);
            } catch (Exception e) {
                // Ignorar si ya fue eliminado
            }
        }
        
        for (Integer id : mangasALimpiar) {
            try {
                mangaDAO.eliminar(id);
            } catch (Exception e) {
                // Ignorar si ya fue eliminado
            }
        }
        
        for (Integer id : scansALimpiar) {
            try {
                scanDAO.eliminar(id);
            } catch (Exception e) {
                // Ignorar si ya fue eliminado
            }
        }
    }

    @Test
    @DisplayName("Given capítulo en BD, when cambiar número de capítulo, then cambio persiste en BD")
    public void given_capitulo_when_change_numero_then_changes_persist() {
        // Arrange - Crear estructura completa en BD
        AdminScan admin = adminScanDAO.buscarPorEmail("test.functional@test.com");
        if (admin == null) {
            admin = new AdminScan();
            admin.setUsername("testfunc");
            admin.setEmail("test.functional@test.com");
            admin.setPassword("test123");
            adminScanDAO.guardar(admin);
        }
        
        Scan scan = new Scan();
        scan.setNombre("Test Scan Funcional");
        scan.setDescripcion("Para test funcional de cambio de número");
        scan.setCreadoPor(admin);
        scanDAO.guardar(scan);
        scansALimpiar.add(scan.getId());
        
        Manga manga = new Manga();
        manga.setTitulo("Test Manga Funcional");
        manga.setEstado(EstadoManga.EN_PROGRESO);
        manga.setScan(scan);
        mangaDAO.guardar(manga);
        mangasALimpiar.add(manga.getId());
        
        Capitulo capituloOriginal = new Capitulo();
        capituloOriginal.setTitulo("Capítulo Original");
        capituloOriginal.setNumero(1);
        capituloOriginal.setDescripcion("Primer capítulo del manga");
        capituloOriginal.setManga(manga);
        
        // Crear una imagen de prueba con datos simulados
        CapituloImagen imagenPrueba = new CapituloImagen();
        imagenPrueba.setImagenNombre("imagen-1.jpg");
        imagenPrueba.setImagenTipo("image/jpeg");
        // Simulamos bytes de imagen para el test (no necesitamos imagen real)
        imagenPrueba.setImagenBlob(new byte[]{1, 2, 3, 4, 5});
        capituloOriginal.agregarImagen(imagenPrueba);
        
        capituloDAO.guardar(capituloOriginal);
        int capituloId = capituloOriginal.getId();
        capitulosALimpiar.add(capituloId);
        
        System.out.println("✅ Capítulo original creado en BD:");
        System.out.println("   Capítulo ID: " + capituloId);
        System.out.println("   Número original: " + capituloOriginal.getNumero());
        System.out.println("   Título: " + capituloOriginal.getTitulo());
        
        // Act - Cambiar número de capítulo (simula reordenamiento)
        Capitulo capituloAEditar = capituloDAO.buscarPorId(capituloId);
        assertNotNull(capituloAEditar);
        
        int numeroOriginal = capituloAEditar.getNumero();
        int nuevoNumero = 5;
        
        // Usar el nuevo método que probaremos
        boolean cambiado = capituloAEditar.cambiarNumeroCapitulo(nuevoNumero);
        assertTrue(cambiado, "Cambio de número debe ser exitoso");
        
        // Persistir cambio en BD
        boolean guardado = capituloDAO.actualizar(capituloAEditar);
        assertTrue(guardado, "Actualización en BD debe ser exitosa");
        
        System.out.println("✅ Número cambiado de " + numeroOriginal + " a " + nuevoNumero);
        
        // Assert - Recuperar de BD y verificar que el cambio persistió
        Capitulo capituloVerificado = capituloDAO.buscarPorId(capituloId);
        
        assertNotNull(capituloVerificado, "Capítulo debe existir en BD");
        assertEquals(nuevoNumero, capituloVerificado.getNumero(), 
                    "Número debe persistir en BD");
        assertEquals(capituloOriginal.getTitulo(), capituloVerificado.getTitulo(),
                    "Otros campos no deben cambiar");
        
        System.out.println("✅ Test funcional EXITOSO - Cambio verificado en BD:");
        System.out.println("   Número actualizado: " + capituloVerificado.getNumero());
        System.out.println("   Título se mantiene: " + capituloVerificado.getTitulo());
    }
}

