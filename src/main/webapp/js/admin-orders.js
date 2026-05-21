/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
const API_BASE = '/ecommerce';

async function loadAdminOrders() {
    const tbody = document.getElementById('adminOrdersTableBody');
    
    try {
        const res = await fetch(`${API_BASE}/api/admin/orders`, { credentials: 'include' });
        if (!res.ok) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:red;">Error al cargar los pedidos.</td></tr>';
            return;
        }
        
        const orders = await res.json();
        
        if (orders.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: var(--color-gray);">No hay pedidos registrados en el sistema.</td></tr>';
            return;
        }

        tbody.innerHTML = orders.map(o => {
            const date = new Date(o.createdAt).toLocaleDateString('es-MX');
            
            // Preseleccionar el estado actual del pedido
            const isPending = o.status === 'pending' ? 'selected' : '';
            const isShipped = o.status === 'shipped' ? 'selected' : '';
            const isDelivered = o.status === 'delivered' ? 'selected' : '';

            return `
            <tr>
                <td><strong>${o.orderNumber}</strong></td>
                <td>${date}</td>
                <td>$${Number(o.total).toFixed(2)}</td>
                <td>
                    <select class="form-control" id="status-${o.id}" style="padding: 5px; width: 100%;">
                        <option value="pending" ${isPending}>⏳ Pendiente</option>
                        <option value="shipped" ${isShipped}>🚚 Enviado</option>
                        <option value="delivered" ${isDelivered}>✅ Entregado</option>
                    </select>
                </td>
                <td>
                    <button class="btn btn-primary" style="padding: 5px 15px; font-size: 0.9em;" onclick="updateOrderStatus(${o.id})">Actualizar</button>
                </td>
            </tr>
            `;
        }).join('');
        
    } catch (error) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:red;">Error de conexión con el servidor.</td></tr>';
    }
}

async function updateOrderStatus(orderId) {
    const newStatus = document.getElementById(`status-${orderId}`).value;
    const alertEl = document.getElementById('adminOrdersAlert');
    
    try {
        const res = await fetch(`${window.BASE_URL}/api/admin/orders`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ 
                id: orderId, 
                status: newStatus 
            })
        });

        if (res.ok) {
            alertEl.textContent = '✅ Estado del pedido actualizado correctamente';
            alertEl.className = 'alert alert-success show';
            setTimeout(() => { alertEl.className = 'alert'; }, 3000);
        } else {
            const data = await res.json();
            alertEl.textContent = data.error || '❌ Error al actualizar el pedido';
            alertEl.className = 'alert alert-error show';
        }
    } catch (error) {
        alertEl.textContent = '❌ Error de red. Revisa tu conexión.';
        alertEl.className = 'alert alert-error show';
    }
}

// Cargar la tabla en cuanto la página termine de renderizarse
document.addEventListener('DOMContentLoaded', loadAdminOrders);