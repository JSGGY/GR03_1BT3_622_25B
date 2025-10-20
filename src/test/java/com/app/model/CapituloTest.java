package com.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitarios para Capítulo - actualizarDatos")
class CapituloTest {

    private Capitulo capitulo;
    private Manga manga;

    @BeforeEach
    void setUp() {
        manga = new Manga();
        manga.setTitulo("One Piece");
        manga.setEstado(EstadoManga.EN_PROGRESO);

        capitulo = new Capitulo();
        capitulo.setTitulo("Capítulo 1");
        capitulo.setNumero(1);
        capitulo.setDescripcion("Descripción inicial");
        capitulo.setManga(manga);
    }

    @Test
    @DisplayName("Given capítulo, when actualizar with valid data, then success")
    public void given_capitulo_when_update_with_valid_data_then_success() {
        String nuevoTitulo = "Capítulo 1 - Editado";
        String nuevaDescripcion = " Manga de piratas";

        boolean resultado = capitulo.actualizarDatos(nuevoTitulo, nuevaDescripcion);

        assertTrue(resultado, "Actualización con datos válidos exitosa");
        assertEquals(nuevoTitulo, capitulo.getTitulo());
        assertEquals(nuevaDescripcion, capitulo.getDescripcion());

        System.out.println("✅ Capítulo actualizado correctamente");
        System.out.println("Nueva titulo: " + capitulo.getTitulo() + "\nNueva Descripcion: " + capitulo.getDescripcion());
    }

    @Test
    @DisplayName("Given capítulo, when actualizar with invalid data, then fails")
    public void given_capitulo_when_update_with_invalid_data_then_fails() {
        // Arrange
        String tituloOriginal = capitulo.getTitulo();
        String descripcionOriginal = capitulo.getDescripcion();

        // Act - título null o vacío debe fallar
        boolean resultadoNull = capitulo.actualizarDatos(null, "Nueva descripción");
        boolean resultadoVacio = capitulo.actualizarDatos("", "Nueva descripción");
        boolean resultadoBlanco = capitulo.actualizarDatos("   ", "Nueva descripción");

        // Assert
        assertFalse(resultadoNull, "Actualización con título null debe fallar");
        assertFalse(resultadoVacio, "Actualización con título vacío debe fallar");
        assertFalse(resultadoBlanco, "Actualización con título en blanco debe fallar");

        // Verificar que no cambió nada
        assertEquals(tituloOriginal, capitulo.getTitulo(), "Título no debe cambiar");
        assertEquals(descripcionOriginal, capitulo.getDescripcion(), "Descripción no debe cambiar");

        System.out.println("✅ Validaciones de datos incorrectos funcionan");
    }
}