package com.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.app.dao.MangaDAO;
import com.app.dao.MangaLikeDAO;
import com.app.model.Manga;

public class MangaLikeServiceParametersTest {

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
     * Escenario 1: Visualización de likes.
     * Verifica que se obtenga el total de likes de un manga publicado.
     */
    @ParameterizedTest
    @CsvSource({
            "1, 5",   // Manga con 5 likes
            "2, 12",  // Manga con 12 likes
            "3, 0"    // Manga sin likes
    })
    public void given_published_manga_when_get_total_likes_then_return_correct_count(int mangaId, int totalLikes) {
        // Dado un manga con Likes
        when(mangaLikeDAO.contarLikes(mangaId)).thenReturn(totalLikes);

        // Cuando se consulta el total de likes
        int likesObtenidos = mangaLikeService.obtenerTotalLikes(mangaId);

        // Entonces debe devolver la cantidad correcta de likes
        assertEquals(totalLikes, likesObtenidos);
        verify(mangaLikeDAO).contarLikes(mangaId);
    }

    /**
     * Escenario 3: Ordenamiento de mangas por likes.
     * Verifica que los mangas se ordenen correctamente de mayor a menor likes.
     */
    @ParameterizedTest
    @CsvSource({
            "1, 10, 5, 2",  // Caso 1: Primer manga tiene más likes
            "2, 3, 8, 1",   // Caso 2: Segundo manga tiene más likes
            "3, 15, 15, 7"  // Caso 3: Múltiples mangas con diferentes likes
    })
    public void given_mangas_with_different_likes_when_order_by_likes_then_return_sorted_descending(
            int scanId, int likes1, int likes2, int likes3) {

        // Dados tres mangas con diferentes cantidades de likes
        Manga manga1 = new Manga();
        manga1.setId(1);
        manga1.setTitulo("Manga 1");
        manga1.setTotalLikes(0); // Valor inicial seteado como 0

        Manga manga2 = new Manga();
        manga2.setId(2);
        manga2.setTitulo("Manga 2");
        manga2.setTotalLikes(0); // Valor inicial seteado como 0

        Manga manga3 = new Manga();
        manga3.setId(3);
        manga3.setTitulo("Manga 3");
        manga3.setTotalLikes(0); // Valor inicial seteado como 0

        List<Manga> mangas = Arrays.asList(manga1, manga2, manga3);
        when(mangaDAO.buscarPorScanId(scanId)).thenReturn(mangas);

        // Configurar los likes desde MangaLikeDAO
        when(mangaLikeDAO.contarLikes(1)).thenReturn(likes1);
        when(mangaLikeDAO.contarLikes(2)).thenReturn(likes2);
        when(mangaLikeDAO.contarLikes(3)).thenReturn(likes3);

        // Cuando ordenamos por likes
        List<Manga> mangasOrdenados = mangaLikeService.obtenerMangasOrdenadosPorLikes(scanId);

        // Entonces deben estar ordenados de mayor a menor likes
        assertNotNull(mangasOrdenados);
        assertEquals(3, mangasOrdenados.size());

        // Verificar que están en orden descendente
        for (int i = 0; i < mangasOrdenados.size() - 1; i++) {
            assertTrue(mangasOrdenados.get(i).getTotalLikes() >=
                            mangasOrdenados.get(i + 1).getTotalLikes(),
                    "Los mangas deben estar ordenados de mayor a menor likes");
        }

        verify(mangaDAO).buscarPorScanId(scanId);
        verify(mangaLikeDAO).contarLikes(1);
        verify(mangaLikeDAO).contarLikes(2);
        verify(mangaLikeDAO).contarLikes(3);
    }
}