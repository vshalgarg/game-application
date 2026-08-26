import { useState, useRef, useEffect, useLayoutEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import LudoBoard from "../../components/ludoComponents/LudoBoard";
import DiceHolder from "../../components/ludoComponents/DiceHolder";
import { rollDice, makeMove, getBoard } from "../../services/ludoService.js";
import useGameRealtime from "../../hooks/useGameRealtime";
import { useAuth } from "../../context/AuthContext";
import PlayerCard from "../../components/ludoComponents/PlayerCard";
import WinModal from "../../modals/FinalWinner.jsx";
import ExitGamePopup from "../../components/ui/ExitGamePopup";
import useBackExitGuard from "../../hooks/useBackExitGuard";
import { LuX } from "react-icons/lu";

const LudoGameRoom = () => {
  const { auth } = useAuth();
  const { roomCode } = useParams();
  const navigate = useNavigate();

  // for get board api
  const [boardData, setBoardData] = useState(null);
  const [boardLoading, setBoardLoading] = useState(true);
  const [boardError, setBoardError] = useState(null);
  const [rolling, setRolling] = useState(false);
  const [selectedToken, setSelectedToken] = useState(null);
  const [diceValue, setDiceValue] = useState(null);
  const [gameState, setGameState] = useState(null);
  const [currentTurnUserId, setCurrentTurnUserId] = useState(null);
  const [winnerUserId, setWinnerUserId] = useState(null);
  const [status, setStatus] = useState(null);
  const [diceOptions, setDiceOptions] = useState([]);
  const [animationComplete, setAnimationComplete] = useState(true);
  const [moveInProgress, setMoveInProgress] = useState(false);
  const [boardScale, setBoardScale] = useState(1);
  const [showExitPopup, setShowExitPopup] = useState(false);
  const closeExitPopup = useCallback(() => setShowExitPopup(false), []);
  const openExitPopup = useCallback(() => setShowExitPopup(true), []);

  useBackExitGuard(openExitPopup);

  // for delay in dice roll when all token at base refs
  const previousTurnUserIdRef = useRef(null);
  const previousPlayerTurnStageRef = useRef(null);
  const turnTransitionTimeoutRef = useRef(null);

  // for winning players confetti
  const [celebratingPlayers, setCelebratingPlayers] = useState([]);
  const previousFinishedRef = useRef({});

  const previousBoardRef = useRef(null);
  const animatingRef = useRef(false);
  const autoMovingRef = useRef(false);
  const autoMoveKeyRef = useRef(null);
  const gameAreaRef = useRef(null);

  const currentUserId = auth?.userId;

  // Fetch board layout from API
  useEffect(() => {
    let cancelled = false;

    const fetchBoard = async () => {
      try {
        setBoardLoading(true);
        setBoardError(null);

        const res = await getBoard(roomCode);
        console.info("get board api", res);

        if (!cancelled) {
          setBoardData(res.data);
        }
      } catch (error) {
        console.error("Failed to fetch board:", error);
        if (!cancelled) {
          setBoardError(error.message || "Failed to load board");
        }
      } finally {
        if (!cancelled)
          setBoardLoading(false);
      }
    };

    if (roomCode) fetchBoard();

    return () => {
      cancelled = true;
    };
  }, [roomCode]);

  // Realtime game update
  useGameRealtime({
    roomCode,

    onGameUpdate: async (game) => {
      const board = game.game_state_data.board;
      console.info("Realtime received:", game);

      board.players.forEach((player) => {
        const wasFinished = previousFinishedRef.current[player.playerId] ?? false;

        if (!wasFinished && player.hasFinished) {
          triggerCelebration(player.playerId);
        }
        previousFinishedRef.current[player.playerId] = player.hasFinished;
      });

      const newTurnUserId = game.current_turn_user_id;
      const newDiceValue = board.lastDice;
      const newPlayerTurnStage = board.playerTurnStage;

      const rollerId = previousTurnUserIdRef.current;
      const previousStage = previousPlayerTurnStageRef.current;
      const turnChanged = rollerId !== null && newTurnUserId !== rollerId;

      // hold the switch when the current player rolls dice during all 4 base slot condition
      const isAutoSkipAfterRoll = turnChanged && previousStage === "ROLL_DICE";

      previousTurnUserIdRef.current = newTurnUserId;
      previousPlayerTurnStageRef.current = newPlayerTurnStage;

      clearTimeout(turnTransitionTimeoutRef.current);

      if (isAutoSkipAfterRoll) {

        // the current player see their rolled number 
        setDiceValue(newDiceValue);

        turnTransitionTimeoutRef.current = setTimeout(() => {
          setCurrentTurnUserId(newTurnUserId);
        }, 2000); 
      } else {
        setCurrentTurnUserId(newTurnUserId);
        setDiceValue(newDiceValue);
      }

      setWinnerUserId(game.winner_user_id);
      setStatus(game.game_status);

      if (!previousBoardRef.current) {
        previousBoardRef.current = structuredClone(board);
        setGameState(game.game_state_data);
      } else if (!animatingRef.current) {
        await animateBoard(previousBoardRef.current, board);
        previousBoardRef.current = structuredClone(board);
      }
    },
  });

  // Cleanup any pending turn-transition timeout on unmount
  useEffect(() => {
    return () => clearTimeout(turnTransitionTimeoutRef.current);
  }, []);

  const board = gameState?.board;
  const legalMoves = board?.legalMoves ?? [];
  const playerTurnStage = board?.playerTurnStage;
  const currentPlayer = board?.players?.find((player) => player.playerId === currentTurnUserId);
  const pendingDice = currentPlayer?.pendingDice ?? [];
  const isMyTurn = currentTurnUserId === currentUserId;
  const canRoll = isMyTurn && playerTurnStage === "ROLL_DICE" && !rolling;
  const movableTokenIds = isMyTurn && playerTurnStage === "TOKEN_MOVE" ? legalMoves.map((move) => move.tokenId) : [];

  // for automatic move when single token is on track
  useEffect(() => {
    // Don't auto move while animation is running
    if (!animationComplete) return;

    // Don't auto move while API call is running
    if (moveInProgress) return;

    if (!isMyTurn) {
      autoMoveKeyRef.current = null;
      return;
    }

    if (playerTurnStage !== "TOKEN_MOVE") return;

    if (legalMoves.length !== 1) {
      autoMoveKeyRef.current = null;
      return;
    }

    const move = legalMoves[0];
    const moveKey = `${roomCode}-${currentTurnUserId}-${move.tokenId}-${move.dice}`;

    // Already processed exact move
    if (autoMoveKeyRef.current === moveKey) return;

    autoMoveKeyRef.current = moveKey;

    // Dice animation duration when single token on track
    const timer = setTimeout(async () => {
      await handleTokenClick(move.tokenId, move.dice);
    }, 1500);

    return () => clearTimeout(timer);
  }, [legalMoves, playerTurnStage, isMyTurn, currentTurnUserId, roomCode, animationComplete, moveInProgress]);

  // Gamearena responsiveness
  useLayoutEffect(() => {
    const el = gameAreaRef.current;
    if (!el) return;

    const updateScale = (width) => setBoardScale(width / 450);
    updateScale(el.getBoundingClientRect().width);

    const observer = new ResizeObserver((entries) => {
      const width = entries[0]?.contentRect?.width;
      if (width) updateScale(width);
    });

    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  // Player card position according to player color/corner
  const playerCardPositions = {
    1: { right: "-34%", bottom: "1%", transformOrigin: "bottom right" },
    2: { left: "-34%", bottom: "1%", transformOrigin: "bottom left" },
    3: { left: "-34%", top: "1%", transformOrigin: "top left" },
    4: { right: "-34%", top: "1%", transformOrigin: "top right" },
  };

  // Roll Dice handler
  const handleRollDice = async () => {
    if (!canRoll) return;

    try {
      setRolling(true);

      await rollDice({
        roomCode,
        userId: currentUserId,
      });

      setTimeout(() => {
        setRolling(false);
      }, 800);
    } catch (error) {
      console.error(error);
      setRolling(false);
    }
  };

  // Make move api
  const moveToken = async (tokenId, consumedDice) => {
    // Prevent duplicate API calls
    if (moveInProgress) return;
    setMoveInProgress(true);

    try {
      await makeMove({
        roomCode,
        userId: currentUserId,
        tokenId,
        consumedDice,
      });

      setSelectedToken(null);
      setDiceOptions([]);
      autoMovingRef.current = false;
    } catch (error) {
      console.error(error);
    } finally {
      setMoveInProgress(false);
    }
  };

  // Make move api when token is clicked
  const handleTokenClick = async (tokenId, selectedDice = null) => {
    if (currentTurnUserId !== currentUserId) return;
    if (playerTurnStage !== "TOKEN_MOVE") return;

    // All legal moves for this token
    const tokenMoves = legalMoves.filter((move) => move.tokenId === tokenId);

    if (tokenMoves.length === 0) return;

    // Token can move using multiple dice number
    if (tokenMoves.length > 1 && selectedDice === null) {
      setSelectedToken(tokenId);

      const uniqueDiceOptions = tokenMoves.filter(
        (move, index, self) => index === self.findIndex((m) => m.dice === move.dice),
      );

      if (uniqueDiceOptions.length === 1 && selectedDice === null) {
        await moveToken(tokenId, uniqueDiceOptions[0].dice);
        return;
      }

      setSelectedToken(tokenId);
      setDiceOptions(uniqueDiceOptions);
      return;
    }

    // Find which dice to consume
    const consumedDice = selectedDice ?? tokenMoves[0].dice;
    await moveToken(tokenId, consumedDice);
  };

  // Make move api when a number is clicked
  const handleDiceSelection = async (dice) => {
    if (selectedToken === null) return;
    await moveToken(selectedToken, dice);
  };

  // Find current player's color
  const currentTurnColorIndex = currentPlayer?.colorIndex;

  const animateBoard = async (oldBoard, newBoard) => {
    animatingRef.current = true;
    setAnimationComplete(false);

    const board = structuredClone(oldBoard);

    // Store killed tokens here
    const killedAnimations = [];

    for (const latestPlayer of newBoard.players) {
      const currentPlayer = board.players.find((p) => p.playerId === latestPlayer.playerId);

      if (!currentPlayer) continue;

      for (const latestToken of latestPlayer.tokens) {
        const currentToken = currentPlayer.tokens.find((t) => t.tokenId === latestToken.tokenId);

        if (!currentToken) continue;

        // Forward Animation
        if (
          currentToken.state === "TRACK" &&
          (latestToken.state === "TRACK" || latestToken.state === "FINISHED") &&
          !latestToken.tokenKilled
        ) {
          const previousLength = currentToken.forwardJourney?.length ?? 0;
          const journey = latestToken.forwardJourney ?? [];
          const newSteps = journey.slice(previousLength);

          for (const cellId of newSteps) {
            currentToken.pathId = cellId;

            setGameState((prev) => ({
              ...prev,
              board: structuredClone(board),
            }));

            await new Promise((resolve) => setTimeout(resolve, 150));
          }

          currentToken.forwardJourney = latestToken.forwardJourney;
          currentToken.backwardJourney = latestToken.backwardJourney;
          currentToken.pathIndex = latestToken.pathIndex;
          currentToken.tokenKilled = latestToken.tokenKilled;
        }

        // Token Reached Center Home
        if (currentToken.state !== "FINISHED" && latestToken.state === "FINISHED") {
          currentToken.state = "FINISHED";
          currentToken.pathId = latestToken.pathId;
          currentToken.pathIndex = latestToken.pathIndex;
          currentToken.forwardJourney = latestToken.forwardJourney;
          currentToken.backwardJourney = latestToken.backwardJourney;

          setGameState((prev) => ({
            ...prev,
            board: structuredClone(board),
          }));
        }

        // Killed Token Animation
        if (
          currentToken.state === "TRACK" &&
          latestToken.state === "BASE" &&
          latestToken.tokenKilled
        ) {
          killedAnimations.push({
            currentToken,
            latestToken,
          });
        }
      }
    }

    // Player Killed Backward Animation
    for (const { currentToken, latestToken } of killedAnimations) {
      const backwardJourney = latestToken.backwardJourney?.length
        ? latestToken.backwardJourney
        : (currentToken.backwardJourney ?? []);

      for (let i = backwardJourney.length - 1; i >= 0; i--) {
        currentToken.pathId = backwardJourney[i];
        currentToken.pathIndex = i;

        setGameState((prev) => ({
          ...prev,
          board: structuredClone(board),
        }));

        await new Promise((resolve) => setTimeout(resolve, 70));
      }

      currentToken.state = "BASE";
      currentToken.pathId = null;
      currentToken.pathIndex = null;
      currentToken.baseSlotId = latestToken.baseSlotId;
      currentToken.forwardJourney = [];
      currentToken.backwardJourney = [];
      currentToken.tokenKilled = false;

      setGameState((prev) => ({
        ...prev,
        board: structuredClone(board),
      }));
    }

    // Final Sync
    setGameState((prev) => ({
      ...prev,
      board: structuredClone(newBoard),
    }));

    animatingRef.current = false;
    setAnimationComplete(true);
  };

  // player celebration delay
  const triggerCelebration = (playerId) => {
    setCelebratingPlayers((prev) => [...prev, playerId]);
    setTimeout(() => {
      setCelebratingPlayers((prev) =>
        prev.filter((id) => id !== playerId)
      );
    }, 3500);
  };

  // while loading board through api
  if (boardLoading) {
    return (
      <div className="min-h-screen w-full bg-gradient-to-br from-black via-gray-900 to-black flex items-center justify-center">
        <ExitGamePopup open={showExitPopup} onClose={closeExitPopup} />
        <p className="text-white text-sm sm:text-base">Loading board...</p>
      </div>
    );
  }

  if (boardError || !boardData) {
    return (
      <div className="min-h-screen w-full bg-gradient-to-br from-black via-gray-900 to-black flex items-center justify-center">
        <ExitGamePopup open={showExitPopup} onClose={closeExitPopup} />
        <p className="text-red-400 text-sm sm:text-base">
          {boardError || "Board not found"}
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen w-full bg-gradient-to-br from-black via-gray-900 to-black flex items-center justify-center p-2 sm:p-4 md:p-6">
      <button
        type="button"
        className="gz-exit-trigger"
        onClick={() => setShowExitPopup(true)}
        aria-label="Exit game"
      >
        <LuX />
      </button>

      <ExitGamePopup open={showExitPopup} onClose={closeExitPopup} />

      <div className="flex flex-col items-center gap-4 w-full">
        {/* Turn Info */}
        <div className="text-center text-white">
          <h2 className="font-bold text-sm sm:text-base md:text-lg">
            {isMyTurn ? "Your Turn" : "Other Player's Turn"}
          </h2>

          <p className="text-xs sm:text-sm break-all">Your User ID: {currentUserId}</p>
          <p className="text-xs sm:text-sm break-all">Current Turn Player: {currentTurnUserId}</p>
        </div>

        {/* Game Area */}
        <div className="relative">
          <div ref={gameAreaRef} className="relative w-fit">
            {/* Board */}
            <div className="bg-white/10 backdrop-blur-lg border border-white/20 rounded-2xl md:rounded-3xl p-2 sm:p-4 w-fit">
              <LudoBoard
                boardData={boardData}
                gameState={gameState}
                selectedToken={selectedToken}
                setSelectedToken={setSelectedToken}
                handleTokenClick={handleTokenClick}
                legalMoves={legalMoves}
                movableTokenIds={movableTokenIds}
                currentTurnColorIndex={currentTurnColorIndex}
              />
            </div>

            {/* Player Cards and Dice */}
            {(board?.players ?? []).map((player) => {
              const isTurnPlayer = player.playerId === currentTurnUserId;
              const isTopPlayer = player.colorIndex === 3 || player.colorIndex === 4;

              const diceElement = isTurnPlayer && (
                <DiceHolder
                  turnColorIndex={currentTurnColorIndex}
                  diceValue={diceValue ?? 1}
                  rolling={rolling}
                  onRoll={handleRollDice}
                  colors={boardData.metadata.colors}
                  isCurrentTurn={isMyTurn}
                />
              );

              const cardElement = (
                <PlayerCard
                  name={`Player ${player.colorIndex}`}
                  userId={player.playerId}
                  color={boardData.metadata.colors?.[player.colorIndex]}
                  currentUserId={currentUserId}
                  isCurrentTurnPlayer={isTurnPlayer}
                  isMyTurn={isMyTurn}
                  playerTurnStage={playerTurnStage}
                  pendingDice={pendingDice}
                  diceOptions={diceOptions}
                  onDiceSelect={handleDiceSelection}
                  celebrating={celebratingPlayers.includes(player.playerId)}
                  avatarUrl="https://plus.unsplash.com/premium_photo-1739786996022-5ed5b56834e2?q=80&w=2080&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                />
              );

              return (
                <div
                  key={player.playerId}
                  className="absolute z-20 flex flex-col items-center gap-2"
                  style={{
                    ...playerCardPositions[player.colorIndex],
                    transform: `scale(${boardScale})`,
                  }}
                >
                  {isTopPlayer ? (
                    <>
                      {cardElement}
                      {diceElement}
                    </>
                  ) : (
                    <>
                      {diceElement}
                      {cardElement}
                    </>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {winnerUserId && (
          <WinModal
            winnerId={winnerUserId}
            isHost={winnerUserId === currentUserId}
            avatarUrl={null}
            onPlayAgain={() => {
              setWinnerUserId(null);
              // navigate(`/ludo-waiting-room/${roomCode}`);
            }}
            onClose={() => setWinnerUserId(null)}
            onBackToHome={() => navigate("/")}
          />
        )}
      </div>
    </div>
  );
};

export default LudoGameRoom;