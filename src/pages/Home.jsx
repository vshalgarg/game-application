import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaBorderAll, FaGamepad, FaPlusCircle, FaSignInAlt } from "react-icons/fa";
import { createRoom, joinRoom } from "../services/roomService";
import { useSnackbar } from "../context/SnackbarContext";
import { useAuth } from "../context/AuthContext";
import PageShell from "../components/layout/PageShell";
import GameZoneLogo from "../components/brand/GameZoneLogo";
import ModeOption from "../components/ui/ModeOption";
import TextField from "../components/ui/TextField";

const Home = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const [loading, setLoading] = useState(false);
  const [roomCode, setRoomCode] = useState("");
  const [joining, setJoining] = useState(false);

  const handleCreateRoom = async () => {
    if (loading) return;
    setLoading(true);
    try {
      const res = await createRoom({
        tenantId: "test-1",
        userId: auth?.userId,
        gameType: "TIC_TAC_TOE",
        matchType: "PVP",
      });
      showSnackbar(res.message, "success");
      navigate(`/waiting-room/${res.data.roomCode}`, { replace: true });
    } catch (error) {
      console.error("Failed to create room:", error);
      showSnackbar(error.message || "Failed to create room", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleJoinRoom = async () => {
    if (joining) return;
    if (!roomCode) {
      showSnackbar("Please enter room ID", "error");
      return;
    }
    try {
      setJoining(true);
      const res = await joinRoom({
        roomCode,
        tenantId: "test-1",
        userId: auth?.userId,
      });
      showSnackbar(res.message, "success");
      navigate(`/waiting-room/${roomCode}`, { replace: true });
    } catch (error) {
      showSnackbar(error.message || "Failed to join room.", "error");
      console.error("Failed to join room:", error);
    } finally {
      setJoining(false);
    }
  };

  const handleJoinSubmit = (e) => {
    e.preventDefault();
    handleJoinRoom();
  };

  return (
    <PageShell>
      <div className="gz-select-card w-full">
        {/* Header */}
        <div className="mb-5 flex flex-col items-center">
          <GameZoneLogo className="mb-3 h-10 w-10" />
          <h1 className="text-2xl font-bold text-gz-text sm:text-3xl">Gaming Room</h1>
          <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
            <FaGamepad className="text-gz-primary-cyan" size={12} />
          </div>
          <p className="mt-2 text-center text-sm text-gz-text-secondary">
            Create a room or join with Room ID
            <br />
            and start playing together!
          </p>
        </div>

        {/* Create Room */}
        <ModeOption
          icon={FaPlusCircle}
          label={loading ? "Creating..." : "Create Room"}
          tone="cyan"
          onClick={handleCreateRoom}
        />

        {/* OR divider */}
        <div className="gz-divider my-4">
          <span className="text-xs font-semibold tracking-widest text-gz-text-secondary">OR</span>
        </div>

        <form onSubmit={handleJoinSubmit} className="flex flex-col gap-2.5">
          <TextField
            placeholder="Enter Room ID"
            value={roomCode}
            onChange={(e) => setRoomCode(e.target.value.toUpperCase())}
            leftIcon={<FaBorderAll size={14} className="text-gz-primary-cyan" />}
          />

          <ModeOption
            icon={FaSignInAlt}
            label={joining ? "Joining..." : "Join Room"}
            tone="purple"
            onClick={handleJoinRoom}
          />
        </form>
      </div>
    </PageShell>
  );
};

export default Home;
