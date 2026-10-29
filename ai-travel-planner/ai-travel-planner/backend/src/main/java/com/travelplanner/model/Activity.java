package com.travelplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private String timeOfDay;   // "Morning" | "Afternoon" | "Evening"
    private String title;
    private String description;
    private String category;    // matches an interest tag, e.g. "culture"
    private double estimatedCostUsd;
}
