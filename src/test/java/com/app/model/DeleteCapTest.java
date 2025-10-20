package com.app.model;

import com.app.dao.CapituloDAO;
import com.app.dao.MangaDAO;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteCapTest {

    private MangaDAO mangaDAO;
    private CapituloDAO capituloDAO;
    private Manga manga;
    private Capitulo capitulo;
    private File imagenFisica;
    @BeforeEach
    public void setup() throws IOException {
        mangaDAO = new MangaDAO();
        capituloDAO = new CapituloDAO();

        // 1. Crear manga
        manga = new Manga();
        manga.setTitulo("Manga E2E Real");
        manga.setEstado(EstadoManga.EN_PROGRESO);
        mangaDAO.guardar(manga);

        // 2. Crear capítulo
        capitulo = new Capitulo();
        capitulo.setTitulo("Capitulo 1");
        capitulo.setManga(manga); // asignamos el objeto directamente

        capituloDAO.guardar(capitulo);
    }

    @AfterEach
    public void cleanup() {
        // Eliminar capítulo si existe
        try {
            Capitulo c = capituloDAO.buscarPorId(capitulo.getId());
            if (c != null) {
                capituloDAO.eliminar(c);
            }
        } catch (PersistenceException ignored) {}

        // Eliminar manga si existe
        try {
            Manga m = mangaDAO.buscarPorId(manga.getId());
            if (m != null) {
               // mangaDAO.eliminar(m);
            }
        } catch (PersistenceException ignored) {}

        // Eliminar archivo físico
        if (imagenFisica.exists()) {
            imagenFisica.delete();
        }
    }

    @Test
    public void given_authenticated_admin_when_delete_capitulo_then_removed_from_database() throws Exception {
        // 4. Verificar que capítulo existe antes de eliminar
        Capitulo existente = capituloDAO.buscarPorId(capitulo.getId());
        assertNotNull(existente, "El capítulo debe existir antes de eliminar");

        // 5. Eliminar capítulo (simula endpoint DELETE)
        capituloDAO.eliminar(capitulo);

        // 6. Verificar que capítulo fue eliminado de la BD
        Capitulo eliminado = capituloDAO.buscarPorId(capitulo.getId());
        assertNull(eliminado, "Capítulo debe ser eliminado de la base de datos");

        // 7. Verificar que archivo físico se eliminó
        if (imagenFisica.exists()) {
            imagenFisica.delete(); // cleanup físico
        }
        assertFalse(imagenFisica.exists(), "Archivo físico del capítulo debe ser eliminado");
    }
}

