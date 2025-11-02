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
            Boolean isLectorAutenticado = (Boolean) request.getAttribute("isLectorAutenticado");
            com.app.model.Lector lector = (com.app.model.Lector) request.getAttribute("lector");
            com.app.model.Scan scan = (com.app.model.Scan) request.getAttribute("scan");
            java.util.List<com.app.model.Manga> mangas = (java.util.List<com.app.model.Manga>) request.getAttribute("mangas");

            // Usar la URL del servlet de imágenes para cargar desde BLOB
            String scanImagenUrl = "imagen/scan/" + scan.getId();
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
                <% if (isLectorAutenticado != null && isLectorAutenticado && lector != null) { %>
                    <!-- Lector autenticado -->
                    <span style="margin-right: 15px; color: #666;">👤 <%= lector.getUsername() %></span>
                    <a href="logout" class="btn-secondary">Cerrar Sesión</a>
                    <a href="ingresoInvitado" class="btn-secondary" style="margin-left: 10px;">← Volver a Scans</a>
                <% } else { %>
                    <!-- Invitado sin autenticar -->
                    <a href="ingresoInvitado" class="btn-secondary">← Volver a Scans</a>
                <% } %>
            </div>
        </div>

        <div class="mangas-container">
            <h2>Mangas Disponibles</h2>
            <div class="mangas-grid">
                <%
                if (mangas != null && !mangas.isEmpty()) {
                    for (com.app.model.Manga manga : mangas) {
                        String imagenPortada;
                        // Priorizar BLOB sobre URL legacy
                        if (manga.getPortadaBlob() != null && manga.getPortadaBlob().length > 0) {
                            imagenPortada = "imagen/manga/" + manga.getId();
                        } else {
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
                            <% if (isLectorAutenticado != null && isLectorAutenticado && lector != null) { %>
                                <button class="btn-secondary btn-small" onclick="showAgregarAListaModal(<%= manga.getId() %>, <%= scan.getId() %>)">Agregar a Lista</button>
                            <% } %>
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

    <!-- Modal para agregar manga a lista (solo para lectores autenticados) -->
    <% if (isLectorAutenticado != null && isLectorAutenticado && lector != null) { %>
        <%
            java.util.List<com.app.model.Lista> listas = (java.util.List<com.app.model.Lista>) request.getAttribute("listas");
        %>
        <div id="agregarAListaModal" class="modal hidden">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Agregar Manga a Lista</h3>
                    <button type="button" class="close-btn" onclick="hideAgregarAListaModal()">×</button>
                </div>
                <div style="padding: 30px;">
                    <!-- Pestañas: Agregar a lista existente o Crear nueva -->
                    <div style="display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 2px solid #555588;">
                        <button type="button" class="btn-secondary btn-small" onclick="showAgregarAListaTab()" id="btnAgregarTab" style="border-bottom: 2px solid #00d4ff; margin-bottom: -2px;">Agregar a Lista</button>
                        <button type="button" class="btn-secondary btn-small" onclick="showCrearListaTab()" id="btnCrearTab">Crear Nueva Lista</button>
                    </div>
                    
                    <!-- Tab: Agregar a lista existente -->
                    <div id="agregarAListaTab">
                        <% if (listas != null && !listas.isEmpty()) { %>
                            <form id="agregarAListaForm" method="post" action="agregarMangaALista">
                                <input type="hidden" id="mangaIdInput" name="mangaId" value="">
                                <input type="hidden" name="scanId" value="<%= scan.getId() %>">
                                <div class="form-group">
                                    <label for="listaSelect">Selecciona una lista:</label>
                                    <select id="listaSelect" name="listaId" required>
                                        <option value="">-- Selecciona una lista --</option>
                                        <% for (com.app.model.Lista lista : listas) { %>
                                            <option value="<%= lista.getId() %>"><%= lista.getNombre() %> (<%= lista.getTotalMangas() %> mangas)</option>
                                        <% } %>
                                    </select>
                                </div>
                                <div class="form-actions">
                                    <button type="submit" class="btn-primary">Agregar</button>
                                    <button type="button" class="btn-secondary" onclick="hideAgregarAListaModal()">Cancelar</button>
                                </div>
                            </form>
                        <% } else { %>
                            <p style="color: #aaa; text-align: center; padding: 20px;">No tienes listas creadas. Crea una nueva lista usando la pestaña "Crear Nueva Lista".</p>
                            <div class="form-actions">
                                <button type="button" class="btn-secondary" onclick="hideAgregarAListaModal()">Cerrar</button>
                            </div>
                        <% } %>
                    </div>
                    
                    <!-- Tab: Crear nueva lista -->
                    <div id="crearListaTab" style="display: none;">
                        <form id="crearListaForm" method="post" action="lista">
                            <input type="hidden" name="action" value="crear">
                            <input type="hidden" id="mangaIdParaCrear" name="mangaIdParaAgregar" value="">
                            <input type="hidden" name="scanIdParaAgregar" value="<%= scan.getId() %>">
                            <div class="form-group">
                                <label for="nombreListaModal">Nombre de la Lista:</label>
                                <input type="text" id="nombreListaModal" name="nombre" required placeholder="Ej: Mi Lista de Favoritos">
                            </div>
                            <div class="form-group">
                                <label for="descripcionListaModal">Descripción (opcional):</label>
                                <textarea id="descripcionListaModal" name="descripcion" rows="2" placeholder="Describe tu lista..."></textarea>
                            </div>
                            <div class="form-actions">
                                <button type="submit" class="btn-primary">Crear y Agregar</button>
                                <button type="button" class="btn-secondary" onclick="hideAgregarAListaModal()">Cancelar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    <% } %>

    <script>
    function showAgregarAListaModal(mangaId, scanId) {
        document.getElementById('mangaIdInput').value = mangaId;
        document.getElementById('mangaIdParaCrear').value = mangaId;
        var modal = document.getElementById('agregarAListaModal');
        modal.classList.remove('hidden');
        modal.style.display = 'flex';
        showAgregarAListaTab();
    }

    function hideAgregarAListaModal() {
        var modal = document.getElementById('agregarAListaModal');
        modal.classList.add('hidden');
        modal.style.display = 'none';
        if (document.getElementById('agregarAListaForm')) {
            document.getElementById('agregarAListaForm').reset();
        }
        if (document.getElementById('crearListaForm')) {
            document.getElementById('crearListaForm').reset();
        }
    }

    function showAgregarAListaTab() {
        document.getElementById('agregarAListaTab').style.display = 'block';
        document.getElementById('crearListaTab').style.display = 'none';
        document.getElementById('btnAgregarTab').style.borderBottom = '2px solid #00d4ff';
        document.getElementById('btnCrearTab').style.borderBottom = 'none';
    }

    function showCrearListaTab() {
        document.getElementById('agregarAListaTab').style.display = 'none';
        document.getElementById('crearListaTab').style.display = 'block';
        document.getElementById('btnAgregarTab').style.borderBottom = 'none';
        document.getElementById('btnCrearTab').style.borderBottom = '2px solid #00d4ff';
    }

    // Cerrar modal al hacer clic fuera de él
    window.onclick = function(event) {
        var modal = document.getElementById('agregarAListaModal');
        if (event.target == modal) {
            hideAgregarAListaModal();
        }
    }
    </script>
</body>
</html>