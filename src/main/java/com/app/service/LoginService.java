package com.app.service;

import com.app.dao.AdminScanDAO;
import com.app.dao.LectorDAO;
import com.app.model.AdminScan;
import com.app.model.Lector;

public class LoginService {
    private final AdminScanDAO adminScanDAO = new AdminScanDAO();
    private final LectorDAO lectorDAO = new LectorDAO();

    public AdminScan authenticate(String username, String password) {
        return adminScanDAO.findByUsernameAndPassword(username, password);
    }

    public boolean validarLogin(String username, String password) {
        AdminScan admin = adminScanDAO.findByUsernameAndPassword(username, password);
        return admin != null;
    }
    
    /**
     * Verifica si ya existe un usuario con el username o email dados
     */
    public boolean existeUsuario(String username, String email) {
        try {
            return adminScanDAO.existePorUsernameOEmail(username, email);
        } catch (Exception e) {
            System.err.println("ERROR verificando usuario existente: " + e.getMessage());
            return true;
        }
    }
    public Lector registrarLector(String username, String email, String password) {
        try {

            Lector lector = new Lector();
            lector.setUsername(username);
            lector.setCorreo(email);
            lector.setContrase\u00f1a(password);

            return lectorDAO.guardarCompleto(lector);

        } catch (Exception e) {
            System.err.println("ERROR registrando AdminScan: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Registra un nuevo AdminScan en el sistema
     */
    public AdminScan registrarAdminScan(String username, String email, String password) {
        try {

            AdminScan nuevoAdmin = new AdminScan();
            nuevoAdmin.setUsername(username);
            nuevoAdmin.setCorreo(email);
            nuevoAdmin.setContrase\u00f1a(password);

            return adminScanDAO.guardarCompleto(nuevoAdmin);
            
        } catch (Exception e) {
            System.err.println("ERROR registrando AdminScan: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
