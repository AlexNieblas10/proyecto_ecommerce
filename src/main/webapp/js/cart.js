async function loadCart() {
    const container = document.getElementById('cartContainer');
    if (!container) return;

    const res = await fetch(`${BASE}/api/cart`, { credentials: 'include' });
    if (res.status === 401) { window.location.href = `${BASE}/login`; return; }
    if (!res.ok) return;

    const cart = await res.json();
    renderCart(cart);
    updateCartBadge(cart.itemCount);
}

function renderCart(cart) {
    const container = document.getElementById('cartContainer');
    const summaryEl = document.getElementById('cartSummary');

    if (!cart.items || cart.items.length === 0) {
        container.innerHTML = `
            <div class="cart-empty">
                <div class="empty-icon">🛒</div>
                <h3>Tu carrito está vacío</h3>
                <p>Agrega productos desde la tienda.</p>
                <a href="${BASE}/tienda" class="btn btn-primary" style="margin-top:1rem">Ir a la tienda</a>
            </div>`;
        if (summaryEl) summaryEl.innerHTML = '';
        return;
    }

    container.innerHTML = `
        <table class="cart-table">
            <thead>
                <tr>
                    <th>Producto</th>
                    <th>Precio</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                ${cart.items.map(item => `
                    <tr id="row-${item.productId}">
                        <td>
                            <div class="cart-product-info">
                                ${item.imageUrl
                                    ? `<img src="${BASE}/${item.imageUrl}" alt="${item.productName}">`
                                    : `<div style="width:64px;height:64px;background:var(--color-light);border-radius:4px;display:flex;align-items:center;justify-content:center;font-size:1.5rem">👗</div>`}
                                <span>${item.productName}</span>
                            </div>
                        </td>
                        <td>$${Number(item.productPrice).toFixed(2)}</td>
                        <td>
                            <input type="number" class="cart-qty-input" min="1" max="99"
                                value="${item.quantity}"
                                onchange="updateQuantity(${item.productId}, this.value)">
                        </td>
                        <td>$${Number(item.subtotal).toFixed(2)}</td>
                        <td>
                            <button class="btn btn-danger btn-sm" onclick="removeItem(${item.productId})">✕</button>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>`;

    if (summaryEl) {
        summaryEl.innerHTML = `
            <div class="cart-summary">
                <h3>Resumen</h3>
                <div class="summary-row"><span>Subtotal</span><span>$${Number(cart.total).toFixed(2)}</span></div>
                <div class="summary-row"><span>Envío</span><span>Gratis</span></div>
                <div class="summary-row summary-total"><span>Total</span><span>$${Number(cart.total).toFixed(2)}</span></div>
                <a href="${BASE}/checkout" class="btn btn-primary btn-full" style="margin-top:1rem">
                    Proceder al pago
                </a>
            </div>`;
    }
}

async function updateQuantity(productId, quantity) {
    quantity = parseInt(quantity);
    if (isNaN(quantity) || quantity < 1) return;
    const res = await fetch(`${BASE}/api/cart`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ productId, quantity })
    });
    if (res.ok) {
        const cart = await res.json();
        renderCart(cart);
        updateCartBadge(cart.itemCount);
    }
}

async function removeItem(productId) {
    const res = await fetch(`${BASE}/api/cart/${productId}`, {
        method: 'DELETE',
        credentials: 'include'
    });
    if (res.ok) {
        const cart = await res.json();
        renderCart(cart);
        updateCartBadge(cart.itemCount);
    }
}

function updateCartBadge(count) {
    document.querySelectorAll('.cart-badge').forEach(el => {
        el.textContent = count || 0;
        el.style.display = count > 0 ? 'flex' : 'none';
    });
}

document.addEventListener('DOMContentLoaded', loadCart);
