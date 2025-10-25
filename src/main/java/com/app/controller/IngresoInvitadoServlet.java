package com.app.controller;

import java.io.IOException;
import java.util.List;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.dao.ScanDAO;
import com.app.model.Lector;
import com.app.model.Scan;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ingresoInvitado")
public class IngresoInvitadoServlet extends HttpServlet {

    private final ScanDAO scanDAO = new ScanDAO();

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
        } else {
            // Invitado sin autenticar
            System.out.println("DEBUG: Acceso de invitado sin autenticar");
            request.setAttribute("isLectorAutenticado", false);
        }
        
        // Cargar todos los scans disponibles
        List<Scan> scans = scanDAO.listarTodos();
        for (Scan scan : scans) {
            System.out.println(scan);
        }
        request.setAttribute("scans", scans);

        request.getRequestDispatcher("dashboard-invitados.jsp").forward(request, response);
    }
}
