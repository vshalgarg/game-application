import { useEffect, useRef, useState } from "react";

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

  const [spectatorRolling, setSpectatorRolling] = useState(false);
  const [displayedValue, setDisplayedValue] = useState(value || 1);

  const previousValue = useRef(value);

  const currentRotation = useRef(faceRotations[1]);
  const prevRolling = useRef(false);

  // Current player uses backend rolling and others simulate rolling.
  const actualRolling = isCurrentTurn ? rolling : spectatorRolling;

  useEffect(() => {
    if (isCurrentTurn) {
      setDisplayedValue(value || 1);
      previousValue.current = value;
      return;
    }

    if (previousValue.current === undefined) {
      previousValue.current = value;
      return;
    }

    if (previousValue.current !== value) {
      previousValue.current = value;

      setSpectatorRolling(true);

      const timer = setTimeout(() => {
        setDisplayedValue(value || 1);
        setSpectatorRolling(false);
      }, 500);

      return () => clearTimeout(timer);
    }
  }, [value, isCurrentTurn]);

  useEffect(() => {
    const justStoppedRolling =
      prevRolling.current && !actualRolling;

    const target =
      faceRotations[displayedValue] || faceRotations[1];

    if (justStoppedRolling) {
      const cur = currentRotation.current;

      const newRotation = {
        x: cur.x - (cur.x % 360) + target.x + 720,
        y: cur.y - (cur.y % 360) + target.y + 720,
      };

      currentRotation.current = newRotation;
      setCubeRotation(newRotation);
    } else if (!actualRolling) {
      currentRotation.current = target;
      setCubeRotation(target);
    }

    prevRolling.current = actualRolling;
  }, [displayedValue, actualRolling]);

  return (
    <div
      onClick={
        isCurrentTurn && !actualRolling
          ? onRoll
          : undefined
      }
      className={`dice-scene ${
        isCurrentTurn ? "cursor-pointer" : "cursor-not-allowed"
      } ${
        isCurrentTurn && !actualRolling
          ? "dice-active"
          : ""
      }`}
    >
      <div
        className={`dice-cube ${
          actualRolling ? "rolling" : ""
        }`}
        style={
          actualRolling
            ? undefined
            : {
                transform: `rotateX(${cubeRotation.x}deg) rotateY(${cubeRotation.y}deg)`,
              }
        }
      >
        {faces.map(({ className, number }) => (
          <div
            key={number}
            className={`dice-face ${className}`}
          >
            {isCurrentTurn && !actualRolling && (
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