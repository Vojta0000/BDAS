# Bank Information System (BankIS)

## Project Overview
This is a JavaFX-based Bank Information System (BankIS) designed to manage bank operations, including branches, tellers, clients, accounts, and transactions. The application uses an Oracle Database (running in Docker) to store data and execute business logic through PL/SQL.

## System Requirements
- **Java 21** or higher
- **Gradle** (included via wrapper)
- **Docker** and **Docker Compose**
- **Oracle Database** (Oracle Free version used in Docker)

## Launch Guide

Follow these steps to set up and run the application:

### 1. Start the Oracle Database
Navigate to the `docker-oracle` directory and start the database container:
```powershell
cd docker-oracle
docker-compose up -d
```
Wait for the container to be fully initialized. You can check the logs using `docker logs -f docker-oracle-oracle-1`.

### 2. Initialize the Database Schema
The application includes a built-in "setup mode" that automatically handles the database initialization (creating tables, views, PL/SQL routines, and populating sample data).

To run the setup:
1.  Open `src/main/java/upce/javafx/Launcher.java`.
2.  Change the `IS_SETUP_MODE` variable to `true`:
    ```java
    public static final boolean IS_SETUP_MODE = true;
    ```
3.  Run the application.
4.  The application will execute all necessary scripts and then exit. Check the console output for "Setup completed successfully."
5.  **Change the variable back to `false`** to run the application normally.

*Alternative*: You can also run the setup without changing the code by passing the `--setup` flag as a program argument in your Run Configuration.

*Note: Use the connection details from `ConnectionSingleton.java`:*
- **URL**: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`
- **User**: `my_user`
- **Password**: `password_i_should_change`

### 3. Build and Run the Application
From the project root directory, use the Gradle wrapper to run the application:
```powershell
./gradlew run
```
Alternatively, you can run the `upce.javafx.Launcher` class directly from your IDE.

## Technical Documentation & Inner Workings

### 1. Architecture Overview
The system follows a **Three-Tier Architecture** pattern:
- **Presentation Tier**: JavaFX desktop application.
- **Application Tier**: Java logic handling orchestration, connection management, and scheduling.
- **Data Tier**: Oracle Database containing tables, views, and complex business logic in PL/SQL.

### 2. Database Design & Logic
The database is not just a passive storage but actively enforces business rules:

#### **Core Entities & Relationships**
- **User & Roles**: A base `User` table is extended by `Client` and `Teller` through shared primary keys (1:1 relationship). Roles control access levels within the Java application.
- **Branch Hierarchy**: Branches support a parent-child relationship (`Parent_branch_id`). We use **Recursive CTEs** (Oracle `CONNECT BY`) in views like `v_branch_tree` to resolve the full hierarchy of branches.
- **Transactions**: Every money movement is recorded in the `Transaction` table. A `Transaction_type` distinguishes between transfers, deposits, withdrawals, fees, and interest.

#### **Advanced PL/SQL Features**
- **Stored Procedures**: Critical operations like `execute_transfer` are implemented as PL/SQL procedures to ensure atomicity. If a transfer fails (e.g., insufficient funds), the procedure raises a custom exception (`ORA-20001`), and the Java tier performs a rollback.
- **Triggers for Auditing**: The `Audit_log` table is automatically populated via database triggers whenever sensitive data is modified. This ensures a tamper-proof trail of changes.
- **Views**: All data displayed in the UI is fetched through specific database views (prefixed with `v_`). This decouples the Java code from the underlying table structure.

### 3. Java Application Internals

#### **Connection Management (`ConnectionSingleton.java`)**
The application uses a thread-safe **Singleton** pattern to manage database connections. It provides a `getConnection()` method that returns a fresh JDBC connection. 
*   **Transaction Handling**: We explicitly disable `auto-commit` in critical operations (like payments) to ensure that multiple SQL steps (e.g., finding an account ID and then executing a transfer) are treated as a single atomic unit of work.

#### **Automated Background Tasks (`MonthlyInterestScheduler.java`)**
The system includes a dedicated scheduler that runs as a daemon thread:
- **Logic**: It calculates the time until the 1st day of the next month (at 00:15) and schedules an execution.
- **Execution**: It calls the `apply_monthly_interest` PL/SQL procedure, which iterates through all active accounts and applies interest based on the current balance.
- **Resilience**: Includes a built-in retry mechanism; if the database is unavailable, it waits 60 seconds before trying again.

#### **UI Orchestration**
- **Controllers**: Each FXML view has a corresponding Java controller (e.g., `PaymentViewController`). 
- **Data Binding**: Controllers use `FXCollections` and `ObservableList` to bind database view results to JavaFX `TableView` and `ListView` components, allowing for a responsive UI.

### 4. Security & Data Integrity
- **Password Safety**: The system uses separate credentials for the application user (`my_user`) and the system administrator.
- **Data Validation**: Enforced at three levels:
    1.  **UI Level**: Basic null checks and format validation (e.g., `amount > 0`).
    2.  **Application Level**: Transactional integrity through JDBC.
    3.  **Database Level**: Check constraints (e.g., `Account_balance >= 0`) and triggers.