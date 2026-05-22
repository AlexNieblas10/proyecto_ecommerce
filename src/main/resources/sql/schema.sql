-- FashionHub Ecommerce - Schema MySQL
-- Do NOT run manually. This file is executed automatically on app startup.
-- Configure credentials via environment variables or a .env file (see .env.example).

CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_db;

-- =============================================
-- TABLAS
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(100)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    phone         VARCHAR(20),
    address       TEXT,
    role          ENUM('customer','admin') NOT NULL DEFAULT 'customer',
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS products (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(200)   NOT NULL,
    description    TEXT,
    price          DECIMAL(10,2)  NOT NULL,
    stock          INT            NOT NULL DEFAULT 0,
    image_url      VARCHAR(500),
    category_id    INT,
    specifications TEXT,
    active         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS carts (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT  NOT NULL UNIQUE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    cart_id    INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL DEFAULT 1,
    UNIQUE KEY uq_cart_product (cart_id, product_id),
    FOREIGN KEY (cart_id)    REFERENCES carts(id)    ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    order_number     VARCHAR(20)   NOT NULL UNIQUE,
    user_id          INT           NOT NULL,
    shipping_address TEXT          NOT NULL,
    payment_method   ENUM('card','transfer','cash_on_delivery') NOT NULL,
    total            DECIMAL(10,2) NOT NULL,
    status           ENUM('pending','shipped','delivered') NOT NULL DEFAULT 'pending',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT           NOT NULL,
    product_id INT           NOT NULL,
    quantity   INT           NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT  NOT NULL,
    product_id INT  NOT NULL,
    rating     INT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_product (user_id, product_id),
    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- =============================================
-- DATOS INICIALES
-- =============================================

INSERT IGNORE INTO categories (id, name, description) VALUES
  (1, 'Camisetas',   'Camisetas, tops y blusas'),
  (2, 'Pantalones',  'Jeans, pantalones y shorts'),
  (3, 'Vestidos',    'Vestidos y faldas'),
  (4, 'Accesorios',  'Bolsos, cinturones y joyería'),
  (5, 'Zapatos',     'Calzado para hombre y mujer');

-- Admin user: admin@fashionhub.com / admin123
INSERT IGNORE INTO users (id, name, email, password_hash, phone, role) VALUES
  (1, 'Admin', 'admin@fashionhub.com',
   '$2a$10$Hh6ikXKoeEV8Ays9MgTdu.JntyF7ZSMMp/RFf3cRRH0O.az3Q9R7m',
   '555-0001', 'admin');

INSERT IGNORE INTO products (id, name, description, price, stock, image_url, category_id, specifications) VALUES
  (1, 'Camiseta Básica Blanca',
      'Camiseta de algodón premium de corte clásico, perfecta para cualquier ocasión.',
      299.00, 50, 'images/products/p1.jpg', 1,
      'Material: 100% Algodón | Tallas disponibles: XS, S, M, L, XL, XXL | Lavado: máquina fría'),
  (2, 'Jeans Clásicos Azul',
      'Jeans de corte recto con lavado clásico azul medio. Denim de alta calidad.',
      599.00, 30, 'images/products/p2.jpg', 2,
      'Material: 98% Algodón, 2% Elastano | Tallas: 28-38 | Corte: Recto'),
  (3, 'Vestido Floral Verano',
      'Vestido ligero con estampado floral ideal para el verano. Caída elegante.',
      799.00, 20, 'images/products/p3.jpg', 3,
      'Material: 100% Viscosa | Tallas: XS, S, M, L, XL | Largo: midi'),
  (4, 'Bolso de Cuero Marrón',
      'Bolso de mano artesanal en cuero genuino. Espacio interior amplio.',
      1299.00, 15, 'images/products/p4.jpg', 4,
      'Material: Cuero genuino | Dimensiones: 30×20×10 cm | Cierre: cremallera'),
  (5, 'Zapatillas Blancas',
      'Zapatillas de estilo casual deportivo. Cómodas para uso diario.',
      899.00, 25, 'images/products/p5.jpg', 5,
      'Material: Cuero sintético | Tallas: 35-45 | Suela: goma antideslizante'),
  (6, 'Blusa Elegante Negra',
      'Blusa de seda con escote en V, perfecta para ocasiones formales.',
      549.00, 35, 'images/products/p6.jpg', 1,
      'Material: 100% Seda | Tallas: XS, S, M, L | Lavado: solo en seco'),
  (7, 'Pantalón Beige Casual',
      'Pantalón de tiro alto en tela chino. Versátil y cómodo.',
      449.00, 40, 'images/products/p7.jpg', 2,
      'Material: 65% Poliéster, 35% Algodón | Tallas: 28-36 | Corte: slim'),
  (8, 'Cinturón de Cuero Negro',
      'Cinturón clásico de cuero genuino con hebilla metálica dorada.',
      349.00, 60, 'images/products/p8.jpg', 4,
      'Material: Cuero genuino | Largo: 110 cm | Hebilla: metal dorado');
