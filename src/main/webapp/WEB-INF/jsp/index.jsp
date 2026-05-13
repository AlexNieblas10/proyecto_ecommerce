<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FashionHub — Moda Online</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<main class="main-content">
    <section class="hero">
        <h1>Tu estilo,<br><span>tu identidad</span></h1>
        <p>Descubre las últimas tendencias en moda. Ropa, accesorios y calzado de alta calidad.</p>
        <a href="${ctx}/tienda" class="btn btn-primary btn-lg">Explorar tienda</a>
    </section>

    <section class="products-section">
        <div class="container">
            <div class="section-header">
                <div>
                    <h2 class="section-title">Productos destacados</h2>
                    <p class="section-subtitle">Los favoritos de nuestra temporada</p>
                </div>
                <a href="${ctx}/tienda" class="btn btn-outline-dark">Ver todos</a>
            </div>
            <div id="productGrid" class="product-grid">
                <div class="loading-state"><div class="spinner"></div></div>
            </div>
        </div>
    </section>

    <section class="categories-section">
        <div class="container">
            <h2 class="section-title">Categorías</h2>
            <p class="section-subtitle">Encuentra lo que buscas</p>
            <div class="category-grid">
                <a href="${ctx}/tienda?categoryId=1" class="category-card">
                    <div class="category-icon"><i data-lucide="shirt"></i></div>
                    <div class="category-name">Camisetas</div>
                </a>
                <a href="${ctx}/tienda?categoryId=2" class="category-card">
                    <div class="category-icon"><i data-lucide="tag"></i></div>
                    <div class="category-name">Pantalones</div>
                </a>
                <a href="${ctx}/tienda?categoryId=3" class="category-card">
                    <div class="category-icon"><i data-lucide="sparkles"></i></div>
                    <div class="category-name">Vestidos</div>
                </a>
                <a href="${ctx}/tienda?categoryId=4" class="category-card">
                    <div class="category-icon"><i data-lucide="gem"></i></div>
                    <div class="category-name">Accesorios</div>
                </a>
                <a href="${ctx}/tienda?categoryId=5" class="category-card">
                    <div class="category-icon"><i data-lucide="footprints"></i></div>
                    <div class="category-name">Zapatos</div>
                </a>
            </div>
        </div>
    </section>
</main>
<jsp:include page="fragments/footer.jsp"/>
<script src="${ctx}/js/store.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', async () => {
        const res = await fetch('${ctx}/api/products?', { credentials: 'include' });
        if (res.ok) {
            const products = await res.json();
            const featured = products.slice(0, 4);
            renderProducts(featured);
        }
        updateCartBadge();
    });
</script>
</body>
</html>
