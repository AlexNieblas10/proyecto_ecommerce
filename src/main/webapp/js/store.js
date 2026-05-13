const BASE = window.location.pathname.split('/').slice(0,2).join('/');

let allProducts = [];
let debounceTimer;

async function loadCategories() {
    const res = await fetch(`${BASE}/api/categories`, { credentials: 'include' });
    if (!res.ok) return;
    const cats = await res.json();
    const container = document.getElementById('categoryFilters');
    if (!container) return;
    container.innerHTML = cats.map(c => `
        <label>
            <input type="checkbox" name="category" value="${c.id}"> ${c.name}
        </label>
    `).join('');
    container.querySelectorAll('input').forEach(cb => cb.addEventListener('change', applyFilters));
}

async function loadProducts(filters = {}) {
    const grid = document.getElementById('productGrid');
    if (!grid) return;
    grid.innerHTML = '<div class="loading-state"><div class="spinner"></div></div>';

    const params = new URLSearchParams();
    if (filters.name)       params.set('name', filters.name);
    if (filters.categoryId) params.set('categoryId', filters.categoryId);
    if (filters.minPrice)   params.set('minPrice', filters.minPrice);
    if (filters.maxPrice)   params.set('maxPrice', filters.maxPrice);

    const res = await fetch(`${BASE}/api/products?${params}`, { credentials: 'include' });
    if (!res.ok) { grid.innerHTML = '<p>Error al cargar productos.</p>'; return; }

    allProducts = await res.json();
    renderProducts(allProducts);
}

function renderProducts(products) {
    const grid = document.getElementById('productGrid');
    if (!grid) return;
    if (products.length === 0) {
        grid.innerHTML = '<p style="color:var(--color-gray);grid-column:1/-1">No se encontraron productos.</p>';
        return;
    }
    grid.innerHTML = products.map(p => `
        <div class="product-card">
            <a href="${BASE}/tienda/producto?id=${p.id}">
                ${p.imageUrl
                    ? `<img src="${BASE}/${p.imageUrl}" alt="${p.name}" loading="lazy">`
                    : `<div class="product-img-placeholder">👗</div>`}
            </a>
            <div class="product-info">
                <span class="product-category">${p.categoryName || ''}</span>
                <a href="${BASE}/tienda/producto?id=${p.id}" class="product-name">${p.name}</a>
                <div class="product-price">$${Number(p.price).toFixed(2)}</div>
                <div class="product-stock ${p.stock === 0 ? 'out' : p.stock < 5 ? 'low' : ''}">
                    ${p.stock === 0 ? 'Agotado' : p.stock < 5 ? `Solo ${p.stock} disponibles` : 'En stock'}
                </div>
                <button class="btn btn-primary btn-full" onclick="addToCart(${p.id})"
                    ${p.stock === 0 ? 'disabled' : ''}>
                    Agregar al carrito
                </button>
            </div>
        </div>
    `).join('');
}

async function addToCart(productId) {
    const res = await fetch(`${BASE}/api/cart`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ productId, quantity: 1 })
    });
    if (res.status === 401) {
        window.location.href = `${BASE}/login`;
        return;
    }
    if (res.ok) {
        const cart = await res.json();
        updateCartBadge(cart.itemCount);
        showToast('Producto agregado al carrito');
    } else {
        const err = await res.json();
        showToast(err.error || 'Error al agregar al carrito', 'error');
    }
}

async function updateCartBadge(count) {
    if (count === undefined) {
        const res = await fetch(`${BASE}/api/cart`, { credentials: 'include' });
        if (res.ok) {
            const cart = await res.json();
            count = cart.itemCount;
        }
    }
    document.querySelectorAll('.cart-badge').forEach(el => {
        el.textContent = count || 0;
        el.style.display = count > 0 ? 'flex' : 'none';
    });
}

function showToast(msg, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'error' ? 'error' : 'success'} show`;
    toast.style.cssText = 'position:fixed;bottom:1.5rem;right:1.5rem;z-index:300;min-width:220px';
    toast.textContent = msg;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

function applyFilters() {
    const nameInput = document.getElementById('searchInput');
    const minPrice  = document.getElementById('minPrice');
    const maxPrice  = document.getElementById('maxPrice');
    const checked   = [...document.querySelectorAll('input[name="category"]:checked')];

    const filters = {};
    if (nameInput?.value.trim()) filters.name = nameInput.value.trim();
    if (minPrice?.value) filters.minPrice = minPrice.value;
    if (maxPrice?.value) filters.maxPrice = maxPrice.value;
    if (checked.length === 1) filters.categoryId = checked[0].value;

    loadProducts(filters);
}

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadProducts();
    updateCartBadge();

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(applyFilters, 300);
        });
    }

    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        filterForm.addEventListener('submit', (e) => { e.preventDefault(); applyFilters(); });
    }

    const clearBtn = document.getElementById('clearFilters');
    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            if (searchInput) searchInput.value = '';
            document.querySelectorAll('input[name="category"]').forEach(cb => cb.checked = false);
            const minP = document.getElementById('minPrice');
            const maxP = document.getElementById('maxPrice');
            if (minP) minP.value = '';
            if (maxP) maxP.value = '';
            loadProducts();
        });
    }
});
