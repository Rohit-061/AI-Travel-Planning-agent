import React, { useState } from 'react';
import PreferenceForm from './components/PreferenceForm';
import ItineraryResult from './components/ItineraryResult';
import { generateItinerary } from './api/api';

export default function App() {
  const [itinerary, setItinerary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (formData) => {
    setLoading(true);
    setError(null);
    setItinerary(null);
    try {
      const result = await generateItinerary({ ...formData, userId: 'guest' });
      setItinerary(result);
    } catch (err) {
      setError(
        err.response?.data?.error ||
        'Could not generate an itinerary. Is the backend running on :8080?'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>✈️ AI Travel Planning Agent</h1>
        <p>Tell us your preferences — we'll build a personalized, day-by-day itinerary.</p>
      </header>

      <main className="app-main">
        <PreferenceForm onSubmit={handleSubmit} loading={loading} />
        {error && <p className="error-banner">{error}</p>}
        <ItineraryResult itinerary={itinerary} />
      </main>
    </div>
  );
}
