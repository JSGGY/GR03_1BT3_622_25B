package com.app.controller;

import java.io.IOException;
import java.util.List;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.dao.LectorDAO;
import com.app.dao.MangaDAO;
import com.app.model.ComentarioManga;
import com.app.model.Lector;
import com.app.model.Manga;
import com.app.service.ComentarioMangaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/comentarioManga")
public class PublicarComentarioServlet extends HttpServlet {

    private ComentarioMangaService comentarioService;
    private MangaDAO mangaDAO;
    private LectorDAO lectorDAO;

    @Override
    public void init() throws ServletException {
        comentarioService = new ComentarioMangaService();
        mangaDAO = new MangaDAO();
        lectorDAO = new LectorDAO();
    }

    // Constructor para tests (inyección de dependencias)
    public PublicarComentarioServlet(ComentarioMangaService comentarioService, MangaDAO mangaDAO, LectorDAO lectorDAO) {
        this.comentarioService = comentarioService;
        this.mangaDAO = mangaDAO;
        this.lectorDAO = lectorDAO;
    }

    // Constructor por defecto requerido por servlet
    public PublicarComentarioServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        Lector lectorSesion = (Lector) request.getSession().getAttribute(SESSION_LECTOR);

        // Validar que hay un lector autenticado
        if (lectorSesion == null) {
            request.getSession().setAttribute("error", "❌ Debes iniciar sesión para comentar.");
            String referer = request.getHeader("Referer");
            if (referer != null) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect("index.jsp");
            }
            return;
        }

        try {
            int mangaId = Integer.parseInt(request.getParameter("mangaId"));
            int scanId = Integer.parseInt(request.getParameter("scanId"));

            // Obtener el lector desde la base de datos
            Lector lectorBD = lectorDAO.buscarPorId(lectorSesion.getId());
            if (lectorBD == null) {
                request.getSession().setAttribute("error", "❌ Error: lector no encontrado en la base de datos.");
                response.sendRedirect("mangaInvitados?scanId=" + scanId);
                return;
            }

            // Obtener el manga desde la base de datos
            Manga manga = mangaDAO.obtenerPorId(mangaId);
            if (manga == null) {
                request.getSession().setAttribute("error", "❌ Error: manga no encontrado.");
                response.sendRedirect("mangaInvitados?scanId=" + scanId);
                return;
            }

            // Procesar según la acción
            if ("publicar".equals(action)) {
                publicarComentario(request, response, lectorBD, manga, scanId);
            } else if ("eliminar".equals(action)) {
                eliminarComentario(request, response, scanId);
            } else {
                request.getSession().setAttribute("error", "❌ Acción no válida.");
                response.sendRedirect("mangaInvitados?scanId=" + scanId);
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "❌ Error: parámetros inválidos.");
            String referer = request.getHeader("Referer");
            if (referer != null) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect("ingresoInvitado");
            }
        } catch (jakarta.persistence.PersistenceException e) {
            // Capturar errores de persistencia (incluyendo conexión a BD)
            System.err.println("❌ Error de persistencia/conexión a la base de datos: " + e.getMessage());
            
            // Preservar el comentario si se estaba intentando publicar
            String textoComentario = request.getParameter("comentario");
            if (textoComentario != null && !textoComentario.trim().isEmpty()) {
                request.getSession().setAttribute("comentarioTemporal", textoComentario);
            }
            
            // Determinar si es un error de conexión específicamente
            String errorMsg = "❌ Error de conexión: No se pudo conectar con la base de datos. Por favor, intenta nuevamente en unos momentos.";
            if (e.getMessage() != null && (e.getMessage().contains("Communications link failure") || 
                                          e.getMessage().contains("Connection refused") ||
                                          e.getMessage().contains("Unable to acquire JDBC Connection"))) {
                errorMsg = "❌ Error de conexión: La base de datos no está disponible. Tu comentario no se ha perdido.";
            }
            
            request.getSession().setAttribute("error", errorMsg);
            String referer = request.getHeader("Referer");
            if (referer != null) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect("ingresoInvitado");
            }
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            
            // Preservar el comentario en caso de cualquier error
            String textoComentario = request.getParameter("comentario");
            if (textoComentario != null && !textoComentario.trim().isEmpty()) {
                request.getSession().setAttribute("comentarioTemporal", textoComentario);
            }
            
            request.getSession().setAttribute("error", "❌ Error al procesar la solicitud. Por favor, intenta nuevamente.");
            String referer = request.getHeader("Referer");
            if (referer != null) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect("ingresoInvitado");
            }
        }
    }

    /**
     * Publica un nuevo comentario
     */
    private void publicarComentario(HttpServletRequest request, HttpServletResponse response,
                                   Lector lector, Manga manga, int scanId) throws IOException {
        String textoComentario = request.getParameter("comentario");

        // Validar que el comentario no esté vacío
        if (!comentarioService.validarTextoComentario(textoComentario)) {
            request.getSession().setAttribute("error", "⚠️ El comentario no puede estar vacío.");
            request.getSession().setAttribute("comentarioTemporal", textoComentario); // Preservar el texto
            redirigirAlOrigen(request, response, manga.getId(), scanId);
            return;
        }

        try {
            // Intentar publicar el comentario
            boolean publicado = comentarioService.publicarComentario(lector, manga, textoComentario);

            if (publicado) {
                request.getSession().setAttribute("mensaje", "✅ Comentario publicado exitosamente.");
                request.getSession().removeAttribute("comentarioTemporal"); // Limpiar el texto temporal
            } else {
                request.getSession().setAttribute("error", "❌ Error al publicar el comentario. Intenta nuevamente.");
                request.getSession().setAttribute("comentarioTemporal", textoComentario); // Preservar el texto en caso de error
            }
        } catch (jakarta.persistence.PersistenceException e) {
            // Error de persistencia (incluyendo conexión a BD)
            System.err.println("❌ Error de conexión al publicar comentario: " + e.getMessage());
            request.getSession().setAttribute("error", "❌ Error de conexión: No se pudo conectar con la base de datos. Tu comentario no se ha perdido.");
            request.getSession().setAttribute("comentarioTemporal", textoComentario); // PRESERVAR el texto
        } catch (Exception e) {
            // Cualquier otro error
            System.err.println("❌ Error al publicar comentario: " + e.getMessage());
            request.getSession().setAttribute("error", "❌ Error al publicar el comentario. Tu texto se ha preservado, intenta nuevamente.");
            request.getSession().setAttribute("comentarioTemporal", textoComentario); // PRESERVAR el texto
        }

        redirigirAlOrigen(request, response, manga.getId(), scanId);
    }

    /**
     * Elimina un comentario existente
     */
    private void eliminarComentario(HttpServletRequest request, HttpServletResponse response, int scanId)
            throws IOException {
        int mangaId = 0;
        try {
            int comentarioId = Integer.parseInt(request.getParameter("comentarioId"));
            mangaId = Integer.parseInt(request.getParameter("mangaId"));
            boolean eliminado = comentarioService.eliminarComentario(comentarioId);

            if (eliminado) {
                request.getSession().setAttribute("mensaje", "🗑️ Comentario eliminado correctamente.");
            } else {
                request.getSession().setAttribute("error", "⚠️ No se pudo eliminar el comentario.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "❌ Error: ID de comentario inválido.");
        } catch (jakarta.persistence.PersistenceException e) {
            System.err.println("❌ Error de conexión al eliminar comentario: " + e.getMessage());
            request.getSession().setAttribute("error", "❌ Error de conexión: No se pudo conectar con la base de datos para eliminar el comentario.");
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar comentario: " + e.getMessage());
            request.getSession().setAttribute("error", "❌ Error al eliminar el comentario. Por favor, intenta nuevamente.");
        }

        redirigirAlOrigen(request, response, mangaId, scanId);
    }

    /**
     * Redirige al origen correcto según el referer
     * Si viene de manga-detalle.jsp, redirige ahí, sino a mangaInvitados
     */
    private void redirigirAlOrigen(HttpServletRequest request, HttpServletResponse response, int mangaId, int scanId)
            throws IOException {
        String referer = request.getHeader("Referer");
        
        // Si viene de manga-detalle, redirigir ahí
        if (referer != null && referer.contains("mangaDetalle")) {
            response.sendRedirect("mangaDetalle?mangaId=" + mangaId + "&scanId=" + scanId);
        } else {
            // Por defecto, redirigir a la lista de mangas
            response.sendRedirect("mangaInvitados?scanId=" + scanId);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Si la acción es "listar", devolver JSON para AJAX
        if ("listar".equals(action)) {
            obtenerComentariosJSON(request, response);
        } else {
            // Obtener comentarios de un manga específico para JSP
            try {
                int mangaId = Integer.parseInt(request.getParameter("mangaId"));
                List<ComentarioManga> comentarios = comentarioService.obtenerComentariosDeMangaPorId(mangaId);
                
                request.setAttribute("comentarios", comentarios);
                request.getRequestDispatcher("/manga-invitados.jsp").forward(request, response);
                
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de manga inválido");
            } catch (jakarta.persistence.PersistenceException e) {
                System.err.println("❌ Error de conexión al cargar comentarios: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Error de conexión a la base de datos");
            } catch (Exception e) {
                System.err.println("❌ Error al cargar comentarios: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar comentarios");
            }
        }
    }

    /**
     * Devuelve los comentarios en formato JSON para peticiones AJAX
     */
    private void obtenerComentariosJSON(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            int mangaId = Integer.parseInt(request.getParameter("mangaId"));
            List<ComentarioManga> comentarios = comentarioService.obtenerComentariosDeMangaPorId(mangaId);
            
            // Construir JSON manualmente
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            for (int i = 0; i < comentarios.size(); i++) {
                ComentarioManga c = comentarios.get(i);
                if (i > 0) json.append(",");
                
                json.append("{");
                json.append("\"id\":").append(c.getId()).append(",");
                json.append("\"lectorUsername\":\"").append(escapeJSON(c.obtenerNombreLector())).append("\",");
                json.append("\"comentario\":\"").append(escapeJSON(c.getComentario())).append("\",");
                json.append("\"fechaComentario\":\"").append(c.getFechaComentario() != null ? c.getFechaComentario().toString() : "").append("\"");
                
                if (c.getFechaModificacion() != null) {
                    json.append(",\"fechaModificacion\":\"").append(c.getFechaModificacion().toString()).append("\"");
                }
                
                json.append("}");
            }
            
            json.append("]");
            
            response.getWriter().write(json.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID de manga inválido\"}");
        } catch (jakarta.persistence.PersistenceException e) {
            System.err.println("❌ Error de conexión al cargar comentarios: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"error\":\"Error de conexión a la base de datos\"}");
        } catch (Exception e) {
            System.err.println("❌ Error al cargar comentarios: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al cargar comentarios\"}");
        }
    }

    /**
     * Escapa caracteres especiales para JSON
     */
    private String escapeJSON(String text) {
        if (text == null) return "";
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}

