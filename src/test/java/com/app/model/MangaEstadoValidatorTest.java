package com.app.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Test Parametrizado - Validación de Estados de Manga")
class MangaEstadoValidatorTest {

    @ParameterizedTest(name = "Estado {0} puede transicionar a {1}: {2}")
    @CsvSource({
        "EN_PROGRESO, COMPLETADO, true",
        "EN_PROGRESO, PAUSADO, true",
        "EN_PROGRESO, CANCELADO, true",
        "COMPLETADO, EN_PROGRESO, false",
        "COMPLETADO, PAUSADO, false",
        "COMPLETADO, CANCELADO, false",
        "PAUSADO, EN_PROGRESO, true",
        "PAUSADO, COMPLETADO, true",
        "PAUSADO, CANCELADO, true",
        "CANCELADO, EN_PROGRESO, false",
        "CANCELADO, COMPLETADO, false",
        "CANCELADO, PAUSADO, false"
    })
    @DisplayName("Validar transiciones de estado validas")
    public void given_current_state_when_transition_to_new_state_then_validate(
            EstadoManga estadoActual, 
            EstadoManga nuevoEstado, 
            boolean esperado) {

        System.out.println("Probando transicion: " + estadoActual + " -> " + nuevoEstado + " (esperado: " + esperado + ")");

        Manga manga = new Manga();
        manga.setEstado(estadoActual);
        
        boolean result = manga.puedeTransicionarA(nuevoEstado);

        assertEquals(esperado, result);
        System.out.println("Resultado: " + (result == esperado ? "PASS" : "FAIL"));
    }
}

