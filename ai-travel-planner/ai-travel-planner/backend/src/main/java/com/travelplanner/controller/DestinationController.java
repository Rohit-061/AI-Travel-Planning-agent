package com.travelplanner.controller;

import com.travelplanner.model.Destination;
import com.travelplanner.service.DestinationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping
    public List<Destination> getAll() {
        return destinationService.getAllDestinations();
    }

    @GetMapping("/{code}")
    public Destination getByCode(@PathVariable String code) {
        return destinationService.findByCode(code)
            .orElseThrow(() -> new java.util.NoSuchElementException("Destination not found: " + code));
    }

    @GetMapping("/recommend")
    public List<Destination> recommend(@RequestParam List<String> interests,
                                        @RequestParam(defaultValue = "150") double dailyBudgetUsd) {
        return destinationService.recommend(interests, dailyBudgetUsd);
    }
}
