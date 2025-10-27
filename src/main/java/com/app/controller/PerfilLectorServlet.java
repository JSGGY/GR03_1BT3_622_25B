package com.app.controller;

import java.io.IOException;

import static com.app.constants.AppConstants.SESSION_LECTOR;
import com.app.dao.LectorDAO;
import com.app.model.Lector;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para gestionar el perfil del Lector.
 * 
 * Responsabilidades:
 * - GET /perfil: Mostrar el perfil actual del lector autenticado
 * - POST /perfil: Actualizar los datos del perfil (username, correo, contraseña)
 * 
 * Historia de Usuario: Gestionar Perfil
 * - Escenario 1: Visualización exitosa del perfil
 * - Escenario 2: Edición exitosa del perfil
 * - Escenario 3: Error al actualizar el perfil
 */
@WebServlet("/perfil")
public class PerfilLectorServlet extends HttpServlet {
    
    private final LectorDAO lectorDAO = new LectorDAO();
    
    /**
     * GET /perfil - Muestra el formulario de perfil con los datos actuales del lector.
     * Escenario 1: Visualización exitosa del perfil
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verificar que el lector esté autenticado
        if (session == null || session.getAttribute(SESSION_LECTOR) == null) {
            System.out.println("⚠️ Intento de acceso a perfil sin autenticación");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);
        
        System.out.println("✅ Mostrando perfil de: " + lector.getUsername());
        
        // Pasar los datos del lector al JSP
        request.setAttribute("lector", lector);
        request.getRequestDispatcher("/perfil-lector.jsp").forward(request, response);
    }
    
    /**
     * POST /perfil - Actualiza los datos del perfil del lector.
     * Escenario 2: Edición exitosa del perfil
     * Escenario 3: Error al actualizar el perfil
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Verificar que el lector esté autenticado
        if (session == null || session.getAttribute(SESSION_LECTOR) == null) {
            System.out.println("⚠️ Intento de actualización sin autenticación");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        Lector lector = (Lector) session.getAttribute(SESSION_LECTOR);
        
        // Obtener los nuevos datos del formulario
        String nuevoUsername = request.getParameter("username");
        String nuevoCorreo = request.getParameter("correo");
        String nuevaContraseña = request.getParameter("password");
        
        System.out.println("\n=== Actualizando perfil de: " + lector.getUsername() + " ===");
        System.out.println("Nuevo username: " + nuevoUsername);
        System.out.println("Nuevo correo: " + nuevoCorreo);
        
        // Intentar actualizar el perfil
        boolean perfilActualizado = lector.actualizarPerfil(nuevoUsername, nuevoCorreo, nuevaContraseña);
        
        if (!perfilActualizado) {
            // Escenario 3: Error - Datos inválidos
            System.out.println("❌ Error: Datos inválidos");
            request.setAttribute("error", "Error al actualizar perfil: Datos incompletos o inválidos");
            request.setAttribute("lector", lector);
            request.getRequestDispatcher("/perfil-lector.jsp").forward(request, response);
            return;
        }
        
        // Intentar guardar en la base de datos
        boolean guardadoExitoso = lectorDAO.actualizar(lector);
        
        if (!guardadoExitoso) {
            // Escenario 3: Error - Fallo en BD (ej: username/correo duplicado)
            System.out.println("❌ Error: Fallo al guardar en base de datos");
            request.setAttribute("error", "Error al actualizar perfil: El username o correo ya están en uso");
            request.setAttribute("lector", lector);
            request.getRequestDispatcher("/perfil-lector.jsp").forward(request, response);
            return;
        }
        
        // Escenario 2: Éxito - Actualizar sesión con los nuevos datos
        session.setAttribute(SESSION_LECTOR, lector);
        session.setAttribute("username", nuevoUsername);
        
        System.out.println("✅ Perfil actualizado exitosamente");
        
        // Redirigir con mensaje de éxito
        request.setAttribute("success", "Perfil actualizado exitosamente");
        request.setAttribute("lector", lector);
        request.getRequestDispatcher("/perfil-lector.jsp").forward(request, response);
    }
}

