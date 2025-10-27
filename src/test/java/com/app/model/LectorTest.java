package com.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Unitario - Gestión de Perfil de Lector")
class LectorTest {

    private Lector lector;

    @BeforeEach
    void setUp() {
        lector = new Lector();
        lector.setId(1);
        lector.setUsername("juan_lector");
        lector.setCorreo("juan@example.com");
        lector.setContraseña("password123");
    }

    @Test
    @DisplayName("Escenario 1: Given lector autenticado, when obtener resumen perfil, then retorna información formateada")
    public void given_lector_when_obtener_datos_perfil_then_datos_correctos() {
        System.out.println("\n=== Test 1: Visualización de Perfil ===");
        
        // Act - Obtener resumen del perfil
        String resumenPerfil = lector.obtenerResumenPerfil();
        
        // Assert - Verificar que el resumen no es nulo
        assertNotNull(resumenPerfil, "El resumen del perfil no debe ser nulo");
        
        // Assert - Verificar que contiene los datos del usuario
        assertTrue(resumenPerfil.contains("juan_lector"), "El resumen debe contener el username");
        assertTrue(resumenPerfil.contains("juan@example.com"), "El resumen debe contener el correo");
        
        // Assert - Verificar que el ID es válido
        assertTrue(lector.getId() > 0, "El ID debe ser mayor a 0");
        
        System.out.println("✅ Perfil visualizado correctamente:");
        System.out.println(resumenPerfil);
    }

    @Test
    @DisplayName("Escenario 2: Given lector, when actualizar con datos válidos, then perfil actualizado exitosamente")
    public void given_lector_when_actualizar_datos_validos_then_success() {
        System.out.println("\n=== Test 2: Edición Exitosa de Perfil ===");
        
        // Arrange
        String nuevoUsername = "juan_updated";
        String nuevoCorreo = "juan.updated@example.com";
        String nuevaContraseña = "newPassword456";
        
        System.out.println("Datos originales:");
        System.out.println("   Username: " + lector.getUsername());
        System.out.println("   Correo: " + lector.getCorreo());
        
        // Act - Actualizar perfil
        boolean resultado = lector.actualizarPerfil(nuevoUsername, nuevoCorreo, nuevaContraseña);
        
        // Assert - Verificar actualización exitosa
        assertTrue(resultado, "La actualización debe ser exitosa");
        assertEquals(nuevoUsername, lector.getUsername());
        assertEquals(nuevoCorreo, lector.getCorreo());
        assertEquals(nuevaContraseña, lector.getContraseña());
        
        System.out.println("✅ Perfil actualizado exitosamente:");
        System.out.println("   Nuevo Username: " + lector.getUsername());
        System.out.println("   Nuevo Correo: " + lector.getCorreo());
    }

    @Test
    @DisplayName("Escenario 3: Given datos de perfil, when validar, then rechaza datos inválidos")
    public void given_lector_when_actualizar_datos_invalidos_then_error() {
        // Act & Assert - Username vacío
        System.out.println("\n2️⃣ Probando username vacío...");
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            lector.validarDatosPerfil("", "test@example.com", "newPass123");
        });
        System.out.println("   ❌ Excepción capturada: " + exception1.getClass().getSimpleName());
        System.out.println("   📝 Mensaje: \"" + exception1.getMessage() + "\"");
        assertEquals("Username no puede estar vacío", exception1.getMessage());

        // Act & Assert - Correo inválido (sin @)
        System.out.println("\n3️⃣ Probando correo inválido...");
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            lector.validarDatosPerfil("nuevo_user", "correo-sin-arroba", "newPass123");
        });
        System.out.println("   ❌ Excepción capturada: " + exception2.getClass().getSimpleName());
        System.out.println("   📝 Mensaje: \"" + exception2.getMessage() + "\"");
        assertEquals("Correo electrónico inválido", exception2.getMessage());

        // Act & Assert - Contraseña vacía
        System.out.println("\n4️⃣ Probando contraseña vacía...");
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            lector.validarDatosPerfil("nuevo_user", "test@example.com", "");
        });
        System.out.println("   ❌ Excepción capturada: " + exception3.getClass().getSimpleName());
        System.out.println("   📝 Mensaje: \"" + exception3.getMessage() + "\"");
        assertEquals("Contraseña no puede estar vacía", exception3.getMessage());
    }
}

