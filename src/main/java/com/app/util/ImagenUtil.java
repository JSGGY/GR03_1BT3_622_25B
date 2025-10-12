package com.app.util;

import java.util.Arrays;
import java.util.List;

public class ImagenUtil {
    
    // Tipos MIME permitidos para imágenes
    private static final List<String> TIPOS_PERMITIDOS = Arrays.asList(
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "image/webp"
    );
    
    // Tamaño máximo en bytes (5MB)
    private static final long TAMANO_MAXIMO = 5 * 1024 * 1024; 
    
    /**
     * Valida si el tipo MIME es una imagen permitida
     */
    public static boolean esImagenValida(String mimeType) {
        return mimeType != null && TIPOS_PERMITIDOS.contains(mimeType.toLowerCase());
    }
    
    /**
     * Valida si el tamaño de la imagen está dentro de los límites
     */
    public static boolean esTamañoValido(long tamaño) {
        return tamaño > 0 && tamaño <= TAMANO_MAXIMO;
    }
    
    /**
     * Obtiene la extensión de archivo basada en el tipo MIME
     */
    public static String obtenerExtension(String mimeType) {
        if (mimeType == null) return ".jpg";
        
        switch (mimeType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                return ".jpg";
        }
    }
    
    /**
     * Valida completamente una imagen (tipo y tamaño)
     */
    public static boolean validarImagen(String mimeType, long tamaño) {
        return esImagenValida(mimeType) && esTamañoValido(tamaño);
    }
    
    /**
     * Obtiene el tamaño máximo permitido en MB para mostrar en mensajes
     */
    public static String getTamañoMaximoMB() {
        return String.valueOf(TAMANO_MAXIMO / (1024 * 1024)) + " MB";
    }
}