import { useState } from "react";
import { useNavigate } from "react-router-dom";
import GameButton from "../components/GameButton";
import { joinRoom } from "../services/roomService";
import { useSnackbar } from "../context/SnackbarContext";
import { useAuth } from "../context/AuthContext";

const JoinRoom = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const [roomCode, setRoomCode] = useState("");

  // Handle join room
  const handleJoinRoom = async () => {
    if (!roomCode) {
      showSnackbar("Please enter room ID", "error");
      return;
    }

    try {
      const joinUserId = auth?.userId;

      const res = await joinRoom({
        roomCode,
        tenantId: "test-1",
        userId: joinUserId,
      });
      showSnackbar(res.message, "success");
      navigate(`/waiting-room/${roomCode}`);
    } catch (error) {
      showSnackbar(error.message || "Failed to join room.", "error");
      console.error("Failed to join room:", err);
    }
  };

  // for enter button click form submisison
  const handleSubmit = async (e) => {
    e.preventDefault();
    await handleJoinRoom();
  };

  return (
    <div
      className="
      min-h-screen
      bg-gradient-to-br
      from-gray-900
      via-black
      to-gray-800
      flex
      items-center
      justify-center
      px-4
    "
    >
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
      "
      >
        <h1
          className="
          text-4xl
          font-bold
          text-white
          text-center
          mb-4
        "
        >
          Join Room
        </h1>

        <p
          className="
          text-gray-300
          text-center
          mb-8
        "
        >
          Enter room ID to join the game
        </p>

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Enter Room ID"
            value={roomCode}
            onChange={(e) => setRoomCode(e.target.value.toUpperCase())}
            className="
            w-full
            bg-black/30
            border
            border-purple-500/40
            rounded-2xl
            px-5
            py-4
            text-white
            text-lg
            outline-none
            mb-8
            focus:border-purple-500
            transition
          "
          />

          <GameButton
            title="Join Waiting Room"
            color="
            bg-purple-500
            hover:bg-purple-600
            shadow-purple-500/40
          "
            type="submit"
          />
        </form>
      </div>
    </div>
  );
};

export default JoinRoom;