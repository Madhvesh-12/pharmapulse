# Problem Statement & Scope: PharmaPulse

## 1. Problem Statement
Small and mid-sized pharmacies frequently manage medicine stock and customer orders using paper registers or disconnected spreadsheets. This makes it difficult to know current stock levels in real time, track which orders have been fulfilled and produce accurate customer invoices.

PharmaPulse addresses these shortcomings by providing a engineered, thread-safe Java Command Line Interface (CLI) platform backed by relational database (MySQL). It guarantees that stock verification, concurrent serialization, and physical billling export occur with data integrity.

## 2. Scope of the Project
The scpoe of PharmaPulse covers retail pharmaceutical inventory management and consumer point-of-sale execution:
- **Persistent Relational Storage:** MAintaining structured database catalogs for pharmaceutical drugs and prescription orders using MySQL via JDBC.
- **Dynamic cart Compilation:** Allowing pharmacy operators to dynamically assemble multi-item, variable-quantity prescription baskets within a unified session.
- **In_Memory Defensive Validation:** Performing programmatic inventory checks againstlive databse records prior to dinalizing sale to prevent negative inventory states.
- **Thread-Safe Asynchronous Concurrency:** Spawning synchronized background worker threads (`OrderProcessorThread`) to commit transaction records and deduct inventory stock values.
- **Physical Docuument Generation:** Interfacing directly with filesystem characterstreams (`BufferedWriter`) to export formal, audit-ready text receipts for customers.

## 3. Target Users
- **Pharmacy Administrations & Store Managers:** Primary users respondible for catalog maintenance, monitoring active inventory batches, adjusting stock quantities, and overseeing administrative audit traits.
- **Billing Opeartors & Cashiers:** Front-counter operators tasked with quering medicine availability, creating pateint prescription carts, executing sales, generating physical customer receipts.
- **Clinical Dispensary Staff:** Health clinic personnel needing clear tracking of medicine prescription fulfillment errors.

## 4. High-Level Features
- Admin sign-in with access-code authentication.
- Product inventory management (add / view / update / remove).
- Order creation with a runniing cart and real-timr stock validation
- Custom `insufficientStockExceptin` prevents orders that exceed available stck.
- Concurrent, thread-safe order persistance (`OrderProcessorThread`, synchronized stock updatres).
- Order cancellation and order history viewing
- per-customer invoice generation with discount calcualtion support (`Billable` interface) and file export via buffered I/O.
- Leveled console logging for all signifin=cant actions.