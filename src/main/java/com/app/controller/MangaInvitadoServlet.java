package com.app.controller;

import java.io.IOException;
import java.util.List;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.dao.MangaDAO;
import com.app.dao.MangaLikeDAO;
import com.app.dao.ScanDAO;
import com.app.model.Lector;
import com.app.model.Manga;
import com.app.model.Scan;
import com.app.service.MangaLikeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/mangaInvitados")
public class MangaInvitadoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MangaDAO mangaDAO = new MangaDAO();
    private ScanDAO scanDAO = new ScanDAO();
    private MangaLikeService mangaLikeService = new MangaLikeService(new MangaDAO(), new MangaLikeDAO());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        obtenerInfo(request, response);
    }

    private void obtenerInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String scanIdParam = request.getParameter("scanId");
        String ordenarPorLikes = request.getParameter("ordenarPorLikes");

        try {
            int scanId = Integer.parseInt(scanIdParam);

            Scan scan = scanDAO.buscarPorId(scanId);
            if (scan == null) {
                response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
                return;
            }

            List<Manga> mangas;

            if ("true".equals(ordenarPorLikes)) {
                mangas = mangaLikeService.obtenerMangasOrdenadosPorLikes(scanId);
                request.setAttribute("ordenadoPorLikes", true);
            } else {
                mangas = mangaDAO.buscarPorScanId(scanId);
                request.setAttribute("ordenadoPorLikes", false);
            }

            HttpSession session = request.getSession();
            Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);

            if (lector != null) {
                request.setAttribute("lector", lector);
                request.setAttribute("isLectorAutenticado", true);

                com.app.service.ListaService listaService = new com.app.service.ListaService();
                java.util.List<com.app.model.Lista> listas = listaService.obtenerListasPorLector(lector.getId());
                request.setAttribute("listas", listas);
            } else {
                request.setAttribute("isLectorAutenticado", false);
            }

            request.setAttribute("scan", scan);
            request.setAttribute("mangas", mangas);
            request.setAttribute("id", scanId);

            request.getRequestDispatcher("manga-invitados.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
        }
    }
}