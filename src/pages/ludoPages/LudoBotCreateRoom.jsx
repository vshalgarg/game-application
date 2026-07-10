import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import GameButton from "../../components/GameButton";
import { createRoom } from "../../services/roomService";
import { useSnackbar } from "../../context/SnackbarContext";

const ComputerRoom = () => {
  const navigate = useNavigate();

  const [roomCode, setRoomCode] = useState("");
  const [loading, setLoading] = useState(false);
  const { showSnackbar } = useSnackbar();
  
      // Prevent duplicate API calls
  const hasCreatedRoom = useRef(false);
    
      // Create room when page loads
  useEffect(() => {
        // Stop second execution in StrictMode
    if (hasCreatedRoom.current) return;
    
        // Mark as already executed
      hasCreatedRoom.current = true;
      const initRoom = async () => {
        try {
          const storedAuth = JSON.parse(localStorage.getItem("user") );
          const hostUserId = storedAuth?.userId;  // checks for userId in local storage 
            
          const res = await createRoom({ 
            tenantId: "test-1",
            userId: hostUserId,
            gameType: "LUDO",
            matchType: "PVP",
      });
          
            showSnackbar(res.message, "success");
            setRoomCode(res.data.roomCode);
    
          } catch (err) {
            console.error("Failed to create room:", err);
            showSnackbar(err.res?.message || "Failed to create room","error");
          } finally {
            setLoading(false);
          }
        };
        initRoom();
      }, []);
    

  const handleStartGame = async () => {
    navigate(`/ludogame-room/${roomCode}`);
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
          p-8
          text-center
        "
      >
        <h1
          className="
            text-4xl
            font-bold
            text-white
            mb-4
          "
        >
          Room Created
        </h1>

        <p
          className="
            text-gray-300
            mb-8
          "
        >
          Ready to play against Computer
        </p>

        <div
          className="
            relative
            bg-black/30
            border
            border-green-500/40
            rounded-2xl
            py-6
            mb-8
            shadow-lg
            shadow-green-500/20
          "
        >
          
         

          <p className="text-gray-400 mb-2">
            ROOM ID
          </p>

          <h2
            className="
              text-5xl
              font-bold
              tracking-widest
              text-green-400
            "
          >
            {roomCode}
          </h2>
        </div>

        <GameButton
          title="Start Game"
          color="
            bg-green-500
            hover:bg-green-600
            shadow-green-500/40
          "
          onClick={handleStartGame}
        />
      </div>
    </div>
  );
};

export default ComputerRoom;