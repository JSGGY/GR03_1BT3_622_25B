package com.app.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import com.app.dao.ComentarioMangaDAO;
import com.app.model.ComentarioManga;
import com.app.model.Lector;
import com.app.model.Manga;

/**
 * Tests para el sistema de comentarios de mangas
 * 
 * ESTRUCTURA:
 * - 1 Test con MOCKITO (mockea dependencias del DAO)
 * - 3 Tests UNITARIOS PUROS (sin mocks, solo lógica interna)
 * 
 * MÉTODOS PRINCIPALES TESTEADOS (según TDD):
 * - ComentarioMangaService.publicarComentario() - Lógica de negocio principal
 * - ComentarioMangaService.validarTextoComentario() - Validación de entrada
 * - ComentarioManga (modelo) - Validaciones y comportamiento de la entidad
 * - ComentarioMangaDAO.guardar() - Persistencia en BD (mockeado)
 */
class ComentarioMangaServiceTest {

    // Solo para el test con Mockito
    @Mock
    private ComentarioMangaDAO comentarioDAO;

    private ComentarioMangaService comentarioService;
    private ComentarioMangaService comentarioServiceSinMock;

    private Lector lector;
    private Manga manga;

    @BeforeEach
    void setUp() {
        // Crear mock de ComentarioMangaDAO (solo para el test con Mockito)
        comentarioDAO = mock(ComentarioMangaDAO.class);

        // Service con DAO mockeado (para test con Mockito)
        comentarioService = new ComentarioMangaService(comentarioDAO);

        // Service sin mock (para tests unitarios puros)
        comentarioServiceSinMock = new ComentarioMangaService();

        // Crear lector y manga de prueba
        lector = new Lector();
        lector.setId(1);
        lector.setUsername("testUser");

        manga = new Manga();
        manga.setId(100);
        manga.setTitulo("One Piece");
    }

    // ====================================================
    // TEST 1 (MOCKITO): Publicación Exitosa con DAO Mockeado
    // ====================================================
    /**
     * ⭐ TEST CON MOCKITO ⭐
     * 
     * Criterio de Aceptación 1: Publicación Exitosa
     * 
     * Dado que el usuario está autenticado y visualiza un manga,
     * cuando escribe un comentario y presiona "Publicar",
     * entonces el comentario debe guardarse y mostrarse de inmediato en la lista de comentarios.
     * 
     * MÉTODOS CLAVE USADOS (TDD):
     * - ComentarioMangaService.publicarComentario(Lector, Manga, String): boolean
     *   → Método principal que coordina la publicación
     * - ComentarioMangaDAO.guardar(ComentarioManga): boolean [MOCKEADO]
     *   → Persiste el comentario en la base de datos
     * - ComentarioMangaService.obtenerComentariosDeMangaPorId(int): List<ComentarioManga>
     *   → Recupera comentarios para mostrar inmediatamente
     * - ComentarioManga(Manga, Lector, String) [constructor]
     *   → Crea la entidad con validaciones básicas
     * 
     * USO DE MOCKITO:
     * - when().thenReturn() para simular guardado exitoso
     * - verify() para verificar que el DAO fue llamado
     */
    @Test
    @DisplayName("TEST 1 (MOCKITO): Publicación Exitosa - El comentario se guarda y muestra inmediatamente")
    void testEscenario1_PublicacionExitosaConMockito() {
        // DADO: Usuario autenticado con un comentario válido
        String textoComentario = "¡Excelente manga! Me encanta la historia.";
        
        // Mock del DAO para simular guardado exitoso
        when(comentarioDAO.guardar(any(ComentarioManga.class))).thenReturn(true);
        
        // Mock para obtener comentarios después de publicar
        ComentarioManga comentarioPublicado = new ComentarioManga(manga, lector, textoComentario);
        comentarioPublicado.setId(1);
        List<ComentarioManga> listaComentarios = Arrays.asList(comentarioPublicado);
        when(comentarioDAO.obtenerComentariosPorMangaId(manga.getId())).thenReturn(listaComentarios);

        // CUANDO: El usuario intenta publicar el comentario
        boolean resultadoPublicacion = comentarioService.publicarComentario(lector, manga, textoComentario);
        
        // Y consulta la lista de comentarios inmediatamente
        List<ComentarioManga> comentariosActualizados = comentarioService.obtenerComentariosDeMangaPorId(manga.getId());

        // ENTONCES: El comentario debe guardarse exitosamente
        assertTrue(resultadoPublicacion, 
            "El método publicarComentario() debe retornar true indicando éxito");
        
        // Y el DAO debe haber sido llamado para guardar
        verify(comentarioDAO).guardar(any(ComentarioManga.class));
        
        // Y el comentario debe aparecer en la lista inmediatamente
        assertNotNull(comentariosActualizados, 
            "La lista de comentarios no debe ser null");
        assertEquals(1, comentariosActualizados.size(), 
            "Debe haber exactamente 1 comentario en la lista");
        assertEquals(textoComentario, comentariosActualizados.get(0).getComentario(), 
            "El texto del comentario debe coincidir con el publicado");
        assertEquals("testUser", comentariosActualizados.get(0).obtenerNombreLector(), 
            "El autor del comentario debe ser el usuario que lo publicó");
        
        // Verificar que se consultaron los comentarios actualizados
        verify(comentarioDAO).obtenerComentariosPorMangaId(manga.getId());
    }

    // ====================================================
    // TEST 2 (UNITARIO): Validación de Comentarios Vacíos
    // ====================================================
    /**
     * 🔵 TEST UNITARIO PURO (sin Mockito) 🔵
     * 
     * Criterio de Aceptación 2: Comentario Vacío
     * 
     * Dado que el usuario intenta publicar sin escribir texto,
     * cuando presiona "Publicar",
     * entonces el sistema debe mostrar un mensaje de error indicando que el campo no puede estar vacío.
     * 
     * MÉTODOS CLAVE USADOS (TDD):
     * - ComentarioMangaService.validarTextoComentario(String): boolean
     *   → Validación crítica de entrada antes de procesar
     * - String.trim(): String
     *   → Elimina espacios en blanco para validación robusta
     * 
     * VALIDACIONES CRÍTICAS:
     * - Texto null → Rechazado
     * - Texto vacío ("") → Rechazado
     * - Solo espacios ("   ") → Rechazado
     * - Texto válido → Aceptado
     * 
     * NO USA MOCKITO: Solo prueba la lógica de validación interna del servicio
     */
    @Test
    @DisplayName("TEST 2 (UNITARIO): Validación de Comentarios Vacíos - Rechaza textos inválidos")
    void testEscenario2_ValidacionComentarioVacio() {
        // DADO: Diferentes tipos de comentarios inválidos (vacío, null, solo espacios)
        String comentarioVacio = "";
        String comentarioNull = null;
        String comentarioSoloEspacios = "     ";
        String comentarioValido = "Este es un comentario válido";

        // CUANDO: Se intenta validar cada tipo de comentario (sin mock, lógica pura)
        boolean validacionVacio = comentarioServiceSinMock.validarTextoComentario(comentarioVacio);
        boolean validacionNull = comentarioServiceSinMock.validarTextoComentario(comentarioNull);
        boolean validacionEspacios = comentarioServiceSinMock.validarTextoComentario(comentarioSoloEspacios);
        boolean validacionValido = comentarioServiceSinMock.validarTextoComentario(comentarioValido);

        // ENTONCES: El sistema debe rechazar todos los comentarios inválidos
        assertFalse(validacionVacio, 
            "validarTextoComentario() debe rechazar string vacío");
        assertFalse(validacionNull, 
            "validarTextoComentario() debe rechazar null");
        assertFalse(validacionEspacios, 
            "validarTextoComentario() debe rechazar solo espacios en blanco");
        assertTrue(validacionValido, 
            "validarTextoComentario() debe aceptar texto válido");
        
        // Verificación adicional: publicarComentario también debe rechazar inválidos
        // (esto usa el service sin mock, pero llamará al DAO real - validación de lógica antes del DAO)
        boolean resultadoVacio = comentarioServiceSinMock.publicarComentario(lector, manga, comentarioVacio);
        boolean resultadoNull = comentarioServiceSinMock.publicarComentario(lector, manga, comentarioNull);
        boolean resultadoEspacios = comentarioServiceSinMock.publicarComentario(lector, manga, comentarioSoloEspacios);

        // Todas las publicaciones deben fallar ANTES de llamar al DAO (por validación)
        assertFalse(resultadoVacio, 
            "publicarComentario() debe retornar false para comentario vacío");
        assertFalse(resultadoNull, 
            "publicarComentario() debe retornar false para comentario null");
        assertFalse(resultadoEspacios, 
            "publicarComentario() debe retornar false para comentario con solo espacios");
    }

    // ====================================================
    // TEST 3 (UNITARIO): Validación del Modelo ComentarioManga
    // ====================================================
    /**
     * 🔵 TEST UNITARIO PURO (sin Mockito) 🔵
     * 
     * Prueba la lógica del modelo ComentarioManga:
     * - Constructor y inicialización de campos
     * - Método esComentarioValido()
     * - Método obtenerNombreLector()
     * - Getters y setters básicos
     * 
     * MÉTODOS CLAVE USADOS (TDD):
     * - ComentarioManga(Manga, Lector, String) [constructor]
     *   → Crea la entidad con valores iniciales
     * - ComentarioManga.esComentarioValido(): boolean
     *   → Validación a nivel de entidad
     * - ComentarioManga.obtenerNombreLector(): String
     *   → Obtiene el nombre del lector
     * - ComentarioManga.getComentario(): String
     *   → Obtiene el texto del comentario
     * 
     * NO USA MOCKITO: Prueba directa del modelo sin dependencias
     */
    @Test
    @DisplayName("TEST 3 (UNITARIO): Validación del Modelo ComentarioManga")
    void testEscenario3_ModeloComentarioManga() {
        // DADO: Datos válidos para crear un comentario
        String textoComentario = "Este es un comentario de prueba";
        
        // CUANDO: Se crea un ComentarioManga usando el constructor
        ComentarioManga comentario = new ComentarioManga(manga, lector, textoComentario);
        
        // ENTONCES: El comentario debe inicializarse correctamente
        assertNotNull(comentario, "El comentario no debe ser null");
        assertEquals(manga, comentario.getManga(), "El manga debe coincidir");
        assertEquals(lector, comentario.getLector(), "El lector debe coincidir");
        assertEquals(textoComentario, comentario.getComentario(), "El texto debe coincidir");
        assertNotNull(comentario.getFechaComentario(), "La fecha de comentario debe auto-generarse");
        
        // Y el método esComentarioValido() debe retornar true
        assertTrue(comentario.esComentarioValido(), 
            "esComentarioValido() debe retornar true para comentario válido");
        
        // Y el método obtenerNombreLector() debe retornar el username correcto
        assertEquals("testUser", comentario.obtenerNombreLector(), 
            "obtenerNombreLector() debe retornar el username del lector");
        
        // CASO INVÁLIDO: Comentario vacío
        ComentarioManga comentarioInvalido = new ComentarioManga(manga, lector, "");
        assertFalse(comentarioInvalido.esComentarioValido(), 
            "esComentarioValido() debe retornar false para comentario vacío");
        
        // CASO INVÁLIDO: Comentario null
        ComentarioManga comentarioNull = new ComentarioManga(manga, lector, null);
        assertFalse(comentarioNull.esComentarioValido(), 
            "esComentarioValido() debe retornar false para comentario null");
        
        // CASO INVÁLIDO: Comentario solo espacios
        ComentarioManga comentarioEspacios = new ComentarioManga(manga, lector, "    ");
        assertFalse(comentarioEspacios.esComentarioValido(), 
            "esComentarioValido() debe retornar false para comentario con solo espacios");
    }

    // ====================================================
    // TEST 4 (UNITARIO): Validación de Setters y Modificación
    // ====================================================
    /**
     * 🔵 TEST UNITARIO PURO (sin Mockito) 🔵
     * 
     * Prueba los setters del modelo ComentarioManga:
     * - setComentario() actualiza el texto y la fecha de modificación
     * - Setters de ID, manga, lector
     * - Comportamiento de fechas
     * 
     * MÉTODOS CLAVE USADOS (TDD):
     * - ComentarioManga.setComentario(String): void
     *   → Actualiza el texto y setea fechaModificacion
     * - ComentarioManga.getFechaModificacion(): LocalDateTime
     *   → Debe actualizarse al modificar el comentario
     * - ComentarioManga.setId(int): void
     *   → Setea el ID del comentario
     * 
     * NO USA MOCKITO: Prueba directa del comportamiento del modelo
     */
    @Test
    @DisplayName("TEST 4 (UNITARIO): Validación de Setters y Modificación de Comentario")
    void testEscenario4_SettersYModificacion() {
        // DADO: Un comentario inicial
        String textoInicial = "Comentario inicial";
        ComentarioManga comentario = new ComentarioManga(manga, lector, textoInicial);
        
        // Verificar que inicialmente NO tiene fecha de modificación
        assertNotNull(comentario.getFechaComentario(), "Debe tener fecha de comentario");
        
        // CUANDO: Se modifica el texto del comentario usando setComentario()
        String textoModificado = "Comentario modificado";
        comentario.setComentario(textoModificado);
        
        // ENTONCES: El texto debe actualizarse
        assertEquals(textoModificado, comentario.getComentario(), 
            "El texto debe haberse actualizado");
        
        // Y debe tener una fecha de modificación
        assertNotNull(comentario.getFechaModificacion(), 
            "setComentario() debe actualizar la fecha de modificación");
        
        // TEST DE SETTERS BÁSICOS
        comentario.setId(999);
        assertEquals(999, comentario.getId(), "setId() debe actualizar el ID");
        
        // Cambiar el manga
        Manga nuevoManga = new Manga();
        nuevoManga.setId(200);
        nuevoManga.setTitulo("Naruto");
        comentario.setManga(nuevoManga);
        assertEquals(nuevoManga, comentario.getManga(), "setManga() debe actualizar el manga");
        assertEquals(200, comentario.getManga().getId(), "El nuevo manga debe tener el ID correcto");
        
        // Cambiar el lector
        Lector nuevoLector = new Lector();
        nuevoLector.setId(2);
        nuevoLector.setUsername("nuevoUsuario");
        comentario.setLector(nuevoLector);
        assertEquals(nuevoLector, comentario.getLector(), "setLector() debe actualizar el lector");
        assertEquals("nuevoUsuario", comentario.obtenerNombreLector(), 
            "obtenerNombreLector() debe reflejar el nuevo lector");
    }


// ====================================================
// TESTS HU003 - ELIMINAR COMENTARIO
// ====================================================

    /**
     * HU003: Eliminar Comentario
     *
     * HISTORIA DE USUARIO:
     * Como lector quiero poder eliminar mis propios comentarios
     * para mantener mi perfil limpio y retirar mensajes que ya no deseo mostrar.
     *
     * CRITERIOS DE ACEPTACIÓN:
     * 1️⃣ Solo debe mostrarse el botón “Eliminar” en comentarios propios.
     * 2️⃣ El sistema debe pedir confirmación antes de eliminar.
     * 3️⃣ Al eliminar, debe desaparecer de la vista y mostrar mensaje de éxito.
     */
    @Nested
    class EliminarComentarioTest {

        private ComentarioMangaDAO comentarioDAO;
        private ComentarioMangaService comentarioService;
        private Lector lector;
        private Manga manga;
        private ComentarioManga comentarioPropio;
        private ComentarioManga comentarioAjeno;

        @BeforeEach
        void setUp() {
            comentarioDAO = mock(ComentarioMangaDAO.class);
            comentarioService = new ComentarioMangaService(comentarioDAO);

            lector = new Lector();
            lector.setId(1);
            lector.setUsername("lector1");

            Lector otroLector = new Lector();
            otroLector.setId(2);
            otroLector.setUsername("lector2");

            manga = new Manga();
            manga.setId(10);
            manga.setTitulo("Berserk");

            comentarioPropio = new ComentarioManga(manga, lector, "Mi comentario");
            comentarioPropio.setId(100);

            comentarioAjeno = new ComentarioManga(manga, otroLector, "Comentario de otro lector");
            comentarioAjeno.setId(101);
        }

        // ====================================================
        // TEST 1 (MOCKITO): Eliminación Exitosa
        // ====================================================
        /**
         * Dado que el lector intenta eliminar su propio comentario,
         * cuando confirma la acción,
         * entonces el comentario debe eliminarse exitosamente y devolver true.
         */
        @Test
        @DisplayName("TEST 1 (MOCKITO): Eliminación Exitosa - El lector elimina su propio comentario")
        void testEliminarComentarioPropio_Exitoso() {
            // DADO: Se configura un lector y un comentario simulado
            Lector lector = new Lector();
            lector.setId(1);
            lector.setUsername("lectorPrueba");

            ComentarioManga comentarioPropio = new ComentarioManga();
            comentarioPropio.setId(10);
            comentarioPropio.setLector(lector);
            comentarioPropio.setComentario("Este es mi comentario");

            // El DAO simula que la eliminación fue exitosa
            when(comentarioDAO.buscarPorId(comentarioPropio.getId())).thenReturn(comentarioPropio);
            when(comentarioDAO.eliminar(comentarioPropio.getId())).thenReturn(true);

            // CUANDO: El servicio intenta eliminar el comentario
            boolean resultado = comentarioService.eliminarComentario(comentarioPropio.getId(), lector);

            // ENTONCES: La eliminación debe ser exitosa
            assertTrue(resultado, "El método debe retornar true si el comentario se elimina correctamente");

            // Verificar que el DAO fue llamado con el ID correcto
            verify(comentarioDAO, times(1)).eliminar(comentarioPropio.getId());
        }

        // ====================================================
        // TEST 2 (UNITARIO): No puede eliminar comentario ajeno
        // ====================================================
        /**
         * Dado que el lector visualiza comentarios de otros usuarios,
         * cuando intenta eliminar uno que no le pertenece,
         * entonces el sistema no debe permitirlo y debe retornar false.
         */
        @Test
        @DisplayName("TEST 2 (UNITARIO): No puede eliminar comentarios ajenos")
        void testEliminarComentarioAjeno() {
            // CUANDO: Un lector intenta eliminar un comentario ajeno
            boolean resultado = comentarioService.eliminarComentario(comentarioAjeno.getId(), lector);

            // ENTONCES: La eliminación debe fallar
            assertFalse(resultado, "El lector no debe poder eliminar comentarios que no le pertenecen");

            // Verificar que el DAO nunca fue invocado
            verify(comentarioDAO, never()).eliminar(any(Integer.class));
        }

        // ====================================================
        // TEST 3 (UNITARIO): Confirmación antes de eliminar
        // ====================================================
        /**
         * Dado que el lector intenta eliminar su comentario,
         * cuando el sistema solicita confirmación,
         * entonces el comentario solo debe eliminarse si confirma.
         */
        @Test
        @DisplayName("TEST 3 (UNITARIO): Confirmación requerida antes de eliminar")
        void testConfirmacionAntesDeEliminar() {
            // Simular confirmación (true)
            boolean confirmacionUsuario = true;
            when(comentarioDAO.eliminar(comentarioPropio.getId())).thenReturn(true);

            // CUANDO: El lector confirma la eliminación
            boolean resultadoConfirmado = comentarioService.eliminarComentarioConConfirmacion(
                    lector, comentarioPropio.getId(), confirmacionUsuario
            );

            // ENTONCES: Se debe eliminar correctamente
            assertTrue(resultadoConfirmado, "Debe eliminar el comentario si el lector confirma la acción");

            // Simular cancelación (false)
            boolean resultadoCancelado = comentarioService.eliminarComentarioConConfirmacion(
                    lector, comentarioPropio.getId(), false
            );

            // ENTONCES: No debe eliminarse si el lector cancela
            assertFalse(resultadoCancelado, "No debe eliminar el comentario si el lector cancela");
        }
    }
// ====================================================
// TEST 4 (UNITARIO): No elimina comentario sin lector asignado
// ====================================================
    /**
     * Dado que un comentario no tiene lector asociado,
     * cuando se intenta eliminar,
     * entonces el sistema no debe permitirlo y debe retornar false.
     */
    @Test
    @DisplayName("TEST 4 (UNITARIO): No elimina comentario sin lector asociado")
    void testEliminarComentarioSinLector() {
        // DADO: Comentario sin lector
        ComentarioManga comentarioSinLector = new ComentarioManga();
        comentarioSinLector.setId(200);
        comentarioSinLector.setLector(null);
        comentarioSinLector.setComentario("Comentario huérfano");

        // El DAO devuelve el comentario sin lector
        when(comentarioDAO.buscarPorId(comentarioSinLector.getId())).thenReturn(comentarioSinLector);

        // CUANDO: El lector intenta eliminarlo
        boolean resultado = comentarioService.eliminarComentario(comentarioSinLector.getId(), lector);

        // ENTONCES: No debe eliminarse
        assertFalse(resultado, "Debe retornar false si el comentario no tiene lector asociado");

        // Verificar que el DAO nunca intente eliminar
        verify(comentarioDAO, never()).eliminar(any(Integer.class));
    }


}

