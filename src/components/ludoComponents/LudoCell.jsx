const LudoCell = ({ type }) => {
  let background = type;

  if (
    type === "safe" ||
    type.includes("arrow")
  ) {
    background = "#ffffff";
  }

  return (
    <div
      className="
        border
        border-gray-400
        flex
        items-center
        justify-center
        relative
      "
      style={{
        backgroundColor: background,
      }}
    >
      {type === "safe" && (
        <span className="text-[11px] text-gray-500 leading-none">
          ★
        </span>
      )}

      {type === "red-arrow" && (
        <span className="text-red-500 text-[14px] leading-none">
          →
        </span>
      )}

      {type === "green-arrow" && (
        <span className="text-green-500 text-[14px] leading-none">
          ↓
        </span>
      )}

      {type === "blue-arrow" && (
        <span className="text-blue-500 text-[14px] leading-none">
          ↑
        </span>
      )}

      {type === "yellow-arrow" && (
        <span className="text-yellow-500 text-[14px] leading-none">
          ←
        </span>
      )}
    </div>
  );
};

export default LudoCell;