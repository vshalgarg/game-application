import api from "./axios";

// 1. CREATE ROOM
export const createRoom = async ({ tenantId, userId, gameType, matchType }) => {
  const res = await api.post("/rooms/create", {
    tenantId,
    userId,
    gameType,
    matchType,
  });

  return res.data; // important
};


// 2. JOIN ROOM
export const joinRoom = async ({ roomCode, tenantId, userId }) => {
  const res = await api.post(`/rooms/${roomCode}/join`, {
    tenantId,
    userId,
  });

  return res.data;
};


// 3. START ROOM
export const startRoom = async ({ roomCode, userId }) => {
  const res = await api.post(`/rooms/${roomCode}/start`, null, {
    params: { userId },
  });

  return res.data;
};


// 4. MAKE MOVE
export const makeMove = async ({ roomCode, userId, row, col }) => {
  const res = await api.post(`/game/${roomCode}/move`, {
    userId,
    moveData: {
      row,
      col,
    },
  });

  return res.data;
};

// 5. GET ROOM (Waiting Room Data)
// export const getRoom = async (roomCode) => {
//   const res = await api.get(`/rooms/${roomCode}`);
//   return res.data;
// };