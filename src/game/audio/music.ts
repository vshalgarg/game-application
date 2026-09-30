import Phaser from "phaser";

const armedKey = "chief-drop-music";
const beatSeconds = 0.34;
const lead = [523, 659, 784, 880, 784, 659, 698, 784, 523, 587, 659, 698, 659, 587, 523, 392];
const bass = [131, 98, 165, 147];

export function armMusic(scene: Phaser.Scene): void {
  if (scene.game.registry.get(armedKey) === true) {
    return;
  }
  scene.game.registry.set(armedKey, true);
  const begin = () => {
    window.removeEventListener("pointerdown", begin);
    startLoop(scene);
  };
  window.addEventListener("pointerdown", begin);
}

function startLoop(scene: Phaser.Scene): void {
  const manager = webAudio(scene);
  if (manager === undefined) {
    return;
  }
  void manager.context.resume();
  let next = manager.context.currentTime + 0.08;
  let step = 0;
  const timer = window.setInterval(() => {
    const scheduled = fill(manager, next, step);
    next = scheduled.next;
    step = scheduled.step;
  }, 80);
  scene.game.events.once(Phaser.Core.Events.DESTROY, () => {
    window.clearInterval(timer);
  });
}

function fill(
  manager: Phaser.Sound.WebAudioSoundManager,
  nextAt: number,
  stepAt: number,
): { next: number; step: number } {
  let next = nextAt;
  let step = stepAt;
  if (next < manager.context.currentTime) {
    next = manager.context.currentTime + 0.05;
  }
  const horizon = manager.context.currentTime + 0.5;
  while (next < horizon) {
    playBeat(manager, next, step);
    next += beatSeconds;
    step += 1;
  }
  return { next, step };
}

function playBeat(manager: Phaser.Sound.WebAudioSoundManager, at: number, step: number): void {
  const note = lead[step % lead.length];
  if (note !== undefined) {
      tone(manager, note, at, beatSeconds * 0.85, "triangle", 0.14);
  }
  if (step % 4 !== 0) {
    return;
  }
  const root = bass[Math.floor(step / 4) % bass.length];
  if (root !== undefined) {
    tone(manager, root, at, beatSeconds * 3.2, "sine", 0.18);
  }
}

function tone(
  manager: Phaser.Sound.WebAudioSoundManager,
  frequency: number,
  at: number,
  duration: number,
  type: OscillatorType,
  volume: number,
): void {
  const oscillator = manager.context.createOscillator();
  const gain = manager.context.createGain();
  oscillator.type = type;
  oscillator.frequency.value = frequency;
  gain.gain.setValueAtTime(volume, at);
  gain.gain.exponentialRampToValueAtTime(0.001, at + duration);
  oscillator.connect(gain);
  gain.connect(manager.destination);
  oscillator.start(at);
  oscillator.stop(at + duration);
}

function webAudio(scene: Phaser.Scene): Phaser.Sound.WebAudioSoundManager | undefined {
  if ("context" in scene.sound) {
    return scene.sound;
  }
  return undefined;
}
