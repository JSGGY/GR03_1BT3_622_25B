package com.app.controller;

import java.io.IOException;

import com.app.dao.CapituloDAO;
import com.app.model.Capitulo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        String scanIdParam = request.getParameter("scanId");

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
        
        // Convert mangaId to Integer if it's not null
        if (mangaIdParam != null && !mangaIdParam.isEmpty()) {
            try {
                Integer mangaId = Integer.parseInt(mangaIdParam);
                request.setAttribute("mangaId", mangaId);
            } catch (NumberFormatException e) {
                request.setAttribute("mangaId", null);
            }
        } else {
            request.setAttribute("mangaId", null);
        }
        
        // Pass scanId as String
        request.setAttribute("scanId", scanIdParam);

        request.getRequestDispatcher("capituloVisor-dashboard.jsp").forward(request, response);
    }
}
