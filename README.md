# J4RR - Pet Shop Supply POS and Inventory Management App

A lightweight, clean, and modern Point of Sale (POS) and Inventory Management mobile application built for pet supply stores using **Android Studio, Java, and XML**. Designed with Material 3 components and local storage (SharedPreferences).

---

## 🚀 Getting Started & Default Login

When you launch the app, you will be greeted by the Login screen. 

* **Username:** `admin`
* **Password:** `admin123`

*(Tap the eye icon in the password field to toggle password visibility, or use the "Forgot Password?" helper link).*

---

## 📱 Navigation & User Guide

The app is navigated using a clean **Bottom Navigation Bar** with five main tabs:

### 1. Dashboard
The central hub providing an overview of your store's performance:
* **Today's Sales**: Total revenue generated from completed POS sales.
* **Products**: Total number of active products in inventory.
* **Low Stock**: Alert counter for items at or below minimum stock level.
* **Transactions**: Total count of completed sales transactions.
* **Quick Actions**: Fast-access buttons for New Sale (POS), Products, Inventory, and Sales History.

### 2. POS (Point of Sale)
Streamlined checkout workflow:
* **Search Products**: Use the search bar to quickly find items by name or category.
* **Add to Cart**: Tap **Add** on any product to add it to the active cart.
* **Cart Management**: Increase quantities (`+`), decrease quantities (`-`), or remove items (`X`).
* **Checkout**:
  * View real-time Subtotal and Total.
  * Enter **Cash Amount** to automatically calculate **Change**.
  * Tap **COMPLETE SALE** to finalize the transaction, record the sale in history, and deduct purchased quantities from inventory automatically.

### 3. Products
Manage your store catalog:
* View all products with category, price, and stock count.
* **Add Product**: Tap the floating `+` button in the bottom right to add a new product (Name, Category [Dog, Cat, Bird, Fish, Other], Price, Stock, Minimum Stock).
* **Edit / Delete**: Quickly modify product details or remove items.
* **Quick Links**: Access [Inventory Management](#inventory-management) and [Manage Prices](#manage-prices) directly from the top header buttons.

### 4. Sales History
Review past transactions:
* Displays a chronological list of all completed POS checkouts.
* Each entry shows the **Transaction ID**, **Date & Time**, **Items Summary**, **Total Amount**, **Cash Tendered**, and **Change**.

### 5. Settings
Store configuration and preferences:
* **Store Information**: Update your Pet Shop Name, Address, and Contact Number.
* **Dark Mode**: Toggle between Light and Dark themes instantly.
* **Clear Local Data**: Reset all stored products and sales records.
* **Logout**: Safely end the current session and return to the Login screen.

---

## 📦 Secondary Features

* **Inventory Management** (Accessible from Products):
  * Monitor stock status color-coded as **In Stock** (Green), **Low Stock** (Yellow), or **Out of Stock** (Red).
  * Tap **Adjust** on any product to instantly update stock levels.
* **Manage Prices** (Accessible from Products):
  * Quickly review current selling prices and update them on the fly.

---

## 🛠️ Technology Stack
* **Language**: Java
* **UI**: XML Layouts & Material Design 3 components
* **Storage**: Local Storage via `SharedPreferences` & JSON
* **Architecture**: Activity & Fragment-based MVVM-friendly structure
