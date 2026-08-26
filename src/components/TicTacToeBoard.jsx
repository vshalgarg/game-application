import Square from "./Square";

const TicTacToeBoard = ({ board = [], handleClick, winningPattern }) => {
  const flattenedBoard = board.flat();

  const lineStyles = {
    "0,1,2": {
      top: "16%",
      left: "50%",
      width: "100%",
      rotate: "0deg",
    },
    "3,4,5": {
      top: "50%",
      left: "50%",
      width: "100%",
      rotate: "0deg",
    },
    "6,7,8": {
      top: "84%",
      left: "50%",
      width: "100%",
      rotate: "0deg",
    },
    "0,3,6": {
      top: "50%",
      left: "16%",
      width: "100%",
      rotate: "90deg",
    },
    "1,4,7": {
      top: "50%",
      left: "50%",
      width: "100%",
      rotate: "90deg",
    },
    "2,5,8": {
      top: "50%",
      left: "84%",
      width: "100%",
      rotate: "90deg",
    },
    "0,4,8": {
      top: "50%",
      left: "50%",
      width: "140%",
      rotate: "45deg",
    },
    "2,4,6": {
      top: "50%",
      left: "50%",
      width: "140%",
      rotate: "-45deg",
    },
  };

  const lineStyle = winningPattern && lineStyles[winningPattern.join(",")];

  return (
    <div className="gz-ttt-board">
      <div className="grid grid-cols-3 gap-2.5">
        {flattenedBoard.map((square, index) => (
          <Square
            key={index}
            value={square}
            onClick={() => handleClick(index)}
            isWinningCell={winningPattern?.includes(index)}
            disabled={square !== null && square !== ""}
          />
        ))}
      </div>
      {lineStyle && (
        <div
          className="absolute z-20 h-1.5 animate-pulse rounded-full bg-gz-primary-cyan shadow-[0_0_18px_rgba(0,217,232,0.9)]"
          style={{
            top: lineStyle.top,
            left: lineStyle.left,
            width: lineStyle.width,
            transform: `
              translate(-50%, -50%)
              rotate(${lineStyle.rotate})
            `,
          }}
        />
      )}
    </div>
  );
};

export default TicTacToeBoard;
