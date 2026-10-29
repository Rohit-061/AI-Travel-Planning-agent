package com.travelplanner.service;

import com.travelplanner.model.Destination;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Stands in for a third-party "destination info" REST API.
 * Swap the body of getAllDestinations() for a real HTTP client call
 * (e.g. RestTemplate/WebClient hitting a provider like Amadeus or TripAdvisor)
 * without touching any calling code.
 */
@Service
public class DestinationService {

    private final List<Destination> catalogue = List.of(
        new Destination("PAR", "Paris", "France",
            List.of("culture", "food", "romantic", "history"), 180, 95, "Apr-Jun",
            "The City of Light — world-class museums, cafes, and architecture."),
        new Destination("BKK", "Bangkok", "Thailand",
            List.of("food", "culture", "nightlife", "budget"), 60, 88, "Nov-Feb",
            "Vibrant street food scene, temples, and buzzing markets."),
        new Destination("ROM", "Rome", "Italy",
            List.of("history", "culture", "food", "romantic"), 150, 93, "Apr-Jun",
            "Ancient ruins, world-famous cuisine, and timeless piazzas."),
        new Destination("BAL", "Bali", "Indonesia",
            List.of("beach", "relaxation", "adventure", "nature"), 70, 90, "Apr-Oct",
            "Lush rice terraces, surf breaks, and laid-back beach towns."),
        new Destination("NYC", "New York City", "USA",
            List.of("nightlife", "culture", "food", "shopping"), 250, 92, "Sep-Nov",
            "Iconic skyline, Broadway shows, and every cuisine imaginable."),
        new Destination("TOK", "Tokyo", "Japan",
            List.of("culture", "food", "technology", "history"), 160, 94, "Mar-May",
            "A dazzling mix of neon-lit streets and centuries-old temples."),
        new Destination("BAR", "Barcelona", "Spain",
            List.of("beach", "culture", "nightlife", "food"), 130, 89, "May-Jun",
            "Gaudi architecture, Mediterranean beaches, and tapas culture."),
        new Destination("CPT", "Cape Town", "South Africa",
            List.of("nature", "adventure", "beach", "wildlife"), 90, 85, "Nov-Mar",
            "Table Mountain, wine country, and dramatic coastlines."),
        new Destination("MRK", "Marrakech", "Morocco",
            List.of("culture", "history", "adventure", "budget"), 65, 82, "Mar-May",
            "Bustling souks, desert excursions, and ornate palaces."),
        new Destination("QUE", "Queenstown", "New Zealand",
            List.of("adventure", "nature", "relaxation"), 140, 87, "Dec-Feb",
            "The adventure capital of the world, framed by alpine lakes.")
    );

    public List<Destination> getAllDestinations() {
        return catalogue;
    }

    public Optional<Destination> findByCode(String code) {
        return catalogue.stream()
            .filter(d -> d.getCode().equalsIgnoreCase(code))
            .findFirst();
    }

    /**
     * Ranks destinations by how well they match the traveller's interests and budget.
     * Score = (tag overlap weight) + (popularity weight) - (budget mismatch penalty).
     */
    public List<Destination> recommend(List<String> interests, double dailyBudgetUsd) {
        return catalogue.stream()
            .sorted(Comparator.comparingDouble(
                (Destination d) -> -matchScore(d, interests, dailyBudgetUsd)))
            .toList();
    }

    public Destination bestMatch(List<String> interests, double dailyBudgetUsd) {
        return recommend(interests, dailyBudgetUsd).get(0);
    }

    private double matchScore(Destination d, List<String> interests, double dailyBudgetUsd) {
        long overlap = d.getTags().stream()
            .filter(t -> interests.stream().anyMatch(i -> i.equalsIgnoreCase(t)))
            .count();
        double interestScore = overlap * 30.0;
        double popularityScore = d.getPopularityScore() * 0.3;
        double budgetPenalty = d.getAvgDailyCostUsd() > dailyBudgetUsd
            ? (d.getAvgDailyCostUsd() - dailyBudgetUsd) * 0.5
            : 0;
        return interestScore + popularityScore - budgetPenalty;
    }
}
