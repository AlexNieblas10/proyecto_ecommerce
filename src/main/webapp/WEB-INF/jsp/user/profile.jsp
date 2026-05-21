<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="../fragments/header.jsp"/>
<main class="main-content">
    <div class="container">
        <div class="profile-layout">
            <aside class="profile-sidebar">
                <div class="profile-avatar" id="avatarInitial">?</div>
                <div class="profile-name" id="sidebarName">Cargando...</div>
                <div class="profile-email" id="sidebarEmail"></div>
                <nav class="profile-nav">
                    <a href="#info" class="active">Mis datos</a>
                    <a href="#password">Cambiar contraseña</a>
                    <a href="#orders">Mis pedidos</a>
                </nav>
            </aside>
            <div>
                <div class="profile-card" id="info">
                    <h3>Información personal</h3>
                    <div id="profileAlert" class="alert"></div>
                    <form id="profileForm">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="pName">Nombre</label>
                                <input type="text" id="pName" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label for="pPhone">Teléfono</label>
                                <input type="tel" id="pPhone" class="form-control">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pEmail">Correo electrónico</label>
                            <input type="email" id="pEmail" class="form-control" disabled>
                        </div>
                        <div class="form-group">
                            <label for="pAddress">Dirección</label>
                            <textarea id="pAddress" class="form-control" rows="2"></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Guardar cambios</button>
                    </form>
                </div>

                <div class="profile-card" id="password">
                    <h3>Cambiar contraseña</h3>
                    <div id="passwordAlert" class="alert"></div>
                    <form id="passwordForm">
                        <div class="form-group">
                            <label for="currentPass">Contraseña actual</label>
                            <input type="password" id="currentPass" class="form-control" required>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="newPass">Nueva contraseña</label>
                                <input type="password" id="newPass" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label for="confirmPass">Confirmar nueva contraseña</label>
                                <input type="password" id="confirmPass" class="form-control" required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">Cambiar contraseña</button>
                    </form>
                </div>

                <div class="profile-card" id="orders">
                    <h3>Mis pedidos</h3>
                    <div id="ordersContainer">
                        <div class="loading-state"><div class="spinner"></div></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
<jsp:include page="../fragments/footer.jsp"/>

<script>
    window.BASE_URL = '${ctx}';
</script>

<script src="${ctx}/js/profile.js"></script>
<script src="${ctx}/js/store.js"></script>
</body>
</html>