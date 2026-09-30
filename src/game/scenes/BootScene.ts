import Phaser from "phaser";
import { sceneKeys } from "../../config/sceneKeys";
import { STAGE_BACKGROUND } from "../../config/stage";
import { armMusic } from "../audio/music";
import { createTextures } from "../art/createTextures";
import { textureKeys } from "../art/textureKeys";

export class BootScene extends Phaser.Scene {
  public constructor() {
    super({ key: sceneKeys.boot });
  }

  public preload(): void {
    this.load.image(textureKeys.plane, "/art/airliner.png");
    this.load.image(textureKeys.planeBoarded, "/art/airliner-boarded.png");
    this.load.image(textureKeys.chief, "/art/chief.png");
  }

  public create(): void {
    this.cameras.main.setBackgroundColor(STAGE_BACKGROUND);
    createTextures(this);
    armMusic(this);
    this.scene.start(sceneKeys.title);
  }
}
