package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.app.constants.AppConstants.ACTION_CREATE;
import static com.app.constants.AppConstants.PARAM_ACTION;
import static com.app.constants.AppConstants.PARAM_MANGA_ID;
import static com.app.constants.AppConstants.PARAM_SCAN_ID;
import com.app.dao.CapituloDAO;
import com.app.dao.MangaDAO;
import com.app.model.AdminScan;
import com.app.model.Capitulo;
import com.app.model.CapituloImagen;
import com.app.model.Manga;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet({"/mostrarCapitulos", "/capitulo"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 100
)
public class CapituloServlet extends BaseAuthenticatedServlet {
    private static final long serialVersionUID = 1L;

    private final CapituloDAO capituloDAO = new CapituloDAO();
    private final MangaDAO mangaDAO = new MangaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        obtenerPaginas(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validar sesión usando método de clase base
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        String action = request.getParameter(PARAM_ACTION);
        if (ACTION_CREATE.equals(action)) {
            crearCapitulo(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
        }
    }

    private void obtenerPaginas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mangaIdParam = request.getParameter(PARAM_MANGA_ID);
        String scanId = request.getParameter(PARAM_SCAN_ID);

        if (mangaIdParam == null || mangaIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro mangaId");
            return;
        }

        int mangaId;
        try {
            mangaId = Integer.parseInt(mangaIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "mangaId inválido");
            return;
        }

        // Registrar visita del lector autenticado
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            com.app.model.Lector lector =
                    (com.app.model.Lector) session.getAttribute(com.app.constants.AppConstants.SESSION_LECTOR);
            if (lector != null) {
                com.app.model.Manga manga = mangaDAO.buscarPorId(mangaId);
                if (manga != null) {
                    com.app.service.HistorialVisitasService historialVisitasService = new com.app.service.HistorialVisitasService();
                    historialVisitasService.registrarVisita(lector, manga);
                    System.out.println("DEBUG: Historial de visitas actualizado para lector "
                            + lector.getUsername() + " en manga ID " + mangaId);
                }
            }
        }

        // Obtener capítulos del manga
        List<Capitulo> capitulos = capituloDAO.listarPorManga(mangaId);
        request.setAttribute("capitulos", capitulos);
        request.setAttribute("mangaId", mangaId);
        request.setAttribute("id", scanId != null ? scanId : "0");

        request.getRequestDispatcher("capitulos-dashboard-info.jsp").forward(request, response);
    }

    private void crearCapitulo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String mangaIdParam = request.getParameter(PARAM_MANGA_ID);
            String scanIdParam = request.getParameter(PARAM_SCAN_ID);
            String numeroParam = request.getParameter("numero");
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            
            // Validar parámetros requeridos
            boolean parametrosRequeridosMangaScan = (mangaIdParam == null || numeroParam == null ||
                    titulo == null || titulo.trim().isEmpty());
            if (parametrosRequeridosMangaScan) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros requeridos");
                return;
            }

            int mangaId = Integer.parseInt(mangaIdParam);
            int numero = Integer.parseInt(numeroParam);

            Manga manga = mangaDAO.buscarPorId(mangaId);
            if (manga == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Manga no encontrado");
                return;
            }

            Capitulo capitulo = new Capitulo();
            capitulo.setTitulo(titulo);
            capitulo.setNumero(numero);
            capitulo.setDescripcion(descripcion);
            capitulo.setManga(manga);

            // Procesar imágenes como BLOB
            Collection<Part> imageParts = request.getParts();
            List<CapituloImagen> imagenes = new ArrayList<>();

            for (Part part : imageParts) {
                if ("imagenes".equals(part.getName()) && part.getSize() > 0) {
                    CapituloImagen imagen = procesarImagenBlob(part);
                    if (imagen != null) {
                        imagenes.add(imagen);
                    }
                }
            }

            if (imagenes.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere al menos una imagen");
                return;
            }

            // Agregar imágenes al capítulo
            for (CapituloImagen imagen : imagenes) {
                capitulo.agregarImagen(imagen);
            }

            boolean success = capituloDAO.guardar(capitulo);

            if (success) {
                System.out.println("DEBUG: Capítulo creado exitosamente con " + imagenes.size() + " imágenes BLOB");
                response.sendRedirect(request.getContextPath() + "/manga?scanId=" + scanIdParam);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear el capítulo");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetros numéricos inválidos");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
        }
    }

    /**
     * Procesa una imagen y la convierte a BLOB
     */
    private CapituloImagen procesarImagenBlob(Part filePart) {
        try {
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
                                // Crear objeto CapituloImagen
                                CapituloImagen imagen = new CapituloImagen();
                                imagen.setImagenBlob(imageBytes);
                                imagen.setImagenTipo(mimeType);
                                imagen.setImagenNombre(fileName);
                                
                                System.out.println("DEBUG: Imagen procesada para capítulo - " + fileName + " (" + imageBytes.length + " bytes)");
                                return imagen;
                            } else {
                                System.err.println("ERROR: No se pudieron leer bytes de la imagen");
                            }
                        }
                    } else {
                        System.err.println("ERROR: Imagen inválida - " + fileName + " (Tipo: " + mimeType + ", Tamaño: " + fileSize + " bytes)");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR al procesar imagen del capítulo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}