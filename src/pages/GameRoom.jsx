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
  const [showDrawPopup, setShowDrawPopup] = useState(false);
  const [mySide, setMySide] = useState(null);
  const [hostUserId, setHostUserId] = useState(null);
  const { showSnackbar } = useSnackbar();

  const storedAuth = JSON.parse(localStorage.getItem("user"));
  const currentUserId = storedAuth?.userId;
  
  // delay in winner pop up for line animation and cell highlighting 
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

  // delay for draw pop up 
  useEffect(() => {
    if (status === "DRAW") {
      const timer = setTimeout(() => {
        setShowDrawPopup(true);
        }, 1000); // 1 second delay

        return () => clearTimeout(timer);
  } else {
        setShowDrawPopup(false);
      }
    }, [status]);

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
    const hostPlayer = data.players?.find((player) => player.role === "HOST");
    const isHostPlayer = currentPlayer?.role === "HOST";

    setIsHost(isHostPlayer);
    setHostUserId(hostPlayer?.user_id);

    // for visual switching 
    setMySide(isHostPlayer ? "X" : "O");
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
      setStatus("INITIALIZED");
      setWinningPattern(null); // remove the green highlighted cells 

    } catch (err) {
      console.error("Restart failed:", err);
    }
  };

  // Realtime Listener (listens for realtime events)
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

        const pattern = checkWinningPattern(game.game_state_data?.board);
        console.log("Realtime Winning Pattern:", pattern );

        setWinningPattern(pattern);
    } else {
        setWinningPattern(null);
        }

        setCurrentTurn(game.current_turn_user_id);
        setStatus( game.game_status );

        if (game.game_status === "INITIALIZED") {
          setWinner(null);
          setWinningPattern(null);
      } else {
          setWinner(game.winner_user_id);
}
        setWinner(game.winner_user_id);
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
        setBoard(res.data.gameState.board);
        setCurrentTurn(res.data.currentTurnUserId);

        if (res.data.winnerUserId) {
           const pattern = checkWinningPattern(res.data.gameState.board);
            setWinningPattern(pattern);
            setWinner(res.data.winnerUserId);
        }
        setStatus(res.data.status);
      }
    } catch (err) {
      console.error("Move failed:", err);
      showSnackbar("Invalid move.","error");
    }
  };

  const effectiveCurrentTurn = currentTurn ?? hostUserId;

  let xActive = false;
  let oActive = false;

  if (mySide === "X") {
    xActive = effectiveCurrentTurn === currentUserId;
    oActive = effectiveCurrentTurn !== currentUserId;
  } else if (mySide === "O") {
    oActive = effectiveCurrentTurn === currentUserId;
    xActive = effectiveCurrentTurn !== currentUserId;
  }
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
    {showDrawPopup && (
      <>
    {/* Background Overlay */}
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm z-40" />

    {/* Draw Popup */}
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
        border-yellow-500/40
        rounded-2xl
        shadow-2xl
        p-6
        w-[90%]
        max-w-sm
        text-center
      "
    >
      <h2 className="text-3xl font-bold text-yellow-400 mb-4">
         It's a Draw!
      </h2>

      <p className="text-white text-lg mb-2">
        ⚔️ What a battle!
      </p>

      <p className="text-gray-300 mb-6">
        
        <br />
        🎮 Ready for a rematch?
      </p>

      <div className="flex flex-col gap-3">
        {isHost && (
          <GameButton
            title=" Restart Game"
            color="
              bg-red-500
              hover:bg-red-600
              shadow-red-500/40
            "
            onClick={handleRestart}
          />
        )}

        <GameButton
          title=" Back To Home"
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

      {/* confetti */}
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

    {/* game card */}
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


        <div className="mb-5">

  <div className="flex justify-around items-center">

    {/* X */}

    <div className="flex flex-col items-center">

      <span
        className={`text-6xl font-bold transition-all duration-300 ${
          xActive
            ? "text-cyan-400 scale-125 animate-pulse"
            : "text-gray-500"
        }`}
      >
        X
      </span>

     <p className="text-gray-300 mt-2 text-sm">
  {mySide === "X" ? "You" : "Opponent"}
</p>

    </div>

    {/* O */}

    <div className="flex flex-col items-center">

      <span
        className={`text-6xl font-bold transition-all duration-300 ${
          oActive
            ? "text-cyan-400 scale-125 animate-pulse"
            : "text-gray-500"
        }`}
      >
        O
      </span>

      <p className="text-gray-300 mt-2 text-sm">
  {mySide === "O"
      ? "You"
      : currentTurn === -1
      ? "Bot"
      : "Opponent"}
</p>

    </div>

  </div>

</div>

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