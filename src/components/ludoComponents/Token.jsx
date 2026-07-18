const colorMap = {
  red: "#ef4444",
  green: "#22c55e",
  blue: "#3b82f6",
  yellow: "#facc15",
};

const Token = ({
  color,
  selected = false,
  onHandleClick,
}) => {
  const actualColor = colorMap[color] || color;

  return (
    <div
      onClick={onHandleClick}
      className={`
        relative
        w-5
        h-6
        flex
        items-center
        justify-center
        cursor-pointer
        transition-all
        duration-200
        ${
          selected
            ? "scale-125 drop-shadow-[0_0_10px_white]"
            : ""
        }
      `}
    >
      <div
        className="absolute bottom-0 w-4 h-1.5 rounded-full border border-black/40"
        style={{
          background: `linear-gradient(to bottom,#ffffff88,${actualColor})`,
        }}
      />

      <div
        className="absolute bottom-1 w-3.5 h-3 rounded-t-full rounded-b-[35%] border border-black/40"
        style={{
          background: `linear-gradient(to bottom,#ffffffaa,${actualColor})`,
        }}
      />

      <div
        className="absolute top-0 w-2.5 h-2.5 rounded-full border border-black/40"
        style={{
          background: `linear-gradient(to bottom,#ffffffdd,${actualColor})`,
        }}
      />
    </div>
  );
};

export default Token;