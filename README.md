# Stock Tracker

A Spring Boot application for tracking stock prices and managing favorite stocks using the Alpha Vantage API. This project provides RESTful endpoints to fetch real-time stock data, historical prices, overviews, and manage a list of favorite stocks stored in an H2 database.

## Features

- **Real-time Stock Data**: Fetch current stock prices for any symbol (e.g., AAPL, GOOGL).
- **Stock Overview**: Retrieve detailed company information and key metrics.
- **Historical Data**: Get daily stock prices for a specified number of days.
- **Favorites Management**: Add stocks to favorites and retrieve them with live prices.
- **Database Integration**: Uses H2 in-memory/file database for persistence.
- **Caching**: Simple in-memory caching for improved performance.
- **H2 Console**: Web-based database console for easy data inspection.

## Prerequisites
- Java 17+
- Maven 3.6+
- Alpha Vantage API key

## Setup
1. Clone repo and cd into it.
2. Copy `application-example.properties` to `application.properties` and set your API key.
3. Run `./mvnw clean install && ./mvnw spring-boot:run`.
4. App runs on `http://localhost:8080`.

## Configuration
- API: `alpha.vantage.api.key` (your key)
- DB: H2 at `jdbc:h2:file:./data/testdb` (console: `/h2-console`)
- JPA: Auto-update schema, show SQL.

## Usage
Endpoints under `/api/v1/stocks`:
- `GET /{symbol}`: Get stock data (e.g., AAPL).
- `GET /{symbol}/overview`: Get overview.
- `GET /{symbol}/history?days=30`: Get history.
- `POST /favourites`: Add favorite (body: `{"symbol": "AAPL"}`).
- `GET /favourites`: Get favorites with prices.

Example: `curl -X GET "http://localhost:8080/api/v1/stocks/AAPL"`

## Project Structure
- `controller/`: REST endpoints
- `service/`: Business logic
- `client/`: API client
- `dto/`: Data objects
- `entity/`: DB entities
- `repository/`: Data access
- `config/`: WebClient setup

## Dependencies
- Spring Boot (Web, JPA, WebFlux)
- H2, Lombok

## Testing
Run `./mvnw test`.

## Contributing
Fork, branch, test, PR.

## License
MIT.

## Troubleshooting
- Check API key/quota.
- Verify H2 console.
- Change port if needed.
