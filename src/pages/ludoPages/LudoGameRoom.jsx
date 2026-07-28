// import { useState } from "react";
// import LudoBoard from "../../components/ludoComponents/LudoBoard";
// import DiceHolder from "../../components/ludoComponents/DiceHolder";
// import { rollDice } from "../../services/ludoService.js";
// import { makeMove } from "../../services/ludoService.js";
// import useGameRealtime from "../../hooks/useGameRealtime";
// import { useParams } from "react-router-dom";

// const LudoGameRoom = () => {
//   const [rolling, setRolling] = useState(false);
//   const [selectedToken, setSelectedToken] = useState(null);
//   const [diceValue, setDiceValue] = useState(1);
//   const [gameState, setGameState] = useState(null);
//   const [currentTurnUserId, setCurrentTurnUserId] = useState(null);
//   const [winnerUserId, setWinnerUserId] = useState(null);
//   const [status, setStatus] = useState(null);

//   const { roomCode } = useParams();
//   const storedAuth = JSON.parse(localStorage.getItem("user"));
//   const currentUserId = storedAuth?.userId;

//   // useGameRealtime hook call
//   useGameRealtime({
//   roomCode,

//   onGameUpdate: (game) => {
//     console.log("Realtime Game:", game);

//     // Entire latest game state from Supabase
//     setGameState(game.game_state_data);
//     setCurrentTurnUserId(game.current_turn_user_id);
//     setWinnerUserId(game.winner_user_id);
//     setStatus(game.game_status);

//     // Find logged-in player
//     // const currentPlayer = game.game_state_data?.board?.players?.find(
//     //   (player) => player.playerId === currentUserId
//     // );

//     // Update dice from pendingDice
//     const lastDice = game.game_state_data?.board?.lastDice;

//     if (lastDice != null) {
//       setDiceValue(lastDice);
//     } else {
//       setDiceValue(1);
//       }
//   },
// });

// // dice holder position according to turn
//   const dicePositions = {
//     red: {
//       left: "-125px",
//       top: "15px",
//     },

//     green: {
//       right: "-125px",
//       top: "15px",
//     },

//     yellow: {
//       right: "-125px",
//       bottom: "15px",
//     },

//     blue: {
//       left: "-125px",
//       bottom: "15px",
//     },
//   };

//   // Dice Roll API
//   const handleRollDice = async () => {
//   if (rolling) return;

//   try {
//     setRolling(true);

//     await rollDice({
//       roomCode,
//       userId: currentUserId,
//     });

//     setTimeout(() => {
//       setRolling(false);
//     }, 800);

//   } catch (err) {
//     setRolling(false);
//     console.error(err);
//   }
// };

// // Token click handler 
//   const handleTokenClick = async (tokenId) => {
//   if (diceValue <= 0) return;
// console.log("diceValue", diceValue)
//   try {
//     await makeMove({
//       roomCode,
//       userId: currentUserId,
//       tokenId,
//       consumedDice: diceValue,
//     });

//     setSelectedToken(null);

//   } catch (err) {
//     console.error(err);
//   }
// };

// // current turn color of the user
// const currentTurnColor = gameState?.players?.find((player) => player.playerId === currentTurnUserId)
//  ?.color?.toLowerCase();

//   return (
//     <div
//       className="
//         min-h-screen
//         bg-gradient-to-br
//         from-black
//         via-gray-900
//         to-black
//         flex
//         items-center
//         justify-center
//       "
//     >
//       <div className="relative">
//         {/* Board */}
//         <div
//           className="
//             bg-white/10
//             backdrop-blur-lg
//             border
//             border-white/20
//             rounded-3xl
//             p-6
//           "
//         >
//           <LudoBoard 
//             gameState={gameState}
//             selectedToken={selectedToken}
//             setSelectedToken={setSelectedToken}
//             handleTokenClick={handleTokenClick} />
//         </div>

//         {/* Dice Holder */}
//         <div
//           className="absolute transition-all duration-700 ease-in-out"
//           style={dicePositions[currentTurnColor]}
//         >
//           <DiceHolder
//             turn={currentTurnColor}
//             diceValue={diceValue}
//             rolling={rolling}
//             onRoll={handleRollDice}
//           />
//         </div>
//       </div>
//     </div>
//   );
// };

// export default LudoGameRoom;



import { useState } from "react";
import { useParams } from "react-router-dom";
import LudoBoard from "../../components/ludoComponents/LudoBoard";
import DiceHolder from "../../components/ludoComponents/DiceHolder";
import { rollDice, makeMove } from "../../services/ludoService.js";
import useGameRealtime from "../../hooks/useGameRealtime";
import boardData from "../../data/board.json";

const LudoGameRoom = () => {
  const [rolling, setRolling] = useState(false);
  const [selectedToken, setSelectedToken] = useState(null);
  const [diceValue, setDiceValue] = useState(1);
  const [gameState, setGameState] = useState(null);
  const [currentTurnUserId, setCurrentTurnUserId] = useState(null);
  const [winnerUserId, setWinnerUserId] = useState(null);
  const [status, setStatus] = useState(null);
  const { roomCode } = useParams();

  const storedAuth = JSON.parse(localStorage.getItem("user"));
  const currentUserId = storedAuth?.userId;

  useGameRealtime({
    roomCode,

    onGameUpdate: (game) => {
      console.log("Realtime Game:", game);

      setGameState(game.game_state_data);
      setCurrentTurnUserId(game.current_turn_user_id);
      setWinnerUserId(game.winner_user_id);
      setStatus(game.game_status);

      const lastDice = game.game_state_data?.board?.lastDice;

      if (lastDice != null) {
        setDiceValue(lastDice);
      } else {
        setDiceValue(1);
      }
    },
  });

  const dicePositions = {
    1: {
      right: "-125px",
      bottom: "15px",
    }, // Yellow

    2: {
      left: "-125px",
      bottom: "15px",
    }, // Blue

    3: {
      left: "-125px",
      top: "15px",
    }, // Red

    4: {
      right: "-125px",
      top: "15px",
    }, // Green
  };

  const handleRollDice = async () => {
    if (rolling) 
      return;

    try {
      setRolling(true);
      await rollDice({
        roomCode,
        userId: currentUserId,
      });

      setTimeout(() => {
        setRolling(false);
      }, 800);
    } catch (err) {
      setRolling(false);
      console.error(err);
    }
  };

  const handleTokenClick = async (tokenId) => {
    if (diceValue <= 0) 
      return;

    try {
      await makeMove({
        roomCode,
        userId: currentUserId,
        tokenId,
        consumedDice: diceValue,
      });

      setSelectedToken(null);
    } catch (err) {
      console.error(err);
    }
  };

  // to get the color from current_turn_user_id from 
  const currentTurnPlayer = gameState?.board?.players?.find((player) => player.playerId === currentTurnUserId);
  const currentTurnColorIndex = currentTurnPlayer?.colorIndex;

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
        {/* Board */}
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
          <LudoBoard
            boardData={boardData}
            gameState={gameState}
            selectedToken={selectedToken}
            setSelectedToken={setSelectedToken}
            handleTokenClick={handleTokenClick}
          />
        </div>

        {/* Dice */}
        <div
          className="absolute transition-all duration-700 ease-in-out"
          style={dicePositions[currentTurnColorIndex]}
        >
          <DiceHolder
            turnColorIndex={currentTurnColorIndex}
            diceValue={diceValue}
            rolling={rolling}
            onRoll={handleRollDice}
          />
        </div>
      </div>
    </div>
  );
};

export default LudoGameRoom;