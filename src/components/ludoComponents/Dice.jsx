const dotPositions = {
  1: [5],
  2: [1, 9],
  3: [1, 5, 9],
  4: [1, 3, 7, 9],
  5: [1, 3, 5, 7, 9],
  6: [1, 3, 4, 6, 7, 9],
};

const Dice = ({ value = 0, onRoll, rolling }) => {
  return (
    <div
      onClick={!rolling ? onRoll : undefined}
      className={`
        relative
        w-[58px]
        h-[58px]
        rounded-2xl
        border-2
        border-gray-500
        bg-gradient-to-br
        from-white
        via-gray-100
        to-gray-300
        shadow-2xl
        p-1
        ${
          rolling
            ? "animate-spin cursor-not-allowed"
            : "cursor-pointer"
        }
      `}
    >
      <div className="w-full h-full grid grid-cols-3 grid-rows-3">
        {Array.from({ length: 9 }, (_, index) => {
          const cell = index + 1;

          return (
            <div
              key={cell}
              className="flex items-center justify-center"
            >
              {dotPositions[value]?.includes(cell) && (
                <div className="w-2.5 h-2.5 rounded-full bg-black" />
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Dice;