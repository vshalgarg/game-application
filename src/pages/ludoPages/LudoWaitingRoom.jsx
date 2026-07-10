import { useNavigate, useParams } from "react-router-dom";
import GameButton from "../../components/GameButton";

const WaitingRoom = () => {
  const navigate = useNavigate();
  const { roomCode } = useParams();

    const handleStartGame = async () => {
      try {
        if (players.length < 2) {
          showSnackbar("Waiting for another player...", "error");
          return;
        }
  
        console.log("Starting game for room:", roomCode);
        const result = await startRoom({
          roomCode,
          userId : currentUserId,
        });
  
        showSnackbar(result.message, "success");
      } catch (err) {
        console.error("Failed to start game:", err);
        showSnackbar(err.result?.message || "Failed to start game.","error");
      }
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
        <p
          className="
          text-cyan-400
          mb-6
          tracking-widest
        "
        >
          ROOM ID : {roomCode}
        </p>

        {/* Status */}
        <p
          className="
          text-gray-300
          mb-6
        "
        >
          Waiting for players to join...
        </p>

        {/* Waiting Box */}
        <div className="mb-8">
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
        </div>

        {/* Start Button */}
        <GameButton
          title="Start Game"
          color="
            bg-purple-500
            hover:bg-purple-600
            shadow-purple-500/40
          "
          onClick={handleStartGame}
        />
      </div>
    </div>
  );
};

export default WaitingRoom;