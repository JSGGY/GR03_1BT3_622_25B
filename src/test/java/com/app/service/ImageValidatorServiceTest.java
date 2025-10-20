package com.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.util.ImageUtil;

@ExtendWith(MockitoExtension.class)
class ImageValidatorServiceTest {

    @Mock
    private VirusScannerService virusScannerService;

    @Mock
    private ImageUtil imageUtil;

    @InjectMocks
    private ImageValidatorService imageValidatorService;

    private byte[] imagenBytes;
    private String mimeType;

    @BeforeEach
    void setUp() {
        imagenBytes = new byte[2000]; // Simula una imagen de 2KB
        mimeType = "image/jpeg";
    }

    @Test
    void given_clean_image_when_validate_then_returns_true() {
        when(virusScannerService.esSegura(imagenBytes)).thenReturn(true);
        when(imageUtil.esFormatoValido(mimeType)).thenReturn(true);
        when(imageUtil.tieneDimensionesValidas(imagenBytes)).thenReturn(true);

        boolean result = imageValidatorService.validarImagenCompleta(imagenBytes, mimeType);

        assertTrue(result);
        verify(virusScannerService).esSegura(imagenBytes);
        verify(imageUtil).esFormatoValido(mimeType);
        verify(imageUtil).tieneDimensionesValidas(imagenBytes);
    }

    @Test
    void given_infected_image_when_validate_then_returns_false() {
        when(virusScannerService.esSegura(imagenBytes)).thenReturn(false);

        boolean result = imageValidatorService.validarImagenCompleta(imagenBytes, mimeType);

        assertFalse(result);
        verify(virusScannerService).esSegura(imagenBytes);
        verify(imageUtil, never()).esFormatoValido(anyString());
        verify(imageUtil, never()).tieneDimensionesValidas(any());
    }

    @Test
    void given_invalid_format_when_validate_then_returns_false() {
        when(virusScannerService.esSegura(imagenBytes)).thenReturn(true);
        when(imageUtil.esFormatoValido("application/pdf")).thenReturn(false);

        boolean result = imageValidatorService.validarImagenCompleta(imagenBytes, "application/pdf");

        assertFalse(result);
        verify(virusScannerService).esSegura(imagenBytes);
        verify(imageUtil).esFormatoValido("application/pdf");
        verify(imageUtil, never()).tieneDimensionesValidas(any());
    }

    @Test
    void given_null_bytes_when_validate_then_returns_false() {
        boolean result = imageValidatorService.validarImagenCompleta(null, mimeType);

        assertFalse(result);
        verify(virusScannerService, never()).esSegura(any());
        verify(imageUtil, never()).esFormatoValido(anyString());
    }
}
