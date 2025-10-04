package com.app.controller;

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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/editar-scan")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class EditarScanServlet extends HttpServlet {
    private final ScanService scanService = new ScanService();
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
        
        String action = request.getParameter("action");
        String scanIdStr = request.getParameter("scanId");
        
        if (scanIdStr == null || scanIdStr.trim().isEmpty()) {
            System.out.println("ERROR: ID de scan no proporcionado");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        try {
            int scanId = Integer.parseInt(scanIdStr);
            
            if ("delete".equals(action)) {
                // Eliminar scan
                boolean eliminado = scanService.eliminarScan(scanId);
                if (eliminado) {
                    System.out.println("DEBUG: Scan eliminado exitosamente - ID: " + scanId);
                } else {
                    System.out.println("ERROR: No se pudo eliminar el scan - ID: " + scanId);
                }
            } else if ("edit".equals(action)) {
                // Editar scan
                Scan scan = scanService.obtenerScanPorId(scanId);
                if (scan == null || scan.getCreadoPor().getId() != adminScan.getId()) {
                    System.out.println("ERROR: Scan no encontrado o no autorizado - ID: " + scanId);
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
                
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                
                // Actualizar campos básicos
                if (nombre != null && !nombre.trim().isEmpty()) {
                    scan.setNombre(nombre);
                }
                if (descripcion != null) {
                    scan.setDescripcion(descripcion);
                }
                
                // Manejar nueva imagen si se proporciona
                try {
                    Part filePart = request.getPart("nuevaImagen");
                    if (filePart != null && filePart.getSize() > 0) {
                        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                        
                        // Validar que sea una imagen
                        String mimeType = filePart.getContentType();
                        if (mimeType != null && mimeType.startsWith("image/")) {
                            // Generar nombre único
                            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                            
                            // Obtener ruta absoluta
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
                            
                            // Actualizar URL en la entidad
                            String nuevaImagenUrl = UPLOAD_DIR.replace(File.separator, "/") + "/" + uniqueFileName;
                            scan.setImagenUrl(nuevaImagenUrl);
                            
                            System.out.println("DEBUG: Nueva imagen guardada: " + nuevaImagenUrl);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: Error al procesar nueva imagen: " + e.getMessage());
                }
                
                // Guardar cambios
                boolean actualizado = scanService.actualizarScan(scan);
                if (actualizado) {
                    System.out.println("DEBUG: Scan actualizado exitosamente - ID: " + scanId);
                } else {
                    System.out.println("ERROR: No se pudo actualizar el scan - ID: " + scanId);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID de scan inválido: " + scanIdStr);
        } catch (Exception e) {
            System.out.println("ERROR: Error general al procesar scan: " + e.getMessage());
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}