import Phaser from "phaser";
import { clouds, layout } from "../../config/layout";
import { textureKeys } from "./textureKeys";

export interface DriftCloud {
  readonly image: Phaser.GameObjects.Image;
  readonly speed: number;
}

export function spawnClouds(
  scene: Phaser.Scene,
  spots: readonly {
    readonly x: number;
    readonly y: number;
    readonly scale: number;
    readonly speed: number;
    readonly layer: "back" | "front";
  }[],
  layers: { readonly back: number; readonly front: number },
): DriftCloud[] {
  return spots.map((spot) => {
    const image = scene.add.image(spot.x, spot.y, textureKeys.cloud);
    image.setScale(spot.scale).setDepth(spot.layer === "front" ? layers.front : layers.back);
    return { image, speed: spot.speed };
  });
}

export function stepDrift(bank: readonly DriftCloud[], deltaMs: number, span: number): void {
  for (const cloud of bank) {
    cloud.image.x += cloud.speed * deltaMs;
    if (cloud.image.x > span) {
      cloud.image.x -= span;
    }
  }
}

export function spawnWorldClouds(scene: Phaser.Scene): DriftCloud[] {
  return spawnClouds(scene, clouds, { back: 1, front: layout.characterDepth - 0.5 });
}
