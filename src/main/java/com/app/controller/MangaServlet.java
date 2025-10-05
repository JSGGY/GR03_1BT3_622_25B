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

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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
        obtenerInfo(request, response);
    }

    private void obtenerInfo(HttpServletRequest request, HttpServletResponse response)
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
            request.setAttribute("id", scanId);

            request.getRequestDispatcher("manga-dashboard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("dashboard");
        }
    }

}