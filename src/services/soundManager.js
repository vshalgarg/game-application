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

  Object.entries(events).forEach(([eventName, soundData]) => {
    const audio = new Audio();
    audio.preload = "auto";
    audio.src = soundData.url;

    if (eventName === "BACKGROUND_MUSIC") {
      audio.loop = true;
    }

    audio.addEventListener(
      "error",
      () => {
        console.error(`Failed to load sound: ${gameType} -> ${eventName}`);
      },
      { once: true },
    );

    audioCache[gameType][eventName] = audio;
    audio.load();
  });
};

// page refresh sound loading
export const initializeGameSounds = async (gameType) => {
  // Already loaded
  if (audioCache[gameType] && Object.keys(audioCache[gameType]).length > 0) {
    return;
  }

  // Already loading
  if (loadingPromises[gameType]) {
    return loadingPromises[gameType];
  }

  loadingPromises[gameType] = (async () => {
    try {
      const soundResponse = await getGameSounds(gameType);

      await loadGameSounds(gameType, soundResponse);
    } catch (error) {
      console.error(`Failed to initialize ${gameType} sounds:`, error);
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

  audio
    .play()
    .then(() => {
      console.info(`Playing sound: ${gameType} -> ${eventName}`);
    })
    .catch((error) => {
      console.error(`Failed to play sound: ${gameType} -> ${eventName}`, error);
    });
};

// playing bg music
export const playBackgroundMusic = async (gameType) => {
  Object.keys(audioCache).forEach((type) => {
    if (type !== gameType) {
      stopBackgroundMusic(type);
    }
  });

  const audio = audioCache[gameType]?.["BACKGROUND_MUSIC"];

  if (!audio) {
    console.warn(`Background music not found for ${gameType}`);
    return false;
  }

  // Music is already playing
  if (!audio.paused) {
    return true;
  }

  audio.loop = true;

  try {
    await audio.play();

    console.info(`Background music started: ${gameType}`);

    return true;
  } catch (error) {
    console.warn(`Failed to play background music: ${gameType}`, error);

    return false;
  }
};

// stoping bg music
export const stopBackgroundMusic = (gameType) => {
  if (!gameType) {
    Object.keys(audioCache).forEach((type) => stopBackgroundMusic(type));
    return;
  }

  const audio = audioCache[gameType]?.["BACKGROUND_MUSIC"];

  if (!audio) {
    return;
  }

  audio.pause();
  audio.currentTime = 0;
};

// sounds to be played when the animation starts (looping)
export const playLoopingSound = (gameType, eventName) => {
  const audio = audioCache[gameType]?.[eventName];

  if (!audio) {
    console.warn(`Sound not found: ${gameType} -> ${eventName}`);
    return;
  }

  audio.loop = true;
  audio.currentTime = 0;

  audio.play().catch((error) => {
    console.warn(`Failed to play looping sound: ${gameType} -> ${eventName}`, error);
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
