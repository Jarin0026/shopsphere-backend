## ShopSphere – Backend (Spring Boot)

**Frontend (React) → REST API → Spring Boot → MySQL Database**  <br>

This is the backend service for ShopSphere – a full-stack E-Commerce Platform. <br>
It is built using Spring Boot and follows a layered architecture (Controller → Service → Repository). <br>

The backend manages authentication, product management, cart & wishlist logic, order processing, role-based access control, and secure payment verification. <br>

---

### 🚀 Features

🔐 Authentication & Security

* JWT-based authentication
* Role-based authorization (Customer / Vendor / Admin)
* Password encryption using BCrypt
* Secure REST APIs using Spring Security
* Protected endpoints based on user roles

🛍 Product Management

* Add, update, delete products
* Upload product details (price, stock, category, description)
* View all products with pagination
* Search and filter products
* Manage product inventory

🛒 Cart & Wishlist System

* Add to cart
* Update cart quantity
* Remove from cart
* Add / Remove from wishlist
* Persistent cart linked to user account

📦 Order Management

* Place orders
* Order status tracking (Pending / Confirmed / Shipped / Delivered)
* Order history per customer
* Vendor order tracking
* Automatic stock reduction after purchase

💳 Payment Integration

* Razorpay payment verification
* Secure transaction validation
* Order confirmation after successful payment

🏪 Vendor Management

* Vendors can manage their own products
* View sales reports
* Track product performance

🛠 Admin Controls

* Manage users (Customer / Vendor)
* Manage products
* Monitor orders
* Platform-level control over listings

---

### 🧠 Backend Responsibilities

* Handles complete business logic
* Validates product & order rules
* Manages users & role-based access
* Secures APIs using JWT
* Processes and verifies payments
* Connects to MySQL database
* Global exception handling
* Data validation & error responses

---

### 🧑🏽‍💻 Tech Stack

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* MySQL
* Maven
* Razorpay Integration
* Render (Deployment)

---

### 📦 Frontend Repository

[Frontend Repository](https://github.com/jarin0026/shopsphere-frontend)
