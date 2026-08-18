import { useEffect, useState, useRef } from "react";

const dotPositions = {
  1: [5],
  2: [1, 9],
  3: [1, 5, 9],
  4: [1, 3, 7, 9],
  5: [1, 3, 5, 7, 9],
  6: [1, 3, 4, 6, 7, 9],
};

const faceRotations = {
  1: { x: 0, y: 0 },
  2: { x: 0, y: -90 },
  3: { x: -90, y: 0 },
  4: { x: 90, y: 0 },
  5: { x: 0, y: 90 },
  6: { x: 0, y: 180 },
};

const faces = [
  { className: "dice-front", number: 1 },
  { className: "dice-back", number: 6 },
  { className: "dice-right", number: 2 },
  { className: "dice-left", number: 5 },
  { className: "dice-top", number: 3 },
  { className: "dice-bottom", number: 4 },
];

const Dice = ({
  value = 0,
  onRoll,
  rolling,
  isCurrentTurn,
}) => {

  const [cubeRotation, setCubeRotation] = useState(faceRotations[1]);
  const wasRolling = useRef(false);

  useEffect(() => {
    const target = faceRotations[value] || faceRotations[1];

    if (rolling) {
      setCubeRotation({
        x: target.x + 720,
        y: target.y + 720,
      });
    } else {
      setCubeRotation(target);
    }
  }, [value, rolling]);

  // Other players
  if (!isCurrentTurn) {
    return (
      <div className="relative overflow-hidden w-[58px] h-[58px] rounded-2xl border-2 border-gray-500 bg-gradient-to-br from-white via-gray-100 to-gray-300 shadow-2xl p-1 opacity-80 cursor-not-allowed">
        <div className="grid h-full w-full grid-cols-3 grid-rows-3">
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
  }

  return (
    <div
      onClick={!rolling ? onRoll : undefined}
      className={`dice-scene cursor-pointer ${!rolling ? "dice-active" : ""}`}
    >
      <div
        className="dice-cube"
        style={{transform: `rotateX(${cubeRotation.x}deg) rotateY(${cubeRotation.y}deg)`,}}
      >
        {faces.map(({ className, number }) => (
          <div
            key={number}
            className={`dice-face ${className}`}
          >
            {!rolling && (
              <div className="dice-shine" />
            )}

            <div className="grid h-full w-full grid-cols-3 grid-rows-3">
              {Array.from({ length: 9 }, (_, index) => {
                const cell = index + 1;

                return (
                  <div
                    key={cell}
                    className="flex items-center justify-center"
                  >
                    {dotPositions[number]?.includes(cell) && (
                      <div className="w-2.5 h-2.5 rounded-full bg-black" />
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Dice;

