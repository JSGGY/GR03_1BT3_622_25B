package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import static com.app.constants.AppConstants.ACTION_CREATE;
import static com.app.constants.AppConstants.ACTION_DELETE;
import static com.app.constants.AppConstants.ACTION_EDIT;
import static com.app.constants.AppConstants.PARAM_ACTION;
import static com.app.constants.AppConstants.PARAM_SCAN_ID;
import static com.app.constants.AppConstants.ROUTE_DASHBOARD;
import static com.app.constants.AppConstants.ROUTE_MANGA;
import static com.app.constants.AppConstants.SESSION_ADMIN_SCAN;
import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.AdminScan;
import com.app.model.EstadoManga;
import com.app.model.Manga;
import com.app.model.Scan;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/manga")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class MangaServlet extends BaseAuthenticatedServlet {
    private static final long serialVersionUID = 1L;

    private final MangaDAO mangaDAO = new MangaDAO();
    private final ScanDAO scanDAO = new ScanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        obtenerInfo(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validar sesión
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        String action = request.getParameter(PARAM_ACTION);
        if (action == null) {
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return;
        }

        switch (action) {
            case ACTION_CREATE:
                crearManga(request, response, adminScan);
                break;
            case ACTION_EDIT:
                editarManga(request, response);
                break;
            case ACTION_DELETE:
                eliminarManga(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
                break;
        }
    }

    /**
     * Obtiene y muestra la información de mangas para un scan específico.
     */
    private void obtenerInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validar sesión
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        // Validar y obtener scanId
        String scanIdParam = request.getParameter(PARAM_SCAN_ID);
        if (scanIdParam == null || scanIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return;
        }

        try {
            int scanId = Integer.parseInt(scanIdParam);

            // Validar propiedad del scan
            Scan scan = scanDAO.buscarPorId(scanId);
            if (!validateScanOwnership(adminScan, scan, request, response)) return;

            // Obtener mangas del scan
            List<Manga> mangas = mangaDAO.buscarPorScanId(scanId);

            // Establecer atributos
            request.setAttribute("scan", scan);
            request.setAttribute("mangas", mangas);
            request.setAttribute(SESSION_ADMIN_SCAN, adminScan);
            request.setAttribute("id", scanId);

            request.getRequestDispatcher("manga-dashboard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
        }
    }

    /**
     * Crea un nuevo manga en el scan especificado.
     */
    private void crearManga(HttpServletRequest request, HttpServletResponse response,
            AdminScan adminScan) throws ServletException, IOException {

        String scanIdParam = request.getParameter(PARAM_SCAN_ID);

        try {
            int scanId = Integer.parseInt(scanIdParam);
            Scan scan = scanDAO.buscarPorId(scanId);

            // Validar propiedad del scan
            if (!validateScanOwnership(adminScan, scan, request, response)) return;

            // Validar título
            String titulo = request.getParameter("titulo");
            if (titulo == null || titulo.trim().isEmpty()) {
                request.setAttribute("error", "El título es requerido");
                obtenerInfo(request, response);
                return;
            }
            
            // Obtener otros parámetros
            String descripcion = request.getParameter("descripcion");
            String estadoParam = request.getParameter("estado");
            
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
            response.sendRedirect(request.getContextPath() + ROUTE_MANGA + "?scanId=" + scanId);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
        }
    }

    private void editarManga(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
        
        try {
            Manga manga = obtenerMangaSeguro(request, adminScan);
            if (manga == null) {
                response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
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
            response.sendRedirect(request.getContextPath() + ROUTE_MANGA + "?scanId=" + manga.getScan().getId());

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
        }
    }

    private void eliminarManga(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
        
        try {
            Manga manga = obtenerMangaSeguro(request, adminScan);
            if (manga == null) {
                response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
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
            response.sendRedirect(request.getContextPath() + ROUTE_MANGA + "?scanId=" + scanId);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
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

    private Manga obtenerMangaSeguro(HttpServletRequest request, AdminScan adminScan) {
        try {
            int mangaId = Integer.parseInt(request.getParameter("mangaId"));
            Manga manga = mangaDAO.buscarPorId(mangaId);

            if (manga != null && manga.getScan().getCreadoPor().getId() == adminScan.getId()) {
                return manga;
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

}