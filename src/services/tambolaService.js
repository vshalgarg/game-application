import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

// get rules 
export const getAvailableTambolaRules = async (roomCode) => {
  try {
    const response = await api.get(`/rooms/${roomCode}/available-rules`);

    return checkLogicalError(response.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// save rules
export const saveTambolaRules = async (roomCode, payload) => {
  try {
    const response = await api.put(
      `/rooms/${roomCode}/rules`,
      payload
    );

    return checkLogicalError(response.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};