import React from 'react';

export default function ItineraryResult({ itinerary }) {
  if (!itinerary) return null;

  const {
    destinationCity, destinationCountry, numberOfDays, budgetUsd,
    outboundFlight, returnFlight, days, estimatedTotalCostUsd, budgetRemainingUsd,
  } = itinerary;

  return (
    <div className="itinerary-result">
      <div className="itinerary-header">
        <h2>{destinationCity}, {destinationCountry}</h2>
        <p>{numberOfDays} days · Budget ${budgetUsd} · Estimated cost ${estimatedTotalCostUsd}</p>
        <p className={budgetRemainingUsd >= 0 ? 'budget-ok' : 'budget-over'}>
          {budgetRemainingUsd >= 0
            ? `$${budgetRemainingUsd} under budget`
            : `$${Math.abs(budgetRemainingUsd)} over budget`}
        </p>
      </div>

      <div className="flights-section">
        <FlightCard label="Outbound" flight={outboundFlight} />
        <FlightCard label="Return" flight={returnFlight} />
      </div>

      <div className="days-section">
        {days.map((day) => (
          <div className="day-card" key={day.dayNumber}>
            <h3>Day {day.dayNumber} — {day.date}</h3>
            <p className="hotel-line">
              🏨 {day.hotel.name} ({day.hotel.neighborhood}) · ${day.hotel.pricePerNightUsd}/night · ⭐{day.hotel.rating}
            </p>
            <ul className="activity-list">
              {day.activities.map((a, idx) => (
                <li key={idx}>
                  <strong>{a.timeOfDay}:</strong> {a.title}
                  <span className="activity-desc"> — {a.description}</span>
                  {a.estimatedCostUsd > 0 && <span className="activity-cost"> (${a.estimatedCostUsd})</span>}
                </li>
              ))}
            </ul>
            <p className="day-cost">Day total: ${day.estimatedDayCostUsd}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

function FlightCard({ label, flight }) {
  return (
    <div className="flight-card">
      <h4>{label} Flight</h4>
      <p>{flight.airline} · {flight.flightNumber}</p>
      <p>{flight.originCode} {flight.departTime} → {flight.destinationCode} {flight.arriveTime}</p>
      <p>{flight.cabinClass} · {flight.stops === 0 ? 'Nonstop' : `${flight.stops} stop(s)`} · ${flight.priceUsd}</p>
    </div>
  );
}
