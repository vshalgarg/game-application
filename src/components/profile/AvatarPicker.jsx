import { useEffect } from "react";
import { LuX } from "react-icons/lu";
import { AVATARS } from "../../data/avatars";
import Button from "../ui/Button";

const AvatarPicker = ({ open, selectedId, onSelect, onClose }) => {
  useEffect(() => {
    if (!open) return undefined;

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
        className="gz-exit-modal max-w-[34rem]! px-5! text-left"
        role="dialog"
        aria-modal="true"
        aria-labelledby="avatar-picker-title"
        onClick={(event) => event.stopPropagation()}
      >
        <button type="button" className="gz-exit-modal__close" onClick={onClose} aria-label="Close avatar picker">
          <LuX />
        </button>

        <h2 id="avatar-picker-title" className="text-xl font-bold text-gz-text sm:text-2xl">
          Choose Avatar
        </h2>
        <p className="mt-1 text-sm text-gz-text-secondary">Pick a player portrait for your GameZone profile.</p>

        <div className="my-4 grid grid-cols-2 gap-2.5 sm:grid-cols-4" role="listbox" aria-label="Avatar options">
          {AVATARS.map(({ id, name, Art }) => {
            const selected = id === selectedId;
            return (
              <button
                key={id}
                type="button"
                role="option"
                aria-selected={selected}
                className={`flex cursor-pointer flex-col items-center gap-1.5 rounded-xl border bg-gz-popup/50 p-2 transition hover:border-gz-primary-cyan/50 hover:text-gz-text ${
                  selected
                    ? "border-gz-primary-cyan text-gz-primary-cyan shadow-[0_0_16px_rgb(0_217_232/0.28)]"
                    : "border-white/10 text-gz-text-secondary"
                }`}
                onClick={() => {
                  onSelect(id);
                  onClose();
                }}
              >
                <span className="h-16 w-16 overflow-hidden rounded-full sm:h-[4.5rem] sm:w-[4.5rem] [&_svg]:h-full [&_svg]:w-full">
                  <Art />
                </span>
                <span className="text-xs font-semibold">{name}</span>
              </button>
            );
          })}
        </div>

        <Button type="button" onClick={onClose} disabled={!selectedId}>
          Use this avatar
        </Button>
      </div>
    </div>
  );
};

export default AvatarPicker;
