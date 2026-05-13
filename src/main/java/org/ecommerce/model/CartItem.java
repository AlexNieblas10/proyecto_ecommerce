package org.ecommerce.model;

import java.math.BigDecimal;

public class CartItem {
    private int id;
    private int cartId;
    private int productId;
    private String productName;
    private BigDecimal productPrice;
    private String imageUrl;
    private int quantity;

    public CartItem() {}

    public BigDecimal getSubtotal() {
        if (productPrice == null) return BigDecimal.ZERO;
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
