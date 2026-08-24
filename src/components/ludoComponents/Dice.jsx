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

const Dice = ({value = 0, onRoll, rolling, isCurrentTurn,}) => {

const [cubeRotation, setCubeRotation] = useState(faceRotations[1]);
const prevRolling = useRef(rolling);
const currentRotation = useRef(faceRotations[1]);

useEffect(() => {
  const justStoppedRolling = prevRolling.current && !rolling;
  const target = faceRotations[value] || faceRotations[1];

  if (justStoppedRolling) {

    // Roll from wherever the cube currently is
    const cur = currentRotation.current;
    const newRotation = {
      x: cur.x - (cur.x % 360) + target.x + 720,
      y: cur.y - (cur.y % 360) + target.y + 720,
    };
    currentRotation.current = newRotation;
    setCubeRotation(newRotation);
  } else if (!rolling) {
    // Not rolling and not just finished 
    currentRotation.current = target;
    setCubeRotation(target);
  }

  // while rolling is true CSS handle animation
  prevRolling.current = rolling;
}, [value, rolling]);

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
      <div className={`dice-cube ${rolling ? "rolling" : ""}`}
        style={rolling? undefined
                : { transform: `rotateX(${cubeRotation.x}deg) rotateY(${cubeRotation.y}deg)` }
              }
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

