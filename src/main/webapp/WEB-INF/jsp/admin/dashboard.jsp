<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin — FashionHub</title>
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
            <a href="${ctx}/admin" class="active"><i data-lucide="bar-chart-2" class="nav-icon"></i> Dashboard</a>
            <a href="${ctx}/admin/products"><i data-lucide="shirt" class="nav-icon"></i> Productos</a>
            <a href="${ctx}/admin/users"><i data-lucide="users" class="nav-icon"></i> Usuarios</a>
            <a href="${ctx}/tienda"><i data-lucide="store" class="nav-icon"></i> Ver tienda</a>
        </nav>
    </aside>
    <div class="admin-content">
        <div class="admin-page-title">Dashboard</div>
        <div class="stats-grid" id="statsGrid">
            <div class="stat-card">
                <div class="stat-label">Productos</div>
                <div class="stat-value" id="statProducts">—</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Usuarios</div>
                <div class="stat-value" id="statUsers">—</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Categorías</div>
                <div class="stat-value" id="statCategories">—</div>
            </div>
        </div>
        <div style="display:flex;gap:1rem;flex-wrap:wrap">
            <a href="${ctx}/admin/products" class="btn btn-primary">Gestionar productos</a>
            <a href="${ctx}/admin/users" class="btn btn-outline-dark">Gestionar usuarios</a>
        </div>
    </div>
</div>
<jsp:include page="../fragments/footer.jsp"/>
<script>
async function loadStats() {
    const [productsRes, usersRes, catsRes] = await Promise.all([
        fetch(`\${BASE}/api/products`, { credentials: 'include' }),
        fetch(`\${BASE}/api/admin/users`, { credentials: 'include' }),
        fetch(`\${BASE}/api/categories`, { credentials: 'include' })
    ]);
    if (productsRes.ok) document.getElementById('statProducts').textContent = (await productsRes.json()).length;
    if (usersRes.ok)    document.getElementById('statUsers').textContent    = (await usersRes.json()).length;
    if (catsRes.ok)     document.getElementById('statCategories').textContent = (await catsRes.json()).length;
}
document.addEventListener('DOMContentLoaded', loadStats);
</script>
</body>
</html>
