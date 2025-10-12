package com.app.controller;

import java.io.IOException;
import java.io.OutputStream;

import com.app.dao.MangaDAO;
import com.app.model.Manga;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/imagen/*")
public class ImagenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MangaDAO mangaDAO = new MangaDAO();

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
            // Servir imagen por defecto
            response.sendRedirect(response.encodeRedirectURL("images/default-scan.svg"));
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
}