import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

export const getProfile = async () => {
  try {
    const res = await api.get("/profile");
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};
