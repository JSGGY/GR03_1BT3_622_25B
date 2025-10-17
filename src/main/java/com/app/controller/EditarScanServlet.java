package com.app.controller;

import static com.app.constants.AppConstants.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

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
    private static final String UPLOAD_DIR = "images" + File.separator + "scans";

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

        Scan scan = scanService.obtenerScanPorId(scanId);

        // Validar ownership usando método de clase base
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

    private void updateImageIfPresent(HttpServletRequest request, Scan scan) {
        try {
            Part filePart = request.getPart("nuevaImagen");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String mimeType = filePart.getContentType();

                if (mimeType != null && mimeType.startsWith("image/")) {
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

                    File uploadDir = new File(uploadFilePath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    String filePath = uploadFilePath + File.separator + uniqueFileName;
                    filePart.write(filePath);
                    String nuevaImagenUrl = UPLOAD_DIR.replace(File.separator, "/") + "/" + uniqueFileName;
                    scan.setImagenUrl(nuevaImagenUrl);

                    System.out.println("DEBUG: Nueva imagen guardada: " + nuevaImagenUrl);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: Error al procesar nueva imagen: " + e.getMessage());
        }
    }
}