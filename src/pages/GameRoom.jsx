import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Confetti from "react-confetti";
import { useWindowSize } from "react-use";
import { FaRedo } from "react-icons/fa";
import { TbMoodSad, TbMoodSmileBeam } from "react-icons/tb";
import TicTacToeBoard from "../components/TicTacToeBoard";
import { makeMove, restartRoom } from "../services/roomService";
import useGameRealtime from "../hooks/useGameRealtime";
import { supabase } from "../utils/supabaseClient";
import { useSnackbar } from "../context/SnackbarContext";
import { useAuth } from "../context/AuthContext";
import PageShell from "../components/layout/PageShell";
import Button from "../components/ui/Button";

const RestartGameButton = ({ onClick }) => (
  <Button onClick={onClick}>
    <span className="flex items-center justify-center gap-2">
      <FaRedo size={13} />
      Restart Game
    </span>
  </Button>
);

const GameRoom = () => {
  const { roomCode } = useParams();
  const navigate = useNavigate();
  const { showSnackbar } = useSnackbar();
  const { auth } = useAuth();

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
  const currentUserId = auth?.userId;

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
      setBoard(
        game.game_state_data?.board || [
          ["", "", ""],
          ["", "", ""],
          ["", "", ""],
        ],
      );

      // to display the winning pattern green hightlighted cell on both tabs
      if (game.winner_user_id) {
        const pattern = checkWinningPattern(game.game_state_data?.board);
        setWinningPattern(pattern);
      } else {
        setWinningPattern(null);
      }

      setCurrentTurn(game.current_turn_user_id);
      setStatus(game.game_status);

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

      if (flatBoard[a] && flatBoard[a] === flatBoard[b] && flatBoard[a] === flatBoard[c]) {
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

      const res = await makeMove({
        roomCode,
        userId: currentUserId,
        row,
        col,
      });

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
    } catch (error) {
      console.error("Move failed:", error);
      showSnackbar(error.message || "Not your turn", "error");
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
    <PageShell className="gz-page-shell--ttt">
      {showDrawPopup && (
        <div className="gz-exit-overlay">
          <div className="gz-exit-modal">
            <div className="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-full border-2 border-gz-primary-cyan bg-gz-primary-cyan/10 shadow-lg shadow-gz-primary-cyan/30 sm:h-20 sm:w-20">
              <TbMoodSad className="text-[36px] text-gz-primary-cyan sm:text-[42px]" />
            </div>
            <h2 className="text-3xl font-bold tracking-widest text-gz-primary-cyan sm:text-4xl">
              DRAW
            </h2>
            <div className="gz-divider my-5 justify-center">
              <span className="h-1.5 w-1.5 rounded-full bg-gz-primary-cyan" />
            </div>
            <p className="mb-6 text-sm text-gz-text-secondary sm:text-base">
              Ready for another battle?
            </p>
            <div className="flex flex-col gap-3">
              {isHost && <RestartGameButton onClick={handleRestart} />}
              <Button variant="secondary" onClick={() => navigate("/")}>
                Back To Home
              </Button>
            </div>
          </div>
        </div>
      )}

      {showWinnerPopup && winner === currentUserId && (
        <div className="pointer-events-none fixed inset-0 z-[61]">
          <Confetti
            width={width}
            height={height}
            recycle={false}
            numberOfPieces={400}
            gravity={0.15}
          />
        </div>
      )}

      {showWinnerPopup && (
        <div className="gz-exit-overlay">
          <div className="gz-exit-modal">
            <div
              className={`mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-full border-2 sm:h-20 sm:w-20 ${
                winner === currentUserId
                  ? "border-green-400 bg-green-400/15 shadow-[0_0_30px_rgba(74,222,128,0.7)]"
                  : "border-red-400 bg-red-400/15 shadow-[0_0_30px_rgba(248,113,113,0.7)]"
              }`}
            >
              {winner === currentUserId ? (
                <TbMoodSmileBeam className="text-[40px] text-green-300 sm:text-[46px]" />
              ) : (
                <TbMoodSad className="text-[40px] text-red-300 sm:text-[46px]" />
              )}
            </div>
            <h2
              className={`text-3xl font-extrabold tracking-[0.18em] sm:text-4xl ${
                winner === currentUserId ? "text-green-300" : "text-red-300"
              }`}
            >
              {winner === currentUserId ? "YOU WIN" : "YOU LOSE"}
            </h2>
            <div className="my-5 flex items-center justify-center gap-3">
              <div
                className={`h-px w-14 ${winner === currentUserId ? "bg-green-400/50" : "bg-red-400/50"}`}
              />
              <div
                className={`h-1.5 w-1.5 rounded-full ${winner === currentUserId ? "bg-green-300" : "bg-red-300"}`}
              />
              <div
                className={`h-px w-14 ${winner === currentUserId ? "bg-green-400/50" : "bg-red-400/50"}`}
              />
            </div>
            <p className="text-base text-gz-text sm:text-lg">
              {winner === currentUserId ? "Congratulations!" : "Better luck next time!"}
            </p>
            <p className="mt-1 mb-6 text-sm text-gz-text-secondary">
              {winner === currentUserId
                ? "You played brilliantly."
                : "Ready for another challenge?"}
            </p>
            <div className="flex flex-col gap-3">
              {isHost && <RestartGameButton onClick={handleRestart} />}
              <Button variant="secondary" onClick={() => navigate("/")}>
                Back To Home
              </Button>
            </div>
          </div>
        </div>
      )}

      <div className="gz-ttt-card">
        <p className="text-xs font-semibold tracking-[0.22em] text-gz-primary-cyan sm:text-sm">
          ROOM ID : {roomCode}
        </p>
        <h1 className="mt-1.5 text-2xl font-bold text-gz-text sm:text-[1.75rem]">Tic Tac Toe</h1>

        <div className="gz-ttt-versus">
          <div className="flex min-w-0 flex-1 flex-col items-center">
            <span className={`gz-ttt-mark gz-ttt-mark--x ${xActive ? "gz-ttt-mark--active" : ""}`}>
              X
            </span>
            <p className="mt-1 text-xs text-gz-text sm:text-sm">
              {mySide === "X" ? "You" : "Opponent"}
            </p>
          </div>

          <div className="flex shrink-0 items-center gap-2">
            <span className="h-8 w-px bg-gz-input-border/70" />
            <span className="gz-ttt-vs">vs</span>
            <span className="h-8 w-px bg-gz-input-border/70" />
          </div>

          <div className="flex min-w-0 flex-1 flex-col items-center">
            <span className={`gz-ttt-mark gz-ttt-mark--o ${oActive ? "gz-ttt-mark--active" : ""}`}>
              O
            </span>
            <p className="mt-1 text-xs text-gz-text sm:text-sm">
              {mySide === "O" ? "You" : currentTurn === -1 ? "Bot is thinking.." : "Opponent"}
            </p>
          </div>
        </div>

        <div className="gz-ttt-board-wrap">
          <TicTacToeBoard
            board={board}
            handleClick={handleCellClick}
            winningPattern={winningPattern}
          />
        </div>

        {isHost && !winner && <RestartGameButton onClick={handleRestart} />}
      </div>
    </PageShell>
  );
};

export default GameRoom;