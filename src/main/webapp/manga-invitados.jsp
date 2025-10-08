<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mangas - <%= request.getAttribute("scan") != null ? ((com.app.model.Scan) request.getAttribute("scan")).getNombre() : "Scan" %></title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body class="dashboard-page">
    <div class="dashboard-container">
        <%
            com.app.model.Scan scan = (com.app.model.Scan) request.getAttribute("scan");
            java.util.List<com.app.model.Manga> mangas = (java.util.List<com.app.model.Manga>) request.getAttribute("mangas");

            String scanImagenUrl = scan.getImagenUrl();
            if (scanImagenUrl == null || scanImagenUrl.trim().isEmpty()) {
                scanImagenUrl = "images/default-scan.svg";
            }
        %>
        <div class="scan-header">
            <div class="scan-info">
                <div class="scan-image">
                    <img src="<%= scanImagenUrl %>" alt="<%= scan.getNombre() %>" onerror="this.src='images/default-scan.svg'">
                </div>
                <div class="scan-details">
                    <h1><%= scan.getNombre() %></h1>
                    <p class="scan-description"><%= scan.getDescripcion() != null ? scan.getDescripcion() : "" %></p>
                    <div class="scan-stats">
                        <span class="stat">📚 <%= mangas != null ? mangas.size() : 0 %> Mangas</span>
                    </div>
                </div>
            </div>
            <div class="header-actions">
                <a href="ingresoInvitado" class="btn-secondary">← Volver a Scans</a>
            </div>
        </div>

        <div class="mangas-container">
            <h2>Mangas Disponibles</h2>
            <div class="mangas-grid">
                <%
                if (mangas != null && !mangas.isEmpty()) {
                    for (com.app.model.Manga manga : mangas) {
                        String imagenPortada = manga.getImagenPortada();
                        if (imagenPortada == null || imagenPortada.trim().isEmpty()) {
                            imagenPortada = "images/default-scan.svg";
                        }

                        String estadoClase = "";
                        String estadoTexto = "";
                        switch (manga.getEstado()) {
                            case EN_PROGRESO:
                                estadoClase = "estado-progreso";
                                estadoTexto = "En Progreso";
                                break;
                            case PAUSADO:
                                estadoClase = "estado-pausado";
                                estadoTexto = "Pausado";
                                break;
                            case COMPLETADO:
                                estadoClase = "estado-completado";
                                estadoTexto = "Completado";
                                break;
                            case CANCELADO:
                                estadoClase = "estado-cancelado";
                                estadoTexto = "Cancelado";
                                break;
                        }
                %>
                    <div class="manga-card">
                        <div class="card-image">
                            <img src="<%= imagenPortada %>"
                                 alt="<%= manga.getTitulo() %>"
                                 onerror="this.src='images/default-scan.svg'">
                            <div class="manga-estado <%= estadoClase %>">
                                <%= estadoTexto %>
                            </div>
                        </div>
                        <div class="card-header">
                            <h3><%= manga.getTitulo() %></h3>
                        </div>
                        <div class="card-body">
                            <p><%= manga.getDescripcion() != null && !manga.getDescripcion().isEmpty() ? manga.getDescripcion() : "Sin descripción" %></p>
                            <div class="card-stats">
                                <%
                                    int totalCapitulos = 0;
                                    try {
                                        totalCapitulos = manga.getTotalCapitulos();
                                    } catch (Exception e) {
                                        totalCapitulos = 0;
                                    }
                                %>
                                <span>📖 <%= totalCapitulos %> Capítulos</span>
                            </div>
                        </div>
                        <div class="card-actions">
                            <a href="mostrarCapitulos?mangaId=<%= manga.getId() %>&scanId=<%= scan.getId() %>"
                                class="btn-primary btn-small">Leer Capítulos</a>
                        </div>
                    </div>
                <%
                    }
                } else {
                %>
                    <div class="empty-state">
                        <p>No hay mangas disponibles en este scan.</p>
                    </div>
                <%
                }
                %>
            </div>
        </div>
    </div>
</body>
</html>