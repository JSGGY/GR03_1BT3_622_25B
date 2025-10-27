package com.app.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

// Clases de ejemplo
class Lista {
    private String nombre;
    private List<Manga> mangas = new ArrayList<>();

    public Lista(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public List<Manga> getMangas() { return mangas; }

    public void agregarManga(Manga m) { mangas.add(m); }
    public void quitarManga(Manga m) { mangas.remove(m); }
}

class Manga {
    private String titulo;
    public Manga(String titulo) { this.titulo = titulo; }
    public String getTitulo() { return titulo; }
}

class ListaController {
    public void agregarManga(Lista lista, Manga manga) { lista.agregarManga(manga); }
    public void quitarManga(Lista lista, Manga manga) { lista.quitarManga(manga); }
    public Lista crearLista(String nombre) { return new Lista(nombre); }
    public List<Manga> obtenerMangas(Lista lista) { return lista.getMangas(); }
}

public class ListaControllerTest {

    private ListaController controller;
    private Lista lista;
    private Manga manga1;

    @BeforeEach
    public void setup() {
        controller = new ListaController();
        lista = controller.crearLista("Favoritos");
        manga1 = new Manga("Naruto");
    }

    // ============================
    // Prueba unitaria 1: Crear Lista
    // ============================
    @Test
    public void testCrearLista() {
        Lista nuevaLista = controller.crearLista("Shojo");
        assertEquals("Shojo", nuevaLista.getNombre());
        assertTrue(nuevaLista.getMangas().isEmpty(), "La lista debe iniciar vacía");
    }

    // ============================
    // Prueba unitaria 2: Agregar Manga
    // ============================
    @Test
    public void testAgregarManga() {
        controller.agregarManga(lista, manga1);
        assertTrue(lista.getMangas().contains(manga1), "La lista debe contener el manga agregado");
    }

    // ============================
    // Prueba unitaria 3: Quitar Manga
    // ============================
    @Test
    public void testQuitarManga() {
        controller.agregarManga(lista, manga1);
        controller.quitarManga(lista, manga1);
        assertFalse(lista.getMangas().contains(manga1), "El manga debe ser removido de la lista");
    }

    // ============================
    // Prueba parametrizada: Agregar manga a varias listas
    // ============================
    @ParameterizedTest
    @ValueSource(strings = {"Favoritos", "Pendientes", "Terminados"})
    public void testAgregarMangaAListaDistinta(String nombreLista) {
        Lista l = controller.crearLista(nombreLista);
        Manga m = new Manga("One Piece");
        controller.agregarManga(l, m);
        assertTrue(l.getMangas().contains(m), "El manga debe agregarse a la lista " + nombreLista);
    }
}
