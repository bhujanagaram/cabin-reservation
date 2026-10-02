# Cabin Reservation System

A Kotlin + Spring Boot web application for reserving cruise cabins and storing passenger assignments in PostgreSQL.

## Overview

The application allows users to:

- Select a deck.
- View only cabins that still have available capacity.
- Select a cabin.
- Enter one or two passenger details.
- Reserve the selected cabin.
- Store passenger and cabin assignments in PostgreSQL.
- Prevent invalid reservations based on cabin capacity and passenger age rules.
- Display success and error messages in the browser.
- Use a styled, centered reservation form.

## Technology Stack

- Kotlin 2.3.21
- Java 17
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- HTML / CSS / JavaScript
- Gradle

## Current Application URLs

Application:

```text
http://localhost:8080/
```

Available cabin API:

```text
GET http://localhost:8080/api/cabins/available?deckNumber=1
```

All cabins API:

```text
GET http://localhost:8080/api/cabins
```

Reservation API:

```text
POST http://localhost:8080/api/reservations
```

## PostgreSQL Configuration

The application is currently connected to PostgreSQL using:

```text
Database: cabin_reservation
Host: localhost
Port: 5433
```

Example `application.properties`:

```properties
spring.application.name=cabin-reservation

spring.datasource.url=jdbc:postgresql://localhost:5433/cabin_reservation
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

Replace `YOUR_POSTGRES_PASSWORD` with your local PostgreSQL password.

> Do not commit a real database password to a public Git repository.

## Gradle Dependencies

The project includes Spring Web, Spring Data JPA, PostgreSQL, Kotlin reflection, and testing dependencies.

Important dependencies:

```kotlin
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
runtimeOnly("org.postgresql:postgresql")
```

The Kotlin JPA plugin should also be enabled:

```kotlin
kotlin("plugin.jpa") version "2.3.21"
```

## Project Structure

```text
src/
└── main/
    ├── kotlin/
    │   └── com/
    │       └── cruise/
    │           └── cabin_reservation/
    │               ├── CabinReservationApplication.kt
    │               ├── controller/
    │               │   └── CabinReservationController.kt
    │               ├── model/
    │               │   ├── Cabin.kt
    │               │   ├── Passenger.kt
    │               │   ├── PassengerRequest.kt
    │               │   └── ReservationRequest.kt
    │               ├── repository/
    │               │   ├── CabinRepository.kt
    │               │   └── PassengerRepository.kt
    │               └── service/
    │                   └── CabinReservationService.kt
    │
    └── resources/
        ├── application.properties
        └── static/
            └── index.html
```

## Cabin Setup

The application initializes cabins automatically when the database does not already contain them.

There are:

- 3 decks
- 10 cabins per deck
- 30 cabins total
- Capacity of 2 passengers per cabin

Cabin numbers follow this pattern:

```text
Deck 1: D1C1 ... D1C10
Deck 2: D2C1 ... D2C10
Deck 3: D3C1 ... D3C10
```

Example initialization logic:

```kotlin
for (deck in 1..3) {
    for (cabin in 1..10) {
        cabins.add(
            Cabin(
                cabinNumber = "D${deck}C$cabin",
                deckNumber = deck
            )
        )
    }
}
```

## Database Tables

Hibernate creates the database tables from the JPA entities.

### cabins

Typical columns:

```text
id
cabin_number
deck_number
capacity
```

### passengers

Typical columns:

```text
id
name
age
cabin_id
```

`cabin_id` is a foreign key referencing the `cabins` table.

Example relationship:

```text
cabins
  id = 19
  cabin_number = D2C9
  deck_number = 2

passengers
  name = Danny
  age = 50
  cabin_id = 19
```

The value `19` is the database primary key of cabin `D2C9`. It does not mean "Cabin 19."

## Reservation Rules

### Cabin Capacity

Each cabin can hold a maximum of 2 passengers.

If a cabin does not have enough remaining capacity, the reservation is rejected.

Example:

```text
Cabin capacity: 2
Existing passengers: 1
New passengers requested: 2
Result: Reservation rejected
```

### Passenger Age Rule

If any passenger is under 14 years old, at least one passenger in the same reservation must be 18 years old or older.

Examples:

```text
Age 12 + Age 10  -> Rejected
Age 16 + Age 10  -> Rejected
Age 18 + Age 10  -> Allowed
Age 35 + Age 3   -> Allowed
Age 20 + Age 17  -> Allowed
```

Example validation message:

```text
Reservation unsuccessful. Age requirement not met.
At least one adult aged 18 or older must accompany
any passenger under 14 years of age.
```

Invalid reservations are not stored in PostgreSQL.

## Available Cabin Filtering

The cabin dropdown does not use a hardcoded list.

When a deck is selected, the browser calls:

```text
GET /api/cabins/available?deckNumber={deckNumber}
```

The backend checks PostgreSQL and returns only cabins where:

```text
current passengers < cabin capacity
```

Example:

```text
D2C8  -> 0 passengers -> shown
D2C9  -> 2 passengers -> hidden
D2C10 -> 1 passenger  -> shown
```

The UI can display remaining capacity, for example:

```text
D2C8 - 2 seat(s) available
D2C10 - 1 seat(s) available
```

The backend still validates capacity during reservation, even if a cabin is shown in the dropdown.

## API Endpoints

### Get All Cabins

```http
GET /api/cabins
```

Example:

```text
http://localhost:8080/api/cabins
```

### Get Available Cabins by Deck

```http
GET /api/cabins/available?deckNumber=2
```

Returns cabins on the selected deck that still have available capacity.

### Create Reservation

```http
POST /api/reservations
Content-Type: application/json
```

Example request:

```json
{
  "cabinNumber": "D2C9",
  "passengers": [
    {
      "name": "Danny",
      "age": 50
    },
    {
      "name": "Ruth",
      "age": 3
    }
  ]
}
```

Example successful behavior:

```text
Reservation successful for cabin D2C9.
```

Example invalid age request:

```json
{
  "cabinNumber": "D1C3",
  "passengers": [
    {
      "name": "Katie",
      "age": 13
    },
    {
      "name": "Tim",
      "age": 14
    }
  ]
}
```

The request is rejected because there is a passenger under 14 and no adult aged 18 or older.

## Web UI

The reservation page is located at:

```text
src/main/resources/static/index.html
```

The page includes:

- Centered card layout
- Styled inputs and dropdowns
- Deck selection
- Dynamic cabin selection
- Passenger 1 details
- Optional Passenger 2 details
- Reservation button
- Success and error message areas
- Browser alert pop-ups for reservation success and failure

On an invalid reservation:

- The reservation is not saved.
- The selected deck remains selected.
- The selected cabin remains selected.
- Passenger details remain entered.
- An error pop-up is displayed.
- The same error is displayed on the page.

On a successful reservation:

- Passenger records are saved in PostgreSQL.
- The passenger records are linked to the selected cabin.
- A success pop-up is displayed.
- The form is reset.

## Repository Layer

### CabinRepository

The repository supports standard JPA operations and custom lookups:

```kotlin
interface CabinRepository : JpaRepository<Cabin, Long> {

    fun findByCabinNumber(cabinNumber: String): Cabin?

    fun findByDeckNumber(deckNumber: Int): List<Cabin>
}
```

### PassengerRepository

```kotlin
interface PassengerRepository : JpaRepository<Passenger, Long>
```

## Useful PostgreSQL Queries

### View all cabins

```sql
SELECT * FROM cabins;
```

### View all passengers

```sql
SELECT * FROM passengers;
```

### Verify a specific cabin

```sql
SELECT id, cabin_number, deck_number
FROM cabins
WHERE id = 19;
```

### View passengers with human-readable cabin details

```sql
SELECT
    p.id,
    p.name,
    p.age,
    c.cabin_number,
    c.deck_number
FROM passengers p
JOIN cabins c
    ON p.cabin_id = c.id;
```

## How to Run the Application

1. Start PostgreSQL.
2. Make sure the `cabin_reservation` database exists.
3. Verify the database settings in `application.properties`.
4. Open the project in IntelliJ IDEA.
5. Reload Gradle if dependencies were changed.
6. Run:

```text
CabinReservationApplicationKt
```

7. Confirm the console shows a successful PostgreSQL connection.
8. Confirm Tomcat starts on port `8080`.
9. Open:

```text
http://localhost:8080/
```

## Expected Startup Messages

A successful startup should include messages similar to:

```text
Found 2 JPA repository interfaces.
```

```text
HikariPool-1 - Start completed.
```

```text
Database JDBC URL [jdbc:postgresql://localhost:5433/cabin_reservation]
```

```text
Tomcat started on port 8080
```

## Testing Scenarios

### Successful adult + child reservation

```text
Deck: 2
Cabin: D2C9
Passenger 1: Danny, 50
Passenger 2: Ruth, 3
Expected: Success
```

### Two children

```text
Passenger 1: 12
Passenger 2: 10
Expected: Rejected
```

### Teenager + child

```text
Passenger 1: 16
Passenger 2: 10
Expected: Rejected
```

### Adult + child

```text
Passenger 1: 18
Passenger 2: 10
Expected: Success
```

### Full cabin

```text
Cabin already contains 2 passengers
Expected:
- Cabin is not displayed in the available cabin dropdown.
- Backend also rejects an attempt to reserve it.
```

## Future Enhancements

Possible next improvements include:

- Reservation table with reservation IDs
- Reservation date and time
- Ability to cancel a reservation
- Ability to move passengers between cabins
- Passenger search
- Admin dashboard
- Cabin availability summary by deck
- Authentication and authorization
- Better confirmation modal instead of browser `alert()`
- Unit and integration tests
- Flyway or Liquibase database migrations
- Docker support
- Separate frontend and backend applications

## Summary

The current implementation provides a complete basic cabin reservation flow:

```text
User
  -> Web Form
  -> Spring Boot Controller
  -> Cabin Reservation Service
  -> Spring Data JPA Repository
  -> PostgreSQL
```

Cabin availability is driven by the database, reservation validation is enforced on the backend, and valid passenger assignments are persisted to PostgreSQL.
