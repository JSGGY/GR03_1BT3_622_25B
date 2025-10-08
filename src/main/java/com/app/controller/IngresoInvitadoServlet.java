package com.app.controller;

import java.io.IOException;
import java.util.List;

import com.app.dao.ScanDAO;
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
        List<Scan> scans = scanDAO.listarTodos();
        for (Scan scan : scans) {
            System.out.println(scan);
        }
        request.setAttribute("scans", scans);

        request.getRequestDispatcher("dashboard-invitados.jsp").forward(request, response);
    }
}
