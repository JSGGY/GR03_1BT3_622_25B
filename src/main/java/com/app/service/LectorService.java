package com.app.service;

import com.app.dao.LectorDAO;
import com.app.model.Lector;

/**
 * Servicio para gestionar operaciones de negocio relacionadas con Lectores.
 * Coordina la lógica de actualización de perfil y notificaciones.
 */
public class LectorService {
    
    private final LectorDAO lectorDAO;
    private final NotificacionEmailService emailService;
    
    /**
     * Constructor con inyección de dependencias.
     * 
     * @param lectorDAO DAO para operaciones de persistencia
     * @param emailService Servicio de notificaciones por email
     */
    public LectorService(LectorDAO lectorDAO, NotificacionEmailService emailService) {
        this.lectorDAO = lectorDAO;
        this.emailService = emailService;
    }
    
    /**
     * Actualiza el perfil de un lector y envía notificación por email.
     * 
     * @param lector Lector a actualizar
     * @param nuevoUsername Nuevo nombre de usuario
     * @param nuevoCorreo Nuevo correo electrónico
     * @param nuevaContraseña Nueva contraseña
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean actualizarPerfilConNotificacion(Lector lector, String nuevoUsername, 
                                                   String nuevoCorreo, String nuevaContraseña) {
        // Validar con el método del modelo
        if (!lector.actualizarPerfil(nuevoUsername, nuevoCorreo, nuevaContraseña)) {
            System.out.println("ERROR: Datos inválidos para actualizar perfil");
            return false;
        }
        
        // Guardar en BD
        boolean actualizado = lectorDAO.actualizar(lector);
        
        if (!actualizado) {
            System.out.println("ERROR: Falló al guardar el perfil en la base de datos");
            return false;
        }
        
        // Enviar notificación por email
        String mensaje = "Tu perfil ha sido actualizado exitosamente";
        boolean emailEnviado = emailService.enviarEmailActualizacionPerfil(nuevoCorreo, mensaje);
        
        if (emailEnviado) {
            System.out.println("✅ Perfil actualizado y notificación enviada a: " + nuevoCorreo);
        } else {
            System.out.println("⚠️ Perfil actualizado pero falló el envío del email");
        }
        
        return true;
    }
}

