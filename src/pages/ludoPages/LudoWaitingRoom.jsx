import { useNavigate, useParams } from "react-router-dom";
import GameButton from "../../components/GameButton";
import { FaCopy } from "react-icons/fa";
import { useState } from "react";
import { useSnackbar } from "../../context/SnackbarContext";
import { startRoom } from "../../services/roomService";
import useWaitingRoomRealtime from "../../hooks/useWaitingRoomRealtime";
import useRoomRealtime from "../../hooks/useRoomRealtime";
import { useAuth } from "../../context/AuthContext";
import { addBot, removePlayer } from "../../services/ludoService";

const WaitingRoom = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const { roomCode } = useParams();
  const [copied, setCopied] = useState(false);
  const [selectedPlayer, setSelectedPlayer] = useState(null);
  const currentUserId = auth?.userId;

  // Supabase WaitingRealtime Hook
  const { players } = useWaitingRoomRealtime(roomCode);
  console.log("Players in waiting room", players);

  // Supabase RoomRealtime Hook
  useRoomRealtime({
    roomCode,
    onStartGame: () => {
      navigate(`/ludogame-room/${roomCode}`);
    },
  });

  // Start Game
  const handleStartGame = async () => {
    try {
      if (players.length < 2) {
        showSnackbar("Waiting for another player...", "error");
        return;
      } else if (players.length >= 2 && players.length <= 4)
        console.log("Starting game for room:", roomCode);

      const result = await startRoom({
        roomCode,
        userId: currentUserId,
      });

      showSnackbar(result.message, "success");
    } catch (error) {
      console.error("Failed to start game:", error);
      showSnackbar(error.message || "Failed to start game.", "error");
    }
  };

  const currentPlayer = players.find((player) => player.user_id === currentUserId);
  const isHost = currentPlayer?.role === "HOST";

  // Copy room code
  const handleCopy = async () => {
    await navigator.clipboard.writeText(roomCode);
    setCopied(true);

    setTimeout(() => {
      setCopied(false);
    }, 2000);
  };

  // Add bot handler 
  const handleAddBot = async () => {
    console.log("Current User ID:", currentUserId);
    console.log("Room Code:", roomCode);

  try {
    const result = await addBot({
      roomCode,
      hostUserId: currentUserId,
      botDifficulty: "EASY", // or whatever your backend expects
    });

    showSnackbar(result.message, "success");
  } catch (error) {
    console.error(error);
    showSnackbar(error.message || "Failed to add bot", "error");
  }
};

  // Remove player handler
  const handleRemovePlayer = async () => {
    if (!selectedPlayer) {
    showSnackbar("Select a player to remove.", "error");
    return;
  }
  try {
    const result = await removePlayer({
      roomCode,
      hostUserId: currentUserId,
      userId: selectedPlayer.user_id,
    });

    showSnackbar(result.message, "success");
    setSelectedPlayer(null);
  } catch (error) {
    console.error(error);
    showSnackbar(error.message || "Failed to remove player.", "error");
  }
};

  return (
    <div className="min-h-screen bg-gradient-to-br from-black via-gray-900 to-black flex items-center justify-center px-4">
      {/* Card */}
      <div
        className="
          w-full
          max-w-md
          bg-white/10
          backdrop-blur-lg
          border
          border-white/20
          rounded-3xl
          shadow-2xl
          p-8
          text-center
        "
      >
        {/* Title */}
        <h1
          className="
            text-3xl
            font-bold
            text-white
            mb-4
          "
        >
          Waiting Room
        </h1>

        {/* Room ID */}
        <div
          className="
            flex
            items-center
            justify-center
            gap-3
            mb-6
          "
        >
          <p
            className="
              text-cyan-400
              tracking-widest
            "
          >
            ROOM ID : {roomCode}
          </p>

          <button
            onClick={handleCopy}
            className="
              text-white
              hover:text-cyan-400
              transition
            "
            title="Copy Room ID"
          >
            <FaCopy />
          </button>

          {copied && (
            <span
              className="
                text-xs
                text-green-400
              "
            >
              Copied!
            </span>
          )}
        </div>

        {/* Status */}
        <p className="text-gray-300 mb-6">
          {isHost
            ? players.length < 2
              ? "Waiting for players to join..."
              : players.length < 4
                ? "You can wait for other players or start the game."
                : "All players joined. Ready to start!"
            : "Waiting for the host to start the game..."}
        </p>

        {/* Players List */}
        <div className="mb-8 space-y-3">
          {players.map((player) => (
            <div
              key={player.user_id}
              onClick={() => isHost && player.user_id !== currentUserId && setSelectedPlayer(player)}
              className={`
                bg-black/30
                border
                rounded-xl
                py-3
                px-4
                flex
                justify-between
                text-white
                cursor-pointer
                transition

                ${
                  selectedPlayer?.user_id === player.user_id
                    ? "border-red-500 bg-red-500/20"
                    : "border-white/10 hover:border-red-400"
                }

                ${
                  player.user_id === currentUserId
                    ? "cursor-default"
                    : ""
                }
              `}>

              {/* Player Name */}
              <span className={`${player.user_id === currentUserId ? "font-bold" : ""}`}>
                {player.user_id === currentUserId
                  ? `You (${player.user_id})`
                  : `Player ${player.user_id}`}
              </span>

              {/* Role */}
              <span
                className={`text-sm
                  ${player.role === "HOST" ? "text-green-400 font-semibold" : "text-gray-400"}`}
              >
                {player.role === "HOST" ? "Host" : "Player"}
              </span>
            </div>
          ))}

          {/* Empty Slot */}
          {players.length < 2 && (
            <div
              className="
                bg-black/20
                border
                border-dashed
                border-white/10
                rounded-xl
                py-3
                text-gray-400
              "
            >
              Waiting...
            </div>
          )}
        </div>

        {/* Host Controls */}
{isHost && (
  <div className="mb-6">
    <div className="flex justify-center gap-4">
      {/* Add Button */}
      <button
        className="
          px-6
          py-2.5
          rounded-xl
          bg-gradient-to-r
          from-emerald-500
          to-green-600
          text-white
          font-semibold
          shadow-lg
          shadow-green-500/30
          hover:scale-105
          hover:shadow-green-400/50
          active:scale-95
          transition-all
          duration-200"
        onClick={handleAddBot}>
        + Add Bot
      </button>

      {/* Remove Button */}
      <button
        disabled={!selectedPlayer}
        onClick={handleRemovePlayer}
        className={`px-6 py-2.5 rounded-xl text-white font-semibold transition
        ${selectedPlayer
        ? "bg-gradient-to-r from-red-500 to-rose-600 hover:scale-105"
        : "bg-gray-600 cursor-not-allowed opacity-50"
        }`}>
        − Remove Player
      </button>
    </div>
  </div>
)}

        {/* Start Button */}
        {isHost && (
          <GameButton
            title="Start Game"
            color="
              bg-purple-500
              hover:bg-purple-600
              shadow-purple-500/40
            "
            onClick={handleStartGame}
          />
        )}
      </div>
    </div>
  );
};

export default WaitingRoom;
