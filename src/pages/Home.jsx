import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createRoom } from "../services/roomService";
import { useSnackbar } from "../context/SnackbarContext";
import { useAuth } from "../context/AuthContext";
import PageShell from "../components/layout/PageShell";
import GameZoneLogo from "../components/brand/GameZoneLogo";
import Button from "../components/ui/Button";

const Home = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const [loading, setLoading] = useState(false);

  const handleCreateRoom = async () => {
    if (loading) return;

    setLoading(true);

    try {
      const hostUserId = auth?.userId;

      const res = await createRoom({
        tenantId: "test-1",
        userId: hostUserId,
        gameType: "TIC_TAC_TOE",
        matchType: "PVP",
      });

      showSnackbar(res.message, "success");

      const roomCode = res.data.roomCode;

      navigate(`/waiting-room/${roomCode}`);
    } catch (error) {
      console.error("Failed to create room:", error);
      showSnackbar(error.message || "Failed to create room", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageShell>
      <div className="gz-select-card w-full">
        <div className="mb-6 flex flex-col items-center sm:mb-8">
          <GameZoneLogo className="mb-4 h-12 w-12 sm:h-14 sm:w-14" />
          <h1 className="text-3xl font-bold text-gz-text sm:text-4xl">Welcome!</h1>
          <p className="mt-2 text-sm text-gz-text-secondary sm:text-base">Gaming Room</p>
        </div>

        <div className="flex flex-col gap-3 sm:gap-4">
          <Button onClick={handleCreateRoom} disabled={loading}>
            {loading ? "Creating..." : "Create Room"}
          </Button>
          <Button variant="accent" onClick={() => navigate("/join-room")}>
            Join Room
          </Button>
        </div>
      </div>
    </PageShell>
  );
};

export default Home;
