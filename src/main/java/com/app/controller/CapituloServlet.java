package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.app.constants.AppConstants.ACTION_CREATE;
import static com.app.constants.AppConstants.PARAM_ACTION;
import static com.app.constants.AppConstants.PARAM_MANGA_ID;
import static com.app.constants.AppConstants.PARAM_SCAN_ID;
import com.app.dao.CapituloDAO;
import com.app.dao.MangaDAO;
import com.app.model.AdminScan;
import com.app.model.Capitulo;
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
    private static final String UPLOAD_DIR = "images" + File.separator + "capitulos";

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

            Collection<Part> imageParts = request.getParts();
            List<String> imageUrls = new ArrayList<>();

            for (Part part : imageParts) {
                if ("imagenes".equals(part.getName()) && part.getSize() > 0) {
                    String imageUrl = guardarImagen(part, request);
                    if (imageUrl != null) {
                        imageUrls.add(imageUrl);
                    }
                }
            }

            if (imageUrls.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Se requiere al menos una imagen");
                return;
            }

            capitulo.setImagenesUrls(imageUrls);

            boolean success = capituloDAO.guardar(capitulo);

            if (success) {
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

    private String guardarImagen(Part filePart, HttpServletRequest request) throws IOException {
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String mimeType = filePart.getContentType();

        if (mimeType != null && mimeType.startsWith("image/")) {
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String filePath = uploadFilePath + File.separator + uniqueFileName;
            filePart.write(filePath);

            return UPLOAD_DIR.replace(File.separator, "/") + "/" + uniqueFileName;
        }

        return null;
    }
}