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

@WebServlet("/lista")
public class ListaServlet extends HttpServlet {

    private final ListaService listaService = new ListaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirigir al perfil del lector donde se muestran las listas
        response.sendRedirect(request.getContextPath() + "/perfil");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);

        if (lector == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión");
            return;
        }

        String action = request.getParameter("action");
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if ("crear".equals(action)) {
            // Crear nueva lista
            if (nombre == null || nombre.trim().isEmpty()) {
                request.getSession().setAttribute("error", "El nombre de la lista no puede estar vacío");
            } else {
                boolean exito = listaService.crearLista(lector.getId(), nombre, descripcion);
                if (exito) {
                    // Si viene un mangaId, agregarlo automáticamente a la nueva lista
                    String mangaIdParam = request.getParameter("mangaIdParaAgregar");
                    String scanIdParam = request.getParameter("scanIdParaAgregar");
                    
                    if (mangaIdParam != null && !mangaIdParam.isEmpty()) {
                        try {
                            int mangaId = Integer.parseInt(mangaIdParam);
                            // Buscar la lista recién creada (última del lector)
                            java.util.List<com.app.model.Lista> listas = listaService.obtenerListasPorLector(lector.getId());
                            if (!listas.isEmpty()) {
                                int nuevaListaId = listas.get(0).getId(); // La más reciente
                                listaService.agregarMangaALista(nuevaListaId, mangaId, lector.getId());
                                request.getSession().setAttribute("mensaje", "Lista creada y manga agregado exitosamente");
                                // Redirigir a la página de mangas si hay scanId
                                if (scanIdParam != null && !scanIdParam.isEmpty()) {
                                    response.sendRedirect(request.getContextPath() + "/mangaInvitados?scanId=" + scanIdParam);
                                    return;
                                }
                            }
                        } catch (NumberFormatException e) {
                            request.getSession().setAttribute("mensaje", "Lista creada exitosamente");
                        }
                    } else {
                        request.getSession().setAttribute("mensaje", "Lista creada exitosamente");
                    }
                } else {
                    request.getSession().setAttribute("error", "Error al crear la lista. Verifica que el nombre no esté duplicado.");
                }
            }
        } else if ("eliminar".equals(action)) {
            // Eliminar lista
            String listaIdParam = request.getParameter("listaId");
            if (listaIdParam != null) {
                try {
                    int listaId = Integer.parseInt(listaIdParam);
                    boolean exito = listaService.eliminarLista(listaId, lector.getId());
                    if (exito) {
                        request.getSession().setAttribute("mensaje", "Lista eliminada exitosamente");
                    } else {
                        request.getSession().setAttribute("error", "Error al eliminar la lista");
                    }
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("error", "ID de lista inválido");
                }
            }
        }

        // Redirigir al perfil del lector donde se muestran las listas
        response.sendRedirect(request.getContextPath() + "/perfil");
    }
}

