import { FaTrophy, FaCrown, FaTimes } from "react-icons/fa";
import Button from "../components/ui/Button";

const WinModal = ({ winnerName, winnerId, isHost, avatarUrl, onPlayAgain, onClose, onBackToHome }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm px-4">
      <div className="relative w-full max-w-sm rounded-2xl border border-gz-primary-cyan/40 bg-gz-popup shadow-[0_0_60px_-10px_rgba(56,189,248,0.35)] px-6 py-8 overflow-hidden">
        {/* Close button */}
        <button
          type="button"
          onClick={onClose}
          className="absolute right-4 top-4 flex h-7 w-7 cursor-pointer items-center justify-center rounded-full border border-gz-input-border/60 text-gz-text-secondary transition hover:text-gz-text hover:border-gz-primary-cyan/60"
        >
          <FaTimes size={12} />
        </button>

        {/* Trophy */}
        <div className="relative z-10 flex flex-col items-center">
          <div className="mb-3 flex h-24 w-24 items-center justify-center rounded-2xl border border-gz-primary-cyan/50 bg-gradient-to-b from-gz-primary-cyan/20 to-gz-purple-accent/20">
            <FaTrophy size={40} className="text-gz-primary-cyan drop-shadow-[0_0_12px_rgba(56,189,248,0.6)]" />
          </div>

          <h1 className="bg-gradient-to-r from-gz-primary-cyan to-gz-purple-accent bg-clip-text text-3xl font-extrabold text-transparent">
            You Win!
          </h1>
          <p className="mt-1 text-sm text-gz-text-secondary">
            Congratulations! You are the winner.
          </p>

          {/* Winner card */}
          <div className="mt-5 flex w-full items-center gap-3 rounded-xl border border-gz-input-border/60 bg-gz-popup/60 px-3 py-2.5">
            <div className="h-10 w-10 shrink-0 overflow-hidden rounded-full border-2 border-gz-primary-cyan bg-white/10">
              {avatarUrl ? (
                <img src={avatarUrl} alt={winnerName} className="h-full w-full object-cover" />
              ) : (
                <div className="flex h-full w-full items-center justify-center text-gz-text-secondary">
                  <svg viewBox="0 0 24 24" fill="currentColor" className="h-6 w-6">
                    <path d="M12 12a5 5 0 100-10 5 5 0 000 10zm0 2c-4.4 0-8 2.2-8 5v1h16v-1c0-2.8-3.6-5-8-5z" />
                  </svg>
                </div>
              )}
            </div>

            <div className="flex flex-col overflow-hidden text-left">
              <span className="truncate text-sm font-semibold text-gz-text">
                You {winnerId ? `(${winnerId})` : ""}
              </span>
              {isHost && (
                <span className="mt-1 w-fit rounded-md bg-gz-primary-cyan/20 px-2 py-0.5 text-[10px] font-semibold text-gz-primary-cyan">
                  Host
                </span>
              )}
            </div>

            <FaCrown className="ml-auto text-gz-purple-accent" size={22} />
          </div>

          {/* Play Again */}
          <div className="mt-5 w-full">
            <Button onClick={onPlayAgain}>
              <span className="flex items-center justify-center gap-2">
                <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
                  <path d="M3 2l10 6-10 6V2z" />
                </svg>
                Play Again
              </span>
            </Button>
          </div>

          <button
            type="button"
            onClick={onBackToHome}
            className="mt-3 cursor-pointer text-sm font-medium text-gz-primary-cyan hover:underline"
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default WinModal;