package com.app.service;

import com.app.util.ImageUtil;
import com.app.util.ImagenUtil;

public class ImageValidatorService {

    private VirusScannerService virusScannerService;
    private ImageUtil imageUtil;

    // Constructor para inyección de dependencias
    public ImageValidatorService(VirusScannerService virusScannerService,
                                  ImageUtil imageUtil) {
        this.virusScannerService = virusScannerService;
        this.imageUtil = imageUtil;
    }

    /**
     * Valida completamente una imagen verificando seguridad, formato y dimensiones
     */
    public boolean validarImagenCompleta(byte[] imagenBytes, String mimeType) {
        if (imagenBytes == null || imagenBytes.length == 0) {
            return false;
        }

        // 1️⃣ Verificar que no contenga virus
        if (!virusScannerService.esSegura(imagenBytes)) {
            return false;
        }

        // 2️⃣ Verificar formato válido
        if (!imageUtil.esFormatoValido(mimeType)) {
            return false;
        }

        // 3️⃣ Verificar dimensiones válidas
        if (!imageUtil.tieneDimensionesValidas(imagenBytes)) {
            return false;
        }

        return true;
    }
}
