import Phaser from "phaser";
import { palette } from "../../config/palette";
import { paintCloudCanvas } from "./paintCloud";
import { textureKeys, textureSize } from "./textureKeys";

export function createTextures(scene: Phaser.Scene): void {
  if (scene.textures.exists(textureKeys.storm)) {
    return;
  }
  stampCanvas(scene, textureKeys.cloud, textureSize.cloud, paintCloudCanvas);
  stamp(scene, textureKeys.storm, textureSize.storm, paintStorm);
}

function stampCanvas(
  scene: Phaser.Scene,
  key: string,
  size: { readonly width: number; readonly height: number },
  paint: (ctx: CanvasRenderingContext2D) => void,
): void {
  const texture = scene.textures.createCanvas(key, size.width, size.height);
  if (texture === null) {
    return;
  }
  paint(texture.getContext());
  texture.refresh();
}

function stamp(
  scene: Phaser.Scene,
  key: string,
  size: { readonly width: number; readonly height: number },
  paint: (graphics: Phaser.GameObjects.Graphics) => void,
): void {
  const graphics = scene.add.graphics();
  graphics.setVisible(false);
  paint(graphics);
  graphics.generateTexture(key, size.width, size.height);
  graphics.destroy();
}

function paintStorm(graphics: Phaser.GameObjects.Graphics): void {
  graphics.fillStyle(palette.storm, 0.95);
  graphics.fillEllipse(110, 58, 180, 70);
  graphics.fillEllipse(60, 70, 80, 50);
  graphics.fillEllipse(160, 68, 90, 56);
  graphics.fillStyle(palette.lightning);
  graphics.fillTriangle(100, 78, 124, 78, 96, 118);
  graphics.fillTriangle(108, 108, 136, 108, 112, 142);
}
