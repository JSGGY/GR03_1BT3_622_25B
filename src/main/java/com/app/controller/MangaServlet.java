package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.AdminScan;
import com.app.model.EstadoManga;
import com.app.model.Manga;
import com.app.model.Scan;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/manga")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class MangaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MangaDAO mangaDAO = new MangaDAO();
    private ScanDAO scanDAO = new ScanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        obtenerInfo(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminScan") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("dashboard");
            return;
        }

        switch (action) {
            case "create":
                crearManga(request, response);
                break;
            case "edit":
                editarManga(request, response);
                break;
            case "delete":
                eliminarManga(request, response);
                break;
            default:
                response.sendRedirect("dashboard");
                break;
        }
    }

    private void obtenerInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminScan") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String scanIdParam = request.getParameter("scanId");
        if (scanIdParam == null || scanIdParam.isEmpty()) {
            response.sendRedirect("dashboard");
            return;
        }

        try {
            int scanId = Integer.parseInt(scanIdParam);
            AdminScan adminScan = (AdminScan) session.getAttribute("adminScan");


            Scan scan = scanDAO.buscarPorId(scanId);
            if (scan == null || scan.getCreadoPor().getId() != adminScan.getId()) {
                response.sendRedirect("dashboard");
                return;
            }

            List<Manga> mangas = mangaDAO.buscarPorScanId(scanId);

            request.setAttribute("scan", scan);
            request.setAttribute("mangas", mangas);
            request.setAttribute("adminScan", adminScan);
            request.setAttribute("id", scanId);

            request.getRequestDispatcher("manga-dashboard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard");
        }
    }

    private void crearManga(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
        String scanIdParam = request.getParameter("scanId");
        
        try {
            int scanId = Integer.parseInt(scanIdParam);
            Scan scan = scanDAO.buscarPorId(scanId);
            
            if (scan == null || scan.getCreadoPor().getId() != adminScan.getId()) {
                response.sendRedirect("dashboard");
                return;
            }
            
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String estadoParam = request.getParameter("estado");
            
            if (titulo == null || titulo.trim().isEmpty()) {
                request.setAttribute("error", "El título es requerido");
                obtenerInfo(request, response);
                return;
            }
            
            // Crear nuevo manga
            Manga nuevoManga = new Manga();
            nuevoManga.setTitulo(titulo);
            nuevoManga.setDescripcion(descripcion);
            nuevoManga.setScan(scan);
            
            // Establecer estado
            if (estadoParam != null) {
                try {
                    nuevoManga.setEstado(EstadoManga.valueOf(estadoParam));
                } catch (IllegalArgumentException e) {
                    nuevoManga.setEstado(EstadoManga.EN_PROGRESO);
                }
            } else {
                nuevoManga.setEstado(EstadoManga.EN_PROGRESO);
            }
            
            // Manejar subida de imagen de portada
            procesarImagenPortada(request, nuevoManga);
            
            // Guardar manga
            boolean guardado = mangaDAO.guardar(nuevoManga);
            
            if (!guardado) {
                System.err.println("ERROR: Falló al guardar el manga: " + titulo);
                request.setAttribute("error", "Error al crear el manga");
            }
            
            // Redirigir de vuelta al manga dashboard
            response.sendRedirect("manga?scanId=" + scanId);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard");
        }
    }

    private void editarManga(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
        
        try {
            Manga manga = obtenerMangaSeguro(request, adminScan);
            if (manga == null) {
                response.sendRedirect("dashboard");
                return;
            }

            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String estadoParam = request.getParameter("estado");
            
            if (titulo != null && !titulo.trim().isEmpty()) {
                manga.setTitulo(titulo);
            }
            manga.setDescripcion(descripcion);
            
            // Establecer estado
            if (estadoParam != null) {
                try {
                    manga.setEstado(EstadoManga.valueOf(estadoParam));
                } catch (IllegalArgumentException e) {
                    // Mantener el estado actual si es inválido
                }
            }
            
            // Manejar actualización de imagen de portada
            procesarImagenPortada(request, manga);
            
            // Actualizar manga
            boolean actualizado = mangaDAO.guardar(manga);
            
            if (!actualizado) {
                request.setAttribute("error", "Error al actualizar el manga");
            }
            
            // Redirigir de vuelta al manga dashboard
            response.sendRedirect("manga?scanId=" + manga.getScan().getId());
            
        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard");
        }
    }

    private void eliminarManga(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
        
        try {
            Manga manga = obtenerMangaSeguro(request, adminScan);
            if (manga == null) {
                response.sendRedirect("dashboard");
                return;
            }
            
            int scanId = manga.getScan().getId();
            
            // Eliminar manga
            boolean eliminado = mangaDAO.eliminar(manga.getId());
            // boolean eliminado = mangaDAO.eliminar(mangaId);
            
            if (!eliminado) {
                request.setAttribute("error", "Error al eliminar el manga");
            }
            
            // Redirigir de vuelta al manga dashboard
            response.sendRedirect("manga?scanId=" + scanId);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard");
        }
    }

    private void procesarImagenPortada(HttpServletRequest request, Manga manga) {
        try {
            Part filePart = request.getPart("imagenPortada");
            
            if (filePart != null) {
                long fileSize = filePart.getSize();
                String fileName = filePart.getSubmittedFileName();
                String mimeType = filePart.getContentType();
                
                if (fileSize > 0) {
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        fileName = Paths.get(fileName).getFileName().toString();
                        
                        // Validar que sea una imagen válida
                        if (com.app.util.ImagenUtil.validarImagen(mimeType, fileSize)) {
                            // Leer los bytes de la imagen
                            try (InputStream inputStream = filePart.getInputStream()) {
                                byte[] imageBytes = inputStream.readAllBytes();
                                
                                // Verificar que se leyeron bytes
                                if (imageBytes != null && imageBytes.length > 0) {
                                    // Guardar en BLOB
                                    manga.setPortadaBlob(imageBytes);
                                    manga.setPortadaTipo(mimeType);
                                    manga.setPortadaNombre(fileName);
                                    
                                    System.out.println("DEBUG: Imagen procesada - " + fileName + " (" + imageBytes.length + " bytes)");
                                } else {
                                    System.err.println("ERROR: No se pudieron leer bytes de la imagen");
                                }
                            }
                        } else {
                            System.err.println("ERROR: Imagen inválida - " + fileName + " (Tipo: " + mimeType + ", Tamaño: " + fileSize + " bytes)");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR al procesar imagen de portada: " + e.getMessage());
        }
    }

}