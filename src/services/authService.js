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

export const forgotPassword = async ({ phoneNumber }) => {
  try {
    const res = await authApi.post("/forget-password", {
      phoneNumber,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const verifyResetOtp = async ({ phoneNumber, otp }) => {
  try {
    const res = await authApi.post("/verify-reset-otp", {
      phoneNumber,
      otp,
    });
    const result = checkLogicalError(res.data);
    const resetToken = result?.resetToken;

    if (!resetToken) {
      throw new Error("Reset token missing from server response.");
    }

    return { ...result, resetToken };
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const resendResetOtp = async ({ phoneNumber }) => {
  try {
    const res = await authApi.post("/resend-reset-otp", {
      phoneNumber,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const resetPassword = async ({ resetToken, newPassword }) => {
  try {
    const res = await authApi.post("/reset-password", {
      resetToken,
      newPassword,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};
