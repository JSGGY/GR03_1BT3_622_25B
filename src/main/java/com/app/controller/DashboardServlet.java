package com.app.controller;

import java.io.IOException;
import java.util.List;

import com.app.model.AdminScan;
import com.app.model.Scan;
import com.app.service.ScanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private ScanService scanService = new ScanService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        AdminScan adminScan = (AdminScan) session.getAttribute("adminScan");
        
        if (adminScan == null) {
            System.out.println("DEBUG: No hay AdminScan en sesiOn, redirigiendo al login");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        System.out.println("DEBUG: AdminScan ID: " + adminScan.getId() + ", Username: " + adminScan.getUsername());
        

        List<Scan> scans = scanService.obtenerScansPorAdmin(adminScan.getId());
        System.out.println("DEBUG: Scans obtenidos: " + scans.size());
        

        for (Scan scan : scans) {
            System.out.println("  - Scan: " + scan.getNombre() + " (ID: " + scan.getId() + ")");
        }
        
        request.setAttribute("scans", scans);
        request.setAttribute("adminScan", adminScan);
        

        System.out.println("DEBUG: Atributos establecidos - scans: " + request.getAttribute("scans"));
        System.out.println("DEBUG: Atributos establecidos - adminScan: " + request.getAttribute("adminScan"));
        
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}