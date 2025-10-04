package com.app.controller;

import java.io.IOException;

import com.app.model.AdminScan;
import com.app.service.LoginService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validaciones
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "El nombre de usuario es requerido");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "El email es requerido");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "La contrase\u00f1a es requerida");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Las contrase\u00f1as no coinciden");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        // Validar longitud de contrase\u00f1a
        if (password.length() < 6) {
            request.setAttribute("error", "La contrase\u00f1a debe tener al menos 6 caracteres");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        try {
            // Verificar si el usuario ya existe
            if (loginService.existeUsuario(username, email)) {
                request.setAttribute("error", "Ya existe un usuario con ese nombre o email");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }
            
            // Crear nuevo AdminScan
            AdminScan nuevoAdmin = loginService.registrarAdminScan(username, email, password);
            
            if (nuevoAdmin != null) {
                // Registro exitoso - iniciar sesi\u00f3n autom\u00e1ticamente
                HttpSession session = request.getSession();
                session.setAttribute("adminScan", nuevoAdmin);
                session.setAttribute("username", username);
                
                System.out.println("DEBUG: Usuario registrado exitosamente: " + username);
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                request.setAttribute("error", "Error al crear la cuenta. Int\u00e9ntalo nuevamente");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            System.err.println("ERROR en registro: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error interno del servidor. Int\u00e9ntalo m\u00e1s tarde");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirigir al login si acceden por GET
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}