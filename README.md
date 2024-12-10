# ticket-in-server

This project is the backend for the TicketIn application, a ticket booking system developed as part of a coursework project at the University of Westminster for the OOP module. The backend is built using Java and Spring Boot, and it provides RESTful APIs and WebSocket endpoints for managing events, tickets, customers, and vendors. [ticket-in-client →](https://github.com/sameerasw/ticket-in-client)


## Technologies Used

- **Java**: Programming language used for development.
- **Spring Boot**: Framework for building the backend application.
- **Spring Data JPA**: For database interactions.
- **PostgreSQL**: Database used for storing data. (Using NeonPostgre)
- **WebSocket**: For real-time communication.
- **Maven**: Build and dependency management tool.

## Setup Guide

### Prerequisites

- Java 17 or higher
- Maven 3.9.9 or higher
- PostgreSQL database

### Steps

1. **Clone the repository**:
    ```sh
    git clone https://github.com/sameerasw/ticket-in-server.git
    cd ticket-in-server
    ```

2. **Set up the database**:
    - Recommended to use NeonPostgre SQL database for a similar integration.
    - Create a PostgreSQL database named `ticketin`.
    - Update the database connection details in the `.env` file.

3. **Configure environment variables**:
    - Create a `.env` file in the root directory with the following content:
        ```dotenv
        SPRING_DATASOURCE_URL=jdbc:postgresql://[database-name].aws.neon.tech/ticketin?sslmode=require
        SPRING_DATASOURCE_USERNAME=<your-database-username>
        SPRING_DATASOURCE_PASSWORD=<your-database-password>
        SERVER_PORT=1245
        SERVER_ADDRESS=0.0.0.0
        DATABSE_ACTION=update
        ```

4. **Build and run the application**:
    ```sh
    mvn clean install
    mvn spring-boot:run
    ```

## API Documentation (WIP)

### Authentication

- **POST /api/auth/login**
    - Request Body:
        ```json
        {
            "email": "user@example.com",
            "password": "password",
            "userType": "CUSTOMER" // or "VENDOR"
        }
        ```
    - Response:
        ```json
        {
            "token": "generated-token",
            "type": "Bearer",
            "userId": 1,
            "name": "User Name",
            "email": "user@example.com",
            "userType": "CUSTOMER"
        }
        ```

### Customers

- **GET /customers/{customerId}/buy/{eventItemId}**
    - Response: `String` (Ticket purchased)

- **GET /customers/{customerId}/tickets**
    - Response: `List<TicketDTO>`

### Vendors

- **GET /vendors/{eventId}/release**
    - Request Params: `ticketCount`
    - Response: `String` (Tickets released)

### Events

- **POST /events**
    - Request Body:
        ```json
        {
            "eventName": "Event Name",
            "eventLocation": "Location",
            "eventDate": "2023-12-31",
            "eventTime": "18:00",
            "ticketPrice": 50.0,
            "details": "Event details",
            "image": "image-url",
            "vendorId": 1,
            "vendorName": "Vendor Name"
        }
        ```
    - Response: `EventItemDTO`

- **GET /events/list**
    - Response: `List<EventItemDTO>`

- **GET /events/{vendorId}/list**
    - Response: `List<EventItemDTO>`

- **GET /events/{eventId}**
    - Response: `EventItemDTO`

## WebSocket Documentation

### Ticket Purchases

- **Endpoint**: `/ws/event/{eventId}/purchases`
- **Description**: Receives real-time updates when tickets are purchased for a specific event.
- **Message Format**: `String` (e.g., "Ticket (1) was purchased by Customer Name")

### Ticket Availability

- **Endpoint**: `/ws/event/{eventId}/tickets`
- **Description**: Receives real-time updates on the number of available tickets for a specific event.
- **Message Format**: `String` (e.g., "10 tickets available")

## Environment Setup

### Environment Variables

- **SPRING_DATASOURCE_URL**: JDBC URL for the PostgreSQL database.
- **SPRING_DATASOURCE_USERNAME**: Username for the PostgreSQL database.
- **SPRING_DATASOURCE_PASSWORD**: Password for the PostgreSQL database.
- **SERVER_PORT**: Port on which the server will run.
- **SERVER_ADDRESS**: Address on which the server will run.
- **DATABSE_ACTION**: Action to be performed on the database (e.g., `update`).

### Example `.env` File

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://aa-bbbbb-ccc-sdafsdafs.ap-southeast-1.aws.neon.tech/ticketin?sslmode=require
SPRING_DATASOURCE_USERNAME=ticketin_owner
SPRING_DATASOURCE_PASSWORD=samplepass
SERVER_PORT=1245
SERVER_ADDRESS=0.0.0.0
DATABSE_ACTION=update
```

## Additional Details

- **Logging**: The application logs are configured to be stored in the `./logs` directory. The logging configuration can be found in the `application.properties` file.
- **Concurrency Handling**: The application uses `ReentrantLock` to handle concurrent ticket purchases and releases.
- **Simulations**: The application includes simulation classes (`VendorSimulation` and `CustomerSimulation`) to simulate vendor and customer activities.

For further details, please refer to the source code and the provided documentation.

---
This project was developed by [@sameerasw](https://github.com/sameerasw) as part of my coursework at University of
Westminster, OOP module. 
