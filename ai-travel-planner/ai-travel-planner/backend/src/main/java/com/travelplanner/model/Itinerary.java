package com.travelplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Persisted, generated itinerary for a user's trip.
 * Stored in MongoDB so users can revisit / retrieve past trips.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "itineraries")
public class Itinerary {

    @Id
    private String id;

    private String userId;
    private String destinationCity;
    private String destinationCountry;
    private String originCode;
    private String startDate;
    private int numberOfDays;
    private double budgetUsd;
    private List<String> interests;

    private Flight outboundFlight;
    private Flight returnFlight;
    private List<DayPlan> days;

    private double estimatedTotalCostUsd;
    private double budgetRemainingUsd;

    private Instant createdAt;
}
