package com.travelplanner.service;

import com.travelplanner.model.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Stands in for a real hotel-search REST API (e.g. Booking.com, Expedia Rapid).
 * Generates a small, budget-tiered inventory per destination.
 */
@Service
public class HotelService {

    public List<Hotel> searchHotels(String destinationCode, double dailyBudgetUsd) {
        double base = Math.max(30, dailyBudgetUsd * 0.4);

        return List.of(
            new Hotel(id(), "Central Comfort Inn", destinationCode,
                round(base * 0.6), 3.8, List.of("WiFi", "Breakfast"), "City Center"),
            new Hotel(id(), "The Grand " + destinationCode, destinationCode,
                round(base * 1.3), 4.5, List.of("WiFi", "Pool", "Gym", "Breakfast"), "Downtown"),
            new Hotel(id(), destinationCode + " Boutique Stay", destinationCode,
                round(base * 0.9), 4.2, List.of("WiFi", "Rooftop Bar"), "Old Town")
        );
    }

    /** Picks the best hotel that fits within the given per-night budget, preferring higher rating. */
    public Hotel bestWithinBudget(String destinationCode, double dailyBudgetUsd) {
        return searchHotels(destinationCode, dailyBudgetUsd).stream()
            .filter(h -> h.getPricePerNightUsd() <= dailyBudgetUsd)
            .max((a, b) -> Double.compare(a.getRating(), b.getRating()))
            .orElseGet(() -> searchHotels(destinationCode, dailyBudgetUsd).stream()
                .min((a, b) -> Double.compare(a.getPricePerNightUsd(), b.getPricePerNightUsd()))
                .orElseThrow());
    }

    private String id() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
