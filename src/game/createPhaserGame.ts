import Phaser from "phaser";
import { STAGE_BACKGROUND, STAGE_HEIGHT, STAGE_WIDTH } from "../config/stage";
import { BootScene } from "./scenes/BootScene";
import { FallScene } from "./scenes/FallScene";
import { ReadyScene } from "./scenes/ReadyScene";
import { ResultScene } from "./scenes/ResultScene";
import { TitleScene } from "./scenes/TitleScene";

export function createPhaserGame(parent: HTMLElement): Phaser.Game {
  return new Phaser.Game({
    type: Phaser.AUTO,
    parent,
    width: STAGE_WIDTH,
    height: STAGE_HEIGHT,
    backgroundColor: STAGE_BACKGROUND,
    banner: false,
    scale: {
      mode: Phaser.Scale.FIT,
      autoCenter: Phaser.Scale.CENTER_BOTH,
    },
    scene: [BootScene, TitleScene, ReadyScene, FallScene, ResultScene],
  });
}
