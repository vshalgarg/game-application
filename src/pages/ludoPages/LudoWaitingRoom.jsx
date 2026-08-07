import { useNavigate, useParams } from "react-router-dom";
import GameButton from "../../components/GameButton";
import { FaCopy } from "react-icons/fa";
import { useState } from "react";
import { useSnackbar } from "../../context/SnackbarContext";
import { startRoom } from "../../services/roomService";
import useWaitingRoomRealtime from "../../hooks/useWaitingRoomRealtime";
import useRoomRealtime from "../../hooks/useRoomRealtime";

const WaitingRoom = () => {
  const navigate = useNavigate();
  const { roomCode } = useParams();
  const { showSnackbar } = useSnackbar();
  const [copied, setCopied] = useState(false);
  const storedAuth = JSON.parse(localStorage.getItem("user"));
  const currentUserId = storedAuth?.userId;

  // Supabase WaitingRealtime Hook
  const { players } = useWaitingRoomRealtime(roomCode);
  console.log("Players:", players);

  // Supabase RoomRealtime Hook
  useRoomRealtime({
    roomCode,
    onStartGame: () => {
      console.log("NAVIGATION CALLBACK FIRED");
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

  const currentPlayer = players.find(
    (player) => player.user_id === currentUserId
  );

  console.log("currentPlayer", currentPlayer);
  const isHost = currentPlayer?.role === "HOST";
  console.log("isHost", isHost);

  // Copy room code
  const handleCopy = async () => {
    await navigator.clipboard.writeText(roomCode);
    setCopied(true);

    setTimeout(() => {
      setCopied(false);
    }, 2000);
  };

  return (
    <div
      className="
        min-h-screen
        bg-gradient-to-br
        from-black
        via-gray-900
        to-black
        flex
        items-center
        justify-center
        px-4
      "
    >
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
          {players.length < 2
            ? "Waiting for players to join..."
            : players.length < 4
            ? "You can wait for other players or start the game."
            : "All players joined. Ready to start!"}
        </p>

        {/* Players List */}
        <div className="mb-8 space-y-3">
          {players.map((player) => (
            <div key={player.user_id}
              className="
                bg-black/30
                border
                border-white/10
                rounded-xl
                py-3
                text-white
                flex
                justify-between
                px-4">
              {/* Player Name */}
              <span
                className={`${player.user_id === currentUserId ? "font-bold" : ""}`}>
                {player.user_id === currentUserId
                  ? `You (${player.user_id})`
                  : `Player ${player.user_id}`}
              </span>

              {/* Role */}
              <span
                className={`text-sm
                  ${player.role === "HOST"
                      ? "text-green-400 font-semibold"
                      : "text-gray-400"}`}>
                        
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