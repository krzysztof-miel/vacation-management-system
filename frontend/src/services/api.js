import axios from 'axios';

// Utwórz instancję axios
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // URL twojego backendu Spring Boot
  timeout: 10000,
});

// Request interceptor - dodaje token do każdego zapytania
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - obsługuje błędy autoryzacji
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token wygasł lub jest nieprawidłowy
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;