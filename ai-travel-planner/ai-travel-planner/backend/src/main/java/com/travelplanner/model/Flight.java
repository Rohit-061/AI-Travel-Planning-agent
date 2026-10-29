package com.travelplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    private String flightNumber;
    private String airline;
    private String originCode;
    private String destinationCode;
    private String departTime;   // ISO-like display string, mock data
    private String arriveTime;
    private double priceUsd;
    private String cabinClass;
    private int stops;
}
