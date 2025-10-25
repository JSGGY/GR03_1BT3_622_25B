<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body class="dashboard-page">
<div class="dashboard-container">
    <div class="dashboard-header">
        <%
            com.app.model.AdminScan adminScan = (com.app.model.AdminScan) request.getAttribute("adminScan");
            String username = adminScan != null ? adminScan.getUsername() : "Usuario";
        %>
        <h1>Bienvenido, <%= username %></h1>
        <div class="header-actions">
            <button class="btn-primary" onclick="showCreateScanForm()">Crear Nuevo Scan</button>
            <a href="logout" class="btn-secondary">Cerrar Sesión</a>
        </div>
    </div>

    <!-- Modal para crear nuevo scan -->
    <div id="createScanForm" class="create-form hidden">
        <h3>Crear Nuevo Scan</h3>
        <form action="crear-scan" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="nombre">Nombre del Scan:</label>
                <input type="text" id="nombre" name="nombre" required placeholder="Ingresa el nombre del scan">
            </div>
            
            <div class="form-group">
                <label for="descripcion">Descripción:</label>
                <textarea id="descripcion" name="descripcion" rows="4" placeholder="Describe brevemente el scan..."></textarea>
            </div>
            
            <div class="form-group">
                <label for="imagen">Imagen del Scan:</label>
                <input type="file" id="imagen" name="imagen" accept="image/*">
                <small>Opcional. Formatos: JPG, PNG, GIF, WEBP. Máximo 10MB.</small>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn-primary">Crear Scan</button>
                <button type="button" class="btn-secondary" onclick="hideCreateScanForm()">Cancelar</button>
            </div>
        </form>
    </div>

    <div class="scans-container">
        <h2>Mis Scans</h2>
        <div class="scans-grid">
            <%
                java.util.List<com.app.model.Scan> scans = (java.util.List<com.app.model.Scan>) request.getAttribute("scans");
                if (scans != null && !scans.isEmpty()) {
                    for (com.app.model.Scan scan : scans) {
                        // Usar la URL del servlet de imágenes para cargar desde BLOB
                        String imagenUrl = "imagen/scan/" + scan.getId();
            %>
            <div class="scan-card">
                <div class="card-image">
                    <img src="<%= imagenUrl %>" alt="<%= scan.getNombre() %>"
                         onerror="this.src='images/default-scan.svg'">
                </div>
                <div class="card-header">
                    <h3><%= scan.getNombre() %></h3>
                </div>
                <div class="card-body">
                    <p><%= scan.getDescripcion() != null ? scan.getDescripcion() : "Sin descripción" %></p>
                    <div class="card-stats">
                        <span>Mangas: <%= scan.getMangas() != null ? scan.getMangas().size() : 0 %></span>
                    </div>
                </div>
                <div class="card-actions">
                    <a href="manga?scanId=<%= scan.getId() %>" class="btn-primary btn-small">Gestionar Mangas</a>
                    <a href="editar-scan.jsp?scanId=<%= scan.getId() %>" class="btn-secondary btn-small">Editar</a>
                    <button class="btn-danger btn-small" onclick="eliminarScan(<%= scan.getId() %>, '<%= scan.getNombre() %>')">Eliminar</button>
                </div>
            </div>
            <%
                }
            } else {
            %>
            <div class="empty-state">
                <p>No hay scans disponibles en este momento.</p>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>

<script>
function showCreateScanForm() {
    document.getElementById('createScanForm').classList.remove('hidden');
}

function hideCreateScanForm() {
    document.getElementById('createScanForm').classList.add('hidden');
    document.getElementById('createScanForm').querySelector('form').reset();
}

function eliminarScan(scanId, scanNombre) {
    if (confirm('¿Estás seguro de que quieres eliminar el scan "' + scanNombre + '"?')) {
        // Redirigir al servlet de eliminación
        window.location.href = 'eliminar-scan?scanId=' + scanId;
    }
}
</script>
</body>
</html>
