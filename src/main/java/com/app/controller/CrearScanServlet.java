package com.app.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

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

        // Crear nuevo scan
        Scan nuevoScan = new Scan();
        nuevoScan.setNombre(nombre);
        nuevoScan.setDescripcion(descripcion);
        nuevoScan.setCreadoPor(adminScan);

        // Procesar imagen como BLOB
        procesarImagenScan(request, nuevoScan);

        boolean nombreValido = nombre != null && !nombre.trim().isEmpty();
        
        if (nombreValido) {
            boolean guardado = scanService.guardarScan(nuevoScan);
            
            if (guardado) {
                System.out.println("DEBUG: Scan creado exitosamente con imagen BLOB");
            } else {
                System.out.println("ERROR: No se pudo crear el scan");
            }
        }
        response.sendRedirect(request.getContextPath() + ROUTE_DASHBOARD);
    }

    /**
     * Procesa la imagen del scan y la guarda como BLOB
     */
    private void procesarImagenScan(HttpServletRequest request, Scan scan) {
        try {
            Part filePart = request.getPart("imagen");
            
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
                                    
                                    System.out.println("DEBUG: Imagen procesada para scan - " + fileName + " (" + imageBytes.length + " bytes)");
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
            System.err.println("ERROR al procesar imagen del scan: " + e.getMessage());
        }
    }
}