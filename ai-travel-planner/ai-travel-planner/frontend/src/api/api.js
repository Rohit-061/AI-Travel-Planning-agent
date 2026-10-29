import axios from 'axios';

const client = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api',
});

export const getDestinations = () => client.get('/destinations').then(r => r.data);

export const generateItinerary = (tripRequest) =>
  client.post('/itineraries/generate', tripRequest).then(r => r.data);

export const getItinerariesForUser = (userId) =>
  client.get(`/itineraries/user/${userId}`).then(r => r.data);

export default client;
