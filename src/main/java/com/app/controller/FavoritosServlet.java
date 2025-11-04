package com.app.controller;

import com.app.dao.LectorDAO;
import com.app.dao.MangaDAO;
import com.app.model.Lector;
import com.app.model.Manga;
import com.app.service.FavoritosService;

import static com.app.constants.AppConstants.SESSION_LECTOR;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/favoritos")
public class FavoritosServlet extends HttpServlet {

    private FavoritosService favoritosService;
    private MangaDAO mangaDAO;
    private LectorDAO lectorDAO;

    @Override
    public void init() throws ServletException {
        favoritosService = new FavoritosService();
        mangaDAO = new MangaDAO();
        lectorDAO = new LectorDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        Lector lectorSesion = (Lector) request.getSession().getAttribute(SESSION_LECTOR);

        if (lectorSesion == null) {
            request.getSession().setAttribute("error", "Debes iniciar sesión para gestionar favoritos");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // Obtener el lector gestionado desde la base de datos
            Lector lectorBD = lectorDAO.buscarPorId(lectorSesion.getId());

            if (lectorBD == null) {
                request.getSession().setAttribute("error", "Error: lector no encontrado en la base de datos.");
                response.sendRedirect(request.getContextPath() + "/perfil");
                return;
            }

            // Obtener el manga desde el DAO
            int mangaId = Integer.parseInt(request.getParameter("mangaId"));
            Manga manga = mangaDAO.obtenerPorId(mangaId);

            if (manga == null) {
                request.getSession().setAttribute("error", "Error: manga no encontrado.");
                response.sendRedirect(request.getContextPath() + "/perfil");
                return;
            }

            // Obtener scanId y origen para redirección
            String scanIdParam = request.getParameter("scanId");
            String origenParam = request.getParameter("origen");
            String redirectUrl = request.getContextPath() + "/perfil";

            if (scanIdParam != null && !scanIdParam.isEmpty()) {
                // Si viene de mangaDetalle, redirigir de vuelta a mangaDetalle
                if ("detalle".equals(origenParam)) {
                    redirectUrl = request.getContextPath() + "/mangaDetalle?mangaId=" + mangaId + "&scanId=" + scanIdParam;
                } else {
                    // Si viene de mangaInvitados, redirigir a mangaInvitados
                    redirectUrl = request.getContextPath() + "/mangaInvitados?scanId=" + scanIdParam;
                }
            }

            // Evaluar la acción (agregar o eliminar)
            if ("agregar".equals(action)) {
                boolean agregado = favoritosService.agregarAFavoritos(lectorBD, manga);

                if (agregado) {
                    request.getSession().setAttribute("mensaje", "✅ Manga agregado a favoritos correctamente ❤️");
                } else {
                    request.getSession().setAttribute("error", "⚠️ El manga ya está en tus favoritos ◉");
                }

            } else if ("eliminar".equals(action)) {
                boolean eliminado = favoritosService.quitarDeFavoritos(lectorBD, manga);

                if (eliminado) {
                    request.getSession().setAttribute("mensaje", "🗑️ Manga eliminado de tus favoritos correctamente.");
                } else {
                    request.getSession().setAttribute("error", "⚠️ No se pudo eliminar el manga de favoritos.");
                }
            }

            // Redirigir según el contexto
            response.sendRedirect(redirectUrl);

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "❌ Error al procesar la solicitud.");
            response.sendRedirect(request.getContextPath() + "/perfil");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "❌ Error al procesar la acción: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/perfil");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Lector lector = (Lector) request.getSession().getAttribute(SESSION_LECTOR);

        if (lector == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Obtener lista de favoritos
        List<Manga> listaFavoritos = favoritosService.obtenerFavoritos(lector);
        request.setAttribute("favoritos", listaFavoritos);

        // Recuperar mensajes si existen
        String mensaje = (String) request.getSession().getAttribute("mensaje");
        String error = (String) request.getSession().getAttribute("error");

        if (mensaje != null) {
            request.setAttribute("mensaje", mensaje);
            request.getSession().removeAttribute("mensaje");
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getSession().removeAttribute("error");
        }

        request.getRequestDispatcher("/perfil-lector.jsp").forward(request, response);
    }
}