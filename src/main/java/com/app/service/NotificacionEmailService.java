package com.app.service;

/**
 * Interfaz para el servicio de notificaciones por email.
 * Será simulada con Mockito en los tests.
 */
public interface NotificacionEmailService {
    
    /**
     * Envía un email de notificación al usuario cuando actualiza su perfil.
     * 
     * @param email Email del destinatario
     * @param mensaje Mensaje a enviar
     * @return true si el email se envió exitosamente, false en caso contrario
     */
    boolean enviarEmailActualizacionPerfil(String email, String mensaje);
}

