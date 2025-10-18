package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

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

@WebServlet("/crear-scan")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class CrearScanServlet extends BaseAuthenticatedServlet {
    private ScanService scanService = new ScanService();
    private static final String UPLOAD_DIR = "images" + File.separator + "scans";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirigir al dashboard si acceden directamente a la URL
        response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validar sesión usando método de clase base
        AdminScan adminScan = validateSession(request, response);
        if (adminScan == null) return;

        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        String imagenUrl = "images/default-scan.svg";

        try {
            Part filePart = request.getPart("imagen");
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
                    imagenUrl = UPLOAD_DIR.replace(File.separator, "/") + "/" + uniqueFileName;

                    System.out.println("DEBUG: Imagen guardada en: " + filePath);
                    System.out.println("DEBUG: URL relativa: " + imagenUrl);
                } else {
                    System.out.println("DEBUG: Archivo no es una imagen válida: " + mimeType);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: Error al procesar imagen: " + e.getMessage());
            e.printStackTrace();
        }
        boolean nombreValido = nombre != null && !nombre.trim().isEmpty();
        
        if (nombreValido) {
            Scan nuevoScan = scanService.crearScan(nombre, descripcion, imagenUrl, adminScan);
            
            boolean scanCreadoExitosamente = nuevoScan != null;
            
            if (scanCreadoExitosamente) {
                System.out.println("DEBUG: Scan creado exitosamente con imagen: " + imagenUrl);
            } else {
                System.out.println("ERROR: No se pudo crear el scan");
            }
        }
        response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
    }
}