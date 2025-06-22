#  Spring Boot CRUD Application  — Clean Architecture with Lombok

This repository contains a clean and modular Spring Boot CRUD application using Spring Boot `3.4.2`.
It showcases a basic Book Management System built with layered architecture, Lombok, Spring Data JPA, and a PostgreSQL database.

---

##  Features

-  RESTful CRUD APIs for `Book` entity
-  Clean layered architecture: Model → DTO → Repository → Service → Controller
-  Uses Lombok to reduce boilerplate code
-  PostgreSQL database configuration
-  DTOs and `BeanUtils.copyProperties()` for clean data transfer
-  Spring Boot 2.7.17 with Maven

---

##  Learn More — Article

A detailed write-up explaining every design decision:  
==> [Read the Medium Article](https://medium.com/@sayedaasim555/building-a-clean-spring-boot-crud-api-with-lombok-a-beginner-friendly-guide-feb494cf900a)

---

## Project Structure

crud/
├── controller/
│   └── BookController.java
├── dto/
│   └── BookDto.java
├── model/
│   └── Book.java
├── repository/
│   └── BookRepository.java
├── service/
│   ├── BookService.java
│   └── BookServiceImpl.java
├── CrudApplication.java
└── resources/
├── application.properties
---
## Tech Stack
- Java 17
- Spring Boot 2.7.17
- Spring Web
- Spring Data JPA
- Lombok
- PostgreSQL
- Maven

---

## Setup Instructions
Prerequisites
- Java 17+
- Maven
- PostgreSQL running locally

---
## Clone the Repository

git clone https://github.com/aasimsyed97/spring-stack-playground.git
cd spring-stack-playground
---
## Configure Database
Update your src/main/resources/application.properties:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

---
## Run the Application
./mvnw spring-boot:run

---
## Test the Endpoints
Use Postman or curl to test:

POST /books/save — Add a book

GET /books/all — Get all books

PUT /books/update/{id} — Update a book

DELETE /books/delete?title= — Delete a book

---
## API Payload Example
POST /books/save
json
{
"title": "Clean Code",
"author": "Robert C. Martin"
}
---
## Contributing
Pull requests and issues are welcome! If you'd like to contribute, just fork the repo and send a PR.

