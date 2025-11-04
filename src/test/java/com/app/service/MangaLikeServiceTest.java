package com.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.app.dao.MangaDAO;
import com.app.dao.MangaLikeDAO;
import com.app.model.Lector;
import com.app.model.Manga;

public class MangaLikeServiceTest {

    private MangaLikeService mangaLikeService;

    @Mock
    private MangaDAO mangaDAO;

    @Mock
    private MangaLikeDAO mangaLikeDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mangaLikeService = new MangaLikeService(mangaDAO, mangaLikeDAO);
    }

    /**
     * Escenario 1: Visualizacion de likes.
     * Verifica que se obtenga el total de likes de un manga publicado.
     */
    @Test
    public void given_manga_with_likes_when_get_total_likes_then_return_correct_count() {
        // Dado un Manga con 5 likes
        int mangaId = 1;
        when(mangaLikeDAO.contarLikes(mangaId)).thenReturn(5);

        // Cuando se obtiene el total de likes
        int totalLikes = mangaLikeService.obtenerTotalLikes(mangaId);

        // Se debe retornar contador igual a 5
        assertEquals(5, totalLikes);
        verify(mangaLikeDAO).contarLikes(mangaId);
    }

    /**
     * Escenario 2: Actualizacion de likes.
     * Verifica que al dar un like, el total se actualice correctamente.
     */
    @Test
    public void given_manga_when_add_like_then_total_likes_increases() {
        // Dado un Manga con 3 likes iniciales y un lector
        Manga manga = new Manga();
        manga.setId(1);
        manga.setTotalLikes(3);

        Lector lector = new Lector();
        lector.setId(100);

        when(mangaLikeDAO.existeLike(1, 100)).thenReturn(false);
        when(mangaDAO.buscarPorId(1)).thenReturn(manga);
        when(mangaLikeDAO.guardarLike(manga, lector)).thenReturn(true);
        when(mangaDAO.guardar(any(Manga.class))).thenReturn(true);

        // Cuando agregamos un like
        boolean resultado = mangaLikeService.agregarLike(1, lector);

        // El resultado debe ser exitoso y los likes deben actualizarse
        assertTrue(resultado);
        assertEquals(4, manga.getTotalLikes());
        verify(mangaLikeDAO).existeLike(1, 100);
        verify(mangaDAO).buscarPorId(1);
        verify(mangaLikeDAO).guardarLike(manga, lector);
        verify(mangaDAO).guardar(manga);
    }

}