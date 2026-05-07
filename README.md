# Water Bill Calculator API

A Spring Boot 3.4 learning project for calculating residential water bills based on consumption ratios and guest counts.

## Tech Stack
* **Java 21**
* **Spring Boot 3.4 (Web, Data JPA, Actuator)**
* **SQL Server 2022** (Running in Docker)
* **Lombok** & **Maven**

## How to Run
1. **Start the Database:**
   ```bash
   docker-compose up -d

2. **Run the Application:**
    ```bash
    ./mvnw spring-boot:run

**API Usage**
Endpoint: POST /api/waterbill/calculate

Header: x-api-key: water123

Sample Request:
    
    JSON
    {
    "bhk": 2,
    "ratio": "2:3",
    "guests": 5
    }

