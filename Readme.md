# Vehicle API

A Spring Boot RESTful API for managing vehicle information. Perform CRUD operations on vehicles identified by their
unique VIN (Vehicle Identification Number).

---

## Features

- **CRUD Operations**: Create, read, update, and delete vehicles.
- **Validation**: Ensures input data meets defined constraints.
- **Exception Handling**: Provides meaningful error messages.
- **Batch Processing**: Create multiple vehicles in a single request.
- **RESTful Endpoints**: Standard HTTP methods and status codes.

---

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven**
- **Git**

### Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/vehicle-api.git
   cd vehicle-api

2. **Build Application**

     ```bash
    mvn clean install
   
3. **Run Application**
    ```bash
   mvn spring-boot:run

4. **API Endpoints**

    ```bash
   http://localhost:8080/api/vehicles

 - **Create a Vehicle**

   - Endpoint: `/api/vehicles`

   - Method: `POST`

   - Description: Creates a new vehicle.

   - Request Body Example:

          {
          "vin": "1HGCM82633A123456",
          "manufacturerName": "Honda",
          "description": "Reliable sedan",
          "horsePower": 180,
          "modelName": "Accord",
          "modelYear": 2020,
          "purchasePrice": 22000.00,
          "fuelType": "Gasoline"
          }
   - Responses:

       - `201 Created`: Vehicle created successfully.

       - `400 Bad Request`: VIN already exists or invalid data.

       - `422 Unprocessable Entity`: Validation failed.

- **Get All Vehicles**

  - Endpoint: `/api/vehicles`

  - Method: `GET`

  - Description: Retrieves a list of all vehicles.

    - Response Example:
      ```
      [
        {
        "vin": "1HGCM82633A123456",
        "manufacturerName": "Honda",
        "description": "Reliable sedan",
        "horsePower": 180,
        "modelName": "Accord",
        "modelYear": 2020,
        "purchasePrice": 22000.00,
        "fuelType": "Gasoline"
        },
        {
        "vin": "1HGCM82633A654321",
        "manufacturerName": "Toyota",
        "description": "Compact car",
        "horsePower": 150,
        "modelName": "Corolla",
        "modelYear": 2019,
        "purchasePrice": 18000.00,
        "fuelType": "Gasoline"
        }
      ]
  - Response Code: `200 OK`


- **Get Vehicle by VIN**
  - Endpoint: `/api/vehicles/{vin}`
  - Method: `GET`
  - Description: Retrieves a vehicle by its VIN.
  - Path Variable:
    - `vin`: The VIN of the vehicle to retrieve.
  - Responses:
    - `200 OK`: Vehicle found.
    - `404 Not Found`: Vehicle not found.

- **Update Vehicle**
  - Endpoint: `/api/vehicles/{vin}`
  - Method: `PUT`
  - Description: Updates an existing vehicle
  - Path Variable:
    - `vin`: The VIN of the vehicle to retrieve.
    - Response Example:
      ```

        {
        "vin": "1HGCM82633A123456",
        "manufacturerName": "Honda",
        "description": "Reliable sedan",
        "horsePower": 180,
        "modelName": "Accord",
        "modelYear": 2020,
        "purchasePrice": 22000.00,
        "fuelType": "Gasoline"
        }


  - Responses:
    - `200 OK`: Vehicle found.
    - `404 Not Found`: Vehicle not found.
    -  `422 Unprocessable Entity`: Validation failed

- **Delete Vehicle**
  - Endpoint: `/api/vehicles/{vin}`
  - Method: `DELETE`
  - Description: Deletes a vehicle by its VIN.
  - Path Variable:
    - `vin`: The VIN of the vehicle to retrieve.
  - Responses:
    - `204 No Content`: Vehicle deleted successfully.
    - `404 Not Found`: Vehicle not found.


5. **Exception Handling**

The API includes robust exception handling to provide meaningful error messages and appropriate HTTP status codes.

 - Duplicate VIN:

   - Exception: `IllegalStateException`
   - Status Code: `400 Bad Request`
   - Message: `Vehicle with VIN {vin} already exists`
   - Validation Errors:
      - Exception: `MethodArgumentNotValidException`
      - Status Code: `422 Unprocessable Entity`
      - Response:
        ```
        {
          "errors": {
          "field": "error message"
          }
        }
- Vehicle Not Found:
  - Status Code: `404 Not Found`
- Data Integrity Violation:
  - Exception: `DataIntegrityViolationException`
  - Status Code: `400 Bad Request`
  - Message: `Invalid data or null value`

6. **Testing**
- Running Unit Tests
  - The application includes unit tests using **JUnit** and **MockMvc**.
  - bash command:
       ```
       mvn test
- What the test covers
    - Successful creation, retrieval, update, and deletion of vehicles.
    - Handling of duplicate VINs.
    - Validation of input data.
    - Exception handling.

7. **Technologies Used**
- **Java 17**: The programming language used.
- **Spring Boot**: Framework for building the application.
- **Spring Data JPA**: For database interactions.
- **Hibernate**: ORM tool used under the hood by Spring Data JPA.
- **H2 Database**: In-memory database for development and testing.
- **Maven**: Build and dependency management.
- **JUnit 5**: Testing framework.
- **MockMVC**: For mocking dependencies in tests.
- **Jackson**: For JSON serialization/deserialization.
- **Swagger/OpenAPI**: API documentation annotations.