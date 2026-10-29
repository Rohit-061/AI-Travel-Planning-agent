# AI Travel Planning Agent

A full-stack travel planning platform that generates personalized, day-by-day
itineraries based on a traveller's interests, budget, dates, and pace.

- **Backend:** Spring Boot 3 (Java 17) REST API
- **Frontend:** React 18
- **Database:** MongoDB (stores generated itineraries)
- **"AI" layer:** a transparent, rule-based recommendation engine that scores
  destinations against interests/budget and assembles a day-by-day plan from
  an activity template bank — see `ItineraryGenerationService.java`. It's
  built so the scoring/matching logic can be swapped for a real ML or LLM
  ranking model later without touching the controllers or frontend.

## Project structure

```
ai-travel-planner/
├── backend/                Spring Boot REST API
│   └── src/main/java/com/travelplanner/
│       ├── model/          Destination, Flight, Hotel, Activity, DayPlan, Itinerary
│       ├── dto/             TripRequest (incoming preferences)
│       ├── repository/      ItineraryRepository (Spring Data MongoDB)
│       ├── service/         DestinationService, FlightService, HotelService,
│       │                    ItineraryGenerationService (core logic)
│       ├── controller/      REST endpoints
│       └── config/          CORS + global exception handling
└── frontend/                React app
    └── src/
        ├── components/      PreferenceForm, ItineraryResult
        ├── api/              Axios client
        └── App.js
```

## Prerequisites

- Java 17+
- Maven 3.8+ (or use your IDE's built-in Maven)
- Node.js 18+ and npm
- MongoDB running locally on `mongodb://localhost:27017` (or update
  `backend/src/main/resources/application.properties`)

## Running the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Key endpoints:

| Method | Endpoint                              | Description                         |
|--------|----------------------------------------|--------------------------------------|
| GET    | `/api/destinations`                   | List all destinations                |
| GET    | `/api/destinations/{code}`            | Get one destination                  |
| GET    | `/api/destinations/recommend`         | Rank destinations by interests/budget|
| GET    | `/api/flights/search`                 | Search mock flights                  |
| GET    | `/api/hotels/search`                  | Search mock hotels                   |
| POST   | `/api/itineraries/generate`           | Generate & save a personalized trip  |
| GET    | `/api/itineraries/{id}`               | Fetch a saved itinerary              |
| GET    | `/api/itineraries/user/{userId}`      | List a user's itineraries            |

Example request body for `POST /api/itineraries/generate`:

```json
{
  "userId": "guest",
  "originCode": "NYC",
  "destinationCode": "",
  "startDate": "2026-10-10",
  "numberOfDays": 5,
  "budgetUsd": 1500,
  "interests": ["culture", "food", "history"],
  "pace": "balanced"
}
```

Leave `destinationCode` blank to let the engine pick the best-matching
destination for you automatically.

## Running the frontend

```bash
cd frontend
npm install
npm start
```

The app opens on `http://localhost:3000` and talks to the backend at
`http://localhost:8080/api` by default. Override this with an `.env` file:

```
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## Swapping mock data for real providers

`FlightService`, `HotelService`, and `DestinationService` currently return
generated mock data so the whole flow works without external API keys. To
plug in real providers (e.g. Amadeus, Skyscanner, Booking.com):

1. Replace the method bodies in those three services with `RestTemplate` /
   `WebClient` calls to the real provider APIs.
2. Keep the method signatures the same — controllers and the itinerary
   generator don't need to change.
3. Add the provider's API key to `application.properties` (or better, an
   environment variable) and inject it via `@Value`.

## Notes

- This was built and reviewed for correctness in a sandboxed environment
  without internet access to Maven Central, so run `mvn spring-boot:run`
  once locally to pull dependencies and confirm a clean build.
- Lombok is used for boilerplate getters/setters — make sure annotation
  processing is enabled in your IDE.
