import React, { useState } from 'react';

const INTEREST_OPTIONS = [
  'culture', 'food', 'history', 'romantic', 'nightlife',
  'budget', 'beach', 'relaxation', 'adventure', 'nature',
  'technology', 'shopping', 'wildlife'
];

const ORIGIN_OPTIONS = ['NYC', 'LON', 'DEL', 'SYD', 'DXB', 'SIN'];

export default function PreferenceForm({ onSubmit, loading }) {
  const [form, setForm] = useState({
    originCode: 'NYC',
    destinationCode: '',
    startDate: '',
    numberOfDays: 5,
    budgetUsd: 1500,
    interests: [],
    pace: 'balanced',
  });

  const toggleInterest = (interest) => {
    setForm((f) => ({
      ...f,
      interests: f.interests.includes(interest)
        ? f.interests.filter((i) => i !== interest)
        : [...f.interests, interest],
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (form.interests.length === 0) {
      alert('Please select at least one interest.');
      return;
    }
    onSubmit(form);
  };

  return (
    <form className="preference-form" onSubmit={handleSubmit}>
      <h2>Plan your trip</h2>

      <div className="field-row">
        <label>
          Flying from
          <select
            value={form.originCode}
            onChange={(e) => setForm({ ...form, originCode: e.target.value })}
          >
            {ORIGIN_OPTIONS.map((o) => (
              <option key={o} value={o}>{o}</option>
            ))}
          </select>
        </label>

        <label>
          Start date
          <input
            type="date"
            required
            value={form.startDate}
            onChange={(e) => setForm({ ...form, startDate: e.target.value })}
          />
        </label>
      </div>

      <div className="field-row">
        <label>
          Number of days
          <input
            type="number"
            min="1"
            max="30"
            value={form.numberOfDays}
            onChange={(e) => setForm({ ...form, numberOfDays: Number(e.target.value) })}
          />
        </label>

        <label>
          Budget (USD)
          <input
            type="number"
            min="100"
            step="50"
            value={form.budgetUsd}
            onChange={(e) => setForm({ ...form, budgetUsd: Number(e.target.value) })}
          />
        </label>
      </div>

      <label>
        Pace
        <select value={form.pace} onChange={(e) => setForm({ ...form, pace: e.target.value })}>
          <option value="relaxed">Relaxed</option>
          <option value="balanced">Balanced</option>
          <option value="packed">Packed</option>
        </select>
      </label>

      <fieldset>
        <legend>Interests</legend>
        <div className="interest-grid">
          {INTEREST_OPTIONS.map((interest) => (
            <button
              type="button"
              key={interest}
              className={`interest-chip ${form.interests.includes(interest) ? 'active' : ''}`}
              onClick={() => toggleInterest(interest)}
            >
              {interest}
            </button>
          ))}
        </div>
      </fieldset>

      <button type="submit" className="submit-btn" disabled={loading}>
        {loading ? 'Building your itinerary…' : 'Generate Itinerary'}
      </button>
    </form>
  );
}
