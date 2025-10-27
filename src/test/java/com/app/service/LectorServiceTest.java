package com.app.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dao.LectorDAO;
import com.app.model.Lector;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unitario con Mock - Servicio de Notificaciones Email")
class LectorServiceTest {

    @Mock
    private LectorDAO lectorDAO;

    @Mock
    private NotificacionEmailService emailService;

    @InjectMocks
    private LectorService lectorService;

    private Lector lector;

    @BeforeEach
    void setUp() {
        lector = new Lector();
        lector.setId(1);
        lector.setUsername("carlos_reader");
        lector.setCorreo("carlos@example.com");
        lector.setContraseña("oldPassword123");
    }

    @Test
    @DisplayName("Given perfil actualizado, when guardar, then envía notificación email")
    public void given_perfil_actualizado_when_guardar_then_envia_notificacion_email() {
        System.out.println("\n=== Test con Mock: Notificación Email ===");
        System.out.println("Mockito simulará LectorDAO y NotificacionEmailService");
        
        // Arrange
        String nuevoUsername = "carlos_updated";
        String nuevoCorreo = "carlos.updated@example.com";
        String nuevaContraseña = "newSecurePass456";
        
        // Configurar mocks
        when(lectorDAO.actualizar(any(Lector.class))).thenReturn(true);
        when(emailService.enviarEmailActualizacionPerfil(anyString(), anyString())).thenReturn(true);
        // Act
        boolean resultado = lectorService.actualizarPerfilConNotificacion(
            lector, nuevoUsername, nuevoCorreo, nuevaContraseña
        );
        
        assertTrue(resultado);
        System.out.println("✅ Perfil actualizado y notificación enviada a: " + nuevoCorreo);
        
        verify(lectorDAO, times(1)).actualizar(lector);
        System.out.println("✅ LectorDAO.actualizar() fue llamado 1 vez");
        
        verify(emailService, times(1)).enviarEmailActualizacionPerfil(
            eq(nuevoCorreo), 
            contains("actualizado exitosamente")
        );
        System.out.println("   Email enviado a: " + nuevoCorreo);
        System.out.println("   - Email de notificación enviado");
        System.out.println("   - Mockito verificó las interacciones correctamente");
    }
}

