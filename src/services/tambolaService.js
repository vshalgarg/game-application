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

export const claimTambolaRule = async ({ roomCode, userId, ticketId, ruleType }) => {
  try {
    const response = await api.post(`/game/${roomCode}/move`, {
      userId,
      moveData: {
        ticketId,
        ruleType,
      },
    });

    return checkLogicalError(response.data); 
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};