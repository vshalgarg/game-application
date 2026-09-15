import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

const GAME_SOUND_TYPES = {
  ludo: "LUDO",
  "tic-tac-toe": "TIC_TAC_TOE",
};


export const getGameSounds = async (gameType) => {
  try {
    const res = await api.get(`/sounds/list?gameType=${gameType}`);

    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};