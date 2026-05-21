<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Pedidos — Admin FashionHub</title>
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
            <a href="${ctx}/admin/categories"><i data-lucide="tag" class="nav-icon"></i> Categorías</a>
            <a href="${ctx}/admin/orders" class="active"><i data-lucide="package" class="nav-icon"></i> Pedidos</a>
            <a href="${ctx}/tienda"><i data-lucide="store" class="nav-icon"></i> Ver tienda</a>
        </nav>
    </aside>

    <div class="admin-content">
        <div class="admin-page-title">Gestión de Pedidos</div>
        <div class="admin-card">
            <div id="adminOrdersAlert" class="alert"></div>
            
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Nº Pedido</th>
                        <th>Fecha</th>
                        <th>Total</th>
                        <th>Estado Actual</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody id="adminOrdersTableBody">
                    <tr>
                        <td colspan="5" style="text-align: center;">
                            <div class="spinner"></div> Cargando pedidos...
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../fragments/footer.jsp"/>

<script>
    window.BASE_URL = '${ctx}';
</script>
<script src="${ctx}/js/admin-orders.js"></script>
</body>
</html>