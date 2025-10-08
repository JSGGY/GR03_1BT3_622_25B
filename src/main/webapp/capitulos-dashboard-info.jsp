<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.app.model.Capitulo" %>
<html>
<head>
    <title>Capítulos</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<h2>Capítulos del Manga</h2>
<div class="capitulos-container">
    <%
        List<Capitulo> capitulos = (List<Capitulo>) request.getAttribute("capitulos");
        Integer mangaId = (Integer) request.getAttribute("mangaId");
        String id = (String) request.getAttribute("id"); // scanId viene como String
        
        if (capitulos == null || capitulos.isEmpty()) {
    %>
        <p>No hay capítulos disponibles.</p>
    <%
        } else {
            for (Capitulo capitulo : capitulos) {
    %>
        <div class="capitulo-card">
            <h3><%= capitulo.getTitulo() %> (N°<%= capitulo.getNumero() %>)</h3>
            <p><%= capitulo.getDescripcion() != null ? capitulo.getDescripcion() : "" %></p>
            <a href="seleccionarCapitulo?capituloId=<%= capitulo.getId() %>&mangaId=<%= mangaId %>&scanId=<%= id %>" class="btn-primary btn-small">Ver capítulo</a>
        </div>
    <%
            }
        }
    %>
</div>
<%
    if (id != null && !id.equals("0")) {
        // Check if user is admin (has adminScan in session) or guest
        boolean isAdmin = session.getAttribute("adminScan") != null;
        String backUrl = isAdmin ? "manga?scanId=" + id : "mangaInvitados?scanId=" + id;
%>
<a href="<%= backUrl %>" class="btn-primary btn-small">Volver</a>
<%
    } else {
%>
<a href="javascript:history.back()" class="btn-secondary btn-small">Volver</a>
<%
    }
%>
</body>
</html>

