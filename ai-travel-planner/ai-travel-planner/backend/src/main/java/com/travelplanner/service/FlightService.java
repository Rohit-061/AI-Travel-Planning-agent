package com.travelplanner.service;

import com.travelplanner.model.Flight;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Stands in for a real flight-search REST API (e.g. Amadeus, Skyscanner).
 * Generates deterministic-ish mock flights so the same origin/destination
 * pair returns a stable, plausible-looking result set.
 */
@Service
public class FlightService {

    private static final List<String> AIRLINES =
        List.of("SkyLink Air", "Continental Wings", "Horizon Airlines", "Pacific Express");

    public List<Flight> searchFlights(String originCode, String destinationCode, String date) {
        Random rnd = new Random((originCode + destinationCode + date).hashCode());

        return List.of(
            buildFlight(rnd, originCode, destinationCode, "Economy"),
            buildFlight(rnd, originCode, destinationCode, "Economy"),
            buildFlight(rnd, originCode, destinationCode, "Premium Economy")
        );
    }

    public Flight cheapestFlight(String originCode, String destinationCode, String date) {
        return searchFlights(originCode, destinationCode, date).stream()
            .min((a, b) -> Double.compare(a.getPriceUsd(), b.getPriceUsd()))
            .orElseThrow();
    }

    private Flight buildFlight(Random rnd, String origin, String destination, String cabinClass) {
        double basePrice = 200 + rnd.nextInt(600);
        if (cabinClass.equals("Premium Economy")) basePrice *= 1.6;
        int stops = rnd.nextInt(2);
        int departHour = 6 + rnd.nextInt(14);
        int durationHours = 2 + rnd.nextInt(12);

        return new Flight(
            AIRLINES.get(rnd.nextInt(AIRLINES.size())).substring(0, 2).toUpperCase()
                + (100 + rnd.nextInt(900)),
            AIRLINES.get(rnd.nextInt(AIRLINES.size())),
            origin,
            destination,
            String.format("%02d:00", departHour),
            String.format("%02d:00", (departHour + durationHours) % 24),
            Math.round(basePrice * 100.0) / 100.0,
            cabinClass,
            stops
        );
    }
}
