package com.travelplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    private String id;
    private String name;
    private String destinationCode;
    private double pricePerNightUsd;
    private double rating;         // 0-5
    private List<String> amenities;
    private String neighborhood;
}
