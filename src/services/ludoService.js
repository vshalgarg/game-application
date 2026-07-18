import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

// 1. Roll Dice for ludo
export const rollDice = async ({ roomCode, userId }) => {
  try {
    const res = await api.post(
      `/rooms/${roomCode}/roll-dice`,
      {
        userId,
      }
    );
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 2. Make Move for ludo
export const makeMove = async ({roomCode, userId, tokenId, consumedDice,}) => {
    try{
  const res = await api.post(
    `/game/${roomCode}/move`,
    {
      userId,
      moveData: {
        tokenId,
        consumedDice,
      },
    }
  );
   return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};