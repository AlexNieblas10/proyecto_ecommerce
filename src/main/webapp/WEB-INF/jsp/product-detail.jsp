<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Producto — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<main class="main-content product-detail">
    <div class="container">
        <div class="breadcrumb">
            <a href="${ctx}/">Inicio</a> / <a href="${ctx}/tienda">Tienda</a> / <span id="breadcrumbName">Producto</span>
        </div>
        <div id="productContent">
            <div class="loading-state"><div class="spinner"></div></div>
        </div>
    </div>
</main>
<jsp:include page="fragments/footer.jsp"/>
<script src="${ctx}/js/store.js"></script>
<script>
const BASE = window.location.pathname.split('/').slice(0,2).join('/');

async function loadProduct() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) { window.location.href = `${BASE}/tienda`; return; }

    const res = await fetch(`${BASE}/api/products/${id}`, { credentials: 'include' });
    if (!res.ok) { document.getElementById('productContent').innerHTML = '<p>Producto no encontrado.</p>'; return; }

    const p = await res.json();
    document.title = `${p.name} — FashionHub`;
    document.getElementById('breadcrumbName').textContent = p.name;

    document.getElementById('productContent').innerHTML = `
        <div class="product-detail-layout">
            <div class="product-detail-img">
                ${p.imageUrl
                    ? `<img src="${BASE}/${p.imageUrl}" alt="${p.name}">`
                    : `<div class="img-placeholder">👗</div>`}
            </div>
            <div class="product-detail-info">
                <span class="product-category">${p.categoryName || ''}</span>
                <h1>${p.name}</h1>
                <div class="product-detail-price">$${Number(p.price).toFixed(2)}</div>
                <p style="color:var(--color-gray);line-height:1.7">${p.description || ''}</p>
                ${p.specifications ? `
                <div class="product-specs">
                    <h4>Especificaciones</h4>
                    <p>${p.specifications}</p>
                </div>` : ''}
                <div class="product-stock ${p.stock === 0 ? 'out' : p.stock < 5 ? 'low' : ''}">
                    ${p.stock === 0 ? '✗ Agotado' : `✓ ${p.stock} disponibles`}
                </div>
                <div class="qty-control">
                    <button class="qty-btn" onclick="changeQty(-1)">−</button>
                    <span class="qty-value" id="qty">1</span>
                    <button class="qty-btn" onclick="changeQty(1)">+</button>
                </div>
                <button class="btn btn-primary btn-lg" onclick="addToCartDetail(${p.id})"
                    ${p.stock === 0 ? 'disabled' : ''}>
                    Agregar al carrito
                </button>
                <a href="${BASE}/tienda" class="btn btn-outline-dark" style="margin-top:.75rem">Volver a la tienda</a>
            </div>
        </div>`;
}

function changeQty(delta) {
    const el = document.getElementById('qty');
    const current = parseInt(el.textContent);
    const next = Math.max(1, current + delta);
    el.textContent = next;
}

async function addToCartDetail(productId) {
    const qty = parseInt(document.getElementById('qty')?.textContent || '1');
    const res = await fetch(`${BASE}/api/cart`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ productId, quantity: qty })
    });
    if (res.status === 401) { window.location.href = `${BASE}/login`; return; }
    if (res.ok) {
        const cart = await res.json();
        updateCartBadge(cart.itemCount);
        const btn = document.querySelector('.product-detail-info .btn-primary');
        if (btn) { btn.textContent = '✓ Agregado'; setTimeout(() => { btn.textContent = 'Agregar al carrito'; }, 2000); }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadProduct();
    updateCartBadge();
});
</script>
</body>
</html>
