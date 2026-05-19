<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Categorías Admin — FashionHub</title>
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
            <a href="${ctx}/admin/products"><i data-lucide="shirt" class="nav-icon"></i> Productos</a>
            <a href="${ctx}/admin/users"><i data-lucide="users" class="nav-icon"></i> Usuarios</a>
            <a href="${ctx}/admin/categories" class="active"><i data-lucide="tag" class="nav-icon"></i> Categorías</a>
            <a href="${ctx}/tienda"><i data-lucide="store" class="nav-icon"></i> Ver tienda</a>
        </nav>
    </aside>
    <div class="admin-content">
        <div class="admin-page-title">
            Categorías
            <button class="btn btn-primary btn-sm" onclick="openCategoryModal()">+ Nueva categoría</button>
        </div>
        <div class="admin-table-wrapper">
            <div class="admin-table-header">
                <h3>Lista de categorías</h3>
            </div>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="categoriesTableBody">
                    <tr><td colspan="4" class="loading-state"><div class="spinner"></div></td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal-overlay" id="categoryModal">
    <div class="modal">
        <h3 id="modalTitle">Nueva categoría</h3>
        <div id="categoryAlert" class="alert"></div>
        <form id="categoryForm">
            <input type="hidden" id="categoryId">
            <div class="form-group">
                <label for="cName">Nombre</label>
                <input type="text" id="cName" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="cDescription">Descripción</label>
                <textarea id="cDescription" class="form-control" rows="3"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-dark" onclick="closeCategoryModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../fragments/footer.jsp"/>
<script>
var _cats = [];

async function loadCategories() {
    const tbody = document.getElementById('categoriesTableBody');
    try {
        const res = await fetch(`\${BASE}/api/categories`, { credentials: 'include' });
        if (!res.ok) { tbody.innerHTML = '<tr><td colspan="4" class="loading-state">Error al cargar categorías</td></tr>'; return; }
        _cats = await res.json();
    } catch (e) {
        console.error('loadCategories:', e);
        tbody.innerHTML = '<tr><td colspan="4" class="loading-state">Error de conexión</td></tr>';
        return;
    }
    if (_cats.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:var(--color-gray);padding:1.5rem">No hay categorías registradas</td></tr>';
        return;
    }
    tbody.innerHTML = _cats.map(c =>
        '<tr id="cat-row-' + c.id + '">' +
        '<td>' + c.id + '</td>' +
        '<td><strong>' + c.name + '</strong></td>' +
        '<td>' + (c.description || '<span style="color:var(--color-gray)">—</span>') + '</td>' +
        '<td class="actions">' +
            '<button class="btn btn-outline-dark btn-sm" onclick="editCategory(' + c.id + ')">Editar</button> ' +
            '<button class="btn btn-danger btn-sm" onclick="deleteCategory(' + c.id + ')">Eliminar</button>' +
        '</td>' +
        '</tr>'
    ).join('');
}

function openCategoryModal(c) {
    document.getElementById('categoryModal').classList.add('open');
    document.getElementById('modalTitle').textContent = c ? 'Editar categoría' : 'Nueva categoría';
    document.getElementById('categoryId').value   = c ? c.id : '';
    document.getElementById('cName').value        = c ? c.name : '';
    document.getElementById('cDescription').value = c ? (c.description || '') : '';
    document.getElementById('categoryAlert').className = 'alert';
}

function closeCategoryModal() {
    document.getElementById('categoryModal').classList.remove('open');
    document.getElementById('categoryForm').reset();
}

function editCategory(id) {
    var c = _cats.find(function(x) { return x.id === id; });
    if (c) openCategoryModal(c);
}

async function deleteCategory(id) {
    var c = _cats.find(function(x) { return x.id === id; });
    var name = c ? c.name : id;
    if (!confirm('¿Eliminar la categoría "' + name + '"?\nLos productos asociados perderán su categoría.')) return;
    const res = await fetch(`\${BASE}/api/categories/\${id}`, {
        method: 'DELETE',
        credentials: 'include'
    });
    if (res.ok) {
        loadCategories();
    } else {
        const data = await res.json();
        alert(data.error || 'Error al eliminar la categoría');
    }
}

document.getElementById('categoryForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const alertEl = document.getElementById('categoryAlert');
    const id   = document.getElementById('categoryId').value;
    const body = {
        name:        document.getElementById('cName').value.trim(),
        description: document.getElementById('cDescription').value.trim()
    };
    const url    = id ? `\${BASE}/api/categories/\${id}` : `\${BASE}/api/categories`;
    const method = id ? 'PUT' : 'POST';
    const res = await fetch(url, {
        method: method,
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (res.ok) {
        closeCategoryModal();
        loadCategories();
    } else {
        const data = await res.json();
        alertEl.textContent = data.error || 'Error al guardar';
        alertEl.className = 'alert alert-error show';
    }
});

document.getElementById('categoryModal').addEventListener('click', function(e) {
    if (e.target === document.getElementById('categoryModal')) closeCategoryModal();
});

document.addEventListener('DOMContentLoaded', loadCategories);
</script>
</body>
</html>
