package com.app.controller;

import java.io.IOException;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.dao.MangaDAO;
import com.app.dao.MangaLikeDAO;
import com.app.model.Lector;
import com.app.service.MangaLikeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/mangaLike")
public class MangaLikeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final MangaLikeService mangaLikeService = new MangaLikeService(new MangaDAO(), new MangaLikeDAO());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String mangaIdParam = request.getParameter("mangaId");
        String scanIdParam = request.getParameter("scanId");
        String origenParam = request.getParameter("origen");

        HttpSession session = request.getSession();
        Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);

        if (lector == null) {
            request.getSession().setAttribute("error", "Debes iniciar sesión para dar like");
            response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
            return;
        }

        if ("agregar".equals(action) && mangaIdParam != null && scanIdParam != null) {
            try {
                int mangaId = Integer.parseInt(mangaIdParam);
                int scanId = Integer.parseInt(scanIdParam);

                boolean exito = mangaLikeService.agregarLike(mangaId, lector);

                if (exito) {
                    request.getSession().setAttribute("mensaje", "¡Like agregado exitosamente!");
                } else {
                    request.getSession().setAttribute("error", "Ya diste like a este manga");
                }

                // Si viene de mangaDetalle, redirigir de vuelta a mangaDetalle
                if ("detalle".equals(origenParam)) {
                    response.sendRedirect(request.getContextPath() + "/mangaDetalle?mangaId=" + mangaId + "&scanId=" + scanId);
                } else {
                    // Si viene de mangaInvitados, redirigir a mangaInvitados
                    response.sendRedirect(request.getContextPath() + "/mangaInvitados?scanId=" + scanId);
                }
                return;

            } catch (NumberFormatException e) {
                request.getSession().setAttribute("error", "Error al procesar la solicitud");
            }
        }

        response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
    }
}