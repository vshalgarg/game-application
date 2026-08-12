import axios from "axios";
import { loadAuth } from "../storage/authStorage";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Attach JWT token automatically to every request
api.interceptors.request.use(
  (config) => {
    const token = loadAuth()?.token;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);
let isLoggingOut = false;

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error(error);
    if (error.response?.status === 401) {
      console.warn("Token expired or unauthorized. Logging out...");
      isLoggingOut = true;
      
      window.dispatchEvent(
        new CustomEvent("app:logout", {
          detail: {
            reason: "unauthorized",
            status: 401,
            message: "Session expired",
          },
        }),
      );
    }

    return Promise.reject(error);
  },
);

export default api;
