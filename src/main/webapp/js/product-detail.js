/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
/**
 * Gael
 * xk no querra jalar?
 */

async function loadProduct() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) { 
        window.location.href = BASE + '/tienda'; 
        return; 
    }

    const res = await fetch(BASE + '/api/products/' + id, { credentials: 'include' });
    if (!res.ok) { 
        document.getElementById('productContent').innerHTML = '<p>Producto no encontrado.</p>'; 
        return; 
    }

    const p = await res.json();
    document.title = p.name + ' — FashionHub';
    document.getElementById('breadcrumbName').textContent = p.name;
    
    // Al estar en un archivo .js puro, podemos usar template strings de forma limpia e intuitiva
    document.getElementById('productContent').innerHTML = `
        <div class="product-detail-layout">
            <div class="product-detail-img">
                ${p.imageUrl ? `<img src="${BASE}/${p.imageUrl}" alt="${p.name}">` : '<div class="img-placeholder">👗</div>'}
            </div>
            <div class="product-detail-info">
                <span class="product-category">${p.categoryName || ''}</span>
                <h1>${p.name}</h1>
                <div class="product-detail-price">$${Number(p.price).toFixed(2)}</div>
                <p style="color:var(--color-gray);line-height:1.7">${p.description || ''}</p>
                ${p.specifications ? `<div class="product-specs"><h4>Especificaciones</h4><p>${p.specifications}</p></div>` : ''}
                <div class="product-stock ${p.stock === 0 ? 'out' : p.stock < 5 ? 'low' : ''}">
                    ${p.stock === 0 ? '✗ Agotado' : `✓ ${p.stock} disponibles`}
                </div>
                <div class="qty-control">
                    <button class="qty-btn" onclick="changeQty(-1)">−</button>
                    <span class="qty-value" id="qty">1</span>
                    <button class="qty-btn" onclick="changeQty(1)">+</button>
                </div>
                <button class="btn btn-primary btn-lg" onclick="addToCartDetail(${p.id})" ${p.stock === 0 ? 'disabled' : ''}>
                    Agregar al carrito
                </button>
                <a href="${BASE}/tienda" class="btn btn-outline-dark" style="margin-top:.75rem">Volver a la tienda</a>
            </div>
        </div>
    `;

    // Hacer visible la caja de comentarios y cargar sus datos
    document.getElementById('reviewsContainer').style.display = 'block';
    loadReviews(id);
}

function changeQty(delta) {
    const el = document.getElementById('qty');
    if (!el) return;
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
    
    if (res.status === 401) { 
        window.location.href = BASE + '/login'; 
        return; 
    }
    
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
    const res = await fetch(`${BASE}/api/reviews?productId=${productId}`, { credentials: 'include' });
    if (!res.ok) return;
    
    const reviews = await res.json();
    const list = document.getElementById('reviewsList');
    if (!list) return;
    
    // Leemos la variable global compartida de manera segura
    const currentRole = window.LOGGED_USER_ROLE || "";

    if (reviews.length === 0) {
        list.innerHTML = '<p style="color:var(--color-gray); font-style: italic;">Este producto aún no tiene calificaciones.</p>';
        return;
    }

    list.innerHTML = reviews.map(r => {
        const stars = '⭐'.repeat(r.rating);
        const adminButton = (currentRole === 'admin') 
            ? `<button class="btn btn-danger btn-sm" style="margin-top:0.5rem" onclick="deleteReview(${r.id}, ${productId})">Eliminar comentario inapropiado</button>`
            : '';

        return `
            <div style="padding: 1.2rem 0; border-bottom: 1px solid var(--color-light);">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <strong style="color: var(--color-primary);">${r.userName}</strong>
                    <span style="font-size: 0.85rem;">${stars}</span>
                </div>
                <p style="color: var(--color-gray); margin-top: 0.4rem; font-size: 0.95rem;">${r.comment}</p>
                ${adminButton}
            </div>
        `;
    }).join('');
}

async function submitReview(productId) {
    const rating = parseInt(document.getElementById('reviewRating').value);
    const comment = document.getElementById('reviewComment').value.trim();
    const alertEl = document.getElementById('reviewAlert');

    const res = await fetch(`${BASE}/api/reviews`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ productId, rating, comment })
    });

    const data = await res.json();

    if (res.ok) {
        if (alertEl) {
            alertEl.textContent = '¡Gracias! Tu opinión ha sido guardada.';
            alertEl.className = 'alert alert-success show';
            setTimeout(() => { alertEl.className = 'alert'; }, 3000);
        }
        document.getElementById('reviewComment').value = '';
        loadReviews(productId);
    } else {
        if (alertEl) {
            alertEl.textContent = data.error || 'Error al procesar la reseña.';
            alertEl.className = 'alert alert-error show';
        }
    }
}

async function deleteReview(reviewId, productId) {
    if (!confirm('¿Seguro que deseas eliminar permanentemente esta reseña?')) return;

    const res = await fetch(`${BASE}/api/reviews/${reviewId}`, {
        method: 'DELETE',
        credentials: 'include'
    });

    if (res.ok) {
        loadReviews(productId);
    } else {
        const data = await res.json();
        alert(data.error || 'No se pudo eliminar la reseña.');
    }
}

// Inicialización del DOM
document.addEventListener('DOMContentLoaded', () => {
    loadProduct();
    if (typeof updateCartBadge === 'function') {
        updateCartBadge();
    }

    // Escucha delegada para el envío del formulario
    document.body.addEventListener('submit', function (e) {
        if (e.target && e.target.id === 'reviewForm') {
            e.preventDefault();
            const params = new URLSearchParams(window.location.search);
            const productId = parseInt(params.get('id'));
            if (productId) submitReview(productId);
        }
    });
});

