const Square = ({ value, onClick, isWinningCell, disabled }) => {
  const markClass =
    value === "X"
      ? "gz-ttt-mark gz-ttt-mark--x gz-ttt-mark--cell"
      : value === "O"
        ? "gz-ttt-mark gz-ttt-mark--o gz-ttt-mark--cell"
        : "";

  return (
    <button
      type="button"
      onClick={disabled ? undefined : onClick}
      disabled={disabled}
      className={`gz-ttt-cell ${disabled ? "" : "cursor-pointer"} ${isWinningCell ? "gz-ttt-cell--win" : ""}`}
    >
      {value ? <span className={markClass}>{value}</span> : null}
    </button>
  );
};

export default Square;
