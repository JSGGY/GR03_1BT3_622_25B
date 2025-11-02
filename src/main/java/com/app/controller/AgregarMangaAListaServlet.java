package com.app.controller;

import java.io.IOException;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.model.Lector;
import com.app.service.ListaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/agregarMangaALista")
public class AgregarMangaAListaServlet extends HttpServlet {

    private final ListaService listaService = new ListaService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);

        if (lector == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión");
            return;
        }

        String listaIdParam = request.getParameter("listaId");
        String mangaIdParam = request.getParameter("mangaId");
        String scanIdParam = request.getParameter("scanId");

        if (listaIdParam == null || mangaIdParam == null) {
            request.getSession().setAttribute("error", "Parámetros inválidos");
            response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
            return;
        }

        try {
            int listaId = Integer.parseInt(listaIdParam);
            int mangaId = Integer.parseInt(mangaIdParam);

            boolean exito = listaService.agregarMangaALista(listaId, mangaId, lector.getId());
            if (exito) {
                request.getSession().setAttribute("mensaje", "Manga agregado a la lista exitosamente");
            } else {
                request.getSession().setAttribute("error", "El manga ya está en la lista o hubo un error");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID inválido");
        }

        // Redirigir de vuelta a la página de mangas si hay scanId, sino al perfil
        if (scanIdParam != null && !scanIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/mangaInvitados?scanId=" + scanIdParam);
        } else {
            response.sendRedirect(request.getContextPath() + "/perfil");
        }
    }
}

