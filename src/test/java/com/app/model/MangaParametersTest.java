package com.app.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MangaParametersTest {

    private Manga manga;

    @BeforeEach
    void setUp() {
        manga = new Manga();
        manga.setTitulo("Naruto");
        manga.setDescripcion("Un ninja que busca ser Hokage");
        manga.setEstado(EstadoManga.EN_PROGRESO);
    }

    @ParameterizedTest
    @MethodSource("likeCases")
    void testAgregarLike(int likesIniciales, int likesEsperados) {
        manga = new Manga();
        for (int i = 0; i < likesIniciales; i++) {
            manga.agregarLike();
        }
        manga.agregarLike();
        assertEquals(likesEsperados, manga.getTotalLikes());
    }

    // Casos para el test de likes
    static Collection<Object[]> likeCases() {
        return Arrays.asList(new Object[][] {
                { 0, 1 },
                { 5, 6 },
                { 9, 10 }
        });
    }

    @ParameterizedTest
    @MethodSource("capitulosCases")
    void testGetTotalCapitulos(int cantidadCapitulos, int esperado) {
        manga = new Manga();
        for (int i = 0; i < cantidadCapitulos; i++) {
            manga.getCapitulos().add(new Capitulo());
        }
        assertEquals(esperado, manga.getTotalCapitulos());
    }

    // Casos para capítulos
    static Collection<Object[]> capitulosCases() {
        return Arrays.asList(new Object[][] {
                { 0, 0 },
                { 3, 3 },
                { 10, 10 }
        });
    }
}
