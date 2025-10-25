package com.app.controller;

import java.io.IOException;
import java.io.OutputStream;

import com.app.dao.CapituloImagenDAO;
import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.CapituloImagen;
import com.app.model.Manga;
import com.app.model.Scan;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/imagen/*")
public class ImagenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MangaDAO mangaDAO = new MangaDAO();
    private final ScanDAO scanDAO = new ScanDAO();
    private final CapituloImagenDAO capituloImagenDAO = new CapituloImagenDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de manga requerido");
            return;
        }

        // Extraer el tipo y ID de la URL
        String[] pathParts = pathInfo.substring(1).split("/");
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de URL incorrecto");
            return;
        }

        String tipoImagen = pathParts[0]; // "manga", "scan", "capitulo"
        String idStr = pathParts[1];

        try {
            int id = Integer.parseInt(idStr);
            
            if ("manga".equals(tipoImagen)) {
                servirImagenManga(id, response);
            } else if ("scan".equals(tipoImagen)) {
                servirImagenScan(id, response);
            } else if ("capitulo".equals(tipoImagen)) {
                servirImagenCapitulo(id, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de imagen no soportado");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        }
    }

    private void servirImagenManga(int mangaId, HttpServletResponse response) throws IOException {
        Manga manga = mangaDAO.buscarPorId(mangaId);
        
        if (manga == null || manga.getPortadaBlob() == null) {
            // Enviar un error 404 en lugar de redirigir
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
            return;
        }

        // Configurar headers para la imagen
        response.setContentType(manga.getPortadaTipo() != null ? manga.getPortadaTipo() : "image/jpeg");
        response.setContentLength(manga.getPortadaBlob().length);
        
        // Configurar cache headers
        response.setHeader("Cache-Control", "max-age=3600"); // 1 hora de cache
        
        // Escribir los bytes de la imagen
        try (OutputStream out = response.getOutputStream()) {
            out.write(manga.getPortadaBlob());
            out.flush();
        }
    }

    private void servirImagenScan(int scanId, HttpServletResponse response) throws IOException {
        Scan scan = scanDAO.buscarPorId(scanId);
        
        if (scan == null || scan.getImagenBlob() == null) {
            // Enviar un error 404 en lugar de redirigir
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
            return;
        }

        // Configurar headers para la imagen
        response.setContentType(scan.getImagenTipo() != null ? scan.getImagenTipo() : "image/jpeg");
        response.setContentLength(scan.getImagenBlob().length);
        
        // Configurar cache headers
        response.setHeader("Cache-Control", "max-age=3600"); // 1 hora de cache
        
        // Escribir los bytes de la imagen
        try (OutputStream out = response.getOutputStream()) {
            out.write(scan.getImagenBlob());
            out.flush();
        }
    }

    private void servirImagenCapitulo(int capituloImagenId, HttpServletResponse response) throws IOException {
        System.out.println("DEBUG ImagenServlet: Solicitando imagen de capítulo con ID: " + capituloImagenId);
        
        CapituloImagen capituloImagen = capituloImagenDAO.buscarPorId(capituloImagenId);
        
        if (capituloImagen == null) {
            System.err.println("ERROR: CapituloImagen con ID " + capituloImagenId + " NO existe en la BD");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
            return;
        }
        
        if (capituloImagen.getImagenBlob() == null) {
            System.err.println("ERROR: CapituloImagen ID " + capituloImagenId + " existe pero imagenBlob es NULL");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Imagen no encontrada");
            return;
        }
        
        System.out.println("DEBUG: CapituloImagen ID " + capituloImagenId + " encontrada, tamaño: " + capituloImagen.getImagenBlob().length + " bytes");

        // Configurar headers para la imagen
        response.setContentType(capituloImagen.getImagenTipo() != null ? capituloImagen.getImagenTipo() : "image/jpeg");
        response.setContentLength(capituloImagen.getImagenBlob().length);
        
        // Configurar cache headers
        response.setHeader("Cache-Control", "max-age=3600"); // 1 hora de cache
        
        // Escribir los bytes de la imagen
        try (OutputStream out = response.getOutputStream()) {
            out.write(capituloImagen.getImagenBlob());
            out.flush();
        }
    }
}