import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

// 1. Roll Dice for ludo
export const rollDice = async ({ roomCode, userId }) => {
  try {
    const res = await api.post(`/rooms/${roomCode}/roll-dice`, {
      userId,
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 2. Make Move for ludo
export const makeMove = async ({ roomCode, userId, tokenId, consumedDice }) => {
  try {
    const res = await api.post(`/game/${roomCode}/move`, {
      userId,
      moveData: {
        tokenId,
        consumedDice,
      },
    });
    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 3. Get Board api
// export const getBoard = async (roomCode) => {
//   try {
//     const res = await api.get(`/rooms/${roomCode}/board-layout`);

//     return checkLogicalError(res.data);
//   } catch (error) {
//     throw new Error(handleApiError(error));
//   }
// };

// 4. Add bot api 
export const addBot = async ({ roomCode, hostUserId, botDifficulty }) => {
  try {
    const res = await api.post(`/rooms/${roomCode}/bots`, {
      hostUserId,
      botDifficulty,
    });

    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 5. Remove players
export const removePlayer = async ({ roomCode, userId, hostUserId }) => {
  try {
    const res = await api.delete(`/rooms/${roomCode}/players`, {
      data: {
        userId,
        hostUserId,
      },
    });

    return checkLogicalError(res.data);
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};
