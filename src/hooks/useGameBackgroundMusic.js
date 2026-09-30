import { useEffect } from "react";
import { initializeGameSounds, playBackgroundMusic } from "../services/soundManager";

const useGameBackgroundMusic = (gameType) => {
  useEffect(() => {
    if (!gameType) return undefined;

    let cancelled = false;
    let musicStarted = false;

    const resumeMusic = async () => {
      if (cancelled || musicStarted) return;

      const started = await playBackgroundMusic(gameType);
      if (started) {
        musicStarted = true;
        window.removeEventListener("pointerdown", resumeMusic);
      }
    };

    const initializeSounds = async () => {
      await initializeGameSounds(gameType);
      if (cancelled) return;

      const started = await playBackgroundMusic(gameType);
      if (started) {
        musicStarted = true;
        return;
      }

      window.addEventListener("pointerdown", resumeMusic);
    };

    initializeSounds();

    return () => {
      cancelled = true;
      window.removeEventListener("pointerdown", resumeMusic);
    };
  }, [gameType]);
};

export default useGameBackgroundMusic;
