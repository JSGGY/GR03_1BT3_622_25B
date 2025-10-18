package com.app.controller;

import static com.app.constants.AppConstants.*;

import java.io.IOException;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/manga")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class MangaServlet extends BaseAuthenticatedServlet {
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

        // Validar sesión
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        String action = request.getParameter(PARAM_ACTION);
        if (action == null) {
            response.sendRedirect(ROUTE_DASHBOARD);
            return;
        }

        switch (action) {
            case ACTION_CREATE:
                crearManga(request, response, adminScan);
                break;
            case ACTION_EDIT:
                editarManga(request, response, adminScan);
                break;
            case ACTION_DELETE:
                eliminarManga(request, response, adminScan);
                break;
            default:
                response.sendRedirect(ROUTE_DASHBOARD);
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
            response.sendRedirect(ROUTE_DASHBOARD);
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
            response.sendRedirect(ROUTE_DASHBOARD);
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

            // Crear y configurar nuevo manga
            Manga nuevoManga = buildMangaFromRequest(request, scan);

            // Guardar manga
            boolean guardado = mangaDAO.guardar(nuevoManga);

            if (guardado) {
                System.out.println("DEBUG: Manga creado exitosamente: " + titulo);
            } else {
                request.setAttribute("error", "Error al crear el manga");
            }

            // Redirigir de vuelta al manga dashboard
            response.sendRedirect(ROUTE_MANGA + "?scanId=" + scanId);

        } catch (NumberFormatException e) {
            response.sendRedirect(ROUTE_DASHBOARD);
        }
    }

    /**
     * Edita un manga existente.
     */
    private void editarManga(HttpServletRequest request, HttpServletResponse response,
            AdminScan adminScan) throws ServletException, IOException {

        String mangaIdParam = request.getParameter(PARAM_MANGA_ID);

        try {
            int mangaId = Integer.parseInt(mangaIdParam);
            Manga manga = mangaDAO.buscarPorId(mangaId);

            // Validar que el manga pertenece a un scan del admin
            if (!validateMangaOwnership(adminScan, manga, request, response)) return;

            // Actualizar campos del manga
            updateMangaFields(request, manga);

            // Actualizar manga
            boolean actualizado = mangaDAO.guardar(manga);

            if (actualizado) {
                System.out.println("DEBUG: Manga actualizado exitosamente: " + manga.getTitulo());
            } else {
                request.setAttribute("error", "Error al actualizar el manga");
            }

            // Redirigir de vuelta al manga dashboard
            response.sendRedirect(ROUTE_MANGA + "?scanId=" + manga.getScan().getId());

        } catch (NumberFormatException e) {
            response.sendRedirect(ROUTE_DASHBOARD);
        }
    }

    /**
     * Elimina un manga existente.
     */
    private void eliminarManga(HttpServletRequest request, HttpServletResponse response,
            AdminScan adminScan) throws ServletException, IOException {

        String mangaIdParam = request.getParameter(PARAM_MANGA_ID);

        try {
            int mangaId = Integer.parseInt(mangaIdParam);
            Manga manga = mangaDAO.buscarPorId(mangaId);

            // Validar que el manga pertenece a un scan del admin
            if (!validateMangaOwnership(adminScan, manga, request, response)) return;

            int scanId = manga.getScan().getId();

            // Eliminar manga
            boolean eliminado = mangaDAO.eliminar(mangaId);

            if (eliminado) {
                System.out.println("DEBUG: Manga eliminado exitosamente: " + manga.getTitulo());
            } else {
                request.setAttribute("error", "Error al eliminar el manga");
            }

            // Redirigir de vuelta al manga dashboard
            response.sendRedirect(ROUTE_MANGA + "?scanId=" + scanId);

        } catch (NumberFormatException e) {
            response.sendRedirect(ROUTE_DASHBOARD);
        }
    }

    /**
     * Valida que el manga pertenece a un scan del admin autenticado.
     */
    private boolean validateMangaOwnership(AdminScan admin, Manga manga,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (manga == null || manga.getScan().getCreadoPor().getId() != admin.getId()) {
            response.sendRedirect(ROUTE_DASHBOARD);
            return false;
        }
        return true;
    }

    /**
     * Construye un objeto Manga a partir de los parámetros de la petición.
     */
    private Manga buildMangaFromRequest(HttpServletRequest request, Scan scan) {
        Manga manga = new Manga();
        manga.setTitulo(request.getParameter("titulo"));
        manga.setDescripcion(request.getParameter("descripcion"));
        manga.setScan(scan);

        // Establecer estado
        String estadoParam = request.getParameter("estado");
        manga.setEstado(parseEstadoManga(estadoParam));

        // TODO: Manejar subida de imagen de portada

        return manga;
    }

    /**
     * Actualiza los campos de un manga existente con los valores de la petición.
     */
    private void updateMangaFields(HttpServletRequest request, Manga manga) {
        String titulo = request.getParameter("titulo");
        String descripcion = request.getParameter("descripcion");
        String estadoParam = request.getParameter("estado");

        if (titulo != null && !titulo.trim().isEmpty()) {
            manga.setTitulo(titulo);
        }
        manga.setDescripcion(descripcion);

        // Establecer estado
        if (estadoParam != null) {
            manga.setEstado(parseEstadoManga(estadoParam));
        }

        // TODO: Manejar actualización de imagen de portada
    }

    /**
     * Parsea el estado del manga desde un String.
     * Retorna EN_PROGRESO por defecto si el valor es inválido.
     */
    private EstadoManga parseEstadoManga(String estadoParam) {
        if (estadoParam == null) {
            return EstadoManga.EN_PROGRESO;
        }

        try {
            return EstadoManga.valueOf(estadoParam);
        } catch (IllegalArgumentException e) {
            return EstadoManga.EN_PROGRESO;
        }
    }
}