import Phaser from "phaser";
import { layout } from "../../config/layout";
import { palette } from "../../config/palette";
import type { Contact } from "../../logic/contacts";
import type { ObstacleView } from "./paintObstacles";

const burstCount = 10;

export function playImpact(
  scene: Phaser.Scene,
  view: ObstacleView | undefined,
  contact: Contact,
  x: number,
  y: number,
): void {
  const color = impactColor(contact);
  burst(scene, x, y, color);
  shake(scene, contact);
  if (view === undefined || view.effect === "stick") {
    squash(scene, view);
    return;
  }
  scene.tweens.add({
    targets: view.root,
    scale: 1.4,
    alpha: 0,
    angle: contact.obstacleId % 2 === 0 ? -16 : 16,
    duration: 280,
  });
}

function burst(scene: Phaser.Scene, x: number, y: number, color: number): void {
  const ring = scene.add.circle(x, y, 8, color, 0.35).setDepth(layout.characterDepth + 1);
  scene.tweens.add({
    targets: ring,
    scale: 4,
    alpha: 0,
    duration: 320,
    onComplete: () => {
      ring.destroy();
    },
  });
  for (let index = 0; index < burstCount; index += 1) {
    const dot = scene.add.circle(x, y, 4, color).setDepth(layout.characterDepth + 1);
    const angle = (index / burstCount) * Math.PI * 2;
    scene.tweens.add({
      targets: dot,
      x: x + Math.cos(angle) * 56,
      y: y + Math.sin(angle) * 40 - 12,
      alpha: 0,
      scale: 0.2,
      duration: 420,
      onComplete: () => {
        dot.destroy();
      },
    });
  }
}

function squash(scene: Phaser.Scene, view: ObstacleView | undefined): void {
  if (view === undefined) {
    return;
  }
  scene.tweens.add({
    targets: view.root,
    scaleY: 0.78,
    y: view.root.y + 8,
    duration: 90,
    yoyo: true,
  });
}

function shake(scene: Phaser.Scene, contact: Contact): void {
  if (contact.effect === "boost") {
    return;
  }
  const amount = contact.effect === "fatal" ? 0.012 : 0.005;
  scene.cameras.main.shake(160, amount);
}

function impactColor(contact: Contact): number {
  if (contact.effect === "fatal") {
    return palette.storm;
  }
  if (contact.effect === "halve") {
    return palette.notice;
  }
  return palette.gold;
}
