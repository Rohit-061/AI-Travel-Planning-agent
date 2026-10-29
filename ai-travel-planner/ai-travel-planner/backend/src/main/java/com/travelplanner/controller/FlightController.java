package com.travelplanner.controller;

import com.travelplanner.model.Flight;
import com.travelplanner.service.FlightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/search")
    public List<Flight> search(@RequestParam String originCode,
                                @RequestParam String destinationCode,
                                @RequestParam String date) {
        return flightService.searchFlights(originCode, destinationCode, date);
    }
}
