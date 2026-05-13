<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuarios Admin — FashionHub</title>
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
            <a href="${ctx}/admin/users" class="active"><i data-lucide="users" class="nav-icon"></i> Usuarios</a>
            <a href="${ctx}/tienda"><i data-lucide="store" class="nav-icon"></i> Ver tienda</a>
        </nav>
    </aside>
    <div class="admin-content">
        <div class="admin-page-title">Usuarios</div>
        <div class="admin-table-wrapper">
            <div class="admin-table-header">
                <h3>Lista de usuarios</h3>
            </div>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Teléfono</th>
                        <th>Rol</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="usersTableBody">
                    <tr><td colspan="7" class="loading-state"><div class="spinner"></div></td></tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="../fragments/footer.jsp"/>
<script>
async function loadUsers() {
    const res = await fetch(`\${BASE}/api/admin/users`, { credentials: 'include' });
    if (!res.ok) return;
    const users = await res.json();
    document.getElementById('usersTableBody').innerHTML = users.map(u => `
        <tr id="user-row-\${u.id}">
            <td>\${u.id}</td>
            <td>\${u.name}</td>
            <td>\${u.email}</td>
            <td>\${u.phone || '—'}</td>
            <td><span class="badge \${u.role === 'admin' ? 'badge-shipped' : 'badge-delivered'}">\${u.role}</span></td>
            <td class="\${u.active ? 'status-active' : 'status-inactive'}">\${u.active ? 'Activo' : 'Inactivo'}</td>
            <td>
                <button class="btn btn-sm \${u.active ? 'btn-danger' : 'btn-primary'}"
                    onclick="toggleUser(\${u.id}, \${u.active})">
                    \${u.active ? 'Desactivar' : 'Activar'}
                </button>
            </td>
        </tr>
    `).join('');
    if (window.lucide) lucide.createIcons();
}

async function toggleUser(id, currentActive) {
    const res = await fetch(`\${BASE}/api/admin/users/\${id}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ active: !currentActive })
    });
    if (res.ok) loadUsers();
}

document.addEventListener('DOMContentLoaded', loadUsers);
</script>
</body>
</html>
