const Token = ({color, selected = false, onHandleClick, isMovable = false,}) => {

  return (
  <div
    onClick={onHandleClick}
    className={`
      relative
      z-[100]
      w-5
      h-6
      flex
      items-center
      justify-center
      transition-all
      duration-200
      ${
        isMovable
          ? "cursor-pointer scale-110"
          : "cursor-default"
      }
      ${
        selected
          ? "scale-125 drop-shadow-[0_0_12px_white]"
          : ""
      }
    `}
  >
    {isMovable && (
      <>
        {/* Soft glowing aura */}
        <div
          className="absolute w-9 h-9 rounded-full pointer-events-none"
          style={{
            background: "rgba(250, 204, 21, 0.35)",
            animation: "tokenPulse 0.55s ease-in-out infinite",
          }}
        />

        {/* Golden glow */}
        <div
          className="
            absolute
            inset-0
            rounded-full
            pointer-events-none
          "
          style={{
            filter: "drop-shadow(0 0 8px gold)",
          }}
        />
      </>
    )}

    <div
      className="absolute z-[100] bottom-0 w-4 h-1.5 rounded-full border border-black/40"
      style={{
      background: `linear-gradient(to bottom,#ffffff88,${color})`,
      }}
    />

    <div
      className="absolute z-[100] bottom-1 w-3.5 h-3 rounded-t-full rounded-b-[35%] border border-black/40"
      style={{
        background: `linear-gradient(to bottom,#ffffffaa,${color})`,
      }}
    />

    <div
      className="absolute z-[100] top-0 w-2.5 h-2.5 rounded-full border border-black/40"
      style={{
        background: `linear-gradient(to bottom,#ffffffdd,${color})`,
      }}
    />
  </div>
);
};

export default Token;