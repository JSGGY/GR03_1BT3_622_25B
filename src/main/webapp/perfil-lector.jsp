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
                <div class="favoritos-container">
                    <!-- Si no hay favoritos -->
                    <c:if test="${empty favoritos}">
                        <div class="empty-state">
                            <p>📚 Aún no tienes mangas en tus favoritos</p>
                            <p style="font-size: 12px; margin-top: 10px;">Explora el catálogo y agrega tus favoritos</p>
                        </div>
                    </c:if>

                    <!-- Si hay mangas favoritos -->
                    <c:forEach var="manga" items="${favoritos}">
                        <div class="manga-card">
                            <!-- Imagen desde el blob codificado en Base64 -->
                            <c:if test="${not empty manga.portadaBlob}">
                                <img src="data:${manga.portadaTipo};base64,${fn:escapeXml(Base64.getEncoder().encodeToString(manga.portadaBlob))}"
                                     alt="${manga.titulo}" class="manga-img" />
                            </c:if>
                            <c:if test="${empty manga.portadaBlob}">
                                <img src="resources/img/default-cover.jpg" alt="Sin portada" class="manga-img" />
                            </c:if>

                            <div class="manga-info">
                                <h4>${manga.titulo}</h4>
                                <p style="font-size: 13px;">Capítulos: ${manga.totalCapitulos}</p>
                                <p style="font-size: 12px; color: #555;">${manga.descripcion}</p>

                                <!-- Formulario para eliminar de favoritos -->
                                <form action="favoritos" method="post">
                                    <input type="hidden" name="action" value="eliminar">
                                    <input type="hidden" name="mangaId" value="${manga.id}">
                                    <button type="submit" class="btn-danger btn-small">
                                        🗑️ Eliminar de Favoritos
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>

            </div>

            <!-- Sección Listas -->
            <div class="section-card">
                <h3>📋 Mis Listas</h3>
                <div class="empty-state">
                    <p>📝 No has creado listas personalizadas</p>
                    <p style="font-size: 12px; margin-top: 10px;">Organiza tus mangas en listas personalizadas</p>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
