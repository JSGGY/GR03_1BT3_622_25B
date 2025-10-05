<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Visor de Imágenes - ${capitulo.titulo}</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<h2>Capítulo: ${capitulo.titulo} (N°${capitulo.numero})</h2>

<c:if test="${empty capitulo.imagenesUrls}">
    <p>No hay imágenes para este capítulo.</p>
</c:if>

<div class="imagen-centro">
    <c:forEach var="url" items="${capitulo.imagenesUrls}">
        <img src="${url}" alt="Página del capítulo">
    </c:forEach>
</div>
<<a href="mostrarCapitulos?mangaId=${mangaId}" class="btn-primary btn-small">Volver</a>
</body>
</html>
