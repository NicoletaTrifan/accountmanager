# Account Manager

It is a Spring Boot application for managing customer accounts and their balance. 

I went with the account manager naming, because it is a service that manages customer accounts' details, 
creations, handles operations. Also, I assumed in the process of development I might have a service class, and to reduce 
ambiguity.

## Features 🚀
- Create a customer account 
- Retrieve details about one/all customer accounts (including current balance)
- Retrieve operation history on a specific account
- Add funds to an account 
- Withdraw funds from an account


## Prerequisites 🛠️
- Please make sure your setting is compatible with Java 17 and SpringBoot 3.5.0
- Please make sure you have PostgreSQL installed. Optionally you can make use of pgAdmin4 for easier management 
and visibility of the db
- Once everything is set up, run the following commands to initialize and configure the database
```markdown
CREATE DATABASE accountmanagerdb;
CREATE USER amuser WITH PASSWORD "ampassword";
GRANT ALL PRIVILIGES ON DATABASE accountmanagerdb TO amuser;
\c accountmanagerdb
```
- At this point you should be successfully connected to the database
- When you will first time run the application, there will be a few users introduced in the database, to have an initial
batch to test/operate with. You can take a look at the changesets files unders `resources/db/changelog`.

## Architecture
### Controller layer
 Exposes REST endpoints and handles HTTP request/response
```markdown
 | Method   | Endpoint                  | Description                                                            |
 | -------- | ------------------------- | ---------------------------------------------------------------------  |
 | POST     | /accounts/create          |  Create a new customer account                                         |
 | GET      | /accounts                 |  Retrieve all customer accounts                                        |
 | GET      | /accounts/{id}            |  Retrieve a customer account by given id                               |
 | PATCH    | /accounts/{id}/deposit    |  Deposits money for a given id. Provide the amount in the request body |
 | PATCH    | /accounts/{id}/withdraw   |  Withdraw money for a given id. Provide the amount in the request body |
```

```markdown
 | Method   | Endpoint                  | Description                                         |
 | -------- | ------------------------- | --------------------------------------------------  |
 | GET      | /accounts/{id}/history    |  Retrieve all history on a specific customer account|
``` 
### Request body examples 
POST /accounts/create
```json
{
  "name": "Alice",
  "email": "alice3@example.com",
  "accountBalance": 1000
}
```
POST /accounts/1/deposit
```json
{
    "amount" : 50
}
```
### Service layer 
Handles all the business logic regarding how an account is created, depositing/withdrawing funds and history tracking.
### Repository layer 
Handles database access using spring Data JPA.
### Model layer 
Defines the JPA entities used to represent customer accounts and operations in the account history.
### Exception handling 
I defined a few custom exceptions for business errors, such as insufficient funds, duplicate emails etc. 
### Validation 
An important part of the project is validation. I used both input validation and business validation. 
#### Input validation 
- The email field is validated using `@Email` annotation to ensure that it corresponds to the general format
- Fields like name, balance, email, are set to non-nullable at the entity level
#### Business Validation 
- A customer cannot be created if another customer account already exists with the same email, otherwise `EmailAlreadyExistsException` is thrown
- Any kind of operation on customer account retrieval/deposit/withdraw, requires for the customer account to exist, 
otherwise a `CustomerNotFoundException` is thrown
- A customer cannot go lower than 0 on their balance account, therefore if he tries to perform such an operation a `InsufficientBalanceException`
is thrown. 

### Technologies 
- Java 17 
- SpringBoot 3.5.0
- PostgreSQL
- Liquibase
- Gradle

### Run the application 
1. Clone the repository 
```markdown
git clone https://github.com/NicoletaTrifan/accountmanager.git
cd accountmanager
```
2. Make sure you went through prerequisites and are all set
3. Run the application 
