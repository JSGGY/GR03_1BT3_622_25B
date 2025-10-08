<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard - AdminScan</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body class="dashboard-page">
<div class="dashboard-container">
    <div class="dashboard-header">
        <h1>Bienvenido Invitado</h1>
        <%
            java.util.List<com.app.model.Scan> scans = (java.util.List<com.app.model.Scan>) request.getAttribute("scans");
        %>
        <div class="header-actions">
            <a href="index.jsp" class="btn-secondary">Volver al Inicio</a>
        </div>
    </div>
    <div class="scans-container">
        <h2>Scans Disponibles</h2>

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