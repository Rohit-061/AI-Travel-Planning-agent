package com.travelplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a travel destination that can be recommended to a user.
 * In production this would be backed by a real destinations API;
 * here it is served from a curated in-memory catalogue (see DestinationService).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destination {

    private String code;          // e.g. "PAR"
    private String city;
    private String country;
    private List<String> tags;    // e.g. ["culture", "romantic", "food"]
    private double avgDailyCostUsd;
    private double popularityScore; // 0-100, used for ranking
    private String bestSeason;    // e.g. "Apr-Jun"
    private String description;
}
