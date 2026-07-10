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

let isLoggingOut = false;

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response?.status === 401 &&
      !isLoggingOut
    ) {
      isLoggingOut = true;

      // Show snackbar
      showSnackbar(
        "Login session expired. Please login again.",
        "warning"
      );

      // Clear auth data
      localStorage.removeItem("user");

      // Redirect after snackbar is shown
      setTimeout(() => {
        window.location.href = "/login";
      }, 1500);
    }

    return Promise.reject(error);
  }
);

export default api;