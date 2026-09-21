import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

export const getGameSounds = async (gameType) => {
  try {
    const res = await api.get(`/sounds/list?gameType=${gameType}`);
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};