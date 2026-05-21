/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
async function loadAdminOrders() {
    const tbody = document.getElementById('adminOrdersTableBody');
    if (!tbody) return;

    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center"><div class="spinner"></div></td></tr>';

    const res = await fetch(`${BASE}/api/admin/orders`, { credentials: 'include' });
    if (!res.ok) {
        tbody.innerHTML = '<tr><td colspan="6" class="alert alert-error">Error al cargar pedidos.</td></tr>';
        return;
    }

    const orders = await res.json();

    if (orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center">No hay pedidos registrados.</td></tr>';
        return;
    }

    tbody.innerHTML = orders.map(o => {
        const date = new Date(o.createdAt).toLocaleString();
        
        return `
        <tr>
            <td>${o.orderNumber}</td>
            <td>${date}</td>
            <td>${o.userName}<br><small style="color:var(--color-gray)">${o.userEmail}</small></td>
            <td>$${Number(o.total).toFixed(2)}<br><small>(${o.paymentMethod})</small></td>
            <td>
                <select class="form-control" onchange="updateOrderStatus(${o.id}, this.value)">
                    <option value="pending" ${o.status === 'pending' ? 'selected' : ''}>⏳ Pendiente</option>
                    <option value="shipped" ${o.status === 'shipped' ? 'selected' : ''}>🚚 Enviado</option>
                    <option value="delivered" ${o.status === 'delivered' ? 'selected' : ''}>✅ Entregado</option>
                </select>
            </td>
        </tr>
        `;
    }).join('');
}

async function updateOrderStatus(orderId, newStatus) {
    const res = await fetch(`${BASE}/api/admin/orders/${orderId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ status: newStatus })
    });

    if (res.ok) {
        // Podrías mostrar un Toast de éxito aquí si quieres
        console.log(`Pedido ${orderId} actualizado a ${newStatus}`);
    } else {
        const data = await res.json();
        alert('Error al actualizar: ' + data.error);
        loadAdminOrders(); // Recargar tabla si falla
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadAdminOrders();
});

