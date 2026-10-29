package com.travelplanner.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * What the frontend sends when a user asks the agent to build a trip.
 */
@Data
public class TripRequest {

    private String userId; // optional, "guest" if absent

    @NotBlank
    private String originCode;

    /** Optional - if blank, the agent picks the best-matching destination for the interests/budget. */
    private String destinationCode;

    @NotBlank
    private String startDate; // yyyy-MM-dd

    @Min(1)
    private int numberOfDays;

    @Positive
    private double budgetUsd;

    @NotEmpty
    private List<String> interests; // e.g. ["culture", "food", "adventure"]

    private String pace; // "relaxed" | "balanced" | "packed" - defaults to balanced
}
