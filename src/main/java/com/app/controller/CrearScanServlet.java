package com.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import com.app.model.AdminScan;
import com.app.model.Scan;
import com.app.service.ScanService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/crear-scan")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class CrearScanServlet extends HttpServlet {
    private ScanService scanService = new ScanService();
    private static final String UPLOAD_DIR = "images" + File.separator + "scans";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        AdminScan adminScan = (AdminScan) session.getAttribute("adminScan");
        
        if (adminScan == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        
        // Manejar upload de imagen
        String imagenUrl = "images/default-scan.svg"; // Imagen por defecto
        
        try {
            Part filePart = request.getPart("imagen");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                
                // Validar que sea una imagen
                String mimeType = filePart.getContentType();
                if (mimeType != null && mimeType.startsWith("image/")) {
                    // Generar nombre único para evitar conflictos
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    
                    // Obtener ruta absoluta del directorio webapp
                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
                    
                    // Crear directorio si no existe
                    File uploadDir = new File(uploadFilePath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    // Guardar archivo
                    String filePath = uploadFilePath + File.separator + uniqueFileName;
                    filePart.write(filePath);
                    
                    // Guardar ruta relativa en BD
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
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            Scan nuevoScan = scanService.crearScan(nombre, descripcion, imagenUrl, adminScan);
            
            if (nuevoScan != null) {
                System.out.println("DEBUG: Scan creado exitosamente con imagen: " + imagenUrl);
            } else {
                System.out.println("ERROR: No se pudo crear el scan");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}