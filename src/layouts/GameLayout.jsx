import { useEffect } from "react";
import { Outlet } from "react-router-dom";
import useGameBackgroundMusic from "../hooks/useGameBackgroundMusic";
import { stopBackgroundMusic } from "../services/soundManager";

let pendingStop = { gameType: null, id: null };

const GameLayout = ({ gameType }) => {
  useGameBackgroundMusic(gameType);

  useEffect(() => {
    if (pendingStop.id && pendingStop.gameType === gameType) {
      clearTimeout(pendingStop.id);
      pendingStop = { gameType: null, id: null };
    }

    return () => {
      pendingStop = {
        gameType,
        id: setTimeout(() => {
          stopBackgroundMusic(gameType);
          pendingStop = { gameType: null, id: null };
        }, 0),
      };
    };
  }, [gameType]);

  return <Outlet />;
};

export default GameLayout;
