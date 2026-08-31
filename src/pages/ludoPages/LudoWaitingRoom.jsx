import { useNavigate, useParams } from "react-router-dom";
import { useState, useCallback } from "react";
import { FaCopy, FaGamepad, FaRobot, FaUserCircle, FaTrash } from "react-icons/fa";
import { LuX } from "react-icons/lu";
import { useSnackbar } from "../../context/SnackbarContext";
import { useAuth } from "../../context/AuthContext";
import { startRoom } from "../../services/roomService";
import { addBot, removePlayer } from "../../services/ludoService";
import useWaitingRoomRealtime from "../../hooks/useWaitingRoomRealtime";
import useRoomRealtime from "../../hooks/useRoomRealtime";
import useBackExitGuard from "../../hooks/useBackExitGuard";
import PageShell from "../../components/layout/PageShell";
import GameZoneLogo from "../../components/brand/GameZoneLogo";
import Button from "../../components/ui/Button";
import ExitGamePopup from "../../components/ui/ExitGamePopup";

const LudoWaitingRoom = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const { roomCode } = useParams();
  const [copied, setCopied] = useState(false);
  const [showExitPopup, setShowExitPopup] = useState(false);
  const currentUserId = auth?.userId;
  const closeExitPopup = useCallback(() => setShowExitPopup(false), []);
  const openExitPopup = useCallback(() => setShowExitPopup(true), []);

  useBackExitGuard(openExitPopup);

  const { players } = useWaitingRoomRealtime(roomCode);
  console.info("Players in waiting room", players);

  useRoomRealtime({
    roomCode,
    onStartGame: () => {
      navigate(`/ludogame-room/${roomCode}`, { replace: true });
    },
  });

  const currentPlayer = players.find((p) => p.user_id === currentUserId);
  const isHost = currentPlayer?.role === "HOST";

  const handleCopy = async () => {
    await navigator.clipboard.writeText(roomCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleStartGame = async () => {
    try {
      if (players.length < 2) {
        showSnackbar("Waiting for another player...", "error");
        return;
      }
      const result = await startRoom({ roomCode, userId: currentUserId });
      showSnackbar(result.message, "success");
    } catch (error) {
      console.error("Failed to start game:", error);
      showSnackbar(error.message || "Failed to start game.", "error");
    }
  };

  const handleAddBot = async () => {
    try {
      const result = await addBot({
        roomCode,
        hostUserId: currentUserId,
        botDifficulty: "HARD",
      });
      showSnackbar(result.message, "success");
    } catch (error) {
      console.error(error);
      showSnackbar(error.message || "Failed to add bot", "error");
    }
  };

  const handleRemovePlayer = async (player) => {
    try {
      const result = await removePlayer({
        roomCode,
        hostUserId: currentUserId,
        userId: player.user_id,
      });
      showSnackbar(result.message, "success");
    } catch (error) {
      console.error(error);
      showSnackbar(error.message || "Failed to remove player.", "error");
    }
  };

  const sortedPlayers = [
    ...players.filter((p) => p.role === "HOST"),
    ...players.filter((p) => p.role !== "HOST"),
  ];

  const statusText = isHost
    ? players.length < 2
      ? "Waiting for players to join..."
      : players.length < 4
        ? "You can wait for other players or start the game."
        : "All players joined. Ready to start!"
    : "Waiting for the host to start the game...";

  return (
    <PageShell>
      <button
        type="button"
        className="gz-exit-trigger"
        onClick={openExitPopup}
        aria-label="Exit room"
      >
        <LuX />
      </button>

      <ExitGamePopup
        open={showExitPopup}
        onClose={closeExitPopup}
        title="Exit Room?"
        message="Are you sure you want to exit?"
        hint="You will leave the waiting room."
        stayLabel="Stay in Room"
        confirmLabel="Exit Room"
      />

      <div className="gz-select-card w-full">
        {/* Header */}
        <div className="mb-5 flex flex-col items-center">
          <GameZoneLogo className="mb-3 h-10 w-10" />
          <h1 className="text-2xl font-bold text-gz-text sm:text-3xl">Waiting Room</h1>

          {/* Room ID */}
          <div className="mt-3 flex items-center gap-2">
            <span className="text-sm font-semibold tracking-widest text-gz-primary-cyan">
              ROOM ID : {roomCode}
            </span>
            <button
              type="button"
              onClick={handleCopy}
              className="cursor-pointer text-gz-text-secondary transition hover:text-gz-primary-cyan"
              title="Copy Room ID"
            >
              <FaCopy size={14} />
            </button>
            {copied && <span className="text-xs font-medium text-gz-primary-cyan">Copied!</span>}
          </div>

          {/* Divider */}
          <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
            <FaGamepad className="text-gz-primary-cyan" size={12} />
          </div>

          <p className="mt-2 text-sm text-gz-text-secondary">{statusText}</p>
        </div>

        {/* Players List — host pinned to top, others in join order */}
        <div className="mb-4 flex flex-col gap-2">
          {sortedPlayers.map((player) => {
            const isMe = player.user_id === currentUserId;
            const isHost_ = player.role === "HOST";
            return (
              <div
                key={player.user_id}
                className="flex items-center gap-3 rounded-xl border border-gz-input-border/60 bg-gz-popup/60 px-3 py-2.5"
              >
                <FaUserCircle
                  size={22}
                  className={isMe ? "text-gz-primary-cyan" : "text-gz-purple-accent"}
                />
                <span
                  className={`flex-1 text-left text-sm text-gz-text ${isMe ? "font-bold" : ""}`}
                >
                  {isMe
                    ? `You (${player.user_id})`
                    : `Player ${String(player.user_id).replace(/^-/, "")}`}
                </span>
                {isHost_ ? (
                  <span className="rounded-md bg-gz-primary-cyan/20 px-2.5 py-0.5 text-xs font-semibold text-gz-primary-cyan">
                    Host
                  </span>
                ) : isHost ? (
                  <button
                    type="button"
                    onClick={() => handleRemovePlayer(player)}
                    className="flex h-7 w-7 cursor-pointer items-center justify-center rounded-md border border-red-500/50 text-red-400 transition hover:bg-red-500/15"
                    title="Remove player"
                  >
                    <FaTrash size={11} />
                  </button>
                ) : (
                  <span className="text-xs text-gz-text-secondary">Player</span>
                )}
              </div>
            );
          })}

          {/* Empty slots */}
          {Array.from({ length: Math.max(0, 2 - players.length) }).map((_, i) => (
            <div
              key={`empty-${i}`}
              className="rounded-xl border border-dashed border-gz-input-border/40 py-2.5 text-center text-sm text-gz-text-secondary"
            >
              Waiting...
            </div>
          ))}
        </div>

        {/* Add Bot */}
        {isHost && (
          <button
            type="button"
            onClick={handleAddBot}
            className="mb-4 flex w-full cursor-pointer items-center justify-center gap-2 rounded-xl border border-dashed border-gz-primary-cyan/60 py-2.5 text-sm font-semibold text-gz-primary-cyan transition hover:bg-gz-primary-cyan/10"
          >
            <FaRobot size={14} />+ Add Bot
          </button>
        )}

        {/* Start Game */}
        {isHost && (
          <Button onClick={handleStartGame}>
            <span className="flex items-center justify-center gap-2">
              <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
                <path d="M3 2l10 6-10 6V2z" />
              </svg>
              Start Game
            </span>
          </Button>
        )}
      </div>
    </PageShell>
  );
};

export default LudoWaitingRoom;
