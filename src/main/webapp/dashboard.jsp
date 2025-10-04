<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Dashboard - AdminScan</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body class="dashboard-page">
    <div class="dashboard-container">
        <div class="dashboard-header">
            <%
                com.app.model.AdminScan adminScan = (com.app.model.AdminScan) request.getAttribute("adminScan");
                java.util.List<com.app.model.Scan> scans = (java.util.List<com.app.model.Scan>) request.getAttribute("scans");
            %>
            <h1>¡Bienvenido, <%= adminScan != null ? adminScan.getUsername() : "Usuario" %>!</h1>
            <div class="header-actions">
                <button class="btn-primary" onclick="showCreateForm()">+ Crear Scan</button>
                <a href="logout" class="btn-secondary">Cerrar Sesión</a>
            </div>
        </div>

        <!-- Formulario para crear scan (oculto por defecto) -->
        <div id="createScanForm" class="create-form hidden">
            <h3>Crear Nuevo Scan</h3>
            <form action="crear-scan" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="nombre">Nombre del Scan:</label>
                    <input type="text" id="nombre" name="nombre" required>
                </div>
                <div class="form-group">
                    <label for="descripcion">Descripción:</label>
                    <textarea id="descripcion" name="descripcion" rows="4"></textarea>
                </div>
                <div class="form-group">
                    <label for="imagen">Imagen del Scan:</label>
                    <input type="file" id="imagen" name="imagen" accept="image/*">
                    <small>Opcional. Formatos: JPG, PNG, GIF, WEBP. Máximo 10MB.</small>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-primary">Crear Scan</button>
                    <button type="button" class="btn-secondary" onclick="hideCreateForm()">Cancelar</button>
                </div>
            </form>
        </div>

        <!-- Modal para editar scan -->
        <div id="editScanModal" class="modal hidden">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Editar Scan</h3>
                    <button class="close-btn" onclick="cerrarModalEditar()">&times;</button>
                </div>
                <form id="editScanForm" action="editar-scan" method="post" enctype="multipart/form-data">
                    <input type="hidden" id="editScanId" name="scanId">
                    <input type="hidden" name="action" value="edit">
                    
                    <div class="form-group">
                        <label for="editNombre">Nombre del Scan:</label>
                        <input type="text" id="editNombre" name="nombre" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="editDescripcion">Descripción:</label>
                        <textarea id="editDescripcion" name="descripcion" rows="4"></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label>Imagen actual:</label>
                        <div class="current-image">
                            <img id="currentImage" src="" alt="Imagen actual" style="max-width: 150px; border-radius: 8px;">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="nuevaImagen">Cambiar imagen (opcional):</label>
                        <input type="file" id="nuevaImagen" name="nuevaImagen" accept="image/*">
                        <small>Si no seleccionas nada, se mantendrá la imagen actual.</small>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-primary">Guardar Cambios</button>
                        <button type="button" class="btn-secondary" onclick="cerrarModalEditar()">Cancelar</button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Lista de scans como cards -->
        <div class="scans-container">
            <h2>Mis Scans</h2>
            
            <div class="scans-grid">
                <%
                if (scans != null && !scans.isEmpty()) {
                    for (com.app.model.Scan scan : scans) {
                        String imagenUrl = scan.getImagenUrl();
                        if (imagenUrl == null || imagenUrl.trim().isEmpty()) {
                            imagenUrl = "images/default-scan.svg";
                        }
                %>
                    <div class="scan-card">
                        <div class="card-image">
                            <img src="<%= imagenUrl %>" 
                                 alt="<%= scan.getNombre() %>" 
                                 onerror="this.src='images/default-scan.svg'">
                        </div>
                        <div class="card-header">
                            <h3><%= scan.getNombre() %></h3>
                        </div>
                        <div class="card-body">
                            <p><%= scan.getDescripcion() %></p>
                            <div class="card-stats">
                                <span>Mangas: <%= scan.getMangas() != null ? scan.getMangas().size() : 0 %></span>
                            </div>
                        </div>
                        <div class="card-actions">
                            <a href="manga?scanId=<%= scan.getId() %>" class="btn-primary btn-small">Ver Mangas</a>
                            <button class="btn-secondary btn-small" 
                                    data-scan-id="<%= scan.getId() %>" 
                                    data-scan-nombre="<%= scan.getNombre() %>" 
                                    data-scan-descripcion="<%= scan.getDescripcion() != null ? scan.getDescripcion() : "" %>" 
                                    data-scan-imagen="<%= scan.getImagenUrl() != null ? scan.getImagenUrl() : "" %>"
                                    onclick="editarScanData(this)">Editar</button>
                            <button class="btn-danger btn-small" 
                                    data-scan-id="<%= scan.getId() %>" 
                                    data-scan-nombre="<%= scan.getNombre() %>"
                                    onclick="eliminarScanData(this)">Eliminar</button>
                        </div>
                    </div>
                <%
                    }
                } else {
                %>
                    <div class="empty-state">
                        <p>No tienes scans creados aún.</p>
                        <button class="btn-primary" onclick="showCreateForm()">Crear tu primer Scan</button>
                    </div>
                <%
                }
                %>
            </div>
        </div>
    </div>

    <script>
        function showCreateForm() {
            document.getElementById('createScanForm').classList.remove('hidden');
        }
        
        function hideCreateForm() {
            document.getElementById('createScanForm').classList.add('hidden');
        }
        
        function editarScanData(button) {
            const id = button.getAttribute('data-scan-id');
            const nombre = button.getAttribute('data-scan-nombre');
            const descripcion = button.getAttribute('data-scan-descripcion');
            const imagenUrl = button.getAttribute('data-scan-imagen');
            
            document.getElementById('editScanId').value = id;
            document.getElementById('editNombre').value = nombre;
            document.getElementById('editDescripcion').value = descripcion;
            
            const currentImage = document.getElementById('currentImage');
            if (imagenUrl && imagenUrl.trim() !== '') {
                currentImage.src = imagenUrl;
                currentImage.style.display = 'block';
            } else {
                currentImage.src = 'images/default-scan.svg';
                currentImage.style.display = 'block';
            }
            
            document.getElementById('editScanModal').classList.remove('hidden');
        }
        
        function cerrarModalEditar() {
            document.getElementById('editScanModal').classList.add('hidden');
            // Limpiar formulario
            document.getElementById('editScanForm').reset();
        }
        
        function eliminarScanData(button) {
            const id = button.getAttribute('data-scan-id');
            const nombre = button.getAttribute('data-scan-nombre');
            
            if (confirm('¿Estás seguro de que quieres eliminar el scan "' + nombre + '"?\n\nEsta acción no se puede deshacer y eliminará todos los mangas asociados.')) {
                // Crear formulario temporal para enviar la eliminación
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = 'editar-scan';
                
                const scanIdInput = document.createElement('input');
                scanIdInput.type = 'hidden';
                scanIdInput.name = 'scanId';
                scanIdInput.value = id;
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';
                
                form.appendChild(scanIdInput);
                form.appendChild(actionInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        // Cerrar modal al hacer click fuera de él
        window.onclick = function(event) {
            const modal = document.getElementById('editScanModal');
            if (event.target == modal) {
                cerrarModalEditar();
            }
        }
    </script>
</body>
</html>