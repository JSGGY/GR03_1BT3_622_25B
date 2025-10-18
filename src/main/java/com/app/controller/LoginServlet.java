package com.app.controller;

import static com.app.constants.AppConstants.*;

import java.io.IOException;

import com.app.model.AdminScan;
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

        AdminScan adminScan = loginService.authenticate(username, password);

        if (adminScan != null) {

            HttpSession session = request.getSession();
            session.setAttribute(SESSION_ADMIN_SCAN, adminScan);
            session.setAttribute("username", username);

            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
        } else {
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
