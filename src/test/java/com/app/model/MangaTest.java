package com.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class MangaTest {
    private Manga manga;

    @BeforeEach
    void setUp() {
        manga = new Manga();
        manga.setTitulo("One Piece");
        manga.setDescripcion("Manga de piratas");
        manga.setEstado(EstadoManga.EN_PROGRESO);
    }
    @Test
    public void given_manga_when_add_like_then_total_likes_increases() {
        //Manga debe inicializar con 0 likes
        assertEquals(0, manga.getTotalLikes());
        
        // Act
        manga.agregarLike();
        
        // Assert
        assertEquals(1, manga.getTotalLikes());
        System.out.println("Manga con : " + manga.getTotalLikes() + " Likes");
    }
}