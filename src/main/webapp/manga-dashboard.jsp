<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard - <%= request.getAttribute("scan") != null ? ((com.app.model.Scan) request.getAttribute("scan")).getNombre() : "Manga" %></title>
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
                <button class="btn-primary" onclick="showCreateMangaForm()">+ Agregar Manga</button>
                <a href="dashboard" class="btn-secondary">← Volver al Dashboard</a>
            </div>
        </div>
        <div id="createMangaForm" class="create-form hidden">
            <h3>Agregar Nuevo Manga</h3>
            <form action="manga" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="scanId" value="<%= scan.getId() %>">

                <div class="form-group">
                    <label for="titulo">Título del Manga:</label>
                    <input type="text" id="titulo" name="titulo" required>
                </div>

                <div class="form-group">
                    <label for="descripcion">Descripción:</label>
                    <textarea id="descripcion" name="descripcion" rows="4" placeholder="Describe brevemente el manga..."></textarea>
                </div>
                <div class="form-group">
                    <label for="estado">Estado:</label>
                    <select id="estado" name="estado" required>
                        <option value="EN_PROGRESO">En Progreso</option>
                        <option value="PAUSADO">Pausado</option>
                        <option value="COMPLETADO">Completado</option>
                        <option value="CANCELADO">Cancelado</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="imagenPortada">Imagen de Portada:</label>
                    <input type="file" id="imagenPortada" name="imagenPortada" accept="image/*">
                    <small>Opcional. Formatos: JPG, PNG, GIF, WEBP. Máximo 10MB.</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-primary">Crear Manga</button>
                    <button type="button" class="btn-secondary" onclick="hideCreateMangaForm()">Cancelar</button>
                </div>
            </form>
        </div>
        <div id="editMangaModal" class="modal hidden">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Editar Manga</h3>
                    <button class="close-btn" onclick="cerrarModalEditarManga()">&times;</button>
                </div>
                <form id="editMangaForm" action="manga" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" id="editMangaId" name="mangaId">

                    <div class="form-group">
                        <label for="editTitulo">Título del Manga:</label>
                        <input type="text" id="editTitulo" name="titulo" required>
                    </div>

                    <div class="form-group">
                        <label for="editDescripcion">Descripción:</label>
                        <textarea id="editDescripcion" name="descripcion" rows="4"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="editEstado">Estado:</label>
                        <select id="editEstado" name="estado" required>
                            <option value="EN_PROGRESO">En Progreso</option>
                            <option value="PAUSADO">Pausado</option>
                            <option value="COMPLETADO">Completado</option>
                            <option value="CANCELADO">Cancelado</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Imagen actual:</label>
                        <div class="current-image">
                            <img id="currentMangaImage" src="" alt="Portada actual" style="max-width: 150px; border-radius: 8px;">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="nuevaImagenPortada">Cambiar portada (opcional):</label>
                        <input type="file" id="nuevaImagenPortada" name="nuevaImagenPortada" accept="image/*">
                        <small>Si no seleccionas nada, se mantendrá la portada actual.</small>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-primary">Guardar Cambios</button>
                        <button type="button" class="btn-secondary" onclick="cerrarModalEditarManga()">Cancelar</button>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Modal para crear capítulo -->
        <div id="createCapituloModal" class="modal hidden">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Crear Nuevo Capítulo</h3>
                    <button class="close-btn" onclick="hideCreateCapituloForm()">&times;</button>
                </div>
                <form id="createCapituloForm" action="capitulo" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" id="capituloMangaId" name="mangaId">
                    <input type="hidden" id="capituloScanId" name="scanId" value="<%= scan.getId() %>">

                    <div class="form-group">
                        <label for="numeroCapitulo">Número de Capítulo:</label>
                        <input type="number" id="numeroCapitulo" name="numero" min="1" required>
                    </div>

                    <div class="form-group">
                        <label for="tituloCapitulo">Título del Capítulo:</label>
                        <input type="text" id="tituloCapitulo" name="titulo" required>
                    </div>

                    <div class="form-group">
                        <label for="descripcionCapitulo">Descripción:</label>
                        <textarea id="descripcionCapitulo" name="descripcion" rows="3" placeholder="Descripción opcional del capítulo"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="imagenesCapitulo">Páginas del Capítulo:</label>
                        <input type="file" id="imagenesCapitulo" name="imagenes" multiple accept="image/*" required>
                        <small>Selecciona las páginas del capítulo en orden. Formatos: JPG, PNG, WEBP</small>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-primary">Crear Capítulo</button>
                        <button type="button" class="btn-secondary" onclick="hideCreateCapituloForm()">Cancelar</button>
                    </div>
                </form>
            </div>
        </div>
        
        <div class="mangas-container">
            <h2>Mangas de este Scan</h2>
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
                                class="btn-primary btn-small">Ver Capítulos</a>
                            <button class="btn-success btn-small"
                                    data-manga-id="<%= manga.getId() %>"
                                    data-manga-titulo="<%= manga.getTitulo() %>"
                                    onclick="showCreateCapituloForm(this)">+ Capítulo</button>
                            <button class="btn-secondary btn-small"
                                    data-manga-id="<%= manga.getId() %>"
                                    data-manga-titulo="<%= manga.getTitulo() %>"
                                    data-manga-descripcion="<%= manga.getDescripcion() != null ? manga.getDescripcion() : "" %>"
                                    data-manga-estado="<%= manga.getEstado() %>"
                                    data-manga-imagen="<%= manga.getImagenPortada() != null ? manga.getImagenPortada() : "" %>"
                                    onclick="editarMangaData(this)">Editar</button>
                            <button class="btn-danger btn-small"
                                    data-manga-id="<%= manga.getId() %>"
                                    data-manga-titulo="<%= manga.getTitulo() %>"
                                    onclick="eliminarMangaData(this)">Eliminar</button>
                        </div>
                    </div>
                <%
                    }
                } else {
                %>
                    <div class="empty-state">
                        <p>No hay mangas en este scan aún.</p>
                        <button class="btn-primary" onclick="showCreateMangaForm()">Agregar el primer Manga</button>
                    </div>
                <%
                }
                %>
            </div>
        </div>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
    </div>

    <script>
        function showCreateMangaForm() {
            document.getElementById('createMangaForm').classList.remove('hidden');
        }

        function hideCreateMangaForm() {
            document.getElementById('createMangaForm').classList.add('hidden');
        }

        function editarMangaData(button) {
            const id = button.getAttribute('data-manga-id');
            const titulo = button.getAttribute('data-manga-titulo');
            const descripcion = button.getAttribute('data-manga-descripcion');
            const estado = button.getAttribute('data-manga-estado');
            const imagenUrl = button.getAttribute('data-manga-imagen');

            document.getElementById('editMangaId').value = id;
            document.getElementById('editTitulo').value = titulo;
            document.getElementById('editDescripcion').value = descripcion;
            document.getElementById('editEstado').value = estado;

            const currentImage = document.getElementById('currentMangaImage');
            if (imagenUrl && imagenUrl.trim() !== '') {
                currentImage.src = imagenUrl;
                currentImage.style.display = 'block';
            } else {
                currentImage.src = 'images/default-scan.svg';
                currentImage.style.display = 'block';
            }

            document.getElementById('editMangaModal').classList.remove('hidden');
        }

        function cerrarModalEditarManga() {
            document.getElementById('editMangaModal').classList.add('hidden');
            document.getElementById('editMangaForm').reset();
        }

        function eliminarMangaData(button) {
            const id = button.getAttribute('data-manga-id');
            const titulo = button.getAttribute('data-manga-titulo');

            if (confirm('¿Estás seguro de que quieres eliminar el manga "' + titulo + '"?\n\nEsta acción no se puede deshacer y eliminará todos los capítulos asociados.')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = 'manga';

                const mangaIdInput = document.createElement('input');
                mangaIdInput.type = 'hidden';
                mangaIdInput.name = 'mangaId';
                mangaIdInput.value = id;

                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';

                form.appendChild(mangaIdInput);
                form.appendChild(actionInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        // Funciones para el modal de crear capítulo
        function showCreateCapituloForm(button) {
            const mangaId = button.getAttribute('data-manga-id');
            const mangaTitulo = button.getAttribute('data-manga-titulo');
            
            document.getElementById('capituloMangaId').value = mangaId;
            document.querySelector('#createCapituloModal .modal-header h3').textContent = 'Crear Nuevo Capítulo para "' + mangaTitulo + '"';
            document.getElementById('createCapituloModal').classList.remove('hidden');
        }

        function hideCreateCapituloForm() {
            document.getElementById('createCapituloModal').classList.add('hidden');
            document.getElementById('createCapituloForm').reset();
        }
        
        window.onclick = function(event) {
            const editModal = document.getElementById('editMangaModal');
            const createCapituloModal = document.getElementById('createCapituloModal');
            
            if (event.target == editModal) {
                cerrarModalEditarManga();
            }
            
            if (event.target == createCapituloModal) {
                hideCreateCapituloForm();
            }
        }
    </script>
</body>
</html>