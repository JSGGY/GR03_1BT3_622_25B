package com.app.controller;

import com.app.dao.LectorDAO;
import com.app.dao.MangaDAO;
import com.app.model.Lector;
import com.app.model.Manga;
import com.app.service.FavoritosService;

import static com.app.constants.AppConstants.SESSION_LECTOR;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/favoritos")
public class FavoritosServlet extends HttpServlet {

    private FavoritosService favoritosService;
    private MangaDAO mangaDAO;

    @Override
    public void init() throws ServletException {
        favoritosService = new FavoritosService();
        mangaDAO = new MangaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        Lector lectorSesion = (Lector) request.getSession().getAttribute(SESSION_LECTOR);

        if (lectorSesion == null) {
            //response.sendRedirect("index.jsp");
            return;
        }

        try {
            // 🔹 1. Obtener el lector gestionado desde la base de datos
            LectorDAO lectorDAO = new LectorDAO();
            Lector lectorBD = lectorDAO.buscarPorId(lectorSesion.getId());

            if (lectorBD == null) {
                request.getSession().setAttribute("mensajeError", "Error: lector no encontrado en la base de datos.");
                response.sendRedirect("favoritos");
                return;
            }

            // 🔹 2. Obtener el manga desde el DAO
            int mangaId = Integer.parseInt(request.getParameter("mangaId"));
            Manga manga = mangaDAO.obtenerPorId(mangaId);

            if (manga == null) {
                request.getSession().setAttribute("mensajeError", "Error: manga no encontrado.");
                response.sendRedirect("favoritos");
                return;
            }

            // 🔹 3. Evaluar la acción (agregar o eliminar)
            if ("agregar".equals(action)) {
                boolean agregado = favoritosService.agregarAFavoritos(lectorBD, manga);

                if (agregado) {
                    request.getSession().setAttribute("mensaje", "✅ Manga agregado a favoritos correctamente ❤️");
                } else {
                    request.getSession().setAttribute("mensajeError", "⚠️ El manga ya está en tus favoritos ❗");
                }

            } else if ("eliminar".equals(action)) {
                boolean eliminado = favoritosService.quitarDeFavoritos(lectorBD, manga);

                if (eliminado) {
                    request.getSession().setAttribute("mensaje", "🗑️ Manga eliminado de tus favoritos correctamente.");
                } else {
                    request.getSession().setAttribute("mensajeError", "⚠️ No se pudo eliminar el manga de favoritos.");
                }
            }

            // 🔹 4. Redirige siempre al listado de favoritos
            response.sendRedirect("favoritos");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mensajeError", "❌ Error al procesar la acción: " + e.getMessage());
            response.sendRedirect("favoritos");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Lector lector = (Lector) request.getSession().getAttribute(SESSION_LECTOR);

        if (lector == null) {
            System.out.println("DEBUG FavoritosServlet - Sesión inactiva o sin lector, redirigiendo al index.jsp");
            //response.sendRedirect("index.jsp");
            return;
        }

        System.out.println("DEBUG FavoritosServlet - Lector activo en sesión: " + lector.getUsername());

        // Mostrar la lista de favoritos
        List<Manga> listaFavoritos = favoritosService.obtenerFavoritos(lector);
        request.setAttribute("favoritos", listaFavoritos);

        // Recuperar mensajes si existen
        request.setAttribute("mensaje", request.getSession().getAttribute("mensaje"));
        request.setAttribute("mensajeError", request.getSession().getAttribute("mensajeError"));
        request.getSession().removeAttribute("mensaje");
        request.getSession().removeAttribute("mensajeError");

        request.getRequestDispatcher("/perfil-lector.jsp").forward(request, response);
    }
}
