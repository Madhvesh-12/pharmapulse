# PharmaPulse - Pharmacy Inventory & Order Management System

PharmaPulse is terminal-based Java CLI application to automate retail pharmacy inventory tracking, concurrent prescription cart processing, and physical billing operations using JDBC and MySQL.

---

## 1. Overview

The system is split into a presentation layers, that is the CLI Menus, a service layer containing buisness logic and validation, a model layer using OOP domain classes, a concurrency layer over threaded order persistence, and a data access layer using JDBC against MySQL.

---

## 2. Features
* **Administrative Authentication:** Secure session access protected by a passkey (`pp1234`).
* **Mutlithreaded Order Processing:** Asynchronous, synchronized persistence using worker threads (`OrderProcessorThread`) to commit transaction records and deduct stock automatically.
* **Pre-Commit INventory Validation:** Prevents sales of unavailable or out-of-stock itmes using a custom checked `InsufficientStockException`.
* **Invoice Export via File Streams:** Generates customer receipts and automatically saves physical records (`invoice_<CustomerName>.txt`) using java character output streams (`BufferedWriter`).
* **Decoupled Database Access:** Centralized connection handling and lambda-based query execution via functional interfaces (`SQLConsumer`, `SQLFunction`).

---

## 3. Technologies & Tools
* **Programming Language:** Core Java (JCK 17+)
* **Database Management system:** MySQL Server 8.0+
* **JDBC Driver:** MySQL Connector/J (`lib/mysql-connector-j-*.jar`)
* **Development Environment:** Visual Stdio Code / Terminal CLI

---

## 4. Project Structure
```text
pharmapulse/
├── bin/
├── lib/
├── src/
│   ├── DBConnection.java
│   ├── Item.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderProcessorThread.java
│   ├── OrderSerivce.java
│   ├── ProductService.java
│   └──PharmaPulseApp.java
├── db.properties
├── schema.sql
├── staement.md
└── README.md
```
---

## 5. Installation & Setup

### Step 1: Database Initialization
Ensure your MySQL server instance is running, open your MySQl terminal or workbench , and run the following scripts:

```sql
CREATE DATABASE IF NOT EXISTS pharmacy;
USE pharmacy;

CREATE TABLE IF NOT EXISTS products (
    PID INT PRIMARY KEY,
    PNmae VARCAHR(100) NOT NULL,
    Brand VARCHAR(100),
    PType VARCHAR(50),
    Stock INT NOT NULL,
    Cost DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    OrderNo INT PRIMARY KEY,
    CName VARCHAR(100) NOT NULL,
    `Pname:Qty` TEXT NOT NULL,
    Amt DECIMAL(10,2) NOT NULL,
    Date DATE NOt NULL
);
```

### Step 2: Database Configuration
Configure `db.properties` in the project root directory with your local MySQL credentials:

```properties
db.host=localhost
db.port=3306
db.name=pharmacy
db.user=root
db.password=your_mysql_password
```

### Step 3: Compilation
Open the terminal in the project root folder (`pharmapulse`) and compile all Java classes into the `bin` folder:

```cmd
javac -cp "lib/*" -d bin src/*.java
```

### Step 4: Run the Application
Execute the compiled entry point:

```cmd
java -cp "bin;lib/*" PharmaPulseApp
```

---

## 6. Testing & Valiation Guide
1. **Authentication:**
    * Enter the access key `pp1234` when propmted. (Entering an incorrect value deniedaccess).
2. **Product Setup:**
    * Navigate to `1 -> 2` to add items (e.g., `PID: 101`, `PName: Paracetamol`, `Brand: Cipla`, `Type: Tablet`, `Stock: 50`, `Cost: 20.00`).
    * Select `1 -> 1` to verify the table formattiing and loaded data.
3. **Cart Assembly & Concurrency:**
    * Navigate to `2 -> 2` to create a new order.
    * Add `Paracetamol` with quantity `5`, exit the cart by typing `0`, and provide an order date.
    * The terminal will ouput `>> [SUCCESS] Order persisted successfully`.
    * Return to `1 -> 1` to confirm that the inventory stock has automatically decrementedfrom `50` to `45`.
4. **Out-of-Stock Validation:**
    * Attempt to create an order for `Paracetamol` with a requested quantity of `100`.
    * The program catches `InsufficienStockException`,halts the transaction, and prevents database insertion.
5. **Physical Invoice Generation:**
    * Select `2 -> 4` under Orders & Billing and enter the customer name.
    * Verify that the calculated billing breakdown prints to the terminal and that `invoice_<CustomerName>.tct` is created int the project root directory.



