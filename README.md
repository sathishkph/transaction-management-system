**Transaction Management System**
     
  Spring Boot–based backend service that manages users and financial transactions, with built-in support for:
1. Transaction persistence (JPA/Hibernate + In Memory DB (to keep it simple.can be replaced with Relational))
2. User account management
3. Balance validation (prevent overdraft transactions)
4. Thread safety for concurrent transactions (per-user locking)
5. Retry logic for database/infrastructure failures 
6. Global error handling with custom exceptions and response
7. Pagination for transaction history

**Tech Stack**

1. Java 17+
2. Spring Boot
3. Spring Data JPA
4. Spring Retry (with AOP)
5. H2  (configurable)
6. Lombok
7. ModelMapper

**⚙️ Features**

**1. User Management**
* Create a new user
* Retrieve user by ID
* Delete user
* Prevents duplicate user creation (UserIdAlreadyExistsException).

**2.Transaction Management**

* Record transactions (credit/debit)
* Prevent overdrafts (InsufficientBalanceException)
* Thread-safe per-user transaction processing
* Paginated transaction history queries

**3. Reliability**

* Retries on DB/infra errors only using @Retryable
* Business exceptions fail fast (no retries)
* Global error handling via @ControllerAdvice

**📑 API Endpoints**

**1. Users**

* POST /users → Create a user
* GET /users/{userId} → Get user details
* DELETE /users/{userId} → Delete user

**2. Transactions**

* POST /transactions → Create transaction (validates balance if debit)
* GET /transactions/{userId}?startDate=...&endDate=...&page=0&size=10 → Paginated transaction history


**🛠️ Setup & Run**

**1. Clone repo**
   git clone https://github.com/sathishkph/transaction-management-system.git
   cd transaction-management-system

**2. Build & Run**
   mvn clean install
   mvn spring-boot:run

**3. Test APIs**

Default port: http://localhost:8080
Use below postman link to test endpoints.
https://sathish-9143347.postman.co/workspace/sathish's-Workspace~e6b375ae-2a84-493a-8be0-0199a20a2466/collection/47930490-70131c90-bce9-4fa1-ba47-09248b36b097?action=share&creator=47930490

**🔹 Create User**

POST http://localhost:8080/users

Body (JSON):

{
"userId": "sathish1234",
"name": "Sathish Kumar P H",
"email": "sathishkph@yahoo.com"
}


Response (success):

{
"success": true,
"message": "User created successfully."
}


**🔹 Get User by ID**

GET http://localhost:8080/users/sathish1234

Response:

{
"userId": "sathish1234",
"name": "Sathish Kumar P H",
"email": "sathishkph@yahoo.com",
"createdAt": "2025-08-29T10:00:00"
}


🔹 **Create Transaction**

POST http://localhost:8080/api/transactions

Body (JSON):
{
"transactionId": "txn001",
"userId": "sathish1234",
"amount": 1000.00,
"timeStamp": "2025-08-29T11:00:00"
}
Response (success):

{
"success": true,
"message": "Transaction created successfully."
}

Response(Duplicate Transaction)
{
"errorCode": "TRANSACTION_ID_EXIST",
"message": "Transaction already exists with id: txn001"
}


**🔹 Get Transactions by User (with date filter + paging)**

GET http://localhost:8080/transactions?userId=sathish1234&startDate=2025-08-29T10:18:30.122&endDate=2025-08-29T18:18:30.123&page=0&size=10

**Response**
{
"content": [
{
"transactionId": "txn001",
"userId": "sathish1234",
"amount": 1000.0,
"timeStamp": "2025-08-29T11:00"
},
{
"transactionId": "txn002",
"userId": "sathish1234",
"amount": -200.0,
"timeStamp": "2025-08-29T12:30"
}
],
"pageable": {
"pageNumber": 0,
"pageSize": 10,
"sort": {
"empty": true,
"unsorted": true,
"sorted": false
},
"offset": 0,
"unpaged": false,
"paged": true
},
"totalPages": 1,
"totalElements": 2,
"last": true,
"size": 10,
"number": 0,
"sort": {
"empty": true,
"unsorted": true,
"sorted": false
},
"numberOfElements": 2,
"first": true,
"empty": false
}

**🔹 Get Account Summary**

GET http://localhost:8080/account-summary/sathish1234

Response:

{
"userId": "sathish1234",
"currentBalance": 1300.0,
"transactionCount": 3
}


**DB Scripts:**

## 🔗 Accessing H2 Database Console


👉 http://localhost:8080/h2-console

### H2 Console Settings:
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User Name:** `sa`
- **Password:** *(leave empty)*

---

## 🗄️ Insert Sample Data

-- Insert Users
INSERT INTO user_info (user_id, name, email, created_at) VALUES
('sathish1234', 'sathishKumar P H', 'sathishkph@yahoo.com', '2025-08-29 10:00:00'),
('colm1234', 'Colm Coughlan', 'Colm.Coughlan@outseer.com', '2025-08-25 09:30:00'),
('meena9876', 'Meena R', 'meena@gmail.com', '2025-08-20 11:15:00');

-- Insert Transactions for Sathish
INSERT INTO transactions (transaction_id, user_id, amount, timestamp) VALUES
('txn001', 'sathish1234', 1000.00, '2025-08-29T11:00:00'),
('txn002', 'sathish1234', -200.00, '2025-08-29T12:30:00'),
('txn003', 'sathish1234', 500.00, '2025-08-30T09:15:00');

-- Insert Transactions for Colm
INSERT INTO transactions (transaction_id, user_id, amount, timestamp) VALUES
('txn004', 'colm1234', 1500.00, '2025-08-26T10:45:00'),
('txn005', 'colm1234', -300.00, '2025-08-27T14:20:00');

-- Insert Transactions for Meena
INSERT INTO transactions (transaction_id, user_id, amount, timestamp) VALUES
('txn006', 'meena9876', 2000.00, '2025-08-21T16:00:00'),
('txn007', 'meena9876', -500.00, '2025-08-23T09:40:00');


