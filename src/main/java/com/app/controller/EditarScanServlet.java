package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import static com.app.constants.AppConstants.ACTION_DELETE;
import static com.app.constants.AppConstants.ACTION_EDIT;
import static com.app.constants.AppConstants.PARAM_ACTION;
import static com.app.constants.AppConstants.PARAM_SCAN_ID;
import static com.app.constants.AppConstants.ROUTE_DASHBOARD;
import com.app.model.AdminScan;
import com.app.model.Scan;
import com.app.service.ScanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/editar-scan")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class EditarScanServlet extends BaseAuthenticatedServlet {
    private final ScanService scanService = new ScanService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validar sesión usando método de clase base
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        String action = request.getParameter(PARAM_ACTION);
        String scanIdStr = request.getParameter(PARAM_SCAN_ID);

        // Validar scanId usando método de clase base
        if (!validateScanId(scanIdStr, request, response)) return;

        try {
            int scanId = Integer.parseInt(scanIdStr);

            if (ACTION_DELETE.equals(action)) {
                handleDeleteAction(scanId);
            } else if (ACTION_EDIT.equals(action)) {
                handleEditAction(request, response, adminScan, scanId);
            }

        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID de scan inválido: " + scanIdStr);
        } catch (Exception e) {
            System.out.println("ERROR: Error general al procesar scan: " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
    }

    private void handleDeleteAction(int scanId) {
        boolean eliminado = scanService.eliminarScan(scanId);

        if (eliminado) {
            System.out.println("DEBUG: Scan eliminado exitosamente - ID: " + scanId);
        } else {
            System.out.println("ERROR: No se pudo eliminar el scan - ID: " + scanId);
        }
    }

    private void handleEditAction(HttpServletRequest request, HttpServletResponse response,
            AdminScan adminScan, int scanId) throws IOException {

        // Validar acceso al scan antes de obtenerlo
        if (!tieneAccesoAlScan(scanId, adminScan)) {
            System.out.println("ERROR: Scan no encontrado o no autorizado - ID: " + scanId);
            response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
            return;
        }

        Scan scan = scanService.obtenerScanPorId(scanId);

        // Validar ownership usando método de clase base (doble verificación)
        if (!validateScanOwnership(adminScan, scan, request, response)) return;

        // Actualizar campos básicos
        updateBasicFields(request, scan);

        // Procesar nueva imagen si existe
        updateImageIfPresent(request, scan);

        boolean actualizado = scanService.actualizarScan(scan);

        if (actualizado) {
            System.out.println("DEBUG: Scan actualizado exitosamente - ID: " + scanId);
        } else {
            System.out.println("ERROR: No se pudo actualizar el scan - ID: " + scanId);
        }
    }

    private boolean tieneAccesoAlScan(int scanId, AdminScan adminScan) {
        Scan scan = scanService.obtenerScanPorId(scanId);
        // Variables explicativas
        boolean scanExiste = (scan != null);
        boolean creadoPorAdminActual = scanExiste && (scan.getCreadoPor().getId() == adminScan.getId());
        return scanExiste && creadoPorAdminActual;
    }

    private void updateBasicFields(HttpServletRequest request, Scan scan) {
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if (nombre != null && !nombre.trim().isEmpty()) {
            scan.setNombre(nombre);
        }
        if (descripcion != null) {
            scan.setDescripcion(descripcion);
        }
    }

    /**
     * Procesa y actualiza la imagen del scan como BLOB si está presente
     */
    private void updateImageIfPresent(HttpServletRequest request, Scan scan) {
        try {
            Part filePart = request.getPart("nuevaImagen");
            
            if (filePart != null) {
                long fileSize = filePart.getSize();
                String fileName = filePart.getSubmittedFileName();
                String mimeType = filePart.getContentType();
                
                if (fileSize > 0) {
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        fileName = Paths.get(fileName).getFileName().toString();
                        
                        // Validar que sea una imagen válida
                        if (com.app.util.ImagenUtil.validarImagen(mimeType, fileSize)) {
                            // Leer los bytes de la imagen
                            try (InputStream inputStream = filePart.getInputStream()) {
                                byte[] imageBytes = inputStream.readAllBytes();
                                
                                // Verificar que se leyeron bytes
                                if (imageBytes != null && imageBytes.length > 0) {
                                    // Guardar en BLOB
                                    scan.setImagenBlob(imageBytes);
                                    scan.setImagenTipo(mimeType);
                                    scan.setImagenNombre(fileName);
                                    
                                    System.out.println("DEBUG: Nueva imagen procesada para scan - " + fileName + " (" + imageBytes.length + " bytes)");
                                } else {
                                    System.err.println("ERROR: No se pudieron leer bytes de la imagen");
                                }
                            }
                        } else {
                            System.err.println("ERROR: Imagen inválida - " + fileName + " (Tipo: " + mimeType + ", Tamaño: " + fileSize + " bytes)");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR al procesar nueva imagen del scan: " + e.getMessage());
        }
    }
}