# Expense Splitter Application

## Project Description

Expense Splitter Application is a backend REST API built with Spring Boot. The application allows users to register, log in, create shared expenses, split expenses equally between participants, and view personal balances.

The project focuses on backend development, database persistence, authentication, authorization, and business logic for expense sharing.

---

## Main Features

The application includes the following features:

* user registration;
* user login;
* password encryption using BCrypt;
* authentication using Basic Auth;
* role-based authorization with `USER` and `ADMIN`;
* expense creation;
* equal split calculation between participants;
* storing the amount owed by each participant;
* viewing expenses related to the authenticated user;
* viewing the balance of the authenticated user;
* admin access for viewing all users;
* admin access for viewing all expenses;
* automated tests for business logic and authorization.

---

## Technologies Used

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* PostgreSQL
* Hibernate
* Maven
* Lombok
* JUnit
* Mockito
* MockMvc
* Postman

---

## Project Structure

The project is organized into several packages:

```text
controller
```

Contains the REST controllers that expose the API endpoints.

```text
service
```

Contains the business logic of the application.

```text
repository
```

Contains the Spring Data JPA repositories used for database operations.

```text
model
```

Contains the JPA entities.

```text
dto
```

Contains request and response objects used for transferring data.

```text
security
```

Contains the Spring Security configuration and the custom user details service.

---

## Main Entities

### User

Represents an application user.

Main fields:

* `id`
* `username`
* `email`
* `password`
* `role`

Available roles:

```text
USER
ADMIN
```

### Expense

Represents a shared expense.

Main fields:

* `id`
* `description`
* `totalAmount`
* `paidBy`
* `participants`
* `createdAt`

### ExpenseParticipant

Represents the relationship between an expense and a participant.

Main fields:

* `id`
* `expense`
* `user`
* `amountOwed`

This entity stores how much each participant owes for a specific expense.

---

## Security

The application uses Spring Security.

Authentication is handled with Basic Auth. The user's email is used as the username, and the password is verified using BCrypt.

Passwords are not stored in plain text. They are encrypted before being saved in the database.

There are two roles:

### USER

A user with the `USER` role can:

* create expenses;
* view their own expenses;
* view their own balance.

### ADMIN

A user with the `ADMIN` role can:

* view all users;
* view all expenses;
* access administrative endpoints.

---

## Database Configuration

The application uses PostgreSQL.

Example for `application.properties` configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_splitter
spring.datasource.username=postgres
spring.datasource.password=secret_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Before running the application, create the database:

```sql
CREATE DATABASE expense_splitter;
```

Hibernate will automatically create the required tables based on the JPA entities.

---

## Running the Application

The application can be started from IntelliJ IDEA by running:

```text
SplitterApplication
```

Or from the terminal:

```bash
mvn spring-boot:run
```

By default, the application runs on:

```text
http://localhost:8080
```

---

## API Endpoints

### Public Endpoints

```http
POST /api/auth/register
POST /api/auth/login
```

Example register body:

```json
{
  "username": "andrei",
  "email": "andrei@test.com",
  "password": "parola123"
}
```

Example login body:

```json
{
  "email": "andrei@test.com",
  "password": "parola123"
}
```

---

### Authenticated User Endpoints

These endpoints require Basic Auth.

```http
POST /api/expenses
GET /api/expenses/my
GET /api/expenses/balance
```

Example expense creation body:

```json
{
  "description": "Pizza",
  "totalAmount": 100.00,
  "paidById": 1,
  "participantIds": [1, 2]
}
```

The total amount is split equally between all participants.

The balance endpoint returns values that can be interpreted as follows:

```text
negative value = the authenticated user owes money
positive value = another user owes money to the authenticated user
```

---

### Admin Endpoints

These endpoints require the `ADMIN` role.

```http
GET /api/users
GET /api/expenses/all
```

The `/api/users` endpoint returns user information without exposing passwords.

---

## Manual Testing with Postman

For protected endpoints, use Basic Auth.

Example:

```text
Username: andrei@test.com
Password: parola123
```

To test admin endpoints, the user must have the `ADMIN` role.

For testing purposes, a user's role can be updated directly in the database:

```sql
UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@test.com';
```

---

## Automated Tests

The project includes automated tests for relevant functionality.

The tests cover:

* equal expense split calculation;
* creation of one expense participant for each user;
* exception handling when an expense has no participants;
* preventing a `USER` from accessing admin endpoints;
* allowing an `ADMIN` to access admin endpoints.

Tests can be run from IntelliJ IDEA or from the terminal:

```bash
mvn test
```

---

## Access Rules

Public endpoints:

```text
POST /api/auth/register
POST /api/auth/login
```

Authenticated user endpoints:

```text
POST /api/expenses
GET /api/expenses/my
GET /api/expenses/balance
```

Admin-only endpoints:

```text
GET /api/users
GET /api/expenses/all
```

---

## Future Improvements

The application can be extended with:

* JWT authentication;
* frontend interface;
* editing and deleting expenses;
* unequal expense splitting;

---

## Conclusion

This project implements the core functionality of an expense sharing backend application. It includes user management, authentication, role-based authorization, expense creation, equal split calculation, balance calculation, database persistence, and automated testing.
