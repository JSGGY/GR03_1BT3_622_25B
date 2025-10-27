<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard - AdminScan</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body class="dashboard-page">
<div class="dashboard-container">
    <div class="dashboard-header">
        <%
            Boolean isLectorAutenticado = (Boolean) request.getAttribute("isLectorAutenticado");
            com.app.model.Lector lector = (com.app.model.Lector) request.getAttribute("lector");
            java.util.List<com.app.model.Scan> scans = (java.util.List<com.app.model.Scan>) request.getAttribute("scans");
            
            String titulo = "Bienvenido Invitado";
            if (isLectorAutenticado != null && isLectorAutenticado && lector != null) {
                titulo = "Bienvenido, " + lector.getUsername();
            }
        %>
        <h1><%= titulo %></h1>
        <div class="header-actions">
            <% if (isLectorAutenticado != null && isLectorAutenticado) { %>
                <!-- Lector autenticado -->
                <a href="perfil" class="btn-primary" style="margin-right: 10px;">👤 Mi Perfil</a>
                <a href="logout" class="btn-secondary">Cerrar Sesión</a>
            <% } else { %>
                <!-- Invitado sin autenticar -->
                <a href="index.jsp" class="btn-secondary">Volver al Inicio</a>
            <% } %>
        </div>
    </div>
    <div class="scans-container">
        <h2>Scans Disponibles</h2>

        <div class="scans-grid">
            <%
                if (scans != null && !scans.isEmpty()) {
                    for (com.app.model.Scan scan : scans) {
                        // Usar la URL del servlet de imágenes para cargar desde BLOB
                        String imagenUrl = "imagen/scan/" + scan.getId();
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
                    <a href="mangaInvitados?scanId=<%= scan.getId() %>" class="btn-primary btn-small">Ver Mangas</a>
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
</body>
</html>