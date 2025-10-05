package com.app.controller;

import java.io.IOException;

import com.app.model.AdminScan;
import com.app.model.Lector;
import com.app.service.LoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean esLector = "true".equals(request.getParameter("isLector"));

        if (esLector) {
            registrarLector(request, response);
        } else {
            registrarAdmin(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    private void registrarAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!validarCampos(username, email, password, confirmPassword, request, response))
            return;

        try {
            if (loginService.existeUsuario(username, email)) {
                mostrarError("Ya existe un usuario con ese nombre o email", request, response);
                return;
            }

            AdminScan nuevoAdmin = loginService.registrarAdminScan(username, email, password);

            if (nuevoAdmin != null) {
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } else {
                mostrarError("Error al crear la cuenta. Inténtalo nuevamente", request, response);
            }

        } catch (Exception e) {
            manejarExcepcion(e, request, response);
        }
    }

    private void registrarLector(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!validarCampos(username, email, password, confirmPassword, request, response))
            return;

        try {
            if (loginService.existeUsuario(username, email)) {
                mostrarError("Ya existe un usuario con ese nombre o email", request, response);
                return;
            }

            Lector nuevoLector = loginService.registrarLector(username, email, password);

            if (nuevoLector != null) {
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } else {
                mostrarError("Error al crear la cuenta. Inténtalo nuevamente", request, response);
            }

        } catch (Exception e) {
            manejarExcepcion(e, request, response);
        }
    }

    private boolean validarCampos(String username, String email, String password, String confirmPassword,
                                  HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (username == null || username.trim().isEmpty()) {
            mostrarError("El nombre de usuario es requerido", request, response);
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            mostrarError("El email es requerido", request, response);
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            mostrarError("La contraseña es requerida", request, response);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            mostrarError("Las contraseñas no coinciden", request, response);
            return false;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres", request, response);
            return false;
        }

        return true;
    }

    private void iniciarSesion(HttpServletRequest request, Object usuario, String username, String atributo)
            throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute(atributo, usuario);
        session.setAttribute("username", username);
        System.out.println("DEBUG: Usuario registrado exitosamente [" + atributo + "]: " + username);
    }

    private void mostrarError(String mensaje, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    private void manejarExcepcion(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.err.println("ERROR en registro: " + e.getMessage());
        e.printStackTrace();
        mostrarError("Error interno del servidor. Inténtalo más tarde", request, response);
    }
}
