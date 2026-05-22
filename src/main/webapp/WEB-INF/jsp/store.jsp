<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tienda — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<main class="main-content">
    <div class="container">
        <div class="breadcrumb">
            <a href="${ctx}/">Inicio</a> / <span>Tienda</span>
        </div>
        <div class="search-bar">
            <input type="text" id="searchInput" placeholder="Buscar productos..." autocomplete="off">
            <button class="btn btn-primary" onclick="applyFilters()">Buscar</button>
        </div>
        <div class="store-layout">
            <aside class="filters-sidebar">
                <form id="filterForm">
                    <div class="filter-section">
                        <h4>Categorías</h4>
                        <div id="categoryFilters">
                            <div class="spinner"></div>
                        </div>
                    </div>
                    <div class="filter-section">
                        <h4>Precio</h4>
                        <div class="filter-range">
                            <input type="number" id="minPrice" placeholder="Min" min="0">
                            <span>—</span>
                            <input type="number" id="maxPrice" placeholder="Max" min="0">
                        </div>
                    </div>
                    <button type="button" id="clearFilters" class="btn btn-outline-dark btn-full" style="margin-top:.5rem">Limpiar</button>
                </form>
            </aside>
            <div>
                <div id="productGrid" class="product-grid">
                    <div class="loading-state"><div class="spinner"></div></div>
                </div>
            </div>
        </div>
    </div>
</main>
<jsp:include page="fragments/footer.jsp"/>
<script src="${ctx}/js/store.js"></script>
</body>
</html>
