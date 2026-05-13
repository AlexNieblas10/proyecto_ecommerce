<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrito — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<main class="main-content cart-section">
    <div class="container">
        <div class="breadcrumb">
            <a href="${ctx}/">Inicio</a> / <a href="${ctx}/tienda">Tienda</a> / <span>Carrito</span>
        </div>
        <h1 class="section-title" style="margin-bottom:1.5rem">Tu Carrito</h1>
        <div class="cart-layout">
            <div id="cartContainer">
                <div class="loading-state"><div class="spinner"></div></div>
            </div>
            <div id="cartSummary"></div>
        </div>
    </div>
</main>
<jsp:include page="fragments/footer.jsp"/>
<script src="${ctx}/js/cart.js"></script>
</body>
</html>
