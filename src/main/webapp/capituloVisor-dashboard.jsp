<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.app.model.Capitulo" %>
<html>
<head>
    <%
        Capitulo capitulo = (Capitulo) request.getAttribute("capitulo");
        Integer mangaId = (Integer) request.getAttribute("mangaId");
        String scanIdParam = (String) request.getAttribute("scanId");
        String tituloCapitulo = capitulo != null ? capitulo.getTitulo() : "Capítulo";
        String mangaIdParam = mangaId != null ? mangaId.toString() : "";
        
        // Use scanId from request attribute (safer than entity relationships)
        if (scanIdParam == null) {
            scanIdParam = "";
        }
    %>
    <title>Visor de Imágenes - <%= tituloCapitulo %></title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<%
    if (capitulo != null) {
%>
<h2>Capítulo: <%= capitulo.getTitulo() %> (N°<%= capitulo.getNumero() %>)</h2>

<%
    List<String> imagenesUrls = capitulo.getImagenesUrls();
    if (imagenesUrls == null || imagenesUrls.isEmpty()) {
%>
    <p>No hay imágenes para este capítulo.</p>
<%
    } else {
%>
<div class="imagen-centro">
    <%
        for (String url : imagenesUrls) {
    %>
        <img src="<%= url %>" alt="Página del capítulo">
    <%
        }
    %>
</div>
<%
    }
%>

<% if (mangaId != null) { %>
<a href="mostrarCapitulos?mangaId=<%= mangaIdParam %>&scanId=<%= scanIdParam %>" class="btn-primary btn-small">Volver a Capítulos</a>
<% } else { %>
    <%-- Fallback navigation based on user type --%>
    <% if (session.getAttribute("adminScan") != null) { %>
    <a href="dashboard" class="btn-primary btn-small">Volver al Dashboard</a>
    <% } else { %>
    <a href="ingresoInvitado" class="btn-primary btn-small">Volver a Scans</a>
    <% } %>
<% } %>
<%
    } else {
%>
    <p>Error: No se pudo cargar el capítulo.</p>
    <a href="javascript:history.back()" class="btn-secondary btn-small">Volver</a>
<%
    }
%>
</body>
</html>
