import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaCopy } from "react-icons/fa";

import GameButton from "../components/GameButton";
import { createRoom } from "../services/roomService";

const CreateRoom = () => { 
  const navigate = useNavigate();

  const [roomCode, setRoomCode] = useState("");
  const [loading, setLoading] = useState(true);
  const [copied, setCopied] = useState(false);

  // Prevent duplicate API calls
  const hasCreatedRoom = useRef(false);

  // Create room when page loads
  useEffect(() => {

    // Stop second execution in StrictMode
    if (hasCreatedRoom.current) return;

    // Mark as already executed
    hasCreatedRoom.current = true;

    console.log("Room Created");



    const initRoom = async () => {

      // try {

      //   const res = await createRoom({
      //     tenantId: "test-1",
      //     userId,
      //     gameType: "TIC_TAC_TOE",
      //     matchType: "PVP"
      //   });

      try {
        const storedAuth = JSON.parse(localStorage.getItem("user") );

        const hostUserId = storedAuth?.userId;  // checks for userId in local storage 

        console.log("Logged In User ID:", hostUserId);

        const res = await createRoom({       // res stores the response of the create api 
            tenantId: "test-1",
            userId: hostUserId,
            gameType: "TIC_TAC_TOE",
            matchType: "PVP",
  });


        setRoomCode(res.data.roomCode);

      } catch (err) {

        console.error("Failed to create room:", err);

      } finally {

        setLoading(false);

      }
    };

    initRoom();

  }, []);

  // Copy room code
  const handleCopy = async () => {

    await navigator.clipboard.writeText(roomCode);

    setCopied(true);

    setTimeout(() => {
      setCopied(false);
    }, 2000);
  };

  // Navigate to waiting room
  const handleStartGame = () => {

    if (!roomCode) return;

    navigate(`/waiting-room/${roomCode}`);
  };

  return (
    <div className="
      min-h-screen
      bg-gradient-to-br
      from-black
      via-gray-900
      to-black
      flex
      items-center
      justify-center
      px-4
    ">

      {/* Card */}

      <div className="
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
      ">

        {/* Heading */}

        <h1 className="
          text-4xl
          font-bold
          text-white
          mb-4
        ">
          Room Created
        </h1>

        {/* Subtitle */}

        <p className="
          text-gray-300
          mb-8
        ">
          Share this room ID with your friend
        </p>

        {/* Room ID Box */}

        <div className="
          relative
          bg-black/30
          border
          border-blue-500/40
          rounded-2xl
          py-6
          mb-8
          shadow-lg
          shadow-blue-500/20
        ">

          {/* Copy Section */}

          <div className="
            absolute
            top-4
            right-4
            flex
            items-center
            gap-2
          ">

            {copied && (
              <span className="
                text-xs
                text-green-400
              ">
                Copied!
              </span>
            )}

            <button
              onClick={handleCopy}
              className="
                text-white
                hover:text-blue-400
                transition
              "
            >
              <FaCopy />
            </button>

          </div>

          <p className="
            text-gray-400
            mb-2
          ">
            ROOM ID
          </p>

          <h2 className="
            text-5xl
            font-bold
            tracking-widest
            text-blue-400
          ">
            {loading ? "..." : roomCode}
          </h2>

        </div>

        {/* Start Button */}

        <GameButton
          title="Join Waiting Room"
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

export default CreateRoom;