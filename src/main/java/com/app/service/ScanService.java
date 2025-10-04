package com.app.service;

import java.util.List;

import com.app.dao.ScanDAO;
import com.app.model.AdminScan;
import com.app.model.Scan;

public class ScanService {
    private ScanDAO scanDAO = new ScanDAO();

    public Scan crearScan(String nombre, String descripcion, AdminScan adminScan) {
        return crearScan(nombre, descripcion, "images/default-scan.svg", adminScan);
    }
    
    public Scan crearScan(String nombre, String descripcion, String imagenUrl, AdminScan adminScan) {
        try {
            Scan nuevoScan = new Scan();
            nuevoScan.setNombre(nombre);
            nuevoScan.setDescripcion(descripcion);
            nuevoScan.setImagenUrl(imagenUrl != null ? imagenUrl : "images/default-scan.svg");
            nuevoScan.setCreadoPor(adminScan);
            
            scanDAO.guardar(nuevoScan);
            return nuevoScan;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Scan> obtenerScansPorAdmin(int adminScanId) {
        return scanDAO.buscarPorAdminScan(adminScanId);
    }

    public Scan obtenerScanPorId(int id) {
        return scanDAO.buscarPorId(id);
    }

    public List<Scan> obtenerTodosLosScans() {
        return scanDAO.listarTodos();
    }

    public boolean actualizarScan(Scan scan) {
        try {
            scanDAO.actualizar(scan);
            System.out.println("DEBUG: Scan actualizado en BD - ID: " + scan.getId());
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: Error al actualizar scan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarScan(int id) {
        try {
            // Verificar que el scan existe antes de eliminar
            Scan scan = scanDAO.buscarPorId(id);
            if (scan == null) {
                System.out.println("ERROR: Scan no encontrado para eliminar - ID: " + id);
                return false;
            }
            
            scanDAO.eliminar(id);
            System.out.println("DEBUG: Scan eliminado de BD - ID: " + id);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: Error al eliminar scan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}