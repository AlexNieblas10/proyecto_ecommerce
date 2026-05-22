# FashionHub Ecommerce

Aplicación de ecommerce de moda construida con Java Servlet/JSP y MySQL. Permite gestionar productos, categorías, carrito de compras, órdenes y reseñas, con un panel de administración integrado.

## Prerequisitos

- Java 11+
- Apache Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 9.x

## Quick Start

```bash
git clone <repo-url>
cd ecommerce

# 1. Configura tus credenciales de MySQL
cp .env.example .env
# Edita .env con tu contraseña y configuración de MySQL

# 2. Compila el proyecto
mvn clean package

# 3. Modo desarrollo (Tomcat embebido)
mvn tomcat7:run
```

La app estará disponible en `http://localhost:8080/ecommerce`

> **La base de datos se crea automáticamente** al iniciar la aplicación. No necesitas ejecutar ningún script SQL manualmente.

## Configuración

La app lee la configuración con la siguiente prioridad:

**Variables de entorno del sistema > archivo `.env` > `db.properties` (defaults)**

### Variables disponibles

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de MySQL |
| `DB_PORT` | `3306` | Puerto de MySQL |
| `DB_NAME` | `ecommerce_db` | Nombre de la base de datos |
| `DB_USER` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | `changeme` | Contraseña de MySQL |
| `JWT_SECRET` | *(valor en db.properties)* | Clave secreta para tokens JWT |
| `MAIL_USER` | *(vacío)* | Usuario de Gmail para envío de correos |
| `MAIL_PASSWORD` | *(vacío)* | Contraseña de aplicación de Gmail |

### Opción 1: Archivo `.env` (recomendado para desarrollo)

Crea un archivo `.env` en la raíz del proyecto (junto a `pom.xml`):

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ecommerce_db
DB_USER=root
DB_PASSWORD=mi_contraseña
JWT_SECRET=mi-clave-secreta-larga
```

> El archivo `.env` está en `.gitignore` y nunca se commitea. Usa `.env.example` como plantilla.

### Opción 2: Variables de entorno del sistema

```bash
export DB_PASSWORD=mi_contraseña
export JWT_SECRET=mi-clave-secreta-larga
mvn tomcat7:run
```

### Opción 3: Editar `db.properties` directamente

Modifica `src/main/resources/db.properties` con tus valores. No recomendado para producción ya que el archivo se commitea al repositorio.

## Auto-inicialización de la base de datos

Al iniciar la aplicación, Tomcat ejecuta automáticamente `sql/schema.sql`, que:

1. Crea la base de datos `ecommerce_db` si no existe
2. Crea todas las tablas si no existen (`CREATE TABLE IF NOT EXISTS`)
3. Inserta datos de prueba si las tablas están vacías (`INSERT IGNORE`)

Este proceso es **idempotente** — reiniciar la app es completamente seguro.

Los logs de inicio muestran:
```
[FashionHub] Application starting — initializing database...
[FashionHub] Database ensured: ecommerce_db
[FashionHub] Database initialization complete.
[FashionHub] Application ready.
```

## Build

```bash
mvn clean package
```

Genera `target/ecommerce-1.0-SNAPSHOT.war`. Para desplegar en Tomcat standalone, copia el WAR a `$TOMCAT_HOME/webapps/`.

## Credenciales por defecto

| Campo | Valor |
|---|---|
| Email admin | `admin@fashionhub.com` |
| Contraseña admin | `admin123` |

## Estructura del proyecto

```
src/main/
├── java/org/ecommerce/
│   ├── filter/         — Filtros HTTP (Auth, Admin, CORS, OptionalAuth)
│   ├── listener/       — AppContextListener (auto-init de BD al arrancar)
│   ├── model/          — Entidades POJO (User, Product, Order, etc.)
│   ├── repository/     — Acceso a datos JDBC (DBConnection + repositorios)
│   ├── servlet/        — Endpoints HTTP
│   │   ├── auth/       — Login, registro, logout
│   │   ├── cart/       — Carrito de compras
│   │   ├── order/      — Órdenes
│   │   ├── page/       — Páginas JSP
│   │   ├── product/    — Productos, categorías, reseñas
│   │   └── user/       — Perfil de usuario, admin de usuarios
│   └── util/           — EnvConfig, JWTUtil, DatabaseInitializer, etc.
├── resources/
│   ├── db.properties   — Configuración default (valores placeholder)
│   └── sql/schema.sql  — Schema completo + datos seed (auto-ejecutado)
└── webapp/
    ├── WEB-INF/
    │   ├── jsp/        — Templates JSP
    │   └── web.xml     — Configuración de servlets, filtros y listeners
    ├── css/
    ├── js/
    └── images/
```

## Stack tecnológico

- **Java 11** + Servlet API 4.0 + JSP/JSTL
- **MySQL 8.0** + MySQL Connector/J 8.0
- **JJWT 0.11.5** — autenticación con tokens JWT
- **BCrypt** — hash de contraseñas
- **Gson** — serialización JSON
- **JavaMail** — envío de correos
- **Tomcat 9** — servidor de aplicaciones
- **Maven** — gestión de dependencias y build
