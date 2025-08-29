import axios from 'axios';

/**
 * Axios instance for the app.
 * - baseURL '/api' so calls like api.get('/recipes') go through Vite's proxy to :8080
 * - basic request/response logging for easier debugging
 * - safe JSON handling (no auth headers since you removed security)
 */
const api = axios.create({
  baseURL: '/api',
  // You can tweak timeouts as needed:
  timeout: 15000,
});

// ---- Optional: simple logging (remove if you prefer a clean console) ----
api.interceptors.request.use(
  (config) => {
    // console.debug('[API ⇢]', config.method?.toUpperCase(), config.url, config.params ?? '', config.data ?? '');
    return config;
  },
  (error) => {
    // console.error('[API Request Error]', error);
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    // console.debug('[API ⇠]', response.status, response.config.url, response.data);
    return response;
  },
  (error) => {
    // Network or server error (no 2xx)
    // console.error('[API Response Error]', error?.response?.status, error?.config?.url, error?.response?.data ?? error.message);
    return Promise.reject(error);
  }
);

export default api;
