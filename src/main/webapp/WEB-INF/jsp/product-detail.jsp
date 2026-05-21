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

        <div id="reviews-section" style="margin-top: 40px; border-top: 1px solid #ccc; padding-top: 20px; display: none;">
            <h2>Reseñas del Producto</h2>
            
            <div id="reviews-list"></div>

            <div style="margin-top: 30px; background-color: #f9f9f9; padding: 20px; border-radius: 8px;">
                <h3>Deja tu opinión</h3>
                <label>Calificación:</label>
                <select id="reviewRating" style="padding: 5px; margin-bottom: 10px;">
                    <option value="5">5 Estrellas ⭐⭐⭐⭐⭐</option>
                    <option value="4">4 Estrellas ⭐⭐⭐⭐</option>
                    <option value="3">3 Estrellas ⭐⭐⭐</option>
                    <option value="2">2 Estrellas ⭐⭐</option>
                    <option value="1">1 Estrella ⭐</option>
                </select>
                <br>
                <textarea id="reviewComment" rows="3" style="width: 100%; padding: 10px; margin-bottom: 10px;" placeholder="Escribe tu comentario aquí..."></textarea>
                <br>
                <button class="btn btn-primary" onclick="submitReview()">Enviar Reseña</button>
            </div>
        </div>

    </div>
</main>
<jsp:include page="fragments/footer.jsp"/>
<script src="${ctx}/js/store.js"></script>
<script>

async function loadProduct() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) { window.location.href = BASE + '/tienda'; return; }

    const res = await fetch(BASE + '/api/products/' + id, { credentials: 'include' });
    if (!res.ok) { document.getElementById('productContent').innerHTML = '<p>Producto no encontrado.</p>'; return; }

    const p = await res.json();
    document.title = p.name + ' — FashionHub';
    document.getElementById('breadcrumbName').textContent = p.name;
    
    
    document.getElementById('productContent').innerHTML =
        '<div class="product-detail-layout">' +
        '<div class="product-detail-img">' +
        (p.imageUrl ? '<img src="' + BASE + '/' + p.imageUrl + '" alt="' + p.name + '">' : '<div class="img-placeholder">👗</div>') +
        '</div>' +
        '<div class="product-detail-info">' +
        '<span class="product-category">' + (p.categoryName || '') + '</span>' +
        '<h1>' + p.name + '</h1>' +
        '<div class="product-detail-price">$' + Number(p.price).toFixed(2) + '</div>' +
        '<p style="color:var(--color-gray);line-height:1.7">' + (p.description || '') + '</p>' +
        (p.specifications ? '<div class="product-specs"><h4>Especificaciones</h4><p>' + p.specifications + '</p></div>' : '') +
        '<div class="product-stock ' + (p.stock === 0 ? 'out' : p.stock < 5 ? 'low' : '') + '">' +
        (p.stock === 0 ? '✗ Agotado' : '✓ ' + p.stock + ' disponibles') +
        '</div>' +
        '<div class="qty-control">' +
        '<button class="qty-btn" onclick="changeQty(-1)">−</button>' +
        '<span class="qty-value" id="qty">1</span>' +
        '<button class="qty-btn" onclick="changeQty(1)">+</button>' +
        '</div>' +
        '<button class="btn btn-primary btn-lg" onclick="addToCartDetail(' + p.id + ')"' + (p.stock === 0 ? ' disabled' : '') + '>Agregar al carrito</button>' +
        '<a href="' + BASE + '/tienda" class="btn btn-outline-dark" style="margin-top:.75rem">Volver a la tienda</a>' +
        '</div>' +
        '</div>';

    
    document.getElementById('reviews-section').style.display = 'block';
    loadReviews(id);
}

function changeQty(delta) {
    const el = document.getElementById('qty');
    const current = parseInt(el.textContent);
    const next = Math.max(1, current + delta);
    el.textContent = next;
}

async function addToCartDetail(productId) {
    const qty = parseInt(document.getElementById('qty')?.textContent || '1');
    const res = await fetch(BASE + '/api/cart', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ productId: productId, quantity: qty })
    });
    if (res.status === 401) { window.location.href = BASE + '/login'; return; }
    if (res.ok) {
        const cart = await res.json();
        updateCartBadge(cart.itemCount);
        const btn = document.querySelector('.product-detail-info .btn-primary');
        if (btn) {
            btn.textContent = '✓ Agregado';
            setTimeout(() => { btn.textContent = 'Agregar al carrito'; }, 2000);
        }
    }
}


async function loadReviews(productId) {
    const res = await fetch(BASE + '/api/reviews?productId=' + productId, { credentials: 'include' });
    if (!res.ok) return;

    const reviews = await res.json();
    let htmlContent = ''; 

    if (reviews.length === 0) {
        htmlContent = '<p style="color: gray;">Aún no hay reseñas para este producto.</p>';
    } else {
        
        for (let i = 0; i < reviews.length; i++) {
            let r = reviews[i];
            
           
            let stars = '';
            for(let s = 0; s < r.rating; s++) {
                stars += '⭐';
            }

           
            htmlContent += '<div style="border-bottom: 1px solid #ddd; padding: 15px 0;">';
            htmlContent += '  <strong>' + r.userName + '</strong>';
            htmlContent += '  <span style="margin-left: 10px;">' + stars + '</span>';
            htmlContent += '  <p style="margin-top: 5px;">' + r.comment + '</p>';
            
            //boton para borrar admin
            if ('${loggedUserRole}' === 'admin') {
                htmlContent += '  <button style="background: red; color: white; border: none; padding: 5px 10px; cursor: pointer;" ';
                htmlContent += 'onclick="deleteReview(' + r.id + ', ' + productId + ')">Borrar reseña</button>';
            }
            
            htmlContent += '</div>';
        }
    }
    document.getElementById('reviews-list').innerHTML = htmlContent;
}

async function submitReview() {
    
    const params = new URLSearchParams(window.location.search);
    const productId = params.get('id');
    
  
    const ratingValue = document.getElementById('reviewRating').value;
    const commentValue = document.getElementById('reviewComment').value;

    const res = await fetch(BASE + '/api/reviews', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ 
            productId: parseInt(productId), 
            rating: parseInt(ratingValue), 
            comment: commentValue 
        })
    });

    
    if (res.status === 401) { 
        alert('Debes iniciar sesión para dejar una reseña');
        window.location.href = BASE + '/login'; 
        return; 
    }

    if (res.ok) {
        alert('¡Reseña guardada con éxito!');
        document.getElementById('reviewComment').value = ''; 
        loadReviews(productId); 
    } else {
        alert('Hubo un error al guardar la reseña');
    }
}

async function deleteReview(reviewId, productId) {
    if (!confirm('¿Estás seguro de que deseas borrar esta reseña?')) {
        return;
    }

    const res = await fetch(BASE + '/api/reviews/' + reviewId, {
        method: 'DELETE',
        credentials: 'include'
    });

    if (res.ok) {
        alert('Reseña borrada');
        loadReviews(productId); 
    } else {
        alert('Error al intentar borrar');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadProduct();
    if (typeof updateCartBadge === 'function') updateCartBadge();
});
</script>
</body>
</html>
