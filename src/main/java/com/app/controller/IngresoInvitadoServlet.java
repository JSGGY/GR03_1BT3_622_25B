package com.app.controller;

import com.app.dao.ScanDAO;
import com.app.model.Scan;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

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
