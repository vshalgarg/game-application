// import api from "./axios";
import authApi from "./authAxios";

// Signup
export const signupUser = async ({ email, password }) => {
  const res = await authApi.post("/register", {
    email,
    password,
  });

  return res.data;
};

// Login
export const loginUser = async ({ email, password }) => {
  const res = await authApi.post("/login", {
    email,
    password,
  });

  return res.data;
};