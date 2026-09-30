import Phaser from "phaser";

export function startWind(scene: Phaser.Scene): () => void {
  const manager = webAudio(scene);
  if (manager === undefined) {
    return () => undefined;
  }
  void manager.context.resume();
  const oscillator = manager.context.createOscillator();
  const gain = manager.context.createGain();
  const wobble = manager.context.createOscillator();
  const wobbleGain = manager.context.createGain();
  oscillator.type = "sine";
  oscillator.frequency.value = 70;
  gain.gain.value = 0.035;
  wobble.frequency.value = 0.25;
  wobbleGain.gain.value = 16;
  wobble.connect(wobbleGain);
  wobbleGain.connect(oscillator.frequency);
  oscillator.connect(gain);
  gain.connect(manager.destination);
  oscillator.start();
  wobble.start();
  return () => {
    const at = manager.context.currentTime;
    gain.gain.setTargetAtTime(0, at, 0.05);
    oscillator.stop(at + 0.2);
    wobble.stop(at + 0.2);
  };
}

export function playChime(scene: Phaser.Scene): void {
  const manager = webAudio(scene);
  if (manager === undefined) {
    return;
  }
  const at = manager.context.currentTime;
  blip(manager, 523, at, 0.18);
  blip(manager, 659, at + 0.12, 0.22);
}

export function playPop(scene: Phaser.Scene, low: boolean): void {
  const manager = webAudio(scene);
  if (manager === undefined) {
    return;
  }
  const at = manager.context.currentTime;
  blip(manager, low ? 180 : 740, at, 0.12);
}

export function playSting(scene: Phaser.Scene): void {
  const manager = webAudio(scene);
  if (manager === undefined) {
    return;
  }
  const at = manager.context.currentTime;
  const oscillator = manager.context.createOscillator();
  const gain = manager.context.createGain();
  oscillator.type = "square";
  oscillator.frequency.setValueAtTime(180, at);
  oscillator.frequency.exponentialRampToValueAtTime(50, at + 0.4);
  gain.gain.setValueAtTime(0.05, at);
  gain.gain.exponentialRampToValueAtTime(0.001, at + 0.45);
  oscillator.connect(gain);
  gain.connect(manager.destination);
  oscillator.start(at);
  oscillator.stop(at + 0.45);
}

function blip(
  manager: Phaser.Sound.WebAudioSoundManager,
  frequency: number,
  at: number,
  duration: number,
): void {
  const oscillator = manager.context.createOscillator();
  const gain = manager.context.createGain();
  oscillator.type = "sine";
  oscillator.frequency.value = frequency;
  gain.gain.setValueAtTime(0.08, at);
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
