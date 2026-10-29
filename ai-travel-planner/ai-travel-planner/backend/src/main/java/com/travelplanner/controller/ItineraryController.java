package com.travelplanner.controller;

import com.travelplanner.dto.TripRequest;
import com.travelplanner.model.Itinerary;
import com.travelplanner.repository.ItineraryRepository;
import com.travelplanner.service.ItineraryGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController {

    private final ItineraryGenerationService generationService;
    private final ItineraryRepository itineraryRepository;

    public ItineraryController(ItineraryGenerationService generationService,
                                ItineraryRepository itineraryRepository) {
        this.generationService = generationService;
        this.itineraryRepository = itineraryRepository;
    }

    @PostMapping("/generate")
    public Itinerary generate(@Valid @RequestBody TripRequest request) {
        return generationService.generate(request);
    }

    @GetMapping("/{id}")
    public Itinerary getById(@PathVariable String id) {
        return itineraryRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Itinerary not found: " + id));
    }

    @GetMapping("/user/{userId}")
    public List<Itinerary> getByUser(@PathVariable String userId) {
        return itineraryRepository.findByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        itineraryRepository.deleteById(id);
    }
}
