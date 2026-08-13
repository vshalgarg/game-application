import { useNavigate } from "react-router-dom";
import GameButton from "../../components/GameButton";
import { createRoom, joinRoom } from "../../services/roomService";
import { useSnackbar } from "../../context/SnackbarContext";
import { useState } from "react";
import { useAuth } from "../../context/AuthContext";

const Home = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const [loading, setLoading] = useState(false);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [roomCode, setRoomCode] = useState("");
  const [joining, setJoining] = useState(false);

  // create room api handler
  const handleCreateRoom = async () => {
    if (loading) return;

    setLoading(true);

    try {
      const hostUserId = auth?.userId;

      const res = await createRoom({
        tenantId: "test-1",
        userId: hostUserId,
        gameType: "LUDO",
        // matchType: "PVP",
      });

      showSnackbar(res.message, "success");
      console.log("res", res);
      const roomCode = res.data.roomCode;

      navigate(`/ludowaiting-room/${roomCode}`);
    } catch (error) {
      console.error("Failed to create room:", error);
      showSnackbar(error.message || "Failed to create room", "error");
    } finally {
      setLoading(false);
    }
  };

  // join room api handler
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
      navigate(`/ludowaiting-room/${roomCode}`);
    } catch (error) {
      showSnackbar(error.res?.message || "Failed to join room.", "error");
      console.error("Failed to join room:", error);
    }
  };

  // enter submit button handler
  const handleJoinSubmit = async (e) => {
    e.preventDefault();
    await handleJoinRoom();
  };

  return (
    <div className=" min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-800 flex items-center justify-center px-4">
      {/* Main Card */}
      <div className=" w-full max-w-md bg-white/10 backdrop-blur-lg p-8 rounded-3xl shadow-2xl border border-white/20">
        {/* Heading */}
        <h1 className=" text-5xl font-bold text-center text-white mb-3 ">Welcome !</h1>

        {/* Subtitle */}
        <p className=" text-gray-300 text-center mb-10">Gaming Room</p>

        {/* Buttons Container */}
        <div className="flex flex-col gap-5">
          {/* Create Room Button */}
          <GameButton
            title={loading ? "Creating..." : "Create Room"}
            color="bg-blue-500 hover:bg-blue-600 shadow-blue-500/40"
            onClick={handleCreateRoom}
          />

          {/* Join Room Button */}
          <GameButton
            title="Join Room"
            color=" bg-purple-500 hover:bg-purple-600 shadow-purple-500/40"
            onClick={() => setShowJoinModal(true)}
          />
        </div>
      </div>
      {showJoinModal && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm px-4 "
          onClick={() => setShowJoinModal(false)}
        >
          <div
            className="  w-full  max-w-md  bg-white/10  backdrop-blur-xl  border  border-white/20  rounded-3xl  shadow-2xl p-8"
            onClick={(e) => e.stopPropagation()}
          >
            <h2 className="text-3xl font-bold text-white text-center mb-3">Join Room</h2>
            <p className="text-center text-gray-300 mb-8">Enter Room ID to join the game</p>

            <form onSubmit={handleJoinSubmit}>
              <input
                type="text"
                value={roomCode}
                placeholder="Enter Room ID"
                autoFocus
                onChange={(e) => setRoomCode(e.target.value.toUpperCase())}
                className=" w-full bg-black/30 border border-blue-500/40 rounded-2xl px-5 py-4 text-lg text-white outline-none focus:border-blue-500 transition mb-6 "
              />

              <div className="flex gap-4">
                <GameButton
                  title={joining ? "Joining..." : "Join"}
                  color="bg-blue-500  hover:bg-blue-600 shadow-blue-500/40"
                  type="submit"
                />

                <GameButton
                  title="Cancel"
                  color="bg-gray-600 hover:bg-gray-700 shadow-gray-500/40"
                  onClick={() => {
                    setShowJoinModal(false);
                    setRoomCode("");
                  }}
                />
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;