<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="../fragments/header.jsp"/>
<main class="main-content">
    <div class="container">
        <div class="profile-layout">
            <aside class="profile-sidebar">
                <div class="profile-avatar" id="avatarInitial">?</div>
                <div class="profile-name" id="sidebarName">Cargando...</div>
                <div class="profile-email" id="sidebarEmail"></div>
                <nav class="profile-nav">
                    <a href="#info" class="active">Mis datos</a>
                    <a href="#password">Cambiar contraseña</a>
                    <a href="#orders">Mis pedidos</a>
                </nav>
            </aside>
            <div>
                <div class="profile-card" id="info">
                    <h3>Información personal</h3>
                    <div id="profileAlert" class="alert"></div>
                    <form id="profileForm">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="pName">Nombre</label>
                                <input type="text" id="pName" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label for="pPhone">Teléfono</label>
                                <input type="tel" id="pPhone" class="form-control">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="pEmail">Correo electrónico</label>
                            <input type="email" id="pEmail" class="form-control" disabled>
                        </div>
                        <div class="form-group">
                            <label for="pAddress">Dirección</label>
                            <textarea id="pAddress" class="form-control" rows="2"></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Guardar cambios</button>
                    </form>
                </div>

                <div class="profile-card" id="password">
                    <h3>Cambiar contraseña</h3>
                    <div id="passwordAlert" class="alert"></div>
                    <form id="passwordForm">
                        <div class="form-group">
                            <label for="currentPass">Contraseña actual</label>
                            <input type="password" id="currentPass" class="form-control" required>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="newPass">Nueva contraseña</label>
                                <input type="password" id="newPass" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label for="confirmPass">Confirmar nueva contraseña</label>
                                <input type="password" id="confirmPass" class="form-control" required>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">Cambiar contraseña</button>
                    </form>
                </div>

                <div class="profile-card" id="orders">
                    <h3>Mis pedidos</h3>
                    <div id="ordersContainer">
                        <div class="loading-state"><div class="spinner"></div></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
<jsp:include page="../fragments/footer.jsp"/>
<script>
const BASE = window.location.pathname.split('/').slice(0,2).join('/');

async function loadProfile() {
    const res = await fetch(`${BASE}/api/users/me`, { credentials: 'include' });
    if (res.status === 401) { window.location.href = `${BASE}/login`; return; }
    if (!res.ok) return;
    const u = await res.json();
    document.getElementById('pName').value    = u.name    || '';
    document.getElementById('pPhone').value   = u.phone   || '';
    document.getElementById('pEmail').value   = u.email   || '';
    document.getElementById('pAddress').value = u.address || '';
    document.getElementById('sidebarName').textContent  = u.name;
    document.getElementById('sidebarEmail').textContent = u.email;
    document.getElementById('avatarInitial').textContent = (u.name || '?').charAt(0).toUpperCase();
}

async function loadOrders() {
    const res = await fetch(`${BASE}/api/orders`, { credentials: 'include' });
    const container = document.getElementById('ordersContainer');
    if (!res.ok) { container.innerHTML = '<p>Error al cargar pedidos.</p>'; return; }
    const orders = await res.json();
    if (orders.length === 0) {
        container.innerHTML = '<p style="color:var(--color-gray)">Aún no tienes pedidos.</p>';
        return;
    }
    const badges = { pending: 'badge-pending', shipped: 'badge-shipped', delivered: 'badge-delivered' };
    const labels = { pending: 'Pendiente', shipped: 'Enviado', delivered: 'Entregado' };
    container.innerHTML = `<table class="orders-table">
        <thead><tr><th>Número</th><th>Fecha</th><th>Total</th><th>Estado</th></tr></thead>
        <tbody>${orders.map(o => `<tr>
            <td><strong>${o.orderNumber}</strong></td>
            <td>${new Date(o.createdAt).toLocaleDateString('es-MX')}</td>
            <td>$${Number(o.total).toFixed(2)}</td>
            <td><span class="badge ${badges[o.status] || ''}">${labels[o.status] || o.status}</span></td>
        </tr>`).join('')}</tbody>
    </table>`;
}

document.getElementById('profileForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById('profileAlert');
    const res = await fetch(`${BASE}/api/users/me`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
            name:    document.getElementById('pName').value.trim(),
            phone:   document.getElementById('pPhone').value.trim(),
            address: document.getElementById('pAddress').value.trim()
        })
    });
    const data = await res.json();
    if (res.ok) {
        alertEl.textContent = 'Perfil actualizado correctamente';
        alertEl.className = 'alert alert-success show';
        document.getElementById('sidebarName').textContent = data.name;
        document.getElementById('avatarInitial').textContent = (data.name || '?').charAt(0).toUpperCase();
    } else {
        alertEl.textContent = data.error || 'Error al actualizar';
        alertEl.className = 'alert alert-error show';
    }
});

document.getElementById('passwordForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertEl = document.getElementById('passwordAlert');
    const newPass     = document.getElementById('newPass').value;
    const confirmPass = document.getElementById('confirmPass').value;
    if (newPass !== confirmPass) {
        alertEl.textContent = 'Las contraseñas no coinciden';
        alertEl.className = 'alert alert-error show';
        return;
    }
    const res = await fetch(`${BASE}/api/users/me`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
            currentPassword: document.getElementById('currentPass').value,
            newPassword: newPass
        })
    });
    const data = await res.json();
    if (res.ok) {
        alertEl.textContent = 'Contraseña actualizada';
        alertEl.className = 'alert alert-success show';
        document.getElementById('passwordForm').reset();
    } else {
        alertEl.textContent = data.error || 'Error al cambiar contraseña';
        alertEl.className = 'alert alert-error show';
    }
});

document.addEventListener('DOMContentLoaded', () => {
    loadProfile();
    loadOrders();
    updateCartBadge();
});
</script>
<script src="${ctx}/js/store.js"></script>
</body>
</html>
