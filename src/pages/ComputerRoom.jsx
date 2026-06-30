import { useState } from "react";
import { useNavigate } from "react-router-dom";
import GameButton from "../components/GameButton";
import { createRoom, startRoom } from "../services/roomService";

const ComputerRoom = () => {
  const navigate = useNavigate();

  const [difficulty, setDifficulty] = useState("EASY");
  const [loading, setLoading] = useState(false);

  const difficulties = [
    {
      id: "EASY",
      title: "Easy",
      description: "Perfect for beginners",
      emoji: "🟢",
      activeBorder: "border-green-500",
    },
    {
      id: "MEDIUM",
      title: "Medium",
      description: "Balanced challenge",
      emoji: "🟡",
      activeBorder: "border-yellow-500",
    },
    {
      id: "HARD",
      title: "Hard",
      description: "A real challenge",
      emoji: "🔴",
      activeBorder: "border-red-500",
    },
  ];

  const handleStartGame = async () => {
    try {
      setLoading(true);

      const storedAuth = JSON.parse(localStorage.getItem("user"));
      const playerUserId = storedAuth?.userId;

      console.log("Selected Difficulty:", difficulty);

      const createResponse = await createRoom({
        tenantId: "test-1",
        userId: playerUserId,
        gameType: "TIC_TAC_TOE",
        matchType: "BOT",
        botDifficulty: difficulty,
      });

      const roomCode = createResponse.data.roomCode;

      console.log("Room Created:", roomCode);

      await startRoom({
        roomCode,
        userId: playerUserId,
      });

      navigate(`/game-room/${roomCode}`);
    } catch (err) {
      console.error("Failed to start game:", err);
      alert("Unable to start game.");
    } finally {
      setLoading(false);
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
          p-6
          text-center
        "
      >
        <h1
          className="
            text-3xl
            font-bold
            text-white
            mb-2
          "
        >
          Play Against Computer
        </h1>

        <p
          className="
            text-gray-300
            mb-8
          "
        >
          Choose your difficulty level
        </p>

        <div className="space-y-3 mb-6">
          {difficulties.map((item) => (
            <div
              key={item.id}
              onClick={() => !loading && setDifficulty(item.id)}
              className={`
                cursor-pointer
                rounded-2xl
                border
                py-3 px-4
                transition-all
                duration-200
                ${
                  difficulty === item.id
                    ? `${item.activeBorder} bg-white/10 shadow-lg`
                    : "border-white/20 hover:border-white/50 hover:bg-white/5"
                }
                ${loading ? "pointer-events-none opacity-70" : ""}
              `}
            >
              <div className="flex items-center gap-4">
                <span className="text-2xl">{item.emoji}</span>

                <div className="text-left">
                  <h3 className="text-white font-bold text-base">
                    {item.title}
                  </h3>

                  <p className="text-gray-400 text-xs">
                    {item.description}
                  </p>
                </div>
              </div>
            </div>
          ))}
        </div>

        <GameButton
          title={loading ? "Starting..." : "Start Game"}
          color="
            bg-green-500
            hover:bg-green-600
            shadow-green-500/40
          "
          onClick={handleStartGame}
          disabled={loading}
        />
      </div>
    </div>
  );
};

export default ComputerRoom;