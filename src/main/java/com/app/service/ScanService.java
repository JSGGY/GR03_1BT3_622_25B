package com.app.service;

import java.util.List;

import com.app.dao.ScanDAO;
import com.app.model.Scan;

public class ScanService {
    private ScanDAO scanDAO = new ScanDAO();

    /**
     * Guarda un scan en la base de datos
     */
    public boolean guardarScan(Scan scan) {
        try {
            scanDAO.guardar(scan);
            System.out.println("DEBUG: Scan guardado en BD - ID: " + scan.getId());
            return true;
        } catch (Exception e) {
            System.out.println("ERROR: Error al guardar scan: " + e.getMessage());
            e.printStackTrace();
            return false;
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