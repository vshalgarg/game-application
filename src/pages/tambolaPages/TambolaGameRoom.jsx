import { useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { FaUsers, FaTicketAlt, FaTh, FaPlus, FaBookOpen, FaArrowRight } from "react-icons/fa";
import { LuX } from "react-icons/lu";
import PageShell from "../../components/layout/PageShell";
import ExitGamePopup from "../../components/ui/ExitGamePopup";
import useBackExitGuard from "../../hooks/useBackExitGuard";
import useTambolaGameStateRealtime from "../../hooks/useTambolaGameStateRealtime";

const TambolaGameRoom = () => {

  const { roomCode } = useParams();
  const [showExitPopup, setShowExitPopup] = useState(false);

  // Temporary UI state
  const [calledNumbers] = useState([7,12,18,23,26,31,37,44,58,65,72,79,84]);
  const [lastCalled] = useState(31);
  const [markedNumbers, setMarkedNumbers] = useState([18,26,37]);
  const [playersCount] = useState(8);

  const closeExitPopup = () => setShowExitPopup(false);
  const openExitPopup = () => setShowExitPopup(true);

  useBackExitGuard(openExitPopup);

  // game room listner
  const {
    gameState: realtimeGameState,
    loading: gameStateLoading,
  } = useTambolaGameStateRealtime(roomCode);

  console.log("Tambola realtime game state:", realtimeGameState);
  console.log("Game state loading:", gameStateLoading);

  const numbers = useMemo(() => Array.from({ length: 90 }, (_, index) => index + 1),[]);
  const ticketNumbers = [
    [7, 18, 32, 51, 68],
    [3, 26, 45, 56, 72],
    [12, 29, 37, 60, 83],
  ];

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

                  <span className="rounded-full border border-emerald-400/50 bg-emerald-400/15 px-2.5 py-1 text-[10px] font-bold text-emerald-300">
                    LIVE
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
                  Last Called
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
            <div className="mb-3 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <FaTh
                  className="text-gz-primary-cyan"
                  size={17}
                />
                <h2 className="text-base font-bold text-gz-text">
                  Number Board
                </h2>
              </div>

              {/* FILTERS */}
              <div className="hidden items-center gap-1.5 sm:flex">
                <button
                  type="button"
                  className="rounded-xl bg-gradient-to-r from-cyan-400 to-blue-500 px-4 py-2 text-xs font-bold text-white shadow-[0_0_12px_rgba(34,211,238,0.25)]"
                >
                  All
                </button>
                <button
                  type="button"
                  className="rounded-xl border border-gz-input-border/50 bg-gz-popup px-3 py-2 text-xs text-gz-text-secondary"
                >
                  1-30
                </button>
                <button
                  type="button"
                  className="rounded-xl border border-gz-input-border/50 bg-gz-popup px-3 py-2 text-xs text-gz-text-secondary"
                >
                  31-60
                </button>
                <button
                  type="button"
                  className="rounded-xl border border-gz-input-border/50 bg-gz-popup px-3 py-2 text-xs text-gz-text-secondary"
                >
                  61-90
                </button>
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

            {/* LEGEND */}
            <div className="mt-4 flex flex-wrap items-center gap-5 text-[11px] text-gz-text-secondary">
              <div className="flex items-center gap-2">
                <span className="h-3 w-3 rounded-full bg-gz-primary-cyan" />
                Called Number
              </div>

              <div className="flex items-center gap-2">
                <span className="h-3 w-3 rounded-full bg-gz-purple-accent" />
                Last Called
              </div>

              <div className="flex items-center gap-2">
                <span className="h-3 w-3 rounded-full bg-slate-500" />
                Not Called
              </div>

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
                <div className="grid grid-cols-5 gap-1.5">
                  {ticketNumbers.flat().map((number) => {
                    const isMarked = markedNumbers.includes(number);

                    return (
                      <button
                        key={number}
                        type="button"
                        onClick={() =>
                          handleTicketNumberClick(number)
                        }
                        className={`flex h-8 items-center justify-center rounded-md text-xs font-bold transition ${
                          isMarked
                            ? "bg-cyan-400 text-slate-900 shadow-[0_0_10px_rgba(34,211,238,0.55)]"
                            : "bg-white/80 text-slate-700 hover:bg-cyan-100"
                        }`}
                      >
                        {number}
                      </button>
                    );
                  })}

                </div>
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