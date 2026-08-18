import { useState, useRef, useEffect } from "react";
import { useParams } from "react-router-dom";
import LudoBoard from "../../components/ludoComponents/LudoBoard";
import DiceHolder from "../../components/ludoComponents/DiceHolder";
import { rollDice, makeMove } from "../../services/ludoService.js";
import useGameRealtime from "../../hooks/useGameRealtime";
import boardData from "../../data/board.json";
import { useAuth } from "../../context/AuthContext";

const LudoGameRoom = () => {
  const { auth } = useAuth();
  const { roomCode } = useParams();
  const [rolling, setRolling] = useState(false);
  const [selectedToken, setSelectedToken] = useState(null);
  const [diceValue, setDiceValue] = useState(null);
  const [gameState, setGameState] = useState(null);
  const [currentTurnUserId, setCurrentTurnUserId] = useState(null);
  const [winnerUserId, setWinnerUserId] = useState(null);
  const [status, setStatus] = useState(null);
  const [diceOptions, setDiceOptions] = useState([]);
  const [animationComplete, setAnimationComplete] = useState(true);

  const previousBoardRef = useRef(null);
  const animatingRef = useRef(false);
  const autoMovingRef = useRef(false);
  const autoMoveKeyRef = useRef(null);
  const moveInProgressRef = useRef(false);

  const currentUserId = auth?.userId;

  //REALTIME GAME UPDATE
  useGameRealtime({
    roomCode,

    onGameUpdate: async (game) => {
      const board = game.game_state_data.board;
      console.log("Realtime received:", game);

      setCurrentTurnUserId(game.current_turn_user_id);
      setWinnerUserId(game.winner_user_id);
      setStatus(game.game_status);
      setDiceValue(board.lastDice);

      if (!previousBoardRef.current) {
        previousBoardRef.current = structuredClone(board);
        setGameState(game.game_state_data);
      } else if (!animatingRef.current) {
        await animateBoard(previousBoardRef.current, board);
        previousBoardRef.current = structuredClone(board);
      }
    },
  });

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
    if (moveInProgressRef.current) return;

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

    // Already processed this exact move
    if (autoMoveKeyRef.current === moveKey) return;

    autoMoveKeyRef.current = moveKey;

    (async () => {
      await handleTokenClick(move.tokenId, move.dice);
    })();
  }, [legalMoves, playerTurnStage, isMyTurn, currentTurnUserId, roomCode, animationComplete]);

  // Dice position according to player color
  const dicePositions = {
    1: {
      right: "-28%",
      bottom: "3%",
    },

    2: {
      left: "-28%",
      bottom: "3%",
    },

    3: {
      left: "-28%",
      top: "3%",
    },

    4: {
      right: "-28%",
      top: "3%",
    },
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
    if (moveInProgressRef.current) return;
    moveInProgressRef.current = true;

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
      moveInProgressRef.current = false;
    }
  };

  // make move api when token is clicked
  const handleTokenClick = async (tokenId, selectedDice = null) => {
    if (currentTurnUserId !== currentUserId) return;
    if (playerTurnStage !== "TOKEN_MOVE") return;

    // All legal moves for this token
    const tokenMoves = legalMoves.filter((move) => move.tokenId === tokenId);

    if (tokenMoves.length === 0) return;

    // Token can move using multiple dice number
    if (tokenMoves.length > 1 && selectedDice === null) {
      setSelectedToken(tokenId);
      // setDiceOptions(tokenMoves);
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

  // make move api when a number is clicked
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

    // Player Killed Animation Backward Animation
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
  // if (!boardData) {
  return (
    <div
      className="
      min-h-screen
      w-full
      bg-gradient-to-br
      from-black
      via-gray-900
      to-black
      flex
      items-center
      justify-center
      p-2
      sm:p-4
      md:p-6
    "
    >
      <div
        className="
        flex
        flex-col
        items-center
        gap-4
        w-full
      "
      >
        {/* Turn Info */}
        <div className="text-center text-white">
          <h2 className="font-bold text-sm sm:text-base md:text-lg">
            {isMyTurn ? "Your Turn" : "Other Player's Turn"}
          </h2>

          <p className="text-xs sm:text-sm break-all">Your User ID: {currentUserId}</p>
        </div>

        {/* Game Area */}
        <div className="relative">
          {/* Board */}
          <div
            className="
            bg-white/10
            backdrop-blur-lg
            border
            border-white/20
            rounded-2xl
            md:rounded-3xl
            p-2
            sm:p-4
          "
          >
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

          {/* Dice */}
          <div className="absolute z-30" style={dicePositions[currentTurnColorIndex]}>
            <div className="relative">
              <DiceHolder
                turnColorIndex={currentTurnColorIndex}
                diceValue={diceValue ?? 1}
                rolling={rolling}
                onRoll={handleRollDice}
                colors={boardData.metadata.colors}
                // colors={boardData?.metadata?.colors ?? []}
                isCurrentTurn={isMyTurn}
              />

              {diceOptions.length > 0 && (
                <div
                  className="
                  absolute
                  top-full
                  left-1/2
                  -translate-x-1/2
                  mt-2
                  flex
                  flex-wrap
                  justify-center
                  gap-2
                  bg-white
                  rounded-lg
                  shadow-lg
                  p-2
                  max-w-[180px]
                  z-50
                "
                >
                  {diceOptions.map((move, index) => (
                    <button
                      key={index}
                      onClick={() => handleDiceSelection(move.dice)}
                      className="
                      w-8
                      h-8
                      sm:w-9
                      sm:h-9
                      rounded
                      bg-yellow-500
                      hover:bg-yellow-600
                      font-bold
                      text-black
                    "
                    >
                      {move.dice}
                    </button>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Pending Dice */}
          {pendingDice.length > 0 && (
            <div
              className="
              absolute
              left-1/2
              -translate-x-1/2
              -bottom-12
              flex
              gap-2
            "
            >
              {pendingDice.map((dice, index) => (
                <div
                  key={index}
                  className="
                  w-7
                  h-7
                  sm:w-8
                  sm:h-8
                  rounded
                  bg-yellow-500
                  text-black
                  flex
                  items-center
                  justify-center
                  font-bold
                  text-xs
                "
                >
                  {dice}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
// };

export default LudoGameRoom;