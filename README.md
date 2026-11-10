# Spring Boot Onboarding

A hands-on Java and Spring Boot learning project
## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Testing:** JUnit 5
- **Containerization:** Docker
- **Version Control:** Git and GitHub

## Learning Roadmap

| Day | Topics |
|---|---|
| Day 1 | Java fundamentals, variables, data types, conditions, loops, and methods |
| Day 2 | Arrays, user input, exception handling, and problem-solving |
| Day 3 | Object-Oriented Programming, classes, objects, constructors, encapsulation, and static |
| Day 4 | Interfaces, inheritance, polymorphism, abstraction, and custom exceptions in progress |   
 | Day 5 | JUnit learning and added test for account and payment method |
| Day 6 | Contact manager, Learn about Array/List/Set/Map |
| Day 7  | Streams, Optional, lambdas, method references; when a loop is clearer |
| Day 8 | Dates with java.time, BigDecimal for money, enum, null handling. |
| Day 9 | Maven lifecycle, pom.xml, dependencies, package hierarchy, logger; configuration |

## Prerequisites

Make sure you have installed:

- Java JDK 21
- IntelliJ IDEA or VS Code
- Git
- Docker
- PostgreSQL (Docker container is sufficient)

Verify Java and Maven wrapper:

```bash
java -version
javac -version
```

## Getting Started

### 1. Clone the repository

```bash
git clone git@github.com:RameshwarTiwari-BR/Onboarding-Task-Springboot.git
cd Onboarding-Task-Springboot
```

### 2. Build the project

Linux / macOS:

```bash
./mvnw clean install
```

Windows:

```bash
mvnw.cmd clean install
```

### 3. Configure PostgreSQL

Create a PostgreSQL database and configure the connection in:

`src/main/resources/application.properties`

Example:

```properties
spring.application.name=springboot-onboarding

spring.datasource.url=jdbc:postgresql://localhost:5432/springboot_onboarding
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Replace `YOUR_PASSWORD` with your PostgreSQL password.


## Root Directory

| File / Folder | Purpose |
|---|---|
| `.mvn/` | Maven Wrapper configuration |
| `mvnw` | Maven Wrapper script for Linux/macOS |
| `mvnw.cmd` | Maven Wrapper script for Windows |
| `pom.xml` | Maven project configuration and dependencies |
| `.gitignore` | Files and directories Git should ignore |
| `.gitattributes` | Git file handling configuration |
| `README.md` | Project overview and setup instructions
