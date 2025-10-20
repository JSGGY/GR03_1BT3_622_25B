package com.app.service;

/**
 * Servicio de notificaciones (interfaz)
 * Mockito simulará este servicio en los tests
 * NO necesita implementación real para los tests unitarios
 */
public interface NotificacionService {

    boolean enviarNotificacion(String email, String mensaje);
}

