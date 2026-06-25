import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  
  headers: {
    "Content-Type": "application/json",
  },
});

// Attach JWT token automatically to every request
api.interceptors.request.use(
  (config) => {
    const storedAuth = JSON.parse(localStorage.getItem("user"));

    if (storedAuth?.token) {
      config.headers.Authorization = `Bearer ${storedAuth.token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

export default api;