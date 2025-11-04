package com.app.controller;

import java.io.IOException;
import java.util.List;

import static com.app.constants.AppConstants.SESSION_LECTOR;

import com.app.dao.MangaDAO;
import com.app.dao.ScanDAO;
import com.app.model.Lector;
import com.app.model.Manga;
import com.app.model.Scan;
import com.app.service.HistorialVisitasService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ingresoInvitado")
public class IngresoInvitadoServlet extends HttpServlet {

    private final ScanDAO scanDAO = new ScanDAO();
    private final MangaDAO mangaDAO = new MangaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        
        // Verificar si hay un Lector autenticado
        Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);
        
        if (lector != null) {
            // Lector autenticado
            System.out.println("DEBUG: Acceso de Lector autenticado - " + lector.getUsername());
            request.setAttribute("lector", lector);
            request.setAttribute("isLectorAutenticado", true);

            // Cargar mangas visitados recientemente
            HistorialVisitasService historialService = new HistorialVisitasService();
            List<Manga> mangasRecientes = historialService.obtenerMangasVisitadosRecientes(lector);

            if (mangasRecientes != null && !mangasRecientes.isEmpty()) {
                System.out.println("DEBUG: Se encontraron " + mangasRecientes.size() + " mangas visitados recientemente.");
            } else {
                System.out.println("DEBUG: No hay mangas recientes para este lector.");
            }

            request.setAttribute("mangasRecientes", mangasRecientes);
        } else {
            // Invitado sin autenticar
            System.out.println("DEBUG: Acceso de invitado sin autenticar");
            request.setAttribute("isLectorAutenticado", false);
        }
        
        // Cargar solo los scans que tienen al menos un manga
        List<Scan> scans = scanDAO.listarScansConMangas();
        System.out.println("DEBUG: Scans con mangas encontrados: " + scans.size());
        for (Scan scan : scans) {
            System.out.println("  - " + scan.getNombre() + " (Mangas: " + scan.getMangas().size() + ")");
        }
        request.setAttribute("scans", scans);

        // Cargar los mangas más populares (según totalLikes)
        List<Manga> mangasPopulares = mangaDAO.obtenerTopMangasPorLikes(10);
        if (mangasPopulares != null && !mangasPopulares.isEmpty()) {
            System.out.println("DEBUG: Se encontraron " + mangasPopulares.size() + " mangas populares.");
        } else {
            System.out.println("DEBUG: No hay mangas populares registrados.");
        }
        request.setAttribute("mangasPopulares", mangasPopulares);


        // Redirigir a la página de dashboard para invitados
        request.getRequestDispatcher("dashboard-invitados.jsp").forward(request, response);
    }
}
