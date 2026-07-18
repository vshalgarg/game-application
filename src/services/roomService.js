import api from "./axios";
import { checkLogicalError, handleApiError } from "../utils/errorHandler";

// 1. CREATE ROOM
export const createRoom = async ({tenantId, userId, gameType, matchType, botDifficulty,}) => {
  try {
    const res = await api.post("/rooms/create", {
      tenantId,
      userId,
      gameType,
      matchType,
      botDifficulty,
    });

    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 2. JOIN ROOM
export const joinRoom = async ({ roomCode, tenantId, userId }) => {
  try {
    const res = await api.post(`/rooms/${roomCode}/join`, {
      tenantId,
      userId,
    });

    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 3. START ROOM
export const startRoom = async ({ roomCode, userId }) => {
  try {
    const res = await api.post(
      `/rooms/${roomCode}/start`,
      null,
      {
        params: { userId },
      }
    );

    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 4. MAKE MOVE
export const makeMove = async ({ roomCode, userId, row, col }) => {
  try {
    const res = await api.post(`/game/${roomCode}/move`, {
      userId,
      moveData: {
        row,
        col,
      },
    });

    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 5. RESTART ROOM
export const restartRoom = async ({ roomCode, userId }) => {
  try {
    const res = await api.post(
      `/rooms/${roomCode}/restart`,
      null,
      {
        params: { userId },
      }
    );

    const result = checkLogicalError(res.data);
    return result;
  } catch (error) {
    throw new Error(handleApiError(error));
  }
};

// 6. DICE ROLL IN LUDO
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