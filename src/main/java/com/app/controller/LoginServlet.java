package com.app.controller;

import java.io.IOException;

import com.app.model.AdminScan;
import com.app.service.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        AdminScan adminScan = loginService.authenticate(username, password);
        
        if (adminScan != null) {

            HttpSession session = request.getSession();
            session.setAttribute("adminScan", adminScan);
            session.setAttribute("username", username);
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
