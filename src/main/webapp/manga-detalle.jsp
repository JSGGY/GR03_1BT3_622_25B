<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><%= request.getAttribute("manga") != null ? ((com.app.model.Manga) request.getAttribute("manga")).getTitulo() : "Manga" %></title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        .manga-detalle-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px;
        }
        
        .manga-header-section {
            display: flex;
            gap: 30px;
            background: #16213e;
            padding: 30px;
            border-radius: 12px;
            margin-bottom: 30px;
        }
        
        .manga-portada {
            flex-shrink: 0;
        }
        
        .manga-portada img {
            width: 300px;
            height: 450px;
            object-fit: cover;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
        }
        
        .manga-info {
            flex: 1;
        }
        
        .manga-info h1 {
            color: #00d4ff;
            margin-bottom: 15px;
            font-size: 2.5em;
        }
        
        .manga-meta {
            display: flex;
            gap: 20px;
            margin: 20px 0;
            flex-wrap: wrap;
        }
        
        .manga-meta-item {
            background: #1a1a2e;
            padding: 10px 20px;
            border-radius: 5px;
        }
        
        .manga-descripcion {
            color: #ddd;
            line-height: 1.8;
            margin: 20px 0;
        }
        
        .manga-actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            flex-wrap: wrap;
        }
        
        .comentarios-section {
            background: #16213e;
            padding: 30px;
            border-radius: 12px;
        }
        
        .comentarios-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 2px solid #00d4ff;
        }
        
        .comentarios-header h2 {
            color: #00d4ff;
            margin: 0;
        }
        
        .comentarios-count {
            background: #1a1a2e;
            padding: 5px 15px;
            border-radius: 20px;
            color: #00d4ff;
        }
        
        .comentario-form {
            background: #1a1a2e;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        
        .comentario-form textarea {
            width: 100%;
            padding: 15px;
            background: #0f1419;
            border: 1px solid #444;
            border-radius: 5px;
            color: #fff;
            font-family: inherit;
            resize: vertical;
            min-height: 100px;
        }
        
        .comentario-form textarea:focus {
            outline: none;
            border-color: #00d4ff;
        }
        
        .comentarios-lista {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        
        .comentario-item {
            background: #1a1a2e;
            padding: 20px;
            border-radius: 8px;
            border-left: 3px solid #00d4ff;
        }
        
        .comentario-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .comentario-autor {
            color: #00d4ff;
            font-weight: bold;
        }
        
        .comentario-fecha {
            color: #888;
            font-size: 0.9em;
        }
        
        .comentario-texto {
            color: #ddd;
            line-height: 1.6;
        }
        
        .empty-comentarios {
            text-align: center;
            padding: 40px;
            color: #888;
        }
        
        .alert {
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .alert-success {
            background: #1a4d1a;
            border: 1px solid #2d7a2d;
            color: #90ee90;
        }
        
        .alert-error {
            background: #4d1a1a;
            border: 1px solid #7a2d2d;
            color: #ff9999;
        }
        
        .alert-info {
            background: #1a3a4d;
            border: 1px solid #2d5a7a;
            color: #90d5ff;
        }
    </style>
</head>
<body class="dashboard-page">
    <div class="manga-detalle-container">
        <%
            Boolean isLectorAutenticado = (Boolean) request.getAttribute("isLectorAutenticado");
            com.app.model.Lector lector = (com.app.model.Lector) request.getAttribute("lector");
            com.app.model.Manga manga = (com.app.model.Manga) request.getAttribute("manga");
            com.app.model.Scan scan = (com.app.model.Scan) request.getAttribute("scan");
            java.util.List<com.app.model.ComentarioManga> comentarios = (java.util.List<com.app.model.ComentarioManga>) request.getAttribute("comentarios");
            Integer totalComentarios = (Integer) request.getAttribute("totalComentarios");

            String imagenPortada;
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

            int totalCapitulos = 0;
            try {
                totalCapitulos = manga.getTotalCapitulos();
            } catch (Exception e) {
                totalCapitulos = 0;
            }
        %>

        <!-- Botón de regreso -->
        <div style="margin-bottom: 20px;">
            <a href="mangaInvitados?scanId=<%= scan.getId() %>" class="btn-secondary">← Volver a <%= scan.getNombre() %></a>
        </div>

        <!-- Mensajes de éxito/error -->
        <%
            String mensaje = (String) session.getAttribute("mensaje");
            String error = (String) session.getAttribute("error");
            if (mensaje != null) {
                session.removeAttribute("mensaje");
        %>
            <div class="alert alert-success">
                ✓ <%= mensaje %>
            </div>
        <% } %>
        <% if (error != null) {
                session.removeAttribute("error");
        %>
            <div class="alert alert-error">
                ✗ <%= error %>
            </div>
        <% } %>

        <!-- Header del manga -->
        <div class="manga-header-section">
            <div class="manga-portada">
                <img src="<%= imagenPortada %>" alt="<%= manga.getTitulo() %>" onerror="this.src='images/default-scan.svg'">
            </div>
            <div class="manga-info">
                <h1><%= manga.getTitulo() %></h1>
                
                <div class="manga-meta">
                    <div class="manga-meta-item <%= estadoClase %>">
                        <strong>Estado:</strong> <%= estadoTexto %>
                    </div>
                    <div class="manga-meta-item">
                        <strong>📖 Capítulos:</strong> <%= totalCapitulos %>
                    </div>
                    <div class="manga-meta-item">
                        <strong>💬 Comentarios:</strong> <%= totalComentarios != null ? totalComentarios : 0 %>
                    </div>
                    <div class="manga-meta-item">
                        <strong>Scan:</strong> <%= scan.getNombre() %>
                    </div>
                </div>
                
                <div class="manga-descripcion">
                    <h3 style="color: #00d4ff;">Sinopsis</h3>
                    <p><%= manga.getDescripcion() != null && !manga.getDescripcion().isEmpty() ? manga.getDescripcion() : "Sin descripción disponible" %></p>
                </div>
                
                <div class="manga-actions">
                    <a href="mostrarCapitulos?mangaId=<%= manga.getId() %>&scanId=<%= scan.getId() %>" class="btn-primary">
                        📖 Leer Capítulos
                    </a>
                    
                    <% if (isLectorAutenticado != null && isLectorAutenticado && lector != null) { %>
                        <form action="favoritos" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="agregar">
                            <input type="hidden" name="mangaId" value="<%= manga.getId() %>">
                            <input type="hidden" name="scanId" value="<%= scan.getId() %>">
                            <button type="submit" class="btn-secondary">❤️ Agregar a Favoritos</button>
                        </form>
                        
                        <button class="btn-secondary" onclick="showAgregarAListaModal(<%= manga.getId() %>, <%= scan.getId() %>)">
                            📋 Agregar a Lista
                        </button>
                    <% } else { %>
                        <a href="index.jsp" class="btn-secondary">❤️ Inicia sesión para agregar a favoritos</a>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Sección de comentarios -->
        <div class="comentarios-section">
            <div class="comentarios-header">
                <h2>💬 Comentarios</h2>
                <span class="comentarios-count"><%= totalComentarios != null ? totalComentarios : 0 %> comentarios</span>
            </div>

            <!-- Formulario para comentar (solo si está autenticado) -->
            <% if (isLectorAutenticado != null && isLectorAutenticado && lector != null) { %>
                <div class="comentario-form">
                    <h3 style="color: #00d4ff; margin-bottom: 15px;">Escribe tu comentario</h3>
                    <form action="comentarioManga" method="post">
                        <input type="hidden" name="action" value="publicar">
                        <input type="hidden" name="mangaId" value="<%= manga.getId() %>">
                        <input type="hidden" name="scanId" value="<%= scan.getId() %>">
                        
                        <textarea name="comentario" placeholder="Comparte tu opinión sobre este manga..." required><%= session.getAttribute("comentarioTemporal") != null ? session.getAttribute("comentarioTemporal") : "" %></textarea>
                        <% session.removeAttribute("comentarioTemporal"); %>
                        
                        <div class="form-actions" style="margin-top: 15px;">
                            <button type="submit" class="btn-primary">Publicar Comentario</button>
                        </div>
                    </form>
                </div>
            <% } else { %>
                <div class="alert alert-info">
                    ℹ️ <a href="index.jsp" style="color: #00d4ff; text-decoration: underline;">Inicia sesión</a> para poder comentar.
                </div>
            <% } %>

            <!-- Lista de comentarios -->
            <div class="comentarios-lista">
                <%
                if (comentarios != null && !comentarios.isEmpty()) {
                    for (com.app.model.ComentarioManga comentario : comentarios) {
                        boolean esPropio = isLectorAutenticado != null && isLectorAutenticado && 
                                          lector != null && 
                                          lector.getUsername().equals(comentario.obtenerNombreLector());
                %>
                    <div class="comentario-item">
                        <div class="comentario-header">
                            <div>
                                <span class="comentario-autor"><%= comentario.obtenerNombreLector() %></span>
                                <span class="comentario-fecha">
                                    • <%= comentario.getFechaComentario() != null ? 
                                        new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                                            java.sql.Timestamp.valueOf(comentario.getFechaComentario())
                                        ) : "" %>
                                </span>
                            </div>
                            <% if (esPropio) { %>
                                <form action="comentarioManga" method="post" style="margin: 0;" onsubmit="return confirm('¿Estás seguro de eliminar este comentario?')">
                                    <input type="hidden" name="action" value="eliminar">
                                    <input type="hidden" name="comentarioId" value="<%= comentario.getId() %>">
                                    <input type="hidden" name="mangaId" value="<%= manga.getId() %>">
                                    <input type="hidden" name="scanId" value="<%= scan.getId() %>">
                                    <button type="submit" class="btn-danger btn-small">Eliminar</button>
                                </form>
                            <% } %>
                        </div>
                        <div class="comentario-texto">
                            <%= comentario.getComentario() %>
                        </div>
                        <% if (comentario.getFechaModificacion() != null) { %>
                            <small style="color: #666; font-style: italic;">
                                Editado: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(
                                    java.sql.Timestamp.valueOf(comentario.getFechaModificacion())
                                ) %>
                            </small>
                        <% } %>
                    </div>
                <%
                    }
                } else {
                %>
                    <div class="empty-comentarios">
                        <p style="font-size: 1.2em;">💭 No hay comentarios aún.</p>
                        <p>¡Sé el primero en compartir tu opinión sobre este manga!</p>
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
                    <% if (listas != null && !listas.isEmpty()) { %>
                        <form id="agregarAListaForm" method="post" action="agregarMangaALista">
                            <input type="hidden" name="mangaId" value="<%= manga.getId() %>">
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
                        <p style="color: #aaa; text-align: center; padding: 20px;">No tienes listas creadas. Crea una desde tu perfil.</p>
                        <div class="form-actions">
                            <button type="button" class="btn-secondary" onclick="hideAgregarAListaModal()">Cerrar</button>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    <% } %>

    <script>
    function showAgregarAListaModal(mangaId, scanId) {
        var modal = document.getElementById('agregarAListaModal');
        if (modal) {
            modal.classList.remove('hidden');
            modal.style.display = 'flex';
        }
    }

    function hideAgregarAListaModal() {
        var modal = document.getElementById('agregarAListaModal');
        if (modal) {
            modal.classList.add('hidden');
            modal.style.display = 'none';
        }
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

