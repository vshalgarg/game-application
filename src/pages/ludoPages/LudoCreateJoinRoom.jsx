import { useNavigate } from "react-router-dom";
import GameButton from "../../components/GameButton";

const LudoCreateJoinRoom = () => {

  const navigate = useNavigate();

  return (
    
    <div className="
      min-h-screen
      bg-gradient-to-br
      from-gray-900
      via-black
      to-gray-800
      flex
      items-center
      justify-center
      px-4
    ">

      {/* Main Card */}

      <div className="
        w-full
        max-w-md
        bg-white/10
        backdrop-blur-lg
        p-8
        rounded-3xl
        shadow-2xl
        border
        border-white/20
      ">

        {/* Heading */}

        <h1 className="
          text-5xl
          font-bold
          text-center
          text-white
          mb-3
        ">
          Welcome !
        </h1>

        {/* Subtitle */}

        <p className="
          text-gray-300
          text-center
          mb-10
        ">
          Gaming Room
        </p>

        {/* Buttons Container */}

        <div className="flex flex-col gap-5">

          {/* Create Room Button */}

          <GameButton
            title="Create Room"
            color="
              bg-blue-500
              hover:bg-blue-600
              shadow-blue-500/40
            "
            // onClick={handleCreateRoom}
            onClick={() => navigate("/ludocreate-room")}
          />

          {/* Join Room Button */}

          <GameButton
            title="Join Room"
            color="
              bg-purple-500
              hover:bg-purple-600
              shadow-purple-500/40
            "
            onClick={() => navigate("/ludojoin-room")}
          />

        </div>

      </div>

    </div>
  );
};

export default LudoCreateJoinRoom;