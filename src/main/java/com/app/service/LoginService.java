package com.app.service;

import com.app.dao.AdminScanDAO;
import com.app.model.AdminScan;

public class LoginService {
    private final AdminScanDAO adminScanDAO = new AdminScanDAO();

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
            return true; // En caso de error, asumimos que existe para evitar duplicados
        }
    }
    
    /**
     * Registra un nuevo AdminScan en el sistema
     */
    public AdminScan registrarAdminScan(String username, String email, String password) {
        try {
            // Crear nuevo AdminScan
            AdminScan nuevoAdmin = new AdminScan();
            nuevoAdmin.setUsername(username);
            nuevoAdmin.setCorreo(email);
            nuevoAdmin.setContrase\u00f1a(password);
            
            // Guardar en la base de datos
            return adminScanDAO.guardarCompleto(nuevoAdmin);
            
        } catch (Exception e) {
            System.err.println("ERROR registrando AdminScan: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
