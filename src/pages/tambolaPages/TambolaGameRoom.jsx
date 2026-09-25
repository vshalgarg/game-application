import { useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { FaUsers, FaTicketAlt, FaTh, FaPlus, FaBookOpen, FaArrowRight } from "react-icons/fa";
import { LuX } from "react-icons/lu";
import PageShell from "../../components/layout/PageShell";
import ExitGamePopup from "../../components/ui/ExitGamePopup";
import useBackExitGuard from "../../hooks/useBackExitGuard";
import useTambolaGameStateRealtime from "../../hooks/useTambolaGameStateRealtime";
import useTambolaTicketRealtime from "../../hooks/useTambolaTicketRealtime";
import { useAuth } from "../../context/AuthContext";

const TambolaGameRoom = () => {

  const { roomCode } = useParams();
  const { auth } = useAuth();
  const [showExitPopup, setShowExitPopup] = useState(false);
  const [markedNumbers, setMarkedNumbers] = useState([]);

  const closeExitPopup = () => setShowExitPopup(false);
  const openExitPopup = () => setShowExitPopup(true);

  useBackExitGuard(openExitPopup);

  const currentUserId = auth?.userId;

  // game room realtime listner
  const {
    gameState: realtimeGameState,
    loading: gameStateLoading,
  } = useTambolaGameStateRealtime(roomCode);
  console.log("Tambola realtime game state:", realtimeGameState);
  console.log("Game state loading:", gameStateLoading);

  // ticket generator realtime listner
  const {
    ticket,
    loading: ticketLoading,
    error: ticketError
  } = useTambolaTicketRealtime(roomCode, currentUserId);
  console.log("ticket", ticket);
  console.log("ticket loading", ticketLoading);

  const calledNumbers = realtimeGameState?.called_numbers || [];
  const lastCalled = calledNumbers.length > 0 ? calledNumbers[calledNumbers.length - 1] : null;
  const players = realtimeGameState?.players || [];
  const playersCount = players.length;
  const gameStatus = realtimeGameState?.game_status;

  const numbers = useMemo(() => Array.from({ length: 90 }, (_, index) => index + 1),[]);

  const handleTicketNumberClick = (number) => {
    if (!calledNumbers.includes(number)) return;

    setMarkedNumbers((prev) =>
      prev.includes(number)
        ? prev.filter((item) => item !== number)
        : [...prev, number]
    );
  };

  return (
    <PageShell>
  <div className="relative flex min-h-[calc(100vh-2rem)] w-full flex-col gap-3 py-2">

    {/* EXIT BUTTON */}
    <button
      type="button"
      onClick={openExitPopup}
      className="gz-exit-trigger absolute right-1 top-0 z-30"
      aria-label="Exit game"
    >
      <LuX />
    </button>

    <ExitGamePopup
      open={showExitPopup}
      onClose={closeExitPopup}
      title="Exit Game?"
      message="Are you sure you want to exit?"
      hint="You will leave the current game."
      stayLabel="Stay in Game"
      confirmLabel="Exit Game"
    />

    {/* HEADER */}
    <div className="w-full rounded-2xl border border-gz-primary-cyan/50 bg-gz-popup/80 px-4 py-3 shadow-[0_0_25px_rgba(34,211,238,0.08)] backdrop-blur-xl">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">

        {/* LEFT HEADER */}
        <div className="flex items-center gap-4">

          {/* ROOM */}
          <div>
            <p className="text-[10px] font-medium text-gz-text-secondary">
              Room ID
            </p>

            <div className="flex items-center gap-2">
              <span className="text-lg font-bold tracking-wider text-gz-text">
                {roomCode}
              </span>

              <span
                className={`rounded-full border px-2.5 py-1 text-[10px] font-bold ${
                  gameStatus === "RUNNING"
                    ? "border-emerald-400/50 bg-emerald-400/15 text-emerald-300"
                    : "border-slate-400/50 bg-slate-400/15 text-slate-300"
                }`}
              >
                {gameStatus === "RUNNING"
                  ? "LIVE"
                  : gameStatus || "WAITING"}
              </span>
            </div>
          </div>

          <div className="hidden h-8 w-px bg-gz-input-border/50 sm:block" />

          {/* PLAYERS */}
          <div className="flex items-center gap-2">
            <FaUsers
              className="text-gz-primary-cyan"
              size={18}
            />

            <p className="text-sm font-semibold text-gz-text">
              {playersCount} Players
            </p>
          </div>
        </div>

        {/* CENTER / RIGHT HEADER */}
        <div className="flex items-center justify-center gap-6">

          {/* LAST CALLED */}
          <div className="text-center">
            <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-full border-2 border-gz-primary-cyan/70 bg-gz-popup shadow-[0_0_18px_rgba(34,211,238,0.25)]">
              <span className="text-xl font-bold text-gz-text">
                {lastCalled}
              </span>
            </div>

            <p className="mt-1 text-[10px] text-gz-text-secondary">
              Called Number
            </p>
          </div>

          <div className="hidden h-12 w-px bg-gz-input-border/40 sm:block" />

          {/* NEXT NUMBER */}
          <div className="text-center">
            <div className="mx-auto flex h-11 w-11 items-center justify-center rounded-full border border-gz-input-border/60 bg-gz-popup">
              <span className="text-lg font-bold text-gz-text">
                0
              </span>
            </div>

            <p className="mt-1 text-[10px] text-gz-text-secondary">
              Next Number
            </p>
          </div>
        </div>
      </div>
    </div>

    {/* MAIN GAME AREA */}
    <div className="grid min-h-0 flex-1 grid-cols-1 gap-3 lg:grid-cols-[1.25fr_1fr]">

      {/* LEFT : NUMBER BOARD */}
      <div className="flex min-h-0 flex-col rounded-2xl border border-gz-primary-cyan/50 bg-gz-popup/80 p-4 shadow-[0_0_25px_rgba(34,211,238,0.08)] backdrop-blur-xl">

        {/* BOARD HEADER */}
        <div className="mb-3 flex items-center justify-between gap-4">

          {/* TITLE */}
          <div className="flex shrink-0 items-center gap-2">
            <FaTh
              className="text-gz-primary-cyan"
              size={17}
            />

            <h2 className="text-base font-bold text-gz-text">
              Number Board
            </h2>
          </div>

          {/* RIGHT SIDE  LEGEND + FILTERS */}
          <div className="flex flex-wrap items-center justify-end gap-3">

            {/* LEGEND */}
            <div className="flex items-center gap-3 text-[10px] text-gz-text-secondary">

              {/* CALLED */}
              <div className="flex items-center gap-1.5">
                <span className="h-2.5 w-2.5 rounded-full bg-gz-primary-cyan" />
                <span>
                  All Called Number
                </span>
              </div>

              {/* CALLING */}
              <div className="flex items-center gap-1.5">
                <span className="h-2.5 w-2.5 rounded-full bg-gz-purple-accent" />
                <span>
                  Called Number
                </span>
              </div>

              {/* NOT CALLED */}
              <div className="flex items-center gap-1.5">
                <span className="h-2.5 w-2.5 rounded-full bg-slate-500" />
                <span>
                  Not Called Numbers
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* 90 NUMBER GRID */}
        <div className="grid grid-cols-5 gap-1.5 sm:grid-cols-10">
          {numbers.map((number) => {
            const isCalled = calledNumbers.includes(number);
            const isLastCalled = lastCalled === number;

            return (
              <button
                key={number}
                type="button"
                className={`flex aspect-square min-h-[32px] items-center justify-center rounded-lg border text-xs font-semibold transition-all sm:min-h-[38px] ${
                  isLastCalled
                    ? "border-purple-400 bg-gz-purple-accent text-white shadow-[0_0_14px_rgba(168,85,247,0.7)]"
                    : isCalled
                      ? "border-cyan-300/70 bg-gz-primary-cyan text-slate-950 shadow-[0_0_12px_rgba(34,211,238,0.35)]"
                      : "border-gz-input-border/50 bg-gz-popup/70 text-gz-text hover:border-gz-primary-cyan/50"
                }`}
              >
                {number}
              </button>
            );
          })}
        </div>
      </div>

      {/* RIGHT SIDE */}
      <div className="flex min-h-0 flex-col gap-3">

        {/* MY TICKET */}
        <div className="rounded-2xl border border-gz-primary-cyan/50 bg-gz-popup/80 p-4 shadow-[0_0_20px_rgba(34,211,238,0.07)] backdrop-blur-xl">

          <div className="mb-3 flex items-center justify-between">

            <div className="flex items-center gap-2">
              <FaTicketAlt
                className="text-gz-primary-cyan"
                size={17}
              />

              <h2 className="text-base font-bold text-gz-text">
                My Ticket
              </h2>
            </div>
          </div>

          {/* TICKET */}
          <div className="rounded-xl bg-gradient-to-br from-slate-200 to-blue-100 p-3">

            {ticketLoading ? (
              <div className="flex min-h-[140px] items-center justify-center text-sm text-slate-500">
                Loading ticket...
              </div>
            ) : ticketError ? (
              <div className="flex min-h-[140px] items-center justify-center text-sm text-red-500">
                Unable to load ticket
              </div>
            ) : ticket?.ticket_rows?.length ? (

              <div className="grid grid-cols-9 gap-1.5">

              {ticket.ticket_rows.map((row, rowIndex) =>
                row.numbers.map((number, columnIndex) => {

                  const isEmptyCell = number === null;
                  const isMarked = !isEmptyCell && markedNumbers.includes(number);
                  const isCalled = !isEmptyCell && calledNumbers.includes(number);

                    return (
                      <button
                        key={`${rowIndex}-${columnIndex}`}
                        type="button"
                        disabled={isEmptyCell}
                        onClick={() =>{
                          if (isEmptyCell) return;
                          handleTicketNumberClick(number)
                        }}
                        className={`flex aspect-square items-center justify-center rounded-md text-xs font-bold transition ${
                          isEmptyCell
                            ? "pointer-events-none bg-blue-400"
                            : isMarked
                              ? "bg-cyan-400 text-slate-900 shadow-[0_0_10px_rgba(34,211,238,0.55)]"
                              : isCalled
                                ? "bg-white text-slate-700 hover:bg-cyan-100"
                                : "bg-white/80 text-slate-700 hover:bg-cyan-100"
                        }`}>
                        {number ?? ""}
                      </button>
                    );
                })
              )}
              </div>

              ) : (
              <div className="flex min-h-[140px] items-center justify-center text-sm text-slate-500">
            Ticket not available
          </div>
        )}
      </div>


          {/* ADD TICKET */}
          <button
            type="button"
            className="mt-2 flex w-full items-center justify-end gap-1 text-xs font-medium text-gz-primary-cyan"
          >
            <FaPlus size={9} />
            Add Another Ticket
          </button>
        </div>

        {/* GAME RULES */}
        <button
          type="button"
          className="flex flex-1 items-center gap-3 rounded-2xl border border-gz-purple-accent/40 bg-gz-popup/80 p-4 text-left shadow-[0_0_20px_rgba(168,85,247,0.06)] backdrop-blur-xl transition hover:border-gz-primary-cyan/50"
        >
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-gz-primary-cyan/10 text-gz-primary-cyan">
            <FaBookOpen size={16} />
          </div>

          <div className="flex-1">
            <p className="text-sm font-bold text-gz-text">
              Game Rules
            </p>

            <p className="mt-1 text-[10px] text-gz-text-secondary">
              Quick view of all rules and winning patterns
            </p>
          </div>

          <div className="flex items-center gap-1 text-xs font-semibold text-gz-primary-cyan">
            View Rules
            <FaArrowRight size={10} />
          </div>
        </button>
      </div>
    </div>
  </div>
</PageShell>
  );
};

export default TambolaGameRoom;