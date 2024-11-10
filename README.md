# trade-assignment
 assignment
 Project Structure
Java Application: Spring Boot application with endpoints for trade management, using Kafka for streaming and both SQL and NoSQL databases for storage.
SQL Database: For structured trade data storage (e.g., MySQL or PostgreSQL).
NoSQL Database: For high-frequency data access or other requirements (e.g., MongoDB).
Kafka: To simulate trade data streams.
Testing: JUnit tests for all functionalities, using a Test-Driven Development (TDD) approach.
GitHub Actions: CI/CD pipeline with stages for testing, building, security scanning, and deployment.

##Key Components
#Model Class (Trade.java): Define the Trade entity, including fields for trade ID, version, counter-party ID, book ID, maturity date, created date, and expiration status.

#Controller(TradeController.java):Expose endpoints to create, update, and retrieve trades.

#Service (TradeService.java): Implements main processing and validation logic.
    Implement validations:
        Reject trades with lower versions.
        Reject trades with a past maturity date.
        Auto-mark expired trades.

#Repository Interface: Use Spring Data JPA for SQL database interaction.Use Spring Data MongoDB for NoSQL database interaction

#Kafka (producer/Consumer files -KafkaConfig.java, TradeStoreConsumer.java) ):Implement a Kafka consumer to listen for incoming trade data and process it based on the business rules in the problem statement.

#Configuration files (DataseConfig): Boiler plate code to connect to the DB through the attributes mentioned in application.properties 
Junit Test (TradeStoreApplicationTests) : Junit test class covering all positive and negative scenarios based on the validation rule

#DB script to create a TRADES table in DB
CREATE TABLE trades (
    trade_id VARCHAR(10) NOT NULL,
    version INT NOT NULL,
    counter_party_id VARCHAR(10),
    book_id VARCHAR(10),
    maturity_date DATE CHECK (maturity_date >= CURRENT_DATE),
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expired BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (trade_id, version)
);

#Class and design diagram have been added as(.png) file generated through AppMap plugin is Visual stidio IDE.

