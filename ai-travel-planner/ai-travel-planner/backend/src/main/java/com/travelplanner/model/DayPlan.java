package com.travelplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayPlan {

    private int dayNumber;
    private String date;              // display string, derived from trip start date
    private List<Activity> activities;
    private Hotel hotel;
    private double estimatedDayCostUsd;
}
