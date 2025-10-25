package com.app.controller;

import java.io.IOException;

import static com.app.constants.AppConstants.PARAM_PASSWORD;
import static com.app.constants.AppConstants.PARAM_USERNAME;
import static com.app.constants.AppConstants.ROUTE_DASHBOARD;
import static com.app.constants.AppConstants.SESSION_ADMIN_SCAN;
import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.model.AdminScan;
import com.app.model.Lector;
import com.app.service.LoginService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_PASSWORD);

        // Intentar autenticar como AdminScan
        AdminScan adminScan = loginService.authenticateAdminScan(username, password);
        
        if (adminScan != null) {
            // Login exitoso como AdminScan
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_ADMIN_SCAN, adminScan);
            session.setAttribute("username", username);
            
            System.out.println("DEBUG: Login exitoso como AdminScan - " + username);
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return;
        }

        // Intentar autenticar como Lector
        Lector lector = loginService.authenticateLector(username, password);
        
        if (lector != null) {
            // Login exitoso como Lector
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_LECTOR, lector);
            session.setAttribute("username", username);
            
            System.out.println("DEBUG: Login exitoso como Lector - " + username);
            // Redirigir al dashboard de invitados (ahora como lector autenticado)
            response.sendRedirect(request.getContextPath() + "/ingresoInvitado");
            return;
        }

        // Credenciales incorrectas
        System.out.println("DEBUG: Login fallido para usuario - " + username);
        request.setAttribute("error", "Usuario o contraseña incorrectos");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
