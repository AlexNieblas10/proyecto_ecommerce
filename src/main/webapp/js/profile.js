/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
async function loadProfile() {
    const res = await fetch(`${BASE}/api/users/me`, { credentials: 'include' });
    if (res.status === 401) { window.location.href = BASE + '/login'; return; }
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
    
    const rows = orders.map(o =>
        '<tr>' +
        '<td><strong>' + o.orderNumber + '</strong></td>' +
        '<td>' + new Date(o.createdAt).toLocaleDateString('es-MX') + '</td>' +
        '<td>$' + Number(o.total).toFixed(2) + '</td>' +
        '<td><span class="badge ' + (badges[o.status] || '') + '">' + (labels[o.status] || o.status) + '</span></td>' +
        '</tr>'
    ).join('');
    
    container.innerHTML = '<table class="orders-table"><thead><tr><th>Número</th><th>Fecha</th><th>Total</th><th>Estado</th></tr></thead><tbody>' + rows + '</tbody></table>';
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
});