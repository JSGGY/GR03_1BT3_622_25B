package com.app.controller;

import com.app.dao.CapituloDAO;
import com.app.model.AdminScan;
import com.app.model.Capitulo;
import com.app.model.Manga;
import com.app.model.Scan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/mostrarCapitulos")
public class CapituloServlet extends HttpServlet {

    private CapituloDAO capituloDAO = new CapituloDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            obtenerPaginas(request,response);
    }
    private void obtenerPaginas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mangaIdParam = request.getParameter("mangaId");
        String scanId = request.getParameter("scanId");
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
        request.setAttribute("id",scanId);

        request.getRequestDispatcher("capitulos-dashboard-info.jsp").forward(request, response);
    }
}
