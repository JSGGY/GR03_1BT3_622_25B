package com.app.dao;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.app.model.AdminScan;
import com.app.model.Capitulo;
import com.app.model.EstadoManga;
import com.app.model.Manga;
import com.app.model.Scan;
import com.app.test.TestHelper;

@DisplayName("Test de Integración - MangaDAO con Base de Datos H2 en Memoria")
class MangaDAOIntegrationTest {

    private MangaDAO mangaDAO;
    private CapituloDAO capituloDAO;
    private ScanDAO scanDAO;
    private AdminScanDAO adminScanDAO;

    private List<Integer> idsALimpiar = new ArrayList<>();
    private Integer scanIdALimpiar;

    @BeforeAll
    static void setUpDatabase() {
        // Configurar H2 en memoria para todos los tests
        TestHelper.setupTestDatabase();
    }

    @BeforeEach
    void setUp() {
        mangaDAO = new MangaDAO();
        capituloDAO = new CapituloDAO();
        scanDAO = new ScanDAO();
        adminScanDAO = new AdminScanDAO();
        idsALimpiar = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        // Limpiar datos de prueba si quedaron
        for (Integer id : idsALimpiar) {
            try {
                mangaDAO.eliminar(id);
            } catch (Exception e) {
                // Ya fue eliminado o no existe
            }
        }

        if (scanIdALimpiar != null) {
            try {
                scanDAO.eliminar(scanIdALimpiar);
            } catch (Exception e) {
                // Ya fue eliminado
            }
        }
    }

    @Test
    @DisplayName("Given manga con capítulos, when eliminar manga, then capítulos también se eliminan en cascada")
    public void given_manga_with_capitulos_when_delete_then_capitulos_also_deleted() {
        // 1. Obtener o crear AdminScan
        AdminScan admin = adminScanDAO.buscarPorEmail("test.integration@test.com");
        if (admin == null) {
            admin = new AdminScan();
            admin.setUsername("testadmin");
            admin.setNombre("Test Admin Integration"); // Método dummy, no persiste
            admin.setEmail("test.integration@test.com");
            admin.setPassword("test123");
            adminScanDAO.guardar(admin);
        }

        // 2. Crear Scan
        Scan scan = new Scan();
        scan.setNombre("Test Scan - Integration");
        scan.setDescripcion("Scan para test de integración");
        scan.setCreadoPor(admin);
        scanDAO.guardar(scan);
        scanIdALimpiar = scan.getId();

        // 3. Crear Manga
        Manga manga = new Manga();
        manga.setTitulo("Test Manga con Capítulos");
        manga.setDescripcion("Manga para verificar eliminación en cascada");
        manga.setEstado(EstadoManga.EN_PROGRESO);
        manga.setScan(scan);

        boolean mangaGuardado = mangaDAO.guardar(manga);
        assertTrue(mangaGuardado);

        int mangaId = manga.getId();
        idsALimpiar.add(mangaId);

        // 4. Crear Capítulos asociados al manga
        Capitulo cap1 = new Capitulo();
        cap1.setTitulo("Capítulo 1 - Test");
        cap1.setNumero(1);
        cap1.setDescripcion("Primer capítulo de prueba");
        cap1.setManga(manga);

        Capitulo cap2 = new Capitulo();
        cap2.setTitulo("Capítulo 2 - Test");
        cap2.setNumero(2);
        cap2.setDescripcion("Segundo capítulo de prueba");
        cap2.setManga(manga);

        boolean cap1Guardado = capituloDAO.guardar(cap1);
        boolean cap2Guardado = capituloDAO.guardar(cap2);

        assertTrue(cap1Guardado);
        assertTrue(cap2Guardado);

        int cap1Id = cap1.getId();
        int cap2Id = cap2.getId();

        // Verificar que todo existe en BD
        assertNotNull(mangaDAO.buscarPorId(mangaId));
        assertNotNull(capituloDAO.buscarPorId(cap1Id));
        assertNotNull(capituloDAO.buscarPorId(cap2Id));

        System.out.println("✅ Datos de prueba creados: Manga ID=" + mangaId +
                ", Capítulos IDs=" + cap1Id + "," + cap2Id);

        //Eliminar manga
        boolean resultado = mangaDAO.eliminar(mangaId);

        //Verificar eliminación en cascada
        assertTrue(resultado);
        Manga mangaEliminado = mangaDAO.buscarPorId(mangaId);
        assertNull(mangaEliminado);
        // Verificar que los capítulos también fueron eliminados (cascada)
        Capitulo cap1Eliminado = capituloDAO.buscarPorId(cap1Id);
        Capitulo cap2Eliminado = capituloDAO.buscarPorId(cap2Id);
        assertNull(cap1Eliminado);
        assertNull(cap2Eliminado);
        System.out.println("✅ Test exitoso: Manga y capítulos eliminados en cascada correctamente");
        // Limpiar de la lista ya que se eliminó exitosamente
        idsALimpiar.remove(Integer.valueOf(mangaId));
    }
}