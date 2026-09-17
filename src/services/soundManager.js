import { getGameSounds } from "./soundService";

const audioCache = {};
let loadingPromises = {};

export const loadGameSounds = async (gameType, soundResponse) => {
  const events = soundResponse?.data?.events;

  if (!events || Object.keys(events).length === 0) {
    console.warn(`No sounds available for ${gameType}`);
    return;
  }

  if (!audioCache[gameType]) {
    audioCache[gameType] = {};
  }

  const loadPromises = Object.entries(events).map(
    ([eventName, soundData]) => {
      return new Promise((resolve) => {
        const audio = new Audio();

        audio.preload = "auto";
        audio.src = soundData.url;

        audio.addEventListener(
          "canplaythrough",
          () => {
            audioCache[gameType][eventName] = audio;
            resolve();
          },
          { once: true }
        );

        audio.addEventListener(
          "error",
          () => {
            console.error(`Failed to load sound: ${gameType} -> ${eventName}`);
            resolve();
          },
          { once: true }
        );
        audio.load();
      });
    }
  );

  await Promise.all(loadPromises);

  console.info(`${gameType} sounds loaded:`,Object.keys(audioCache[gameType]));
};

// when page refresh sound loading
export const initializeGameSounds = async (gameType) => {
  // Already loaded
  if (
    audioCache[gameType] &&
    Object.keys(audioCache[gameType]).length > 0
  ) {
    return;
  }

  // Already loading
  if (loadingPromises[gameType]) {
    return loadingPromises[gameType];
  }

  loadingPromises[gameType] = (async () => {
    try {
      const soundResponse = await getGameSounds(gameType);

      await loadGameSounds(
        gameType,
        soundResponse
      );
    } catch (error) {
      console.error(`Failed to initialize ${gameType} sounds:`,error);
    } finally {
      delete loadingPromises[gameType];
    }
  })();

  return loadingPromises[gameType];
};

// for sound playing
export const playSound = (gameType, eventName) => {

  const audio = audioCache[gameType]?.[eventName];

  if (!audio) {
    console.warn(`Sound not found: ${gameType} -> ${eventName}`);
    return;
  }
  console.info("Audio found:", audio);

  audio.currentTime = 0;

  audio.play()
    .then(() => {
      console.info(`Playing sound: ${gameType} -> ${eventName}`);
    })
    .catch((error) => {
      console.error(`Failed to play sound: ${gameType} -> ${eventName}`,error);
    });
};


// sound to be played when the animation ends (looping)
export const playLoopingSound = (gameType, eventName) => {
  const audio = audioCache[gameType]?.[eventName];

  if (!audio) {
    console.warn(`Sound not found: ${gameType} -> ${eventName}`);
    return;
  }

  audio.loop = true;
  audio.currentTime = 0;

  audio.play().catch((error) => {
    console.warn(`Failed to play looping sound: ${gameType} -> ${eventName}`,error);
  });
};

// stop the sound when the animation ends
export const stopSound = (gameType, eventName) => {
  const audio = audioCache[gameType]?.[eventName];

  if (!audio) return;

  audio.pause();
  audio.currentTime = 0;
  audio.loop = false;
};