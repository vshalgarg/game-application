import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaGamepad, FaBorderAll, FaSignInAlt } from "react-icons/fa";
import { joinRoom } from "../services/roomService";
import { useSnackbar } from "../context/SnackbarContext";
import { useAuth } from "../context/AuthContext";
import PageShell from "../components/layout/PageShell";
import GameZoneLogo from "../components/brand/GameZoneLogo";
import TextField from "../components/ui/TextField";

const JoinRoom = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const [roomCode, setRoomCode] = useState("");

  const handleJoinRoom = async () => {
    if (!roomCode) {
      showSnackbar("Please enter room ID", "error");
      return;
    }
    try {
      const res = await joinRoom({
        roomCode,
        tenantId: "test-1",
        userId: auth?.userId,
      });
      showSnackbar(res.message, "success");
      navigate(`/waiting-room/${roomCode}`);
    } catch (error) {
      showSnackbar(error.message || "Failed to join room.", "error");
      console.error("Failed to join room:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await handleJoinRoom();
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
            Enter room ID to join the game
          </p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-3">
          {/* Room ID Input */}
          <TextField
            placeholder="Enter Room ID"
            value={roomCode}
            autoFocus
            onChange={(e) => setRoomCode(e.target.value.toUpperCase())}
            leftIcon={<FaBorderAll size={14} className="text-gz-primary-cyan" />}
          />

          {/* Join Room button */}
          <button
            type="submit"
            className="gz-mode-option gz-mode-option--purple"
          >
            <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-lg border border-gz-purple-accent/60">
              <FaSignInAlt size={13} />
            </span>
            <span className="min-w-0 flex-1 text-left text-sm font-semibold text-gz-text">
              Join Room
            </span>
            <svg
              className="shrink-0 text-gz-purple-accent"
              width="14"
              height="14"
              viewBox="0 0 16 16"
              fill="currentColor"
            >
              <path d="M6 3l5 5-5 5" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
        </form>
      </div>
    </PageShell>
  );
};

export default JoinRoom;
