<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error del servidor — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="../fragments/header.jsp"/>
<main class="main-content" style="display:flex;align-items:center;justify-content:center;padding:4rem">
    <div style="text-align:center">
        <div style="font-size:5rem">⚙️</div>
        <h1 class="section-title" style="margin-top:1rem">Error del servidor</h1>
        <p style="color:var(--color-gray);margin:1rem 0">Ocurrió un problema. Intenta de nuevo más tarde.</p>
        <a href="${ctx}/" class="btn btn-primary">Volver al inicio</a>
    </div>
</main>
<jsp:include page="../fragments/footer.jsp"/>
</body>
</html>
