import { useNavigate } from "react-router-dom";
import GameButton from "../../components/GameButton";

const LudoGameMode = () => {
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
          Ludo
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
              bg-orange-500
              hover:bg-orange-600
              shadow-orange-500/40
            "
            onClick={() => navigate("/createjoin-room")}
          />

          <GameButton
            title="Play With Computer"
            color="
              bg-red-500
              hover:bg-red-600
              shadow-red-500/40
            "
            onClick={() => navigate("/ludobotcreate-room")}
          />
        </div>
      </div>
    </div>
  );
};

export default LudoGameMode;