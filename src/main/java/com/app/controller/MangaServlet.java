package com.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.AdminScan;
import com.app.model.EstadoManga;
import com.app.model.Manga;
import com.app.model.Scan;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/manga")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class MangaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private MangaDAO mangaDAO = new MangaDAO();
    private ScanDAO scanDAO = new ScanDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminScan") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        String scanIdParam = request.getParameter("scanId");
        if (scanIdParam == null || scanIdParam.isEmpty()) {
            response.sendRedirect("dashboard");
            return;
        }
        
        try {
            int scanId = Integer.parseInt(scanIdParam);
            AdminScan adminScan = (AdminScan) session.getAttribute("adminScan");
            
            // Verificar que el scan pertenece al usuario
            Scan scan = scanDAO.buscarPorId(scanId);
            if (scan == null || scan.getCreadoPor().getId() != adminScan.getId()) {
                response.sendRedirect("dashboard");
                return;
            }
            
            // Obtener mangas del scan
            List<Manga> mangas = mangaDAO.buscarPorScanId(scanId);
            
            // Establecer atributos para la vista
            request.setAttribute("scan", scan);
            request.setAttribute("mangas", mangas);
            request.setAttribute("adminScan", adminScan);
            
            request.getRequestDispatcher("manga-dashboard.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminScan") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            crearManga(request, response);
        } else if ("edit".equals(action)) {
            editarManga(request, response);
        } else if ("delete".equals(action)) {
            eliminarManga(request, response);
        } else {
            response.sendRedirect("dashboard");
        }
    }
    
    private void crearManga(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
            
            String scanIdParam = request.getParameter("scanId");
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String estadoParam = request.getParameter("estado");
            
            if (scanIdParam == null || titulo == null || titulo.trim().isEmpty() || estadoParam == null) {
                request.setAttribute("error", "Faltan datos obligatorios");
                doGet(request, response);
                return;
            }
            
            int scanId = Integer.parseInt(scanIdParam);
            
            // Verificar que el scan existe y pertenece al usuario
            Scan scan = scanDAO.buscarPorId(scanId);
            if (scan == null || scan.getCreadoPor().getId() != adminScan.getId()) {
                response.sendRedirect("dashboard");
                return;
            }
            
            // Verificar que no existe otro manga con el mismo título en este scan
            if (mangaDAO.existeTituloEnScan(titulo.trim(), scanId)) {
                request.setAttribute("error", "Ya existe un manga con ese título en este scan");
                doGet(request, response);
                return;
            }
            
            // Procesar imagen de portada
            String imagenUrl = null;
            Part imagenPart = request.getPart("imagenPortada");
            if (imagenPart != null && imagenPart.getSize() > 0) {
                imagenUrl = guardarImagen(imagenPart);
                if (imagenUrl == null) {
                    request.setAttribute("error", "Error al procesar la imagen");
                    doGet(request, response);
                    return;
                }
            }
            
            // Crear manga
            Manga manga = new Manga();
            manga.setTitulo(titulo.trim());
            manga.setDescripcion(descripcion != null ? descripcion.trim() : "");
            manga.setEstado(EstadoManga.valueOf(estadoParam));
            manga.setScan(scan);
            manga.setImagenPortada(imagenUrl);
            
            if (mangaDAO.guardar(manga)) {
                response.sendRedirect("manga?scanId=" + scanId);
            } else {
                request.setAttribute("error", "Error al crear el manga");
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error interno del servidor");
            doGet(request, response);
        }
    }
    
    private void editarManga(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
            
            String mangaIdParam = request.getParameter("mangaId");
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String estadoParam = request.getParameter("estado");
            
            if (mangaIdParam == null || titulo == null || titulo.trim().isEmpty() || estadoParam == null) {
                request.setAttribute("error", "Faltan datos obligatorios");
                doGet(request, response);
                return;
            }
            
            int mangaId = Integer.parseInt(mangaIdParam);
            Manga manga = mangaDAO.buscarPorId(mangaId);
            
            if (manga == null || manga.getScan().getCreadoPor().getId() != adminScan.getId()) {
                response.sendRedirect("dashboard");
                return;
            }
            
            int scanId = manga.getScan().getId();
            
            // Verificar que no existe otro manga con el mismo título
            if (mangaDAO.existeTituloEnScanExceptoId(titulo.trim(), scanId, mangaId)) {
                request.setAttribute("error", "Ya existe otro manga con ese título en este scan");
                doGet(request, response);
                return;
            }
            
            // Procesar nueva imagen si se subió
            Part imagenPart = request.getPart("nuevaImagenPortada");
            if (imagenPart != null && imagenPart.getSize() > 0) {
                String nuevaImagen = guardarImagen(imagenPart);
                if (nuevaImagen != null) {
                    // Eliminar imagen anterior si existe
                    eliminarImagenAnterior(manga.getImagenPortada());
                    manga.setImagenPortada(nuevaImagen);
                }
            }
            
            // Actualizar datos
            manga.setTitulo(titulo.trim());
            manga.setDescripcion(descripcion != null ? descripcion.trim() : "");
            manga.setEstado(EstadoManga.valueOf(estadoParam));
            
            if (mangaDAO.guardar(manga)) {
                response.sendRedirect("manga?scanId=" + scanId);
            } else {
                request.setAttribute("error", "Error al actualizar el manga");
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error interno del servidor");
            doGet(request, response);
        }
    }
    
    private void eliminarManga(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            AdminScan adminScan = (AdminScan) request.getSession().getAttribute("adminScan");
            
            String mangaIdParam = request.getParameter("mangaId");
            if (mangaIdParam == null) {
                response.sendRedirect("dashboard");
                return;
            }
            
            int mangaId = Integer.parseInt(mangaIdParam);
            Manga manga = mangaDAO.buscarPorId(mangaId);
            
            if (manga == null || manga.getScan().getCreadoPor().getId() != adminScan.getId()) {
                response.sendRedirect("dashboard");
                return;
            }
            
            int scanId = manga.getScan().getId();
            
            // Eliminar imagen de portada si existe
            eliminarImagenAnterior(manga.getImagenPortada());
            
            if (mangaDAO.eliminar(mangaId)) {
                response.sendRedirect("manga?scanId=" + scanId);
            } else {
                request.setAttribute("error", "Error al eliminar el manga");
                doGet(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error interno del servidor");
            doGet(request, response);
        }
    }
    
    private String guardarImagen(Part imagenPart) throws IOException {
        if (imagenPart == null || imagenPart.getSize() == 0) {
            return null;
        }
        
        String fileName = imagenPart.getSubmittedFileName();
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        
        // Validar tipo de archivo
        String contentType = imagenPart.getContentType();
        if (!contentType.startsWith("image/")) {
            return null;
        }
        
        // Generar nombre único
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        // Crear directorio si no existe
        String uploadDir = getServletContext().getRealPath("/images/mangas/");
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Guardar archivo
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(imagenPart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "images/mangas/" + uniqueFileName;
    }
    
    private void eliminarImagenAnterior(String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.isEmpty() && imagenUrl.startsWith("images/mangas/")) {
            try {
                String realPath = getServletContext().getRealPath("/" + imagenUrl);
                Path path = Paths.get(realPath);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                // Log error pero no interrumpir el proceso
                System.err.println("Error al eliminar imagen: " + e.getMessage());
            }
        }
    }
}