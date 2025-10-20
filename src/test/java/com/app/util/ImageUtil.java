package com.app.util;

public class ImageUtil {

    public boolean esFormatoValido(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.equals("image/jpeg") ||
                mimeType.equals("image/png") ||
                mimeType.equals("image/webp") ||
                mimeType.equals("image/jpg");
    }

    public boolean tieneDimensionesValidas(byte[] imagenBytes) {
        if (imagenBytes == null) return false;
        return imagenBytes.length >= 1000 && imagenBytes.length <= 10 * 1024 * 1024; // 10 MB
    }
}



