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
            java.util.List<com.app.model.Scan> scans = (java.util.List<com.app.model.Scan>) request.getAttribute("scans");
        %>
        <div class="header-actions">
            <a href="logout" class="btn-secondary">Cerrar Sesión</a>
        </div>
    </div>
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
                    <a href="mangaInvitados?scanId=<%= scan.getId() %>" class="btn-primary btn-small">Ver Mangas</a>
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

            <%
                }
            %>
        </div>
    </div>
</div>
</body>
</html>