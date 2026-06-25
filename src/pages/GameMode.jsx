import { useNavigate } from "react-router-dom";
import GameButton from "../components/GameButton";

const GameMode = () => {
  const navigate = useNavigate();

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
          p-8
          rounded-3xl
          shadow-2xl
          border
          border-white/20
        "
      >
        <h1
          className="
            text-5xl
            font-bold
            text-center
            text-white
            mb-3
          "
        >
          Tic Tac Toe
        </h1>

        <p
          className="
            text-gray-300
            text-center
            mb-10
          "
        >
          Choose Game Mode
        </p>

        <div className="flex flex-col gap-5">
          <GameButton
            title="Play With Person"
            color="
              bg-blue-500
              hover:bg-blue-600
              shadow-blue-500/40
            "
            onClick={() => navigate("/tic-tac-toe")}
          />

          <GameButton
            title="Play With Computer"
            color="
              bg-green-500
              hover:bg-green-600
              shadow-green-500/40
            "
            onClick={() => navigate("/computer-room")}
          />
        </div>
      </div>
    </div>
  );
};

export default GameMode;