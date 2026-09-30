import { useEffect, useRef } from "react";
import { createPhaserGame } from "./game/createPhaserGame";

export function App() {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const parent = containerRef.current;
    if (!parent) {
      throw new Error("Game container is missing");
    }

    const game = createPhaserGame(parent);
    return () => {
      game.destroy(true);
    };
  }, []);

  return <div id="game-root" ref={containerRef} />;
}
