<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="site-header">
    <div class="header-inner">
        <a href="${ctx}/" class="logo">Fashion<span>Hub</span></a>
        <nav class="nav-links">
            <a href="${ctx}/tienda">Tienda</a>
            <a href="${ctx}/carrito" class="hide-mobile">Carrito</a>
            <c:if test="${loggedUserRole == 'admin'}">
                <a href="${ctx}/admin">Admin</a>
            </c:if>
        </nav>
        <div class="header-actions">
            <a href="${ctx}/carrito" class="cart-btn">
                🛒 <span class="cart-badge" style="display:none">0</span>
            </a>
            <c:choose>
                <c:when test="${not empty loggedUserRole}">
                    <div class="user-menu">
                        <span class="user-greeting hide-mobile">${loggedUserEmail}</span>
                        <a href="${ctx}/perfil" class="btn btn-outline btn-sm">Mi cuenta</a>
                        <button class="btn btn-outline btn-sm logout-btn">Salir</button>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/login" class="btn btn-outline btn-sm">Iniciar sesión</a>
                    <a href="${ctx}/registro" class="btn btn-primary btn-sm hide-mobile">Registrarse</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>
