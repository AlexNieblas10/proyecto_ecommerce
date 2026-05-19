<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Productos Admin — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
</head>
<body>
<jsp:include page="../fragments/header.jsp"/>
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="admin-sidebar-title">Panel Admin</div>
        <nav class="admin-nav">
            <a href="${ctx}/admin"><i data-lucide="bar-chart-2" class="nav-icon"></i> Dashboard</a>
            <a href="${ctx}/admin/products" class="active"><i data-lucide="shirt" class="nav-icon"></i> Productos</a>
            <a href="${ctx}/admin/users"><i data-lucide="users" class="nav-icon"></i> Usuarios</a>
            <a href="${ctx}/admin/categories"><i data-lucide="tag" class="nav-icon"></i> Categorías</a>
            <a href="${ctx}/tienda"><i data-lucide="store" class="nav-icon"></i> Ver tienda</a>
        </nav>
    </aside>
    <div class="admin-content">
        <div class="admin-page-title">
            Productos
            <button class="btn btn-primary btn-sm" onclick="openProductModal()">+ Nuevo producto</button>
        </div>
        <div class="admin-table-wrapper">
            <div class="admin-table-header">
                <h3>Lista de productos</h3>
            </div>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Imagen</th>
                        <th>Nombre</th>
                        <th>Categoría</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="productsTableBody">
                    <tr><td colspan="7" class="loading-state"><div class="spinner"></div></td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal-overlay" id="productModal">
    <div class="modal">
        <h3 id="modalTitle">Nuevo producto</h3>
        <div id="productAlert" class="alert"></div>
        <form id="productForm">
            <input type="hidden" id="productId">
            <div class="admin-form-grid">
                <div class="form-group">
                    <label for="pName">Nombre</label>
                    <input type="text" id="pName" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="pCategory">Categoría</label>
                    <select id="pCategory" class="form-control" required>
                        <option value="">Seleccionar...</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="pPrice">Precio</label>
                    <input type="number" id="pPrice" class="form-control" step="0.01" min="0" required>
                </div>
                <div class="form-group">
                    <label for="pStock">Stock</label>
                    <input type="number" id="pStock" class="form-control" min="0" required>
                </div>
                <div class="form-group">
                    <label for="pImageUrl">URL de imagen</label>
                    <input type="text" id="pImageUrl" class="form-control" placeholder="images/products/foto.jpg">
                </div>
                <div class="form-group">
                    <label for="pSpecs">Especificaciones</label>
                    <input type="text" id="pSpecs" class="form-control" placeholder="Tallas, material...">
                </div>
                <div class="form-group col-span-2">
                    <label for="pDescription">Descripción</label>
                    <textarea id="pDescription" class="form-control" rows="2"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-dark" onclick="closeProductModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../fragments/footer.jsp"/>
<script>
function thumbError(img) {
    img.outerHTML = '<div class="product-thumb-placeholder"><i data-lucide="shirt"></i></div>';
    if (window.lucide) lucide.createIcons();
}

async function loadProducts() {
    const res = await fetch(`\${BASE}/api/products`, { credentials: 'include' });
    if (!res.ok) return;
    const products = await res.json();
    const tbody = document.getElementById('productsTableBody');
    tbody.innerHTML = products.map(p => `
        <tr>
            <td>\${p.imageUrl ? '<img src="'+BASE+'/'+p.imageUrl+'" class="product-thumb" alt="" onerror="thumbError(this)">' : '<div class="product-thumb-placeholder"><i data-lucide=\'shirt\'></i></div>'}</td>
            <td>\${p.name}</td>
            <td>\${p.categoryName || '—'}</td>
            <td>$\${Number(p.price).toFixed(2)}</td>
            <td>\${p.stock}</td>
            <td class="\${p.active ? 'status-active' : 'status-inactive'}">\${p.active ? 'Activo' : 'Inactivo'}</td>
            <td class="actions">
                <button class="btn btn-outline-dark btn-sm" onclick="editProduct(\${JSON.stringify(p).replace(/"/g,'&quot;')})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="deleteProduct(\${p.id}, this)">\${p.active ? 'Desactivar' : 'Activar'}</button>
            </td>
        </tr>
    `).join('');
    if (window.lucide) lucide.createIcons();
}

async function loadCategories() {
    const res = await fetch(`\${BASE}/api/categories`, { credentials: 'include' });
    if (!res.ok) return;
    const cats = await res.json();
    const sel = document.getElementById('pCategory');
    cats.forEach(c => sel.innerHTML += `<option value="\${c.id}">\${c.name}</option>`);
}

function openProductModal(p = null) {
    document.getElementById('productModal').classList.add('open');
    document.getElementById('modalTitle').textContent = p ? 'Editar producto' : 'Nuevo producto';
    document.getElementById('productId').value      = p?.id || '';
    document.getElementById('pName').value          = p?.name || '';
    document.getElementById('pPrice').value         = p?.price || '';
    document.getElementById('pStock').value         = p?.stock || '';
    document.getElementById('pImageUrl').value      = p?.imageUrl || '';
    document.getElementById('pSpecs').value         = p?.specifications || '';
    document.getElementById('pDescription').value   = p?.description || '';
    document.getElementById('pCategory').value      = p?.categoryId || '';
    document.getElementById('productAlert').className = 'alert';
}

function closeProductModal() {
    document.getElementById('productModal').classList.remove('open');
    document.getElementById('productForm').reset();
}

function editProduct(p) { openProductModal(p); }

async function deleteProduct(id, btn) {
    const res = await fetch(`\${BASE}/api/products/\${id}`, {
        method: 'DELETE', credentials: 'include'
    });
    if (res.ok) loadProducts();
}

document.getElementById('productForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById('productAlert');
    const id = document.getElementById('productId').value;
    const body = {
        name:           document.getElementById('pName').value,
        description:    document.getElementById('pDescription').value,
        price:          parseFloat(document.getElementById('pPrice').value),
        stock:          parseInt(document.getElementById('pStock').value),
        imageUrl:       document.getElementById('pImageUrl').value,
        categoryId:     parseInt(document.getElementById('pCategory').value),
        specifications: document.getElementById('pSpecs').value
    };
    const url    = id ? `\${BASE}/api/products/\${id}` : `\${BASE}/api/products`;
    const method = id ? 'PUT' : 'POST';
    const res = await fetch(url, {
        method, credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const data = await res.json();
    if (res.ok) {
        closeProductModal();
        loadProducts();
    } else {
        alertEl.textContent = data.error || 'Error al guardar';
        alertEl.className = 'alert alert-error show';
    }
});

document.getElementById('productModal').addEventListener('click', (e) => {
    if (e.target === document.getElementById('productModal')) closeProductModal();
});

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadProducts();
});
</script>
</body>
</html>
