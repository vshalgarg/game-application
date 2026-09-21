import { useEffect } from "react";
import { initializeGameSounds, playBackgroundMusic } from "../services/soundManager";

const useGameBackgroundMusic = (gameType) => {
  useEffect(() => {
    let musicStarted = false;

    const removeInteractionListener = () => {
      window.removeEventListener("pointerdown", resumeMusic);
    };

    const resumeMusic = async () => {
      if (musicStarted) {
        return;
      }

      const started = await playBackgroundMusic(gameType);

      if (started) {
        musicStarted = true;
        removeInteractionListener();
      }
    };

    const initializeSounds = async () => {
      // Load all sounds for gameType
      await initializeGameSounds(gameType);

      // start bg music 
      const started = await playBackgroundMusic(gameType);

      if (started) {
        musicStarted = true;
      } else {
        
        // Browser blocked autoplay, music plays on first user interaction
        window.addEventListener("pointerdown", resumeMusic);
      }
    };

    initializeSounds();

    return () => {
      removeInteractionListener();
    };
  }, [gameType]);
};

export default useGameBackgroundMusic;