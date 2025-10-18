package com.app.controller;

import static com.app.constants.AppConstants.*;

import java.io.IOException;

import com.app.model.AdminScan;
import com.app.model.Scan;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Clase base para servlets que requieren autenticación.
 * Proporciona métodos comunes para validación de sesión y autorización.
 *
 * Responsabilidad: SOLO autenticación y autorización
 * Patrón de diseño: Template Method
 * Tipo de refactorización: Extract Method + Extract Superclass
 */
public abstract class BaseAuthenticatedServlet extends HttpServlet {

    /**
     * Valida que existe una sesión activa con un AdminScan autenticado.
     *
     * @param request La petición HTTP
     * @param response La respuesta HTTP
     * @return El AdminScan autenticado, o null si no hay sesión válida
     * @throws IOException Si hay error al redirigir
     */
    protected AdminScan validateSession(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + ROUTE_INDEX);
            return null;
        }

        AdminScan adminScan = (AdminScan) session.getAttribute(SESSION_ADMIN_SCAN);

        if (adminScan == null) {
            response.sendRedirect(request.getContextPath() + ROUTE_INDEX);
            return null;
        }

        return adminScan;
    }

    /**
     * Valida que el parámetro scanId es válido y no está vacío.
     *
     * @param scanIdStr El ID del scan como String
     * @param request La petición HTTP
     * @param response La respuesta HTTP
     * @return true si es válido, false si no lo es (y redirige al dashboard)
     * @throws IOException Si hay error al redirigir
     */
    protected boolean validateScanId(String scanIdStr, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (scanIdStr == null || scanIdStr.trim().isEmpty()) {
            System.out.println("ERROR: ID de scan no proporcionado");
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return false;
        }
        return true;
    }

    /**
     * Valida que el scan existe y que el admin tiene permisos sobre él.
     * Solo el creador del scan puede editarlo o eliminarlo.
     *
     * @param admin El AdminScan autenticado
     * @param scan El Scan a validar
     * @param request La petición HTTP
     * @param response La respuesta HTTP
     * @return true si el admin tiene permisos, false si no (y redirige al dashboard)
     * @throws IOException Si hay error al redirigir
     */
    protected boolean validateScanOwnership(AdminScan admin, Scan scan,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (scan == null) {
            System.out.println("ERROR: Scan no encontrado");
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return false;
        }

        if (scan.getCreadoPor().getId() != admin.getId()) {
            System.out.println("ERROR: Usuario no autorizado para modificar este scan");
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return false;
        }

        return true;
    }
}