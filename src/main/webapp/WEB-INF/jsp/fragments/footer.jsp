<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<footer class="site-footer">
    <div class="footer-grid">
        <div class="footer-brand">
            <div class="logo">Fashion<span>Hub</span></div>
            <p>Tu destino de moda online. Calidad, estilo y tendencias al mejor precio.</p>
        </div>
        <div class="footer-col">
            <h4>Tienda</h4>
            <ul>
                <li><a href="${ctx}/tienda">Todos los productos</a></li>
                <li><a href="${ctx}/tienda?categoryId=1">Camisetas</a></li>
                <li><a href="${ctx}/tienda?categoryId=2">Pantalones</a></li>
                <li><a href="${ctx}/tienda?categoryId=3">Vestidos</a></li>
            </ul>
        </div>
        <div class="footer-col">
            <h4>Mi cuenta</h4>
            <ul>
                <li><a href="${ctx}/login">Iniciar sesión</a></li>
                <li><a href="${ctx}/registro">Registrarse</a></li>
                <li><a href="${ctx}/perfil">Mi perfil</a></li>
                <li><a href="${ctx}/carrito">Carrito</a></li>
            </ul>
        </div>
    </div>
    <div class="footer-bottom">
        &copy; 2026 FashionHub. Todos los derechos reservados.
    </div>
</footer>
<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>
<script>document.addEventListener('DOMContentLoaded', function(){ if(window.lucide) lucide.createIcons(); });</script>
<script src="${ctx}/js/auth.js?v=2"></script>
