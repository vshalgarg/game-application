import { useNavigate } from "react-router-dom";
import GameButton from "../../components/GameButton";
import { createRoom } from "../../services/roomService";
import { useSnackbar } from "../../context/SnackbarContext";
import { useState } from "react";

const Home = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  // const [loading, setLoading] = useState(false);

  // const handleCreateRoom = async () => {
  //   if (loading) return;

  //   setLoading(true);

  //   try {
  //     const storedAuth = JSON.parse(localStorage.getItem("user"));
  //     const hostUserId = storedAuth?.userId;

  //     const res = await createRoom({
  //       tenantId: "test-1",
  //       userId: hostUserId,
  //       gameType: "LUDO",
  //       matchType: "PVP",
  //     });

  //     showSnackbar(res.message, "success");

  //     const roomCode = res.data.roomCode;

  //     navigate(`/ludowaiting-room/${roomCode}`);
  //   } catch (error) {
  //     console.error("Failed to create room:", error);
  //     showSnackbar(error.message || "Failed to create room", "error");
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  const [roomCode, setRoomCode] = useState("");

const handleCreateRoom = () => {
  const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  let code = "";

  for (let i = 0; i < 6; i++) {
    code += characters.charAt(
      Math.floor(Math.random() * characters.length)
    );
  }
navigate(`/ludowaiting-room/${roomCode}`);
  setRoomCode(code);
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
      {/* Main Card */}
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
        {/* Heading */}
        <h1
          className="
            text-5xl
            font-bold
            text-center
            text-white
            mb-3
          "
        >
          Welcome !
        </h1>

        {/* Subtitle */}
        <p
          className="
            text-gray-300
            text-center
            mb-10
          "
        >
          Gaming Room
        </p>

        {/* Buttons Container */}
        <div className="flex flex-col gap-5">
          {/* Create Room Button */}
          <GameButton
            // title={loading ? "Creating..." : "Create Room"}
            title = "Create Room"
            color="
              bg-blue-500
              hover:bg-blue-600
              shadow-blue-500/40
            "
            onClick={handleCreateRoom}
          />

          {/* Join Room Button */}
          <GameButton
            title="Join Room"
            color="
              bg-purple-500
              hover:bg-purple-600
              shadow-purple-500/40
            "
            onClick={() => navigate("/join-room")}
          />
        </div>
      </div>
    </div>
  );
};

export default Home;