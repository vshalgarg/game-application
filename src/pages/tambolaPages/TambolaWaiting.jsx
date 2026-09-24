import { useNavigate, useParams } from "react-router-dom";
import { useState, useCallback, useEffect } from "react";
import { FaCopy, FaGamepad, FaRobot, FaUserCircle, FaTrash, FaCheck } from "react-icons/fa";
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
import { getAvailableTambolaRules, saveTambolaRules } from "../../services/tambolaService";
import useTambolaRulesRealtime from "../../hooks/useTambolaRulesRealtime";

const TambolaWaiting = () => {
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();
  const { roomCode } = useParams();

  const [copied, setCopied] = useState(false);
  const [showExitPopup, setShowExitPopup] = useState(false);
  const [selectedRules, setSelectedRules] = useState([]);
  const [rules, setRules] = useState([]);
  const [rulesLoading, setRulesLoading] = useState(false);

  const currentUserId = auth?.userId;

  // rules realtime listerner
  const {
    rules: realtimeRules,
    loading: rulesRealtimeLoading,
  } = useTambolaRulesRealtime(roomCode);

  console.log("rules", realtimeRules);
  console.log("loading", rulesRealtimeLoading);

  // Rules selected by host and received through realtime
  const realtimeRuleTypes = realtimeRules.map((rule) => rule.rule_type);
  console.log("Realtime rule types:", realtimeRuleTypes);

  const closeExitPopup = useCallback(() => {
    setShowExitPopup(false);
  }, []);

  const openExitPopup = useCallback(() => {
    setShowExitPopup(true);
  }, []);

  useBackExitGuard(openExitPopup);

  // get rules api 
  const fetchTambolaRules = async () => {
    try {

      setRulesLoading(true);
      const response = await getAvailableTambolaRules(roomCode);
      console.log("Rules", response);
      setRules(response.data || []);

    } catch (error) {
      console.error("Failed to fetch Tambola rules:", error);

      showSnackbar(error.message || "Failed to fetch Tambola rules.","error");
    } finally {
      setRulesLoading(false);
    }
  };
  useEffect(() => {
  if (!roomCode) return;
  fetchTambolaRules();
}, [roomCode]);

  // lobby realtime listner
  const { players } = useWaitingRoomRealtime(roomCode);
  console.info("Players in waiting room", players);

  // start button listner
  useRoomRealtime({
    roomCode,
    onStartGame: () => {
      navigate(`/tambola-gameroom/${roomCode}`, { replace: true });
    },
  });

  const currentPlayer = players.find((p) => p.user_id === currentUserId);
  const isHost = currentPlayer?.role === "HOST";

  // copy handler
  const handleCopy = async () => {
    await navigator.clipboard.writeText(roomCode);
    setCopied(true);
    setTimeout(() => {
      setCopied(false);
    }, 2000);
  };

  // start button handler
  const handleStartGame = async () => {
    try {
      if (players.length < 2) {
        showSnackbar("Waiting for another player...", "error");
        return;
      }

      // start room api 
      const result = await startRoom({
        roomCode,
        userId: currentUserId,
      });

      showSnackbar(result.message, "success");
    } catch (error) {
      console.error("Failed to start game:", error);
      showSnackbar(error.message || "Failed to start game.","error");
    }
  };

  // save rule handler
  const handleSaveRules = async () => {

    if (selectedRules.length === 0) {
      showSnackbar("Please select at least one rule.", "error");
      return;
    }

    try {
      const requestRules = selectedRules.map((ruleType, index) => {
        const rule = rules.find((item) => item.ruleType === ruleType);
        return {
          ruleType: rule.ruleType,
          order: index + 1,
          maxWinners: 1,
          threshold: rule.threshold,
        };
      });

      const payload = {
        hostUserId: currentUserId,
        rules: requestRules,
      };

      const result = await saveTambolaRules(roomCode, payload);

      showSnackbar(result.message, "success");
    } catch (error) {
      console.error("Failed to save Tambola rules:", error);

      showSnackbar(
        error.message || "Failed to save Tambola rules.",
        "error"
      );
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

      showSnackbar(
        error.message || "Failed to add bot",
        "error"
      );
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

      showSnackbar(
        error.message || "Failed to remove player.",
        "error"
      );
    }
  };

  const handleRuleChange = (ruleType) => {
    setSelectedRules((prev) =>
      prev.includes(ruleType)
        ? prev.filter((id) => id !== ruleType)
        : [...prev, ruleType]
    );
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
      {/* Exit Button */}
      <button
        type="button"
        className="gz-exit-trigger"
        onClick={openExitPopup}
        aria-label="Exit room"
      >
        <LuX />
      </button>

      {/* Exit Popup */}
      <ExitGamePopup
        open={showExitPopup}
        onClose={closeExitPopup}
        title="Exit Room?"
        message="Are you sure you want to exit?"
        hint="You will leave the waiting room."
        stayLabel="Stay in Room"
        confirmLabel="Exit Room"
      />

      {/* Waiting Room and Rules Card */}
      <div className="flex w-full flex-col items-center justify-center gap-5 px-3 lg:flex-row lg:items-start">

        {/* WAITING ROOM CARD */}
        <div className="gz-select-card w-full">

          {/* Header */}
          <div className="mb-5 flex flex-col items-center">
            <GameZoneLogo className="mb-3 h-10 w-10" />

            <h1 className="text-2xl font-bold text-gz-text sm:text-3xl">
              Waiting Room
            </h1>

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

              {copied && (
                <span className="text-xs font-medium text-gz-primary-cyan">
                  Copied!
                </span>
              )}
            </div>

            {/* Divider */}
            <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
              <FaGamepad
                className="text-gz-primary-cyan"
                size={12}
              />
            </div>

            <p className="mt-2 text-sm text-gz-text-secondary">
              {statusText}
            </p>
          </div>

          {/* Players List */}
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
                    className={
                      isMe
                        ? "text-gz-primary-cyan"
                        : "text-gz-purple-accent"
                    }
                  />

                  <span
                    className={`flex-1 text-left text-sm text-gz-text ${
                      isMe ? "font-bold" : ""
                    }`}
                  >
                    {isMe
                      ? `You (${player.user_id})`
                      : `Player ${String(player.user_id).replace(
                          /^-/,
                          ""
                        )}`}
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
                    <span className="text-xs text-gz-text-secondary">
                      Player
                    </span>
                  )}
                </div>
              );
            })}

            {/* Empty Slots */}
            {Array.from({
              length: Math.max(0, 2 - players.length),
            }).map((_, i) => (
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
              <FaRobot size={14} />
              + Add Bot
            </button>
          )}

          {/* Start Game */}
          {isHost && (
            <Button onClick={handleStartGame}>
              <span className="flex items-center justify-center gap-2">
                <svg
                  width="14"
                  height="14"
                  viewBox="0 0 16 16"
                  fill="currentColor"
                >
                  <path d="M3 2l10 6-10 6V2z" />
                </svg>

                Start Game
              </span>
            </Button>
          )}
        </div>

        {/* GAME RULES CARD */}
        <div className="gz-select-card w-full max-w-md">

          {/* Rules Header */}
          <div className="mb-5 flex flex-col items-center">

            <h2 className="text-xl font-bold text-gz-text sm:text-2xl">
              {isHost ? "Select Game Rules" : "Game Rules"}
            </h2>

            <div className="gz-divider mt-3 w-full max-w-[200px] justify-center">
              <FaGamepad
                className="text-gz-primary-cyan"
                size={12}
              />
            </div>

            {!isHost && (
              <p className="mt-2 text-xs text-gz-text-secondary">
                Rules selected by the host
              </p>
            )}
          </div>

          {/* Rules */}
          <div className="flex flex-col gap-2.5">

            {/*HOST: Show all available rules, PLAYER:Show only rules received through realtime*/}
            {(isHost
              ? rules
              : rules.filter((rule) =>
                  realtimeRuleTypes.includes(rule.ruleType)
                )
            ).map((rule) => {

              /*Host: selectedRules controls the checkbox, Player: realtimeRuleTypes controls the selected state.*/
              const isSelected = isHost
                ? selectedRules.includes(rule.ruleType)
                : realtimeRuleTypes.includes(rule.ruleType);

              return (
                <label
                  key={rule.ruleType}
                  className={`relative flex items-start gap-3 overflow-hidden rounded-xl border px-3 py-3 transition-all duration-200 ${
                    isSelected
                      ? "border-gz-primary-cyan/70 bg-gz-primary-cyan/10 shadow-[0_0_14px_rgba(34,211,238,0.12)]"
                      : "border-gz-input-border/60 bg-gz-popup/60"
                  } ${
                    isHost
                      ? "cursor-pointer hover:border-gz-primary-cyan/30"
                      : "cursor-default"
                  }`}
                >

                  {/* Highlight Layer */}
                  {isSelected && (
                    <span className="pointer-events-none absolute inset-0 bg-gz-primary-cyan/[0.04]" />
                  )}

                  {/* Checkbox / Selected Indicator */}
                  <span
                    className={`relative mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-md border transition-all ${
                      isSelected
                        ? "border-gz-primary-cyan bg-gz-primary-cyan text-black"
                        : "border-gz-input-border bg-gz-popup"
                    }`}
                  >
                    {isSelected && <FaCheck size={10} />}
                  </span>

                  {/* Checkbox is only needed by host */}
                  {isHost && (
                    <input
                      type="checkbox"
                      checked={isSelected}
                      onChange={() => handleRuleChange(rule.ruleType)}
                      className="sr-only"
                    />
                  )}

                  {/* Rule Content */}
                  <div className="relative flex-1">
                    <h3
                      className={`text-sm font-semibold ${
                        isSelected
                          ? "text-gz-primary-cyan"
                          : "text-gz-text"
                      }`}
                    >
                      {rule.description}
                    </h3>
                  </div>

                </label>
              );
            })}

            {/* No rules selected yet for player */}
            {!isHost && realtimeRuleTypes.length === 0 && (
              <div className="rounded-xl border border-dashed border-gz-input-border/40 py-4 text-center text-sm text-gz-text-secondary">
                Waiting for the host to select rules...
              </div>
            )}

            {/* Save button only for host */}
            {isHost && (
              <Button onClick={handleSaveRules}>
                <span className="flex items-center justify-center gap-2">
                  <svg
                    width="14"
                    height="14"
                    viewBox="0 0 16 16"
                    fill="currentColor"
                  >
                    <path d="M3 2l10 6-10 6V2z" />
                  </svg>

                  Save Rules
                </span>
              </Button>
            )}

          </div>
        </div>
      </div>
    </PageShell>
  );
};

export default TambolaWaiting;