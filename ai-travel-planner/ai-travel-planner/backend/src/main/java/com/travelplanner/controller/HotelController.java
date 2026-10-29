package com.travelplanner.controller;

import com.travelplanner.model.Hotel;
import com.travelplanner.service.HotelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/search")
    public List<Hotel> search(@RequestParam String destinationCode,
                               @RequestParam(defaultValue = "150") double dailyBudgetUsd) {
        return hotelService.searchHotels(destinationCode, dailyBudgetUsd);
    }
}
