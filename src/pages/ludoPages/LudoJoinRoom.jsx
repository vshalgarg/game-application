import { useState } from "react";
import { useNavigate } from "react-router-dom";

import GameButton from "../../components/GameButton";
// import { joinRoom } from "../services/roomService";

const JoinRoom = () => {
  const navigate = useNavigate();

  // Store input value
  const [roomCode, setRoomCode] = useState("");

  // Handle join room
  const handleJoinRoom = async () => {
    if (!roomCode) {
      alert("Please enter room ID"); 
      return;
    }
  
//     try {
//          const storedAuth = JSON.parse( localStorage.getItem("user") );

//           const joinUserId = storedAuth?.userId;

//           console.log("Current User ID:", joinUserId);

//           const res = await joinRoom({
//               roomCode,
//               tenantId: "test-1",
//               userId: joinUserId,
//   });

//   console.log("Joined room:", res);   // res stores the response of the join api 

  navigate(`/ludowaiting-room/${roomCode}`);
// } catch (err) {
//   console.error("Failed to join room:", err);
//   alert("Invalid room or unable to join");
// }
  };

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
      ">

        <h1 className="
          text-4xl
          font-bold
          text-white
          text-center
          mb-4
        ">
          Join Room
        </h1>

        <p className="
          text-gray-300
          text-center
          mb-8
        ">
          Enter room ID to join the game
        </p>

        <input
          type="text"
          placeholder="Enter Room ID"
          value={roomCode}
          onChange={(e) => setRoomCode(e.target.value)}
          className="
            w-full
            bg-black/30
            border
            border-blue-500/40
            rounded-2xl
            px-5
            py-4
            text-white
            text-lg
            outline-none
            mb-8
            focus:border-blue-500
            transition
          "
        />

        <GameButton
          title="Join Waiting Room"
          color="
            bg-blue-500
            hover:bg-blue-600
            shadow-blue-500/40
          "
          onClick={handleJoinRoom}
        />

      </div>

    </div>
  );
};

export default JoinRoom;