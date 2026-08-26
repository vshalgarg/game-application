import { useEffect, useId } from "react";
import { LuX } from "react-icons/lu";

const ExitIcon = ({ className = "", gradientId }) => (
  <svg
    className={className}
    viewBox="0 0 24 24"
    fill="none"
    stroke={`url(#${gradientId})`}
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    aria-hidden="true"
  >
    <defs>
      <linearGradient
        id={gradientId}
        x1="0"
        y1="0"
        x2="24"
        y2="24"
        gradientUnits="userSpaceOnUse"
      >
        <stop offset="0%" stopColor="var(--gz-primary-cyan)" />
        <stop offset="100%" stopColor="var(--gz-purple-accent)" />
      </linearGradient>
    </defs>
    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
    <polyline points="16 17 21 12 16 7" />
    <line x1="21" x2="9" y1="12" y2="12" />
  </svg>
);

const ExitGamePopup = ({
  open,
  onClose,
  onConfirm,
  title = "Exit Game?",
  message = "Are you sure you want to exit?",
  hint = "Your progress will not be saved.",
  stayLabel = "Stay in Game",
  confirmLabel = "Exit Game",
}) => {
  const gradientId = useId().replace(/:/g, "");

  useEffect(() => {
    if (!open) return;

    const handleKeyDown = (event) => {
      if (event.key === "Escape") onClose();
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="gz-exit-overlay" onClick={onClose} role="presentation">
      <div
        className="gz-exit-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="exit-confirm-title"
        onClick={(event) => event.stopPropagation()}
      >
        <button
          type="button"
          className="gz-exit-modal__close"
          onClick={onClose}
          aria-label="Close"
        >
          <LuX />
        </button>

        <ExitIcon className="gz-exit-modal__icon" gradientId={gradientId} />

        <h2 id="exit-confirm-title" className="text-2xl font-bold text-gz-text sm:text-3xl">
          {title}
        </h2>

        <p className="mt-3 text-sm leading-relaxed text-gz-text-secondary">
          {message}
          {hint && (
            <>
              <br />
              {hint}
            </>
          )}
        </p>

        <div className="gz-exit-modal__actions">
          <button type="button" className="gz-exit-modal__no" onClick={onClose}>
            <span className="text-lg font-semibold text-gz-primary-cyan">No</span>
            <span className="text-[11px] text-gz-text-secondary">{stayLabel}</span>
          </button>

          <button
            type="button"
            className="gz-exit-modal__yes"
            onClick={() => {
              // api call
              onConfirm?.();
            }}
          >
            <span className="text-lg font-semibold text-white">Yes</span>
            <span className="text-[11px] text-white/80">{confirmLabel}</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default ExitGamePopup;
