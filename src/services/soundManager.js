const audioCache = {};

export const loadGameSounds = async (gameType, soundResponse) => {
  const events = soundResponse?.data?.events;

  if (!events || Object.keys(events).length === 0) {
    console.warn(`No sounds available for ${gameType}`);
    return;
  }

  // Create cache for this game if it doesn't exist
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
            console.error(
              `Failed to load sound: ${gameType} -> ${eventName}`
            );

            resolve();
          },
          { once: true }
        );

        audio.load();
      });
    }
  );

  await Promise.all(loadPromises);

  console.log(
    `${gameType} sounds loaded:`,
    Object.keys(audioCache[gameType])
  );
};

// for sound playing
export const playSound = (gameType, eventName) => {
  console.log("Trying to play sound:", gameType, eventName);
  console.log("Current audio cache:", audioCache);

  const audio = audioCache[gameType]?.[eventName];

  if (!audio) {
    console.warn(
      `Sound not found: ${gameType} -> ${eventName}`
    );
    return;
  }

  console.log("Audio found:", audio);

  audio.currentTime = 0;

  audio.play()
    .then(() => {
      console.log(`Playing sound: ${gameType} -> ${eventName}`);
    })
    .catch((error) => {
      console.error(
        `Failed to play sound: ${gameType} -> ${eventName}`,
        error
      );
    });
};


// sound to be played when the animation ends (looping)
export const playLoopingSound = (gameType, eventName) => {
  const audio = audioCache[gameType]?.[eventName];

  if (!audio) {
    console.warn(
      `Sound not found: ${gameType} -> ${eventName}`
    );
    return;
  }

  audio.loop = true;
  audio.currentTime = 0;

  audio.play().catch((error) => {
    console.warn(
      `Failed to play looping sound: ${gameType} -> ${eventName}`,
      error
    );
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