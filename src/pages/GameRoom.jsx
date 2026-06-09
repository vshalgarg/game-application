// import { useState } from "react";
// import { useParams } from "react-router-dom";

// import TicTacToeBoard from "../components/TicTacToeBoard";
// import GameButton from "../components/GameButton";

// import { makeMove } from "../services/roomService";
// import useGameRealtime from "../hooks/useGameRealtime";

// const GameRoom = () => {
//   const { roomCode } = useParams();

//   // Backend returns a 3x3 board
//   const [board, setBoard] = useState([
//     ["", "", ""],
//     ["", "", ""],
//     ["", "", ""],
//   ]);

//   const [currentTurn, setCurrentTurn] = useState(null);
//   const [winner, setWinner] = useState(null);
//   const [status, setStatus] = useState("RUNNING");

//   const storedAuth = JSON.parse(
//   localStorage.getItem("user")
// );

// const currentUserId = storedAuth?.userId;

//   // Realtime Listener
//   useGameRealtime({
//     roomCode,
//     onGameUpdate: (game) => {
//       console.log("Realtime Move Received:", game);  // 
//       console.log("Move Data:", game.move_data);   // this is the reason for player's board not updated
//       // supabase only returns move_data, but make move api returns everything 

//       // For now only logging because realtime payload
//       // contains row/col only, not full board state.
//     },
//   });

//   // Handle board click
//   const handleCellClick = async (index) => {
//     try {
//       const row = Math.floor(index / 3);
//       const col = index % 3;

//       console.log("Making move:", { row, col });

//       const res = await makeMove({
//         roomCode,
//         userId: currentUserId,
//         row,
//         col,
//       });

//       console.log("Move Response:", res);

//       if (res.success) {
//         setBoard(res.data.gameState.board);
//         console.log("makemove",res.data.currentTurnUserId)

//         setCurrentTurn(res.data.currentTurnUserId);

//         setWinner(res.data.winnerUserId);

//         setStatus(res.data.status);
//       }
//     } catch (err) {
//       console.error("Move failed:", err);
//       console.log( "Backend Error:", err.response?.data );
//       alert("Invalid move or not your turn");
//     }
//   };

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
//         px-4
//       "
//     >
//       <div
//         className="
//           w-full
//           max-w-md
//           bg-white/10
//           backdrop-blur-lg
//           border
//           border-white/20
//           rounded-2xl
//           shadow-xl
//           p-5
//           text-center
//         "
//       >
//         {/* Room ID */}
//         <p
//           className="
//             text-cyan-400
//             mb-2
//             text-sm
//             tracking-widest
//           "
//         >
//           ROOM ID : {roomCode}
//         </p>

//         {/* Heading */}
//         <h1
//           className="
//             text-2xl
//             font-bold
//             text-white
//             mb-3
//           "
//         >
//           Tic Tac Toe
//         </h1>

//         {/* Game Status */}
//         <p className="text-gray-300 text-base mb-2">
//           Status : {status}
//         </p>

//         <p className="text-gray-300 text-base mb-2">
//           Current Turn User : {currentTurn ?? ""}
//         </p>

//         {winner && (
//           <p className="text-green-400 font-semibold mb-4">
//             Winner User ID : {winner}
//           </p>
//         )}

//         {/* Board */}
//         <div className="flex justify-center mb-5">
//           <TicTacToeBoard
//             board={board}
//             handleClick={handleCellClick}
//           />
//         </div>

//         {/* Restart Button */}
//         <GameButton
//           title="Restart Game"
//           color="
//             bg-red-500
//             hover:bg-red-600
//             shadow-red-500/40
//           "
//         />
//       </div>
//     </div>
//   );
// };

// export default GameRoom;


import { useState } from "react";
import { useParams } from "react-router-dom";

import TicTacToeBoard from "../components/TicTacToeBoard";
import GameButton from "../components/GameButton";

import { makeMove } from "../services/roomService";
import useGameRealtime from "../hooks/useGameRealtime";

const GameRoom = () => {
  const { roomCode } = useParams();

  const [board, setBoard] = useState([
    ["", "", ""],
    ["", "", ""],
    ["", "", ""],
  ]);

  const [currentTurn, setCurrentTurn] = useState(null);
  const [winner, setWinner] = useState(null);
  const [status, setStatus] = useState("RUNNING");

  const storedAuth = JSON.parse(
    localStorage.getItem("user")
  );

  const currentUserId = storedAuth?.userId;

  // Realtime Listener
  useGameRealtime({
    roomCode,

    onGameUpdate: (game) => {
      console.log("Realtime Game State:", game);

      setBoard(
        game.game_state_data?.board || [
          ["", "", ""],
          ["", "", ""],
          ["", "", ""],
        ]
      );

      setCurrentTurn(
        game.current_turn_user_id
      );

      setStatus(
        game.game_status
      );

      setWinner(
        game.winner_user_id
      );
    },
  });

  // Handle board click
  const handleCellClick = async (index) => {
    try {
      const row = Math.floor(index / 3);
      const col = index % 3;

      console.log("Making move:", { row, col });

      const res = await makeMove({
        roomCode,
        userId: currentUserId,
        row,
        col,
      });

      console.log("Move Response:", res);

      if (res.success) {
        setBoard(
          res.data.gameState.board
        );

        setCurrentTurn(
          res.data.currentTurnUserId
        );

        setWinner(
          res.data.winnerUserId
        );

        setStatus(
          res.data.status
        );
      }
    } catch (err) {
      console.error("Move failed:", err);

      console.log(
        "Backend Error:",
        err.response?.data
      );

      alert("Invalid move or not your turn");
    }
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
        px-4
      "
    >
      <div
        className="
          w-full
          max-w-md
          bg-white/10
          backdrop-blur-lg
          border
          border-white/20
          rounded-2xl
          shadow-xl
          p-5
          text-center
        "
      >
        <p
          className="
            text-cyan-400
            mb-2
            text-sm
            tracking-widest
          "
        >
          ROOM ID : {roomCode}
        </p>

        <h1
          className="
            text-2xl
            font-bold
            text-white
            mb-3
          "
        >
          Tic Tac Toe
        </h1>

        <p className="text-gray-300 text-base mb-2">
          Status : {status}
        </p>

        <p className="text-gray-300 text-base mb-2">
          Current Turn User : {currentTurn ?? ""}
        </p>

        {winner && (
          <p className="text-green-400 font-semibold mb-4">
            Winner User ID : {winner}
          </p>
        )}

        <div className="flex justify-center mb-5">
          <TicTacToeBoard
            board={board}
            handleClick={handleCellClick}
          />
        </div>

        <GameButton
          title="Restart Game"
          color="
            bg-red-500
            hover:bg-red-600
            shadow-red-500/40
          "
        />
      </div>
    </div>
  );
};

export default GameRoom;