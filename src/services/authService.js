import { checkLogicalError, handleApiError } from "../utils/errorHandler";
import authApi from "./authAxios";

// Signup
export const signupUser = async ({ email, password }) => {
  try {
    const res = await authApi.post("/register", {
      email,
      password,
    });
    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// Login
export const loginUser = async ({ email, password }) => {
  try {
    const res = await authApi.post("/login", {
      email,
      password,
    });
    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const socialLogin = async (payload) => {
  try {
    const res = await authApi.post("/login/social", payload);
    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};
