package com.app.controller;

import java.io.IOException;
import java.util.List;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.ComentarioManga;
import com.app.model.Lector;
import com.app.model.Manga;
import com.app.model.Scan;
import com.app.service.ComentarioMangaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/mangaDetalle")
public class MangaDetalleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MangaDAO mangaDAO;
    private ScanDAO scanDAO;
    private ComentarioMangaService comentarioService;

    @Override
    public void init() throws ServletException {
        mangaDAO = new MangaDAO();
        scanDAO = new ScanDAO();
        comentarioService = new ComentarioMangaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mangaIdParam = request.getParameter("mangaId");
        String scanIdParam = request.getParameter("scanId");

        try {
            int mangaId = Integer.parseInt(mangaIdParam);
            int scanId = Integer.parseInt(scanIdParam);

            // Obtener el manga
            Manga manga = mangaDAO.obtenerPorId(mangaId);
            if (manga == null) {
                response.sendRedirect(request.getContextPath() + "/mangaInvitados?scanId=" + scanId);
                return;
            }

            // Obtener el scan
            Scan scan = scanDAO.buscarPorId(scanId);
            if (scan == null) {
                response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
                return;
            }

            // Obtener comentarios del manga
            List<ComentarioManga> comentarios = comentarioService.obtenerComentariosDeMangaPorId(mangaId);

            // Verificar si hay un lector autenticado
            HttpSession session = request.getSession();
            Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);

            if (lector != null) {
                request.setAttribute("lector", lector);
                request.setAttribute("isLectorAutenticado", true);
                System.out.println("DEBUG: Lector autenticado viendo manga - " + lector.getUsername());

                // Cargar listas del lector para el botón de agregar
                com.app.service.ListaService listaService = new com.app.service.ListaService();
                java.util.List<com.app.model.Lista> listas = listaService.obtenerListasPorLector(lector.getId());
                request.setAttribute("listas", listas);
            } else {
                request.setAttribute("isLectorAutenticado", false);
                System.out.println("DEBUG: Invitado sin autenticar viendo manga");
            }

            // Pasar datos a la vista
            request.setAttribute("manga", manga);
            request.setAttribute("scan", scan);
            request.setAttribute("comentarios", comentarios);
            request.setAttribute("totalComentarios", comentarios.size());

            // Redirigir al JSP de detalle
            request.getRequestDispatcher("manga-detalle.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar el manga");
        }
    }
}

