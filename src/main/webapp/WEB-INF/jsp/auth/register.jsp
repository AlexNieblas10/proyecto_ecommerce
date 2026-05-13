<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear Cuenta — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="../fragments/header.jsp"/>
<main class="main-content auth-page">
    <div class="auth-card" style="max-width:540px">
        <h2>Crear cuenta</h2>
        <p class="subtitle">Es gratis y solo toma un minuto</p>
        <div id="registerAlert" class="alert"></div>
        <form id="registerForm">
            <div class="form-row">
                <div class="form-group">
                    <label for="regName">Nombre completo</label>
                    <input type="text" id="regName" class="form-control"
                        placeholder="Tu nombre" required autocomplete="name">
                </div>
                <div class="form-group">
                    <label for="regPhone">Teléfono</label>
                    <input type="tel" id="regPhone" class="form-control"
                        placeholder="555-0000" autocomplete="tel">
                </div>
            </div>
            <div class="form-group">
                <label for="regEmail">Correo electrónico</label>
                <input type="email" id="regEmail" class="form-control"
                    placeholder="correo@ejemplo.com" required autocomplete="email">
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label for="regPassword">Contraseña</label>
                    <input type="password" id="regPassword" class="form-control"
                        placeholder="Mínimo 6 caracteres" required autocomplete="new-password">
                </div>
                <div class="form-group">
                    <label for="regConfirm">Confirmar contraseña</label>
                    <input type="password" id="regConfirm" class="form-control"
                        placeholder="Repite la contraseña" required autocomplete="new-password">
                </div>
            </div>
            <div class="form-group">
                <label for="regAddress">Dirección</label>
                <textarea id="regAddress" class="form-control" rows="2"
                    placeholder="Calle, número, colonia, ciudad"></textarea>
            </div>
            <button type="submit" class="btn btn-primary btn-full btn-lg">
                Crear Cuenta
            </button>
        </form>
        <div class="auth-link">
            ¿Ya tienes cuenta? <a href="${ctx}/login">Inicia sesión</a>
        </div>
    </div>
</main>
<jsp:include page="../fragments/footer.jsp"/>
<script src="${ctx}/js/auth.js"></script>
</body>
</html>
