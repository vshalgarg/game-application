import axios from "axios";

console.log(
  "AUTH URL =",
  import.meta.env.VITE_AUTH_API_URL
);

const authApi = axios.create({
  baseURL: import.meta.env.VITE_AUTH_API_URL,

  headers: {
    "Content-Type": "application/json",
  },
});

export default authApi; 