# JavaBankNet

JavaBankNet is a desktop banking application built with Java 17 and JavaFX. It features a client-server architecture where a JavaFX client communicates with a backend server that manages a MySQL database. The system supports user authentication, account management, fund transfers, and batch processing of transactions.

## Features

- **User Authentication**: Secure login system for administrators and clients.
- **Dashboard**: Overview of user accounts and balances.
- **Fund Transfers**: Real-time money transfers between accounts.
- **Transaction History**: View history of past transactions.
- **Batch Processing**: High-performance bulk transaction processing using CSV files and Java Parallel Streams.
- **Client-Server Architecture**: TCP socket-based communication between the UI client and the backend server.

## Technologies Used

- **Java 17**
- **JavaFX** (GUI Framework)
- **MySQL** (Database)
- **Maven** (Dependency Management)
- **JDBC** (Database Connectivity)

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- MySQL Server

## Setup Instructions

### 1. Database Configuration

1. Make sure your MySQL server is running.
2. Initialize the database using the provided SQL script:
   ```bash
   mysql -u root -p < init.sql
   ```
   This will create the `JavaBankNet` database, necessary tables (`users`, `accounts`, `transactions`), and insert sample data.

3. Verify the database configuration in `src/main/resources/db.properties`. The default configuration is:
   ```properties
   db.url=jdbc:mysql://localhost:3306/JavaBankNet?serverTimezone=UTC
   db.user=root
   db.password=
   ```
   Update `db.user` and `db.password` if your local setup differs.

### 2. Build the Project

Use Maven to clean and build the project:

```bash
mvn clean install
```

## Running the Application

The application requires the backend server to be running before starting the client.

### Step 1: Start the Server

Run the `BankServer` class to start the backend service (listens on port 12345):

```bash
mvn exec:java -Dexec.mainClass="com.bank.network.BankServer"
```

### Step 2: Start the Client

Open a new terminal and launch the JavaFX client:

```bash
mvn javafx:run
```

## Sample Credentials

You can use the following accounts to log in:

- **Admin**: `admin` / `admin123`
- **Client 1**: `client1` / `pass123`
- **Client 2**: `client2` / `pass123`
