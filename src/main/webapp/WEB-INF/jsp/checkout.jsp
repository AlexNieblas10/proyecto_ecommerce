<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout — FashionHub</title>
    <link rel="stylesheet" href="${ctx}/css/global.css">
    <link rel="stylesheet" href="${ctx}/css/styles.css">
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<main class="main-content">
    <div class="container">
        <div class="breadcrumb">
            <a href="${ctx}/">Inicio</a> / <a href="${ctx}/carrito">Carrito</a> / <span>Checkout</span>
        </div>
        <div class="checkout-layout">
            <div>
                <div class="checkout-card">
                    <h2>Dirección de envío</h2>
                    <div id="checkoutAlert" class="alert"></div>
                    <form id="checkoutForm">
                        <div class="form-group">
                            <label for="shippingAddress">Dirección completa</label>
                            <textarea id="shippingAddress" class="form-control" rows="3"
                                placeholder="Calle, número, colonia, ciudad, estado, CP" required></textarea>
                        </div>
                        <div class="checkout-card" style="margin-top:1.5rem;padding:0;box-shadow:none">
                            <h2 style="margin-bottom:1rem;font-size:1.1rem">Método de pago</h2>
                            <div class="payment-options">
                                <label class="payment-option">
                                    <input type="radio" name="paymentMethod" value="card" required>
                                    <span>💳 Tarjeta de crédito/débito</span>
                                </label>
                                <label class="payment-option">
                                    <input type="radio" name="paymentMethod" value="transfer">
                                    <span>🏦 Transferencia bancaria</span>
                                </label>
                                <label class="payment-option">
                                    <input type="radio" name="paymentMethod" value="cash_on_delivery">
                                    <span>💵 Pago contra entrega</span>
                                </label>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary btn-full btn-lg" style="margin-top:1.5rem">
                            Confirmar Pedido
                        </button>
                    </form>
                </div>
            </div>
            <div id="orderSummary">
                <div class="loading-state"><div class="spinner"></div></div>
            </div>
        </div>
    </div>
</main>

<div class="modal-overlay" id="successModal">
    <div class="modal">
        <div class="order-success">
            <div class="success-icon">✅</div>
            <h2>¡Pedido confirmado!</h2>
            <p>Tu número de pedido es:</p>
            <div class="order-number" id="confirmedOrderNumber"></div>
            <p style="color:var(--color-gray);font-size:.9rem">
                Recibirás un correo de confirmación en breve.
            </p>
            <div style="display:flex;gap:1rem;justify-content:center;margin-top:1.5rem">
                <a href="${ctx}/perfil" class="btn btn-outline-dark">Ver mis pedidos</a>
                <a href="${ctx}/tienda" class="btn btn-primary">Seguir comprando</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
<script src="${ctx}/js/checkout.js"></script>
</body>
</html>
