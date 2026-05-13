<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="../fragments/header.jsp"/>
<main class="main-content auth-page">
    <div class="auth-card">
        <h2>Bienvenido</h2>
        <p class="subtitle">Inicia sesión para continuar</p>
        <div id="loginAlert" class="alert"></div>
        <form id="loginForm">
            <div class="form-group">
                <label for="loginEmail">Correo electrónico</label>
                <input type="email" id="loginEmail" class="form-control"
                    placeholder="correo@ejemplo.com" required autocomplete="email">
            </div>
            <div class="form-group">
                <label for="loginPassword">Contraseña</label>
                <input type="password" id="loginPassword" class="form-control"
                    placeholder="••••••••" required autocomplete="current-password">
            </div>
            <button type="submit" class="btn btn-primary btn-full btn-lg">
                Iniciar Sesión
            </button>
        </form>
        <div class="auth-link">
            ¿No tienes cuenta? <a href="${ctx}/registro">Regístrate gratis</a>
        </div>
    </div>
</main>
<jsp:include page="../fragments/footer.jsp"/>
<script src="${ctx}/js/auth.js"></script>
</body>
</html>
