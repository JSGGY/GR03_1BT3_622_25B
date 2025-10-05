package com.app.controller;

import java.io.IOException;
import java.util.List;

import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.Manga;
import com.app.model.Scan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mangaInvitados")
public class MangaInvitadoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MangaDAO mangaDAO = new MangaDAO();
    private ScanDAO scanDAO = new ScanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        obtenerInfo(request, response);
    }

    private void obtenerInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String scanIdParam = request.getParameter("scanId");

        try {
            int scanId = Integer.parseInt(scanIdParam);

            Scan scan = scanDAO.buscarPorId(scanId);
            if (scan == null) {
                response.sendRedirect("dashboard-invitado");
                return;
            }

            List<Manga> mangas = mangaDAO.buscarPorScanId(scanId);

            request.setAttribute("scan", scan);
            request.setAttribute("mangas", mangas);
            request.setAttribute("id", scanId);


            request.getRequestDispatcher("manga-dashboard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard-invitado");
        }
    }
}
