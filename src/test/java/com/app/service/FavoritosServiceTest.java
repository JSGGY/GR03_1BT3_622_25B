package com.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.app.dao.FavoritosDAO;
import com.app.model.Favoritos;
import com.app.model.Manga;
import com.app.model.Lector;

class FavoritosServiceTest {

    @Mock
    private FavoritosDAO favoritosDAO;

    @InjectMocks
    private FavoritosService favoritosService;

    private Lector lector;
    private Manga manga;

    @BeforeEach
    void setUp() {
        // Crear mock de FavoritosDAO
        favoritosDAO = mock(FavoritosDAO.class);

        // Inyectarlo en el service
        favoritosService = new FavoritosService(favoritosDAO);

        // Crear lector y manga de prueba
        lector = new Lector();
        lector.setId(1);
        manga = new Manga();
        manga.setId(100);
        manga.setTitulo("Attack on Titan");
    }


    // ====================================================
    // Escenario 1: Agregar Manga a Favoritos
    // ====================================================
    @Test
    @DisplayName("Escenario 1: Marcar un manga como favorito")
    void testAgregarMangaAFavoritosExitoso() {
        // Dado que el manga no está en favoritos
        when(favoritosDAO.existeFavorito(lector, manga)).thenReturn(false);
        when(favoritosDAO.guardar(any(Favoritos.class))).thenReturn(true);

        // Cuando se agrega el manga a favoritos
        boolean resultado = favoritosService.agregarAFavoritos(lector, manga);

        // Entonces el sistema debe devolver true
        assertTrue(resultado, "El manga debería agregarse exitosamente a favoritos");

        // Y el DAO debe haber sido llamado para guardar
        verify(favoritosDAO).guardar(any(Favoritos.class));
    }

    @Test
    @DisplayName("Escenario 1: Intentar agregar un manga ya favorito")
    void testAgregarMangaYaFavorito() {
        // Dado que el manga ya está en favoritos
        when(favoritosDAO.existeFavorito(lector, manga)).thenReturn(true);

        // Cuando se intenta agregarlo otra vez
        boolean resultado = favoritosService.agregarAFavoritos(lector, manga);

        // Entonces el sistema no debe duplicarlo
        assertFalse(resultado, "El sistema no debe permitir duplicar un favorito");

        // Y no debe intentar guardarlo otra vez
        verify(favoritosDAO, never()).guardar(any(Favoritos.class));
    }

    // ====================================================
    // Escenario 2: Quitar un Manga de Favoritos
    // ====================================================
    @Test
    @DisplayName("Escenario 2: Quitar un manga de favoritos")
    void testQuitarMangaDeFavoritos() {
        // Dado que el manga ya está en favoritos
        when(favoritosDAO.eliminarFavorito(lector, manga)).thenReturn(true);

        // Cuando se selecciona “Eliminar de favoritos”
        boolean resultado = favoritosService.quitarDeFavoritos(lector, manga);

        // Entonces el sistema debe retirarlo correctamente
        assertTrue(resultado, "El manga debería eliminarse correctamente de favoritos");
        verify(favoritosDAO).eliminarFavorito(lector, manga);
    }

    @Test
    @DisplayName("Escenario 2: Intentar quitar un manga que no está en favoritos")
    void testQuitarMangaNoExistente() {
        // Dado que el manga no está en la lista
        when(favoritosDAO.eliminarFavorito(lector, manga)).thenReturn(false);

        // Cuando se intenta eliminar
        boolean resultado = favoritosService.quitarDeFavoritos(lector, manga);

        // Entonces el sistema debe indicar que no fue posible
        assertFalse(resultado, "No debería poder eliminarse un manga que no está en favoritos");
        verify(favoritosDAO).eliminarFavorito(lector, manga);
    }

    // ====================================================
    // Escenario 3: Visualizar Lista de Favoritos
    // ====================================================
    @Test
    @DisplayName("Escenario 3: Visualizar lista de favoritos")
    void testVisualizarListaDeFavoritos() {
        // Dado que el lector tiene mangas marcados como favoritos
        Manga m1 = new Manga(); m1.setTitulo("Naruto");
        Manga m2 = new Manga(); m2.setTitulo("One Piece");
        List<Manga> listaFavoritos = Arrays.asList(m1, m2);

        when(favoritosDAO.obtenerFavoritosPorLector(lector)).thenReturn(listaFavoritos);

        // Cuando accede a la sección “Mis favoritos”
        List<Manga> resultado = favoritosService.obtenerFavoritos(lector);

        // Entonces el sistema debe mostrar la lista completa
        assertEquals(2, resultado.size(), "El lector debería tener 2 mangas favoritos");
        assertEquals("Naruto", resultado.get(0).getTitulo());
        assertEquals("One Piece", resultado.get(1).getTitulo());

        verify(favoritosDAO).obtenerFavoritosPorLector(lector);
    }
}
