# crypto-market-ranker

Prosty mikroserwis tworzący ranking rynków na podstawie aktualnego spreadu z publicznego API Kanga.

## Stack
- Java 25
- Spring 4.0.2

## Uruchomienie

- Docker: `docker compose up -d --build`
- Lokalnie: `mvn clean package` `java -jar target/crypto-market-ranker-0.0.1-SNAPSHOT.jar`
- Testy: `mvn test`

## Api

- `POST /api/spread/calculate` – wylicza ranking i zapisuje go w pamięci [CURL](calculate.http)
    ```
    POST http://localhost:8080/api/spread/calculate
    Authorization: Bearer ABC123
    ```
- `GET  /api/spread/ranking` – zwraca ostatni zapisany ranking (404 jeśli brak) [CURL](ranking.http)
    ```
    GET http://localhost:8080/api/spread/ranking
    Authorization: Bearer ABC123
    ```

## Źródła danych (Kanga API)

- Lista rynków: `GET https://public.kanga.exchange/api/market/pairs`
- Orderbook: `GET https://public.kanga.exchange/api/market/orderbook/{market}`
