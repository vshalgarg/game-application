import { useState, useRef, useEffect } from "react";
import { useParams } from "react-router-dom";
import LudoBoard from "../../components/ludoComponents/LudoBoard";
import DiceHolder from "../../components/ludoComponents/DiceHolder";
import { rollDice, makeMove } from "../../services/ludoService.js";
import useGameRealtime from "../../hooks/useGameRealtime";
import boardData from "../../data/board.json";

const LudoGameRoom = () => {

  const { roomCode } = useParams();
  const [rolling, setRolling] = useState(false);
  const [selectedToken, setSelectedToken] = useState(null);
  const [diceValue, setDiceValue] = useState(null);
  const [gameState, setGameState] = useState(null);
  const [currentTurnUserId, setCurrentTurnUserId] = useState(null);
  const [winnerUserId, setWinnerUserId] = useState(null);
  const [status, setStatus] = useState(null);
  const [diceOptions, setDiceOptions] = useState([]);

  const storedAuth = JSON.parse(localStorage.getItem("user"));
  const currentUserId = storedAuth?.userId;

  const previousBoardRef = useRef(null);
  const animatingRef = useRef(false);
  const autoMovingRef = useRef(false);

  //REALTIME GAME UPDATE
  useGameRealtime({

  roomCode,

  onGameUpdate: async (game) => {

    const board = game.game_state_data.board;

    if (!previousBoardRef.current) {

      previousBoardRef.current = structuredClone(board);
      setGameState(game.game_state_data);

    }

    else if (!animatingRef.current) {
      await animateBoard(previousBoardRef.current,board);
      previousBoardRef.current = structuredClone(board);
    }

    setCurrentTurnUserId(game.current_turn_user_id);
    setWinnerUserId(game.winner_user_id);
    setStatus(game.game_status);
    setDiceValue(board.lastDice);
  }
});

  const board = gameState?.board;
  const legalMoves = board?.legalMoves ?? [];
  const playerTurnStage = board?.playerTurnStage;
  const currentPlayer = board?.players?.find(player => player.playerId === currentTurnUserId);
  const pendingDice = currentPlayer?.pendingDice ?? [];
  const isMyTurn = currentTurnUserId === currentUserId;
  const canRoll = isMyTurn && playerTurnStage === "ROLL_DICE" && !rolling;
  const movableTokenIds = isMyTurn && playerTurnStage === "TOKEN_MOVE" ? legalMoves.map(move => move.tokenId): [];

  // for automatic move when single token is on track
  useEffect(() => {

  if (!isMyTurn) return;

  if (playerTurnStage !== "TOKEN_MOVE") return;

  if (legalMoves.length !== 1) {
    autoMovingRef.current = false;
    return;
  }

  // Already moving automatically
  if (autoMovingRef.current) return;

  autoMovingRef.current = true;

  handleTokenClick(legalMoves[0].tokenId);

}, [legalMoves, playerTurnStage, isMyTurn]);

  // Dice position according to player color
  const dicePositions = {

    1: {
      right: "-125px",
      bottom: "15px",
    },

    2: {
      left: "-125px",
      bottom: "15px",
    },

    3: {
      left: "-125px",
      top: "15px",
    },

    4: {
      right: "-125px",
      top: "15px",
    },  
  };

  // Roll Dice handler
  const handleRollDice = async()=>{
    if (!canRoll)
    return;

    try{
      setRolling(true);

      await rollDice({
        roomCode,
        userId:currentUserId
      });

      setTimeout(()=>{
        setRolling(false);
      },800);
    }

    catch(error){
      console.error(error);
      setRolling(false);
    }
  };
  
  // Make move api
  const moveToken = async (tokenId, consumedDice) => {
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
    autoMovingRef.current = false;
  }
};
  
// make move api when token is clicked 
  const handleTokenClick = async (tokenId, selectedDice = null) => {

  if (currentTurnUserId !== currentUserId)
    return;

  if (playerTurnStage !== "TOKEN_MOVE")
    return;

  // All legal moves for this token
  const tokenMoves = legalMoves.filter(move => move.tokenId === tokenId);

  if (tokenMoves.length === 0)
    return;

  // Token can move using multiple dice
  if (tokenMoves.length > 1 && selectedDice === null) {
    setSelectedToken(tokenId);
    setDiceOptions(tokenMoves);
    return;
  }

  // Find which dice to consume
  const consumedDice = selectedDice ?? tokenMoves[0].dice;
  await moveToken(tokenId, consumedDice);
};
  // make move api when a number is clicked 
  const handleDiceSelection = async (dice) => {
    console.log("Selected Token:", selectedToken);
    console.log("Selected Dice:", dice);

  if (selectedToken === null)
    return;
  await moveToken(selectedToken, dice);
};
  // Find current player's color
  const currentTurnColorIndex = currentPlayer?.colorIndex;

  const animateBoard = async (oldBoard, newBoard) => {
  animatingRef.current = true;
  const board = structuredClone(oldBoard);

  for (const latestPlayer of newBoard.players) {
    const currentPlayer = board.players.find(p => p.playerId === latestPlayer.playerId);

    if (!currentPlayer) continue;
    for (const latestToken of latestPlayer.tokens) {
      const currentToken = currentPlayer.tokens.find(t => t.tokenId === latestToken.tokenId);

      if (!currentToken) continue;
      // BASE -> TRACK //
      if (currentToken.state === "BASE" && latestToken.state === "TRACK") {

        currentToken.state = "TRACK";
        currentToken.pathId = latestToken.pathId;
        currentToken.pathIndex = latestToken.pathIndex;
        currentToken.forwardJourney = latestToken.forwardJourney ?? [];
        currentToken.backwardJourney = latestToken.backwardJourney ?? [];
        currentToken.tokenKilled = latestToken.tokenKilled;

        setGameState(prev => ({
          ...prev,
          board: structuredClone(board)
        }));

        await new Promise(resolve =>
          setTimeout(resolve, 180)
        );

        continue;
      }
      
      // FORWARD ANIMATION 
      if (currentToken.state === "TRACK" && (latestToken.state === "TRACK" || latestToken.state === "FINISHED") && !latestToken.tokenKilled) {

        const previousLength = currentToken.forwardJourney?.length ?? 0;
        const journey = latestToken.forwardJourney ?? [];
        const newSteps = journey.slice(previousLength);

        for (const cellId of newSteps) {
          currentToken.pathId = cellId;
          setGameState(prev => ({
            ...prev,
            board: structuredClone(board)
          }));

          await new Promise(resolve =>
            setTimeout(resolve, 150)
          );
        }

        currentToken.forwardJourney = latestToken.forwardJourney;
        currentToken.backwardJourney = latestToken.backwardJourney;
        currentToken.pathIndex = latestToken.pathIndex;
        currentToken.tokenKilled = latestToken.tokenKilled;
      }

  // BACKWARD ANIMATION (Killed)
      if (currentToken.state === "TRACK" && latestToken.state === "BASE" && latestToken.tokenKilled) {
        const backwardJourney = latestToken.backwardJourney?.length ? latestToken.backwardJourney : currentToken.backwardJourney ?? [];

      // Move from current position back to start
      for (let i = backwardJourney.length - 1; i >= 0; i--) {
          currentToken.pathId = backwardJourney[i];
          currentToken.pathIndex = i;

          setGameState(prev => ({
              ...prev,
              board: structuredClone(board)
          }));

          await new Promise(resolve =>
              setTimeout(resolve, 70)
          );
      }

      // Finally return to base
      currentToken.state = "BASE";
      currentToken.pathId = null;
      currentToken.pathIndex = null;
      currentToken.baseSlotId = latestToken.baseSlotId;
      currentToken.forwardJourney = [];
      currentToken.backwardJourney = [];
      currentToken.tokenKilled = false;

      setGameState(prev => ({
          ...prev,
          board: structuredClone(board)
      }));

      continue;
  }
      // FINAL SYNC
      currentToken.state = latestToken.state;
      currentToken.pathId = latestToken.pathId;
      currentToken.pathIndex = latestToken.pathIndex;
      currentToken.baseSlotId = latestToken.baseSlotId;
      currentToken.forwardJourney = latestToken.forwardJourney ?? [];
      currentToken.backwardJourney = latestToken.backwardJourney ?? [];
      currentToken.tokenKilled = latestToken.tokenKilled;
    }
  }

  setGameState(prev => ({
    ...prev,
    board: structuredClone(newBoard)
  }));
  animatingRef.current = false;
};
  
  return (

    <div
      className="
        min-h-screen
        bg-gradient-to-br
        from-black
        via-gray-900
        to-black
        flex
        items-center
        justify-center
      "
    >
      <div className="relative">
    
        <div
          className="
            bg-white/10
            backdrop-blur-lg
            border
            border-white/20
            rounded-3xl
            p-6
          "
        >
          <div className="absolute -top-5 left-1/2 -translate-x-1/2 text-white text-center">
            <div className="font-bold text-lg">
              {isMyTurn ? "Your Turn" : "Other Player's Turn"}
              <div className="text-sm">Your User ID: {currentUserId}</div>
            </div>
          </div>

          <LudoBoard
            boardData={boardData}
            gameState={gameState}
            selectedToken={selectedToken}
            setSelectedToken={setSelectedToken}
            handleTokenClick={handleTokenClick}
            legalMoves={legalMoves}
            movableTokenIds={movableTokenIds}
          />
        </div>

        {/* Pending Dice */}
      {pendingDice.length > 0 && (
        <div className="absolute -bottom-12 left-1/2 -translate-x-1/2 flex gap-2">
          {pendingDice.map((dice, index) => (
          <div
            key={index}
            className="w-8 h-8 rounded bg-yellow-500 text-black flex items-center justify-center font-bold">
            {dice}
          </div>
          ))}
        </div>
  )}
        <div
  className="
    absolute
    transition-all
    duration-700
    ease-in-out
  "
  style={dicePositions[currentTurnColorIndex]}
>
  <div className="relative flex flex-col items-center">

    <DiceHolder
      turnColorIndex={currentTurnColorIndex}
      diceValue={diceValue ?? 1}
      rolling={rolling}
      onRoll={handleRollDice}
      colors={boardData.metadata.colors}
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
          gap-2
          bg-white
          rounded-lg
          shadow-lg
          p-2
          z-50
        "
      >
        {diceOptions.map((move, index) => (
          <button
            key={index}
            onClick={() => handleDiceSelection(move.dice)}
            className="
              w-9
              h-9
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

      </div>
    </div>
  );

};

export default LudoGameRoom;

