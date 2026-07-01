import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";

import Confetti from "react-confetti";
import { useWindowSize } from "react-use";

import TicTacToeBoard from "../components/TicTacToeBoard";
import GameButton from "../components/GameButton";

import { makeMove } from "../services/roomService";
import { restartRoom } from "../services/roomService";
import useGameRealtime from "../hooks/useGameRealtime";
import { supabase } from "../utils/supabaseClient";

import { useSnackbar } from "../context/SnackbarContext";

const GameRoom = () => {
  const { roomCode } = useParams();
  const navigate = useNavigate();
  const { width, height } = useWindowSize();


  const [board, setBoard] = useState([
    ["", "", ""],
    ["", "", ""],
    ["", "", ""],
  ]);

  const [currentTurn, setCurrentTurn] = useState();
  const [winner, setWinner] = useState(null);  
  const [status, setStatus] = useState("RUNNING");
  const [winningPattern, setWinningPattern] = useState(null);
  const [showWinnerPopup, setShowWinnerPopup] = useState(false);

  const { showSnackbar } = useSnackbar();

  const storedAuth = JSON.parse(localStorage.getItem("user"));

  const currentUserId = storedAuth?.userId;
  
  // delay for pop up for line animation and cell highlighting 
  useEffect(() => {
  if (winner) {
    const timer = setTimeout(() => {
      setShowWinnerPopup(true);
    }, 2000); // 2 seconds

    return () => clearTimeout(timer);
  } else {
    setShowWinnerPopup(false);
  }
}, [winner]);

  // Supabase used to get the roles for restart button 
  const [isHost, setIsHost] = useState(false);
  useEffect(() => {
  const fetchRole = async () => {
    const { data, error } = await supabase
      .from("realtime_room_lobby") // your table name
      .select("players")
      .eq("room_code", roomCode)
      .single();

    if (error) {
      console.error("Role fetch error:", error);
      return;
    }

    const currentPlayer = data.players?.find((player) => player.user_id === currentUserId);

     console.log("Current Player:", currentPlayer);

    setIsHost(currentPlayer?.role === "HOST");
  };

  fetchRole();
}, [roomCode, currentUserId]);

   //  handleRestart Button Call 
  const handleRestart = async () => {
    try {
      const res = await restartRoom({
        roomCode,
        userId: currentUserId,
      });

       console.log("Game restarted  by:", currentUserId);
       console.log("Game restarted:", res);

      // Immediately reset UI
      setBoard([
        ["", "", ""],
        ["", "", ""],
        ["", "", ""],
      ]);
      setWinner(null);
      setStatus("RUNNING");
      setWinningPattern(null); // remove the green highlighted cells 

    } catch (err) {
      console.error("Restart failed:", err);
    }
  };

  // Realtime Listener
  useGameRealtime({
    roomCode,

    onGameUpdate: (game) => {
      console.log("Realtime Game State:", game);
      console.log("UPDATE",Date.now(),game.game_state_data?.board);
      console.log("Realtime Update", game.version,game.updated_at, game.game_state_data?.board);

      setBoard(
        game.game_state_data?.board || [
          ["", "", ""],
          ["", "", ""],
          ["", "", ""],
        ]
      );
  // to display the winning pattern green hightlighted cell on both tabs 
      if (game.winner_user_id) {
      const pattern = checkWinningPattern(
game.game_state_data?.board
  );

  console.log("Realtime Winning Pattern:", pattern );

  setWinningPattern(pattern);
      }   else {
          setWinningPattern(null);
        }

  setCurrentTurn(game.current_turn_user_id);

  setStatus( game.game_status );
      if (game.game_status === "INITIALIZED") {
           setWinner(null);
           setWinningPattern(null);
}     else {
  setWinner(game.winner_user_id);
}

      setWinner(game.winner_user_id );
    },
  });
  // checking winning patterns from the api response 
const checkWinningPattern = (board) => {
  const lines = [
    [0, 1, 2],
    [3, 4, 5],
    [6, 7, 8],

    [0, 3, 6],
    [1, 4, 7],
    [2, 5, 8],

    [0, 4, 8],
    [2, 4, 6],
  ];

  const flatBoard = board.flat();

  for (const line of lines) {
    const [a, b, c] = line;

    if (
      flatBoard[a] &&
      flatBoard[a] === flatBoard[b] &&
      flatBoard[a] === flatBoard[c]
    ) {
      return line;
    }
  }

  return null;
};

  // Handle board click
  const handleCellClick = async (index) => {
    
    try {
      // Disable board after winner is declared
    if (winner) {
      return;
    }

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

        if (res.data.winnerUserId) {
           const pattern = checkWinningPattern(res.data.gameState.board);

            console.log("Winning Pattern:", pattern);

            setWinningPattern(pattern);

            setWinner(res.data.winnerUserId);
        }

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

      showSnackbar("Invalid move.","error");
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
      {showWinnerPopup && winner === currentUserId && (
  <div className="fixed inset-0 z-45 pointer-events-none">
    <Confetti
      width={width}
      height={height}
      recycle={false}
      numberOfPieces={400}
      gravity={0.15}
    />
  </div>
)}
      {/* pop up */}
      {showWinnerPopup && (
      <>
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm z-40" />

        <div
          className="
            fixed
            top-1/2
            left-1/2
            -translate-x-1/2
            -translate-y-1/2
            z-50
            bg-gray-900
            border
            border-green-500/40
            rounded-2xl
            shadow-2xl
            p-6
            w-[90%]
            max-w-sm
             text-center 
          "
        >
          <h2 className="text-3xl font-bold text-green-400 mb-4">
            🎉 Game Over
          </h2>

          <p className="text-white text-lg mb-6">
            {winner === currentUserId
              ? "🏆 You Won!"
              : "😔 You Lose!"}
          </p>

          <div className="flex flex-col gap-3">
            {isHost && (
              <GameButton
                title="Restart Game"
                color="
                  bg-red-500
                  hover:bg-red-600
                  shadow-red-500/40
                "
                onClick={handleRestart}
              />
            )}
            <GameButton
              title="Back To Home"
              color="
                bg-blue-500
                hover:bg-blue-600
                shadow-blue-500/40
              "
              onClick={() => navigate("/")}
            />
          </div>
        </div>
      </>
    )}
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
          Current Turn User :{" "}
          {
            currentTurn === currentUserId
              ? "You"
                : currentTurn === -1
                ? "Bot"
                : currentTurn
           }
        </p>

        {winner && (
          <p className=" text-white text-lg mb-4">
             {winner === currentUserId ? "🎉 You Won!": "😔 You Lose!"}</p>
        )}

        <div className="flex justify-center mb-5">
          <TicTacToeBoard
            board={board}
            handleClick={handleCellClick}
            winningPattern={winningPattern}
          />
        </div>

        {/* Normal Restart Button visible only while game is running */}
        {isHost && !winner &&  (
        <GameButton
          title="Restart Game"
          color="
            bg-red-500
            hover:bg-red-600
            shadow-red-500/40
          "
        onClick={handleRestart}
        />
        )}
      </div>
    </div>
  );
};

export default GameRoom;

// import { useState, useEffect } from "react";
// import { useParams } from "react-router-dom";
// import { useNavigate } from "react-router-dom";

// import Confetti from "react-confetti";
// import { useWindowSize } from "react-use";

// import TicTacToeBoard from "../components/TicTacToeBoard";
// import GameButton from "../components/GameButton";

// import { makeMove } from "../services/roomService";
// import { restartRoom } from "../services/roomService";
// import useGameRealtime from "../hooks/useGameRealtime";
// import { supabase } from "../utils/supabaseClient";

// const GameRoom = () => {
//   const { roomCode } = useParams();
//   const navigate = useNavigate();
//   const { width, height } = useWindowSize();


//   const [board, setBoard] = useState([
//     ["", "", ""],
//     ["", "", ""],
//     ["", "", ""],
//   ]);

//   const [currentTurn, setCurrentTurn] = useState();
//   const [winner, setWinner] = useState(null);
//   const [status, setStatus] = useState("RUNNING");
//   const [winningPattern, setWinningPattern] = useState(null);
//   const [showWinnerPopup, setShowWinnerPopup] = useState(false);
//   const [hostId, setHostId] = useState(null);
//   const [playerId, setPlayerId] = useState(null);

//   const storedAuth = JSON.parse(localStorage.getItem("user"));

//   const currentUserId = storedAuth?.userId;
  
//   // delay for pop up for line animation and cell highlighting 
//   useEffect(() => {
//   if (winner) {
//     const timer = setTimeout(() => {
//       setShowWinnerPopup(true);
//     }, 2000); // 2 seconds

//     return () => clearTimeout(timer);
//   } else {
//     setShowWinnerPopup(false);
//   }
// }, [winner]);

//   // Supabase used to get the roles for restart button 
//   const [isHost, setIsHost] = useState(false);
//   useEffect(() => {
//   const fetchRole = async () => {
//     const { data, error } = await supabase
//       .from("realtime_room_lobby") // your table name
//       .select("players")
//       .eq("room_code", roomCode)
//       .single();

//     if (error) {
//       console.error("Role fetch error:", error);
//       return;
//     }

//     const currentPlayer = data.players?.find((player) => player.user_id === currentUserId);
//     const host = data.players?.find((player) => player.role === "HOST");
//     const player = data.players?.find((player) => player.role === "PLAYER");

//     setHostId(host?.user_id);
//     setPlayerId(player?.user_id);

//      console.log("Current Player:", currentPlayer);

//     setIsHost(currentPlayer?.role === "HOST");
//   };

//   fetchRole();
// }, [roomCode, currentUserId]);

//    //  handleRestart Button Call 
//   const handleRestart = async () => {
//     try {
//       const res = await restartRoom({
//         roomCode,
//         userId: currentUserId,
//       });

//        console.log("Game restarted  by:", currentUserId);
//        console.log("Game restarted:", res);

//       // Immediately reset UI
//       setBoard([
//         ["", "", ""],
//         ["", "", ""],
//         ["", "", ""],
//       ]);
//       setWinner(null);
//       setStatus("RUNNING");
//       setWinningPattern(null); // remove the green highlighted cells 

//     } catch (err) {
//       console.error("Restart failed:", err);
//     }
//   };

//   // Realtime Listener
//   useGameRealtime({
//     roomCode,

//     onGameUpdate: (game) => {
//       console.log("Realtime Game State:", game);
//       console.log("UPDATE",Date.now(),game.game_state_data?.board);
//       console.log("Realtime Update", game.version,game.updated_at, game.game_state_data?.board);

//       setBoard(
//         game.game_state_data?.board || [
//           ["", "", ""],
//           ["", "", ""],
//           ["", "", ""],
//         ]
//       );
//   // to display the winning pattern green hightlighted cell on both tabs 
//       if (game.winner_user_id) {
//       const pattern = checkWinningPattern(
// game.game_state_data?.board
//   );

//   console.log("Realtime Winning Pattern:", pattern );

//   setWinningPattern(pattern);
//       }   else {
//           setWinningPattern(null);
//         }

//   setCurrentTurn(game.current_turn_user_id);

//   setStatus( game.game_status );
//       if (game.game_status === "INITIALIZED") {
//            setWinner(null);
//            setWinningPattern(null);
// }     else {
//   setWinner(game.winner_user_id);
// }

//       setWinner(game.winner_user_id );
//     },
//   });
//   // checking winning patterns from the api response 
// const checkWinningPattern = (board) => {
//   const lines = [
//     [0, 1, 2],
//     [3, 4, 5],
//     [6, 7, 8],

//     [0, 3, 6],
//     [1, 4, 7],
//     [2, 5, 8],

//     [0, 4, 8],
//     [2, 4, 6],
//   ];

//   const flatBoard = board.flat();

//   for (const line of lines) {
//     const [a, b, c] = line;

//     if (
//       flatBoard[a] &&
//       flatBoard[a] === flatBoard[b] &&
//       flatBoard[a] === flatBoard[c]
//     ) {
//       return line;
//     }
//   }

//   return null;
// };

//   // Handle board click
//   const handleCellClick = async (index) => {
    
//     try {
//       // Disable board after winner is declared
//     if (winner) {
//       return;
//     }

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
//         setBoard(
//           res.data.gameState.board
//         );

//         setCurrentTurn(
//           res.data.currentTurnUserId
//         );

//         if (res.data.winnerUserId) {
//            const pattern = checkWinningPattern(res.data.gameState.board);

//             console.log("Winning Pattern:", pattern);

//             setWinningPattern(pattern);

//             setWinner(res.data.winnerUserId);
//         }

//         setStatus(
//           res.data.status
//         );
//       }
//     } catch (err) {
//       console.error("Move failed:", err);

//       console.log(
//         "Backend Error:",
//         err.response?.data
//       );

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
//       {showWinnerPopup && winner === currentUserId && (
//   <div className="fixed inset-0 z-45 pointer-events-none">
//     <Confetti
//       width={width}
//       height={height}
//       recycle={false}
//       numberOfPieces={400}
//       gravity={0.15}
//     />
//   </div>
// )}
//       {/* pop up */}
//       {showWinnerPopup && (
//       <>
//         <div className="fixed inset-0 bg-black/40 backdrop-blur-sm z-40" />

//         <div
//           className="
//             fixed
//             top-1/2
//             left-1/2
//             -translate-x-1/2
//             -translate-y-1/2
//             z-50
//             bg-gray-900
//             border
//             border-green-500/40
//             rounded-2xl
//             shadow-2xl
//             p-6
//             w-[90%]
//             max-w-sm
//              text-center 
//           "
//         >
//           <h2 className="text-3xl font-bold text-green-400 mb-4">
//             🎉 Game Over
//           </h2>

//           <p className="text-white text-lg mb-6">
//             {winner === currentUserId
//               ? "🏆 You Won!"
//               : "😔 You Lose!"}
//           </p>

//           <div className="flex flex-col gap-3">
//             {isHost && (
//               <GameButton
//                 title="Restart Game"
//                 color="
//                   bg-red-500
//                   hover:bg-red-600
//                   shadow-red-500/40
//                 "
//                 onClick={handleRestart}
//               />
//             )}
//             <GameButton
//               title="Back To Home"
//               color="
//                 bg-blue-500
//                 hover:bg-blue-600
//                 shadow-blue-500/40
//               "
//               onClick={() => navigate("/")}
//             />
//           </div>
//         </div>
//       </>
//     )}
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

//         <p className="text-gray-300 text-base mb-2">
//           Status : {status}
//         </p>

//         <div className="flex justify-between items-center mb-5">

//   {/* X Player */}
//   <div className="text-center">
//     <div
//       className={`
//         text-5xl
//         font-extrabold
//         transition-all
//         duration-300
//         ${
//           currentTurn === hostId
//             ? "text-cyan-400 scale-125"
//             : "text-gray-500"
//         }
//       `}
//     >
//       X
//     </div>

//     <p
//       className={`
//         text-sm mt-2
//         ${
//           currentTurn === hostId
//             ? "text-cyan-300"
//             : "text-gray-500"
//         }
//       `}
//     >
//       {hostId}
//     </p>
//   </div>

//   {/* VS */}
//   <div className="text-gray-600 text-xl font-bold">
//     VS
//   </div>

//   {/* O Player */}
//   <div className="text-center">
//     <div
//       className={`
//         text-5xl
//         font-extrabold
//         transition-all
//         duration-300
//         ${
//           currentTurn === playerId
//             ? "text-cyan-400 scale-125"
//             : "text-gray-500"
//         }
//       `}
//     >
//       O
//     </div>

//     <p
//       className={`
//         text-sm mt-2
//         ${
//           currentTurn === playerId
//             ? "text-cyan-300"
//             : "text-gray-500"
//         }
//       `}
//     >
//       {playerId}
//     </p>
//   </div>

// </div>

//         {winner && (
//           <p className=" text-white text-lg mb-4">
//              {winner === currentUserId ? "🎉 You Won!": "😔 You Lose!"}</p>
//         )}

//         <div className="flex justify-center mb-5">
//           <TicTacToeBoard
//             board={board}
//             handleClick={handleCellClick}
//             winningPattern={winningPattern}
//           />
//         </div>

//         {/* Normal Restart Button visible only while game is running */}
//         {isHost && !winner &&  (
//         <GameButton
//           title="Restart Game"
//           color="
//             bg-red-500
//             hover:bg-red-600
//             shadow-red-500/40
//           "
//         onClick={handleRestart}
//         />
//         )}
//       </div>
//     </div>
//   );
// };

// export default GameRoom;