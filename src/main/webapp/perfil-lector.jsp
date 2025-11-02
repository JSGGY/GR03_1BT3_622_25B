<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.app.model.Lector" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.util.Base64" %>

<%
    Lector lector = (Lector) request.getAttribute("lector");
    
    // Redirigir si no hay lector autenticado
    if (lector == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String mensaje = (String) request.getAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil - <%= lector.getUsername() %></title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/styles.css">
    <style>
        .perfil-edit-form {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-top: 20px;
        }
        
        .form-field {
            display: flex;
            flex-direction: column;
        }
        
        .form-field label {
            color: #00d4ff;
            font-size: 14px;
            margin-bottom: 5px;
            font-weight: 500;
        }
        
        .form-field input {
            padding: 10px;
            border: 1px solid #555588;
            border-radius: 5px;
            background-color: #16213e;
            color: #fff;
            font-size: 14px;
        }
        
        .form-field input:focus {
            outline: none;
            border-color: #00d4ff;
            box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
        }
        
        .perfil-actions {
            grid-column: span 3;
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 10px;
        }
        
        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
            border-left: 4px solid;
        }
        
        .alert-success {
            background-color: rgba(0, 212, 255, 0.1);
            border-left-color: #00d4ff;
            color: #00d4ff;
        }
        
        .alert-error {
            background-color: rgba(255, 64, 129, 0.1);
            border-left-color: #ff4081;
            color: #ff4081;
        }
        
        .section-container {
            margin-top: 30px;
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }
        
        .section-card {
            background: linear-gradient(135deg, #16213e, #1a1a2e);
            border: 1px solid #555588;
            border-radius: 10px;
            padding: 25px;
            transition: all 0.3s ease;
        }
        
        .section-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0, 212, 255, 0.2);
            border-color: #00d4ff;
        }
        
        .section-card h3 {
            color: #00d4ff;
            font-size: 1.5em;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .section-card .empty-state {
            color: #888;
            font-style: italic;
            text-align: center;
            padding: 40px 20px;
        }
        
        @media (max-width: 768px) {
            .perfil-edit-form {
                grid-template-columns: 1fr;
            }
            
            .perfil-actions {
                grid-column: span 1;
            }
            
            .section-container {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body class="dashboard-page">
    <div class="dashboard-container">
        <!-- Header del Perfil con datos editables -->
        <div class="dashboard-header">
            <div>
                <h1>👤 Mi Perfil</h1>
                <p style="color: #888; margin-top: 5px;">Gestiona tu información personal</p>
            </div>
            <div class="header-actions">
                <a href="<%= request.getContextPath() %>/ingresoInvitado" class="btn-secondary">← Volver al Dashboard</a>
            </div>
        </div>
        
        <!-- Mensajes de alerta -->
        <% if (error != null) { %>
            <div class="alert alert-error">
                ❌ <%= error %>
            </div>
        <% } %>
        
        <% if (success != null) { %>
            <div class="alert alert-success">
                ✅ <%= success %>
            </div>
        <% } %>
        
        <!-- Formulario de edición de datos -->
        <div class="card" style="margin-bottom: 30px;">
            <div class="card-header">
                <h2 style="color: #00d4ff; margin: 0;">📝 Editar Información</h2>
            </div>
            <div class="card-body">
                <form action="<%= request.getContextPath() %>/perfil" method="POST">
                    <div class="perfil-edit-form">
                        <div class="form-field">
                            <label for="username">Nombre de Usuario</label>
                            <input 
                                type="text" 
                                id="username" 
                                name="username" 
                                value="<%= lector.getUsername() %>" 
                                required
                                minlength="3"
                                maxlength="50"
                                placeholder="Tu nombre de usuario">
                        </div>
                        
                        <div class="form-field">
                            <label for="correo">Correo Electrónico</label>
                            <input 
                                type="email" 
                                id="correo" 
                                name="correo" 
                                value="<%= lector.getCorreo() %>" 
                                required
                                placeholder="tu@email.com">
                        </div>
                        
                        <div class="form-field">
                            <label for="password">Contraseña</label>
                            <input 
                                type="password" 
                                id="password" 
                                name="password" 
                                value="<%= lector.getContraseña() %>" 
                                required
                                minlength="6"
                                placeholder="Mínimo 6 caracteres">
                        </div>
                        
                        <div class="perfil-actions">
                            <button type="submit" class="btn-primary">💾 Guardar Cambios</button>
                            <a href="<%= request.getContextPath() %>/ingresoInvitado" class="btn-secondary">❌ Cancelar</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        
        <!-- Secciones de Favoritos y Listas -->
        <div class="section-container">
            <!-- Sección Favoritos -->
            <div class="section-card">
                <h3>⭐ Favoritos</h3>
                <div class="empty-state">
                    <p>📚 Aún no tienes mangas favoritos</p>
                    <p style="font-size: 12px; margin-top: 10px;">Explora el catálogo y agrega tus favoritos</p>
                </div>
            </div>
            
            <!-- Sección Listas -->
            <div class="section-card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                    <h3>📋 Mis Listas</h3>
                    <button class="btn-primary btn-small" onclick="showCreateListaForm()">+ Crear Lista</button>
                </div>

                <!-- Mensajes de éxito/error -->
                <% if (mensaje != null) { %>
                    <div class="alert alert-success" style="margin-bottom: 15px;">
                        ✅ <%= mensaje %>
                    </div>
                <% } %>
                <% if (error != null) { %>
                    <div class="alert alert-error" style="margin-bottom: 15px;">
                        ❌ <%= error %>
                    </div>
                <% } %>

                <!-- Formulario para crear nueva lista (oculto por defecto) -->
                <div id="createListaForm" class="create-form hidden" style="margin-bottom: 15px; background: #2a2a3e; padding: 20px; border-radius: 10px; border: 1px solid #444;">
                    <h3 style="color: #00d4ff; margin-bottom: 15px; font-size: 1.2em;">Crear Nueva Lista</h3>
                    <form action="lista" method="post">
                        <input type="hidden" name="action" value="crear">
                        <div class="form-group" style="margin-bottom: 15px;">
                            <label for="nombreLista" style="display: block; margin-bottom: 5px; color: #fff; font-weight: 500;">Nombre de la Lista:</label>
                            <input type="text" id="nombreLista" name="nombre" required
                                   placeholder="Ej: Mi Lista de Favoritos"
                                   style="width: 100%; padding: 10px; border-radius: 5px; border: 1px solid #555588; background: #2c2c2c; color: #fff;">
                        </div>
                        <div class="form-group" style="margin-bottom: 15px;">
                            <label for="descripcionLista" style="display: block; margin-bottom: 5px; color: #fff; font-weight: 500;">Descripción (opcional):</label>
                            <textarea id="descripcionLista" name="descripcion" rows="2"
                                      placeholder="Describe tu lista..."
                                      style="width: 100%; padding: 10px; border-radius: 5px; border: 1px solid #555588; background: #2c2c2c; color: #fff; resize: vertical;"></textarea>
                        </div>
                        <div style="display: flex; gap: 10px;">
                            <button type="submit" class="btn-primary btn-small">Crear Lista</button>
                            <button type="button" class="btn-secondary btn-small" onclick="hideCreateListaForm()">Cancelar</button>
                        </div>
                    </form>
                </div>

                <!-- Lista de listas creadas -->
                <%
                    java.util.List<com.app.model.Lista> listas = (java.util.List<com.app.model.Lista>) request.getAttribute("listas");
                    if (listas != null && !listas.isEmpty()) {
                %>
                    <div style="display: flex; flex-direction: column; gap: 15px;">
                        <% for (com.app.model.Lista lista : listas) { %>
                            <div style="background: #1a1a2e; padding: 15px; border-radius: 8px; border: 1px solid #444;">
                                <!-- Header de la lista -->
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #444;">
                                    <div style="flex: 1;">
                                        <h4 style="color: #00d4ff; margin: 0 0 5px 0; font-size: 1.1em;"><%= lista.getNombre() %></h4>
                                        <p style="color: #aaa; margin: 0 0 5px 0; font-size: 0.9em;"><%= lista.getDescripcion() != null && !lista.getDescripcion().isEmpty() ? lista.getDescripcion() : "Sin descripción" %></p>
                                        <span style="color: #888; font-size: 0.85em;">📚 <%= lista.getTotalMangas() %> mangas</span>
                                    </div>
                                    <button class="btn-danger btn-small" 
                                            data-lista-id="<%= lista.getId() %>" 
                                            data-lista-nombre="<%= lista.getNombre() %>"
                                            onclick="eliminarLista(this)">Eliminar Lista</button>
                                </div>

                                <!-- Mangas en la lista -->
                                <%
                                    java.util.List<com.app.model.ListaManga> listaMangas = lista.getListaMangas();
                                    if (listaMangas != null && !listaMangas.isEmpty()) {
                                %>
                                    <div style="display: flex; flex-direction: column; gap: 8px;">
                                        <% for (com.app.model.ListaManga listaManga : listaMangas) {
                                            com.app.model.Manga manga = listaManga.getManga();
                                            if (manga != null) {
                                        %>
                                            <div style="background: #0d0d1a; padding: 10px; border-radius: 5px; border: 1px solid #333; display: flex; justify-content: space-between; align-items: center;">
                                                <div style="flex: 1;">
                                                    <h5 style="color: #fff; margin: 0 0 3px 0; font-size: 1em; font-weight: 500;"><%= manga.getTitulo() %></h5>
                                                    <p style="color: #888; margin: 0; font-size: 0.85em;">
                                                        <%= manga.getDescripcion() != null && !manga.getDescripcion().isEmpty() && manga.getDescripcion().length() > 100
                                                            ? manga.getDescripcion().substring(0, 100) + "..."
                                                            : (manga.getDescripcion() != null ? manga.getDescripcion() : "Sin descripción") %>
                                                    </p>
                                                </div>
                                                <button class="btn-danger btn-small"
                                                        data-lista-id="<%= lista.getId() %>"
                                                        data-manga-id="<%= manga.getId() %>"
                                                        data-manga-titulo="<%= manga.getTitulo() %>"
                                                        onclick="eliminarMangaDeLista(this)"
                                                        style="margin-left: 10px;">
                                                    ✕ Quitar
                                                </button>
                                            </div>
                                        <%
                                            }
                                        } %>
                                    </div>
                                <% } else { %>
                                    <div style="text-align: center; padding: 20px; color: #888; font-style: italic;">
                                        <p>Esta lista está vacía</p>
                                        <p style="font-size: 0.85em; margin-top: 5px;">Agrega mangas desde el catálogo</p>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                <% } else { %>
                    <div class="empty-state">
                        <p>📝 No has creado listas personalizadas</p>
                        <p style="font-size: 12px; margin-top: 10px;">Organiza tus mangas en listas personalizadas</p>
                    </div>
                <% } %>
            </div>
        </div>
    </div>

    <script>
    function showCreateListaForm() {
        document.getElementById('createListaForm').classList.remove('hidden');
        document.getElementById('createListaForm').style.display = 'block';
    }

    function hideCreateListaForm() {
        document.getElementById('createListaForm').classList.add('hidden');
        document.getElementById('createListaForm').style.display = 'none';
        document.getElementById('createListaForm').querySelector('form').reset();
    }

    function eliminarLista(button) {
        var listaId = button.getAttribute('data-lista-id');
        var listaNombre = button.getAttribute('data-lista-nombre');
        
        if (confirm('¿Estás seguro de que quieres eliminar la lista "' + listaNombre + '"? Se eliminarán todos los mangas de esta lista (solo de tu lista, no del catálogo).')) {
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = 'lista';

            var actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'eliminar';

            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'listaId';
            idInput.value = listaId;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    function eliminarMangaDeLista(button) {
        var listaId = button.getAttribute('data-lista-id');
        var mangaId = button.getAttribute('data-manga-id');
        var mangaTitulo = button.getAttribute('data-manga-titulo');
        
        if (confirm('¿Estás seguro de que quieres eliminar "' + mangaTitulo + '" de esta lista? (Solo se eliminará de tu lista, no del catálogo)')) {
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = 'eliminarMangaDeLista';

            var listaIdInput = document.createElement('input');
            listaIdInput.type = 'hidden';
            listaIdInput.name = 'listaId';
            listaIdInput.value = listaId;

            var mangaIdInput = document.createElement('input');
            mangaIdInput.type = 'hidden';
            mangaIdInput.name = 'mangaId';
            mangaIdInput.value = mangaId;

            form.appendChild(listaIdInput);
            form.appendChild(mangaIdInput);
            document.body.appendChild(form);
            form.submit();
        }
    }
    </script>
</body>
</html>