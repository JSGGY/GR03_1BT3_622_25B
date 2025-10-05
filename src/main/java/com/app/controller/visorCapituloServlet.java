package com.app.controller;

import com.app.dao.CapituloDAO;
import com.app.model.Capitulo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/seleccionarCapitulo")
public class visorCapituloServlet extends HttpServlet {

    private CapituloDAO capituloDAO = new CapituloDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        mostrar(request, response);
    }
    private void mostrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String capituloIdParam = request.getParameter("capituloId");
        String mangaIdParam = request.getParameter("mangaId");

        if (capituloIdParam == null || capituloIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro capituloId");
            return;
        }

        int capituloId;
        try {
            capituloId = Integer.parseInt(capituloIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "capituloId inválido");
            return;
        }

        Capitulo capitulo = capituloDAO.buscarPorId(capituloId);
        if (capitulo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Capítulo no encontrado");
            return;
        }
        request.setAttribute("capituloId", capituloIdParam);
        request.setAttribute("capitulo", capitulo);
        request.setAttribute("mangaId", mangaIdParam);

        request.getRequestDispatcher("capituloVisor-dashboard.jsp").forward(request, response);
    }
}
