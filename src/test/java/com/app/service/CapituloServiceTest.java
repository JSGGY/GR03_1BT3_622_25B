package com.app.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dao.CapituloDAO;
import com.app.dao.LectorDAO;
import com.app.model.Capitulo;
import com.app.model.Lector;
import com.app.model.Manga;
/*
@ExtendWith(MockitoExtension.class)
@DisplayName("Test Unitario con Mocks - CapituloService")
class CapituloServiceTest {

    @Mock
    private CapituloDAO capituloDAO;
    
    @Mock
    private LectorDAO lectorDAO;
    
    @Mock
    private NotificacionService notificacionService;
    
    @InjectMocks
    private CapituloService capituloService;
    
    private Capitulo capitulo;
    private Manga manga;
    private List<Lector> lectores;
    
    @BeforeEach
    void setUp() {
        manga = new Manga();
        manga.setId(1);
        manga.setTitulo("One Piece");
        
        capitulo = new Capitulo();
        capitulo.setId(1);
        capitulo.setTitulo("Capítulo 1000");
        capitulo.setNumero(1000);
        capitulo.setManga(manga);
        
        Lector lector1 = new Lector();
        lector1.setId(1);
        lector1.setCorreo("lector1@mail.com");
        
        Lector lector2 = new Lector();
        lector2.setId(2);
        lector2.setCorreo("lector2@mail.com");
        
        lectores = Arrays.asList(lector1, lector2);
    }
    
    @Test
    @DisplayName("Given nuevo capítulo, when publicar, then notifica a todos los seguidores")
    public void given_new_capitulo_when_publicar_then_notify_all_followers() {
        System.out.println("\nTest con Mockito iniciado");
        System.out.println("Mocks configurados: DAO y NotificacionService simulados");
        
        // Arrange - Configurar comportamiento de los mocks
        when(capituloDAO.guardar(capitulo)).thenReturn(true);
        when(lectorDAO.buscarSeguidoresPorManga(manga.getId())).thenReturn(lectores);
        when(notificacionService.enviarNotificacion(anyString(), anyString())).thenReturn(true);
        
        System.out.println("Ejecutando: publicarCapitulo()");
        
        // Act - Ejecutar el método a probar
        boolean result = capituloService.publicarCapitulo(capitulo);
        
        // Assert - Verificar resultado y que los mocks fueron llamados correctamente
        assertTrue(result);
        verify(capituloDAO, times(1)).guardar(capitulo);
        verify(lectorDAO, times(1)).buscarSeguidoresPorManga(manga.getId());
        verify(notificacionService, times(2)).enviarNotificacion(anyString(), anyString());
        
        System.out.println("Resultado: Exito");
        System.out.println("CapituloDAO.guardar llamado 1 vez");
        System.out.println("LectorDAO.buscarSeguidores llamado 1 vez");
        System.out.println("NotificacionService.enviar llamado 2 veces");
        System.out.println("Test pasado - Mockito simulo todo sin BD\n");
    }
}
*/
