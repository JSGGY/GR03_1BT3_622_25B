package com.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.dao.MangaDAO;

@DisplayName("Test de Lógica de Negocio - Likes de Manga con Mock")
public class MangaLikePersistenciaTest {

    @Test
    @DisplayName("Given manga, when multiple users like, then total calculated correctly with mocked DAO")
    public void given_manga_when_multiple_users_like_then_total_calculated_correctly() {
        // Crear manga "real" para la lógica de likes
        Manga manga = new Manga();
        manga.setTitulo("Popular Manga");
        // Simular 5 usuarios dando like
        for (int i = 0; i < 5; i++) {
            manga.agregarLike();
        }
        assertEquals(5, manga.getTotalLikes());
        // 2 usuarios quitan like
        manga.quitarLike();
        manga.quitarLike();
        assertEquals(3, manga.getTotalLikes());
        // Mock del DAO (sin inicialización estática, solo mock puro)
        MangaDAO mangaDAOMock = mock(MangaDAO.class);
        // Configurar el comportamiento del mock
        when(mangaDAOMock.buscarPorId(manga.getId())).thenReturn(manga);
        // Llamada "falsa" a guardar
        mangaDAOMock.guardar(manga);
        verify(mangaDAOMock).guardar(manga);
        // Recuperar con mock y verificar
        Manga mangaRecuperado = mangaDAOMock.buscarPorId(manga.getId());
        assertEquals(3, mangaRecuperado.getTotalLikes());
    }
}
