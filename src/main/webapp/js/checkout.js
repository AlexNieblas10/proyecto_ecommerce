async function loadOrderSummary() {
    const summaryEl = document.getElementById('orderSummary');
    if (!summaryEl) return;

    const res = await fetch(`${BASE}/api/cart`, { credentials: 'include' });
    if (res.status === 401) { window.location.href = `${BASE}/login`; return; }
    if (!res.ok) return;

    const cart = await res.json();
    if (!cart.items || cart.items.length === 0) {
        window.location.href = `${BASE}/carrito`;
        return;
    }

    summaryEl.innerHTML = `
        <div class="order-summary-card">
            <h3 style="font-family:var(--playfair);margin-bottom:1rem">Tu pedido</h3>
            ${cart.items.map(item => `
                <div class="summary-row">
                    <span>${item.productName} × ${item.quantity}</span>
                    <span>$${Number(item.subtotal).toFixed(2)}</span>
                </div>
            `).join('')}
            <div class="summary-row" style="border-top:1px solid var(--color-light);margin-top:.5rem;padding-top:.5rem">
                <span>Envío</span><span>Gratis</span>
            </div>
            <div class="summary-row summary-total">
                <span>Total</span><span>$${Number(cart.total).toFixed(2)}</span>
            </div>
        </div>`;
}

async function submitOrder(e) {
    e.preventDefault();
    const form = document.getElementById('checkoutForm');
    const alertEl = document.getElementById('checkoutAlert');
    const submitBtn = form.querySelector('[type="submit"]');

    const shippingAddress = document.getElementById('shippingAddress').value.trim();
    const paymentMethod   = form.querySelector('input[name="paymentMethod"]:checked')?.value;

    if (!shippingAddress) {
        showAlert(alertEl, 'Ingresa una dirección de envío');
        return;
    }
    if (!paymentMethod) {
        showAlert(alertEl, 'Selecciona un método de pago');
        return;
    }

    const payload = { shippingAddress, paymentMethod };

    if (paymentMethod === 'card') {
        payload.cardNumber = document.getElementById('cardNumber')?.value.trim();
        payload.cardExpiry = document.getElementById('cardExpiry')?.value.trim();
        payload.cardCvv = document.getElementById('cardCvv')?.value.trim();

        if (!payload.cardNumber || payload.cardNumber.length < 16) {
            showAlert(alertEl, 'Ingresa un número de tarjeta válido (16 dígitos)');
            return;
        }
        if (!payload.cardExpiry) {
            showAlert(alertEl, 'Ingresa la fecha de expiración');
            return;
        }
        if (!payload.cardCvv || payload.cardCvv.length < 3) {
            showAlert(alertEl, 'Ingresa un código CVV válido');
            return;
        }
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner"></span> Procesando...';

    const res = await fetch(`${BASE}/api/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
    });

    const data = await res.json();

    if (res.ok) {
        openSuccessModal(data.orderNumber);
    } else {
        showAlert(alertEl, data.error || 'Error al procesar el pedido');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Confirmar Pedido';
    }
}

function openSuccessModal(orderNumber) {
    const modal = document.getElementById('successModal');
    const numEl = document.getElementById('confirmedOrderNumber');
    if (numEl) numEl.textContent = orderNumber;
    if (modal) modal.classList.add('open');
}

function showAlert(el, message) {
    el.textContent = message;
    el.className = 'alert alert-error show';
}

document.querySelectorAll('.payment-option').forEach(opt => {
    opt.addEventListener('click', () => {
        document.querySelectorAll('.payment-option').forEach(o => o.classList.remove('selected'));
        opt.classList.add('selected');
        const radio = opt.querySelector('input[type="radio"]');
        radio.checked = true;

        const cardForm = document.getElementById('cardDetailsForm');
        if (cardForm) {
            cardForm.style.display = radio.value === 'card' ? 'block' : 'none';
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    loadOrderSummary();
    const form = document.getElementById('checkoutForm');
    if (form) form.addEventListener('submit', submitOrder);
});