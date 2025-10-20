package com.app.service;

import java.util.List;

import com.app.dao.CapituloDAO;
import com.app.dao.LectorDAO;
import com.app.model.Capitulo;
import com.app.model.Lector;

public class CapituloService {
    
    private final CapituloDAO capituloDAO;
    private final LectorDAO lectorDAO;
    private final NotificacionService notificacionService;
    
    public CapituloService(CapituloDAO capituloDAO, LectorDAO lectorDAO, 
                          NotificacionService notificacionService) {
        this.capituloDAO = capituloDAO;
        this.lectorDAO = lectorDAO;
        this.notificacionService = notificacionService;
    }
    
    /**
     * Publica un nuevo capítulo y notifica a todos los seguidores del manga.
     * 
     * @param capitulo El capítulo a publicar
     * @return true si se publicó exitosamente, false en caso contrario
     */
    public boolean publicarCapitulo(Capitulo capitulo) {
        // Paso 1: Guardar el capítulo en la base de datos
        boolean guardado = capituloDAO.guardar(capitulo);
        
        if (!guardado) {
            return false;
        }
        
        List<Lector> seguidores = lectorDAO.buscarSeguidoresPorManga(
            capitulo.getManga().getId()
        );
        
        // Enviar notificación a cada seguidor
        for (Lector lector : seguidores) {
            String mensaje = String.format(
                "Nuevo capítulo '%s' de '%s' disponible!",
                capitulo.getTitulo(),
                capitulo.getManga().getTitulo()
            );
            
            // Enviar notificación (el resultado individual no afecta el retorno)
            notificacionService.enviarNotificacion(lector.getCorreo(), mensaje);
        }
        
        // Retornar éxito
        return true;
    }
}

