package com.travelplanner.service;

import com.travelplanner.dto.TripRequest;
import com.travelplanner.model.*;
import com.travelplanner.repository.ItineraryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The core personalization engine.
 *
 * Given a set of preferences (interests, budget, dates, pace) it:
 *  1. Picks (or validates) a destination that best matches interests + budget.
 *  2. Selects outbound/return flights and a hotel that fit the budget.
 *  3. Builds a day-by-day plan, rotating through activity templates that
 *     match the traveller's interests, respecting the requested pace.
 *  4. Tracks running cost against the total budget.
 *
 * This is intentionally a transparent, rule-based recommender rather than a
 * black box — every choice can be traced back to a specific rule, which
 * makes it straightforward to swap in a real ML/LLM ranking model later
 * without changing the surrounding service contracts.
 */
@Service
public class ItineraryGenerationService {

    private final DestinationService destinationService;
    private final FlightService flightService;
    private final HotelService hotelService;
    private final ItineraryRepository itineraryRepository;

    private static final Map<String, List<String[]>> ACTIVITY_TEMPLATES = buildActivityTemplates();

    public ItineraryGenerationService(DestinationService destinationService,
                                       FlightService flightService,
                                       HotelService hotelService,
                                       ItineraryRepository itineraryRepository) {
        this.destinationService = destinationService;
        this.flightService = flightService;
        this.hotelService = hotelService;
        this.itineraryRepository = itineraryRepository;
    }

    public Itinerary generate(TripRequest request) {
        double dailyBudget = request.getBudgetUsd() / Math.max(1, request.getNumberOfDays());

        Destination destination = (request.getDestinationCode() != null
            && !request.getDestinationCode().isBlank())
            ? destinationService.findByCode(request.getDestinationCode())
                .orElseThrow(() -> new NoSuchElementException(
                    "Unknown destination code: " + request.getDestinationCode()))
            : destinationService.bestMatch(request.getInterests(), dailyBudget);

        Flight outbound = flightService.cheapestFlight(
            request.getOriginCode(), destination.getCode(), request.getStartDate());
        Flight returnFlight = flightService.cheapestFlight(
            destination.getCode(), request.getOriginCode(), request.getStartDate());

        double hotelBudgetPerNight = Math.max(20, dailyBudget * 0.45);
        Hotel hotel = hotelService.bestWithinBudget(destination.getCode(), hotelBudgetPerNight);

        List<DayPlan> days = buildDayPlans(request, destination, hotel, dailyBudget);

        double totalCost = outbound.getPriceUsd() + returnFlight.getPriceUsd()
            + days.stream().mapToDouble(DayPlan::getEstimatedDayCostUsd).sum();

        Itinerary itinerary = new Itinerary();
        itinerary.setUserId(request.getUserId() == null || request.getUserId().isBlank()
            ? "guest" : request.getUserId());
        itinerary.setDestinationCity(destination.getCity());
        itinerary.setDestinationCountry(destination.getCountry());
        itinerary.setOriginCode(request.getOriginCode());
        itinerary.setStartDate(request.getStartDate());
        itinerary.setNumberOfDays(request.getNumberOfDays());
        itinerary.setBudgetUsd(request.getBudgetUsd());
        itinerary.setInterests(request.getInterests());
        itinerary.setOutboundFlight(outbound);
        itinerary.setReturnFlight(returnFlight);
        itinerary.setDays(days);
        itinerary.setEstimatedTotalCostUsd(round(totalCost));
        itinerary.setBudgetRemainingUsd(round(request.getBudgetUsd() - totalCost));
        itinerary.setCreatedAt(Instant.now());

        return itineraryRepository.save(itinerary);
    }

    // ---- day-by-day construction -------------------------------------------------

    private List<DayPlan> buildDayPlans(TripRequest request, Destination destination,
                                         Hotel hotel, double dailyBudget) {
        List<String> interests = normalizedInterests(request.getInterests());
        int activitiesPerDay = switch (Optional.ofNullable(request.getPace()).orElse("balanced")) {
            case "relaxed" -> 2;
            case "packed" -> 4;
            default -> 3;
        };

        LocalDate start = parseDate(request.getStartDate());
        List<DayPlan> days = new ArrayList<>();
        int interestCursor = 0;

        for (int i = 0; i < request.getNumberOfDays(); i++) {
            List<Activity> activities = new ArrayList<>();
            String[] slots = {"Morning", "Afternoon", "Evening", "Late Evening"};

            for (int a = 0; a < activitiesPerDay; a++) {
                String interest = interests.get(interestCursor % interests.size());
                interestCursor++;
                activities.add(buildActivity(slots[a % slots.length], interest, destination));
            }

            double dayActivityCost = activities.stream()
                .mapToDouble(Activity::getEstimatedCostUsd).sum();
            double dayTotal = round(dayActivityCost + hotel.getPricePerNightUsd());

            DayPlan day = new DayPlan();
            day.setDayNumber(i + 1);
            day.setDate(start.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            day.setActivities(activities);
            day.setHotel(hotel);
            day.setEstimatedDayCostUsd(dayTotal);
            days.add(day);
        }

        return days;
    }

    private Activity buildActivity(String timeOfDay, String interest, Destination destination) {
        List<String[]> templates = ACTIVITY_TEMPLATES.getOrDefault(interest,
            ACTIVITY_TEMPLATES.get("culture"));
        String[] template = templates.get(new Random(
            (timeOfDay + interest + destination.getCode()).hashCode())
            .nextInt(templates.size()));

        String title = template[0].replace("{city}", destination.getCity());
        String description = template[1].replace("{city}", destination.getCity());
        double cost = Double.parseDouble(template[2]);

        return new Activity(timeOfDay, title, description, interest, cost);
    }

    private List<String> normalizedInterests(List<String> raw) {
        List<String> known = raw.stream()
            .map(String::toLowerCase)
            .filter(ACTIVITY_TEMPLATES::containsKey)
            .distinct()
            .toList();
        return known.isEmpty() ? List.of("culture") : known;
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            return LocalDate.now().plusDays(30);
        }
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ---- activity template bank ---------------------------------------------------
    // {title, description, base cost in USD}

    private static Map<String, List<String[]>> buildActivityTemplates() {
        Map<String, List<String[]>> map = new HashMap<>();

        map.put("culture", List.of(
            new String[]{"Explore {city}'s Old Town", "Wander historic streets and landmark squares.", "0"},
            new String[]{"Visit the National Museum", "Guided tour through {city}'s art and history collections.", "18"},
            new String[]{"Local Architecture Walk", "A self-guided walk past {city}'s most iconic buildings.", "0"}
        ));
        map.put("food", List.of(
            new String[]{"Street Food Crawl", "Sample {city}'s signature street dishes with a local guide.", "35"},
            new String[]{"Cooking Class", "Hands-on class learning a classic {city} recipe.", "55"},
            new String[]{"Market & Tasting Tour", "Browse the central market and taste local specialties.", "25"}
        ));
        map.put("history", List.of(
            new String[]{"Guided Historical Tour", "Expert-led tour of {city}'s key historical sites.", "30"},
            new String[]{"Ancient Landmarks Visit", "Self-paced visit to {city}'s most significant ruins/monuments.", "15"}
        ));
        map.put("romantic", List.of(
            new String[]{"Sunset River Cruise", "A scenic evening cruise through {city}.", "45"},
            new String[]{"Rooftop Dinner", "Dinner at a rooftop restaurant overlooking {city}.", "60"}
        ));
        map.put("nightlife", List.of(
            new String[]{"Bar Hopping Tour", "Guided tour of {city}'s best-rated bars.", "40"},
            new String[]{"Live Music Venue", "An evening at a popular {city} live-music spot.", "20"}
        ));
        map.put("budget", List.of(
            new String[]{"Free Walking Tour", "Tip-based walking tour covering {city} highlights.", "0"},
            new String[]{"Public Park Picnic", "Relax at a local park with market-bought snacks.", "8"}
        ));
        map.put("beach", List.of(
            new String[]{"Beach Day", "Relax at one of {city}'s best beaches.", "10"},
            new String[]{"Snorkeling Trip", "Half-day snorkeling excursion near {city}.", "50"}
        ));
        map.put("relaxation", List.of(
            new String[]{"Spa Afternoon", "Unwind with a massage and spa treatments.", "70"},
            new String[]{"Scenic Garden Walk", "A quiet stroll through {city}'s botanical gardens.", "10"}
        ));
        map.put("adventure", List.of(
            new String[]{"Zipline Adventure", "An adrenaline-filled zipline course near {city}.", "65"},
            new String[]{"Day Hike", "Guided hike through trails just outside {city}.", "30"}
        ));
        map.put("nature", List.of(
            new String[]{"Nature Reserve Visit", "Explore a protected nature reserve near {city}.", "20"},
            new String[]{"Sunrise Viewpoint Hike", "Early hike to catch sunrise over {city}.", "0"}
        ));
        map.put("technology", List.of(
            new String[]{"Tech District Tour", "Explore {city}'s innovation and tech hub.", "0"},
            new String[]{"Interactive Science Museum", "Hands-on exhibits at {city}'s leading science museum.", "22"}
        ));
        map.put("shopping", List.of(
            new String[]{"Designer District Browse", "Window-shop {city}'s flagship fashion district.", "0"},
            new String[]{"Local Artisan Market", "Shop handmade goods from {city} artisans.", "30"}
        ));
        map.put("wildlife", List.of(
            new String[]{"Wildlife Safari", "Guided safari to spot local wildlife near {city}.", "80"},
            new String[]{"Aquarium Visit", "Explore {city}'s aquarium and marine exhibits.", "24"}
        ));

        return map;
    }
}
