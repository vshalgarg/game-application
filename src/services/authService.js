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

export const forgotPassword = async ({ email }) => {
  try {
    const res = await authApi.post("/forgot-password", {
      email,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const verifyResetOtp = async ({ email, otp }) => {
  try {
    const res = await authApi.post("/forgot-password/verify-otp", {
      email,
      verificationCode: otp,
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
    const res = await authApi.post("/forgot-password/resend-otp", {
      phoneNumber,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const resetPassword = async ({ resetToken, newPassword }) => {
  try {
    const res = await authApi.post("/forgot-password/reset", {
      resetToken,
      newPassword,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

export const getCountries = async () => {
  try {
    const res = await authApi.get("/countries");
    const data = checkLogicalError(res.data);

    return (Array.isArray(data?.data) ? data.data : []).map((country) => ({
      value: country.code,
      label: country.name,
    }));
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};
