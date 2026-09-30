import Phaser from "phaser";
import { layout } from "../../config/layout";
import type { ObstacleKind } from "../../config/obstacles";
import { palette } from "../../config/palette";
import type { Obstacle } from "../../logic/field";

export interface ObstacleView {
  readonly id: number;
  readonly effect: Obstacle["effect"];
  readonly root: Phaser.GameObjects.Container;
}

type DrawnKind = Exclude<ObstacleKind, "cloud" | "platform">;

type FlagPainter = (graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle) => void;

const obstacleDepth = layout.characterDepth - 0.2;

const flagPainters: Record<DrawnKind, FlagPainter> = {
  india: paintIndia,
  canada: paintCanada,
  uae: paintUae,
  korea: paintKorea,
  japan: paintJapan,
  greenland: paintGreenland,
  russia: paintRussia,
  iran: paintIran,
  brazil: paintBrazil,
  china: paintChina,
  angry: paintAngry,
};

export function paintObstacles(scene: Phaser.Scene, obstacles: readonly Obstacle[]): ObstacleView[] {
  return obstacles.map((obstacle) => paintOne(scene, obstacle));
}

function paintOne(scene: Phaser.Scene, obstacle: Obstacle): ObstacleView {
  const root = scene.add.container(centerX(obstacle), centerY(obstacle)).setDepth(obstacleDepth);
  const graphics = scene.add.graphics();
  const local = { ...obstacle, x: -obstacle.width / 2, y: -obstacle.height / 2 };
  if (obstacle.kind === "cloud" || obstacle.kind === "platform") {
    paintPuffs(graphics, local, obstacle.kind === "platform");
  } else {
    flagPainters[obstacle.kind](graphics, local);
  }
  root.add(graphics);
  return { id: obstacle.id, effect: obstacle.effect, root };
}

function paintPuffs(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle, chunky: boolean): void {
  const width = obstacle.width;
  const height = obstacle.height;
  const cx = obstacle.x + width / 2;
  const cy = obstacle.y + height / 2;
  graphics.fillStyle(palette.cloudShade);
  graphics.fillEllipse(cx, cy + height * 0.16, width * 0.94, height * 0.48);
  graphics.fillStyle(palette.paper);
  graphics.fillEllipse(cx - width * 0.24, cy + height * 0.08, width * 0.46, height * 0.5);
  graphics.fillEllipse(cx + width * 0.24, cy + height * 0.06, width * 0.44, height * 0.48);
  graphics.fillEllipse(cx, cy - height * (chunky ? 0.08 : 0.18), width * (chunky ? 0.62 : 0.5), height * 0.46);
  graphics.fillEllipse(cx, cy + height * 0.02, width * 0.78, height * 0.58);
  graphics.fillStyle(palette.white);
  graphics.fillEllipse(cx - width * 0.08, cy - height * 0.2, width * 0.24, height * 0.16);
  graphics.lineStyle(chunky ? 4 : 3, palette.ink);
  graphics.strokeEllipse(cx, cy + 2, width * 0.9, height * 0.72);
}

function paintIndia(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  bands(graphics, obstacle, [palette.saffron, palette.paper, palette.indiaGreen]);
  graphics.fillStyle(palette.indiaBlue);
  graphics.fillCircle(centerX(obstacle), centerY(obstacle), 6);
  outline(graphics, obstacle);
}

function paintCanada(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  verticalBands(graphics, obstacle, [palette.canadaRed, palette.paper, palette.canadaRed]);
  graphics.fillStyle(palette.canadaRed);
  graphics.fillTriangle(
    centerX(obstacle),
    obstacle.y + 8,
    centerX(obstacle) - 8,
    obstacle.y + obstacle.height - 8,
    centerX(obstacle) + 8,
    obstacle.y + obstacle.height - 8,
  );
  outline(graphics, obstacle);
}

function paintUae(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  const hoist = obstacle.width * 0.28;
  graphics.fillStyle(palette.flagRed);
  graphics.fillRect(obstacle.x, obstacle.y, hoist, obstacle.height);
  const rest = shifted(obstacle, hoist);
  bands(graphics, rest, [palette.uaeGreen, palette.paper, palette.ink]);
  outline(graphics, obstacle);
}

function paintKorea(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  graphics.fillStyle(palette.paper);
  graphics.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
  graphics.fillStyle(palette.koreaRed);
  graphics.fillCircle(centerX(obstacle) - 6, centerY(obstacle), 8);
  graphics.fillStyle(palette.koreaBlue);
  graphics.fillCircle(centerX(obstacle) + 6, centerY(obstacle), 8);
  outline(graphics, obstacle);
}

function paintJapan(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  graphics.fillStyle(palette.paper);
  graphics.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
  graphics.fillStyle(palette.japanRed);
  graphics.fillCircle(centerX(obstacle), centerY(obstacle), 9);
  outline(graphics, obstacle);
}

function paintGreenland(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  bands(graphics, obstacle, [palette.paper, palette.greenlandRed]);
  graphics.fillStyle(palette.greenlandRed);
  graphics.fillCircle(obstacle.x + obstacle.width * 0.35, centerY(obstacle), 9);
  graphics.fillStyle(palette.paper);
  graphics.fillCircle(obstacle.x + obstacle.width * 0.35, centerY(obstacle) - 4, 9);
  outline(graphics, obstacle);
}

function paintRussia(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  bands(graphics, obstacle, [palette.paper, palette.russiaBlue, palette.flagRed]);
  outline(graphics, obstacle);
}

function paintIran(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  bands(graphics, obstacle, [palette.iranGreen, palette.paper, palette.flagRed]);
  outline(graphics, obstacle);
}

function paintBrazil(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  graphics.fillStyle(palette.brazilGreen);
  graphics.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
  graphics.fillStyle(palette.brazilYellow);
  graphics.fillTriangle(
    centerX(obstacle),
    obstacle.y + 4,
    obstacle.x + 6,
    centerY(obstacle),
    centerX(obstacle),
    obstacle.y + obstacle.height - 4,
  );
  graphics.fillTriangle(
    centerX(obstacle),
    obstacle.y + 4,
    obstacle.x + obstacle.width - 6,
    centerY(obstacle),
    centerX(obstacle),
    obstacle.y + obstacle.height - 4,
  );
  graphics.fillStyle(palette.brazilBlue);
  graphics.fillCircle(centerX(obstacle), centerY(obstacle), 6);
  outline(graphics, obstacle);
}

function paintChina(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  graphics.fillStyle(palette.chinaRed);
  graphics.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
  graphics.fillStyle(palette.chinaYellow);
  graphics.fillPoints(star(obstacle.x + 16, obstacle.y + 14, 7), true);
  outline(graphics, obstacle);
}

function paintAngry(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  graphics.fillStyle(palette.storm);
  graphics.fillEllipse(centerX(obstacle), centerY(obstacle), obstacle.width, obstacle.height * 0.72);
  graphics.fillStyle(palette.paper);
  graphics.fillCircle(centerX(obstacle) - 18, centerY(obstacle) - 6, 5);
  graphics.fillCircle(centerX(obstacle) + 18, centerY(obstacle) - 6, 5);
  graphics.fillStyle(palette.ink);
  graphics.fillCircle(centerX(obstacle) - 18, centerY(obstacle) - 6, 2);
  graphics.fillCircle(centerX(obstacle) + 18, centerY(obstacle) - 6, 2);
  graphics.lineStyle(3, palette.ink);
  graphics.beginPath();
  graphics.arc(centerX(obstacle), centerY(obstacle) + 14, 12, Math.PI * 0.15, Math.PI * 0.85, true);
  graphics.strokePath();
}

function bands(
  graphics: Phaser.GameObjects.Graphics,
  obstacle: Obstacle,
  colors: readonly number[],
): void {
  const height = obstacle.height / colors.length;
  colors.forEach((color, index) => {
    graphics.fillStyle(color);
    graphics.fillRect(obstacle.x, obstacle.y + index * height, obstacle.width, height);
  });
}

function verticalBands(
  graphics: Phaser.GameObjects.Graphics,
  obstacle: Obstacle,
  colors: readonly number[],
): void {
  const width = obstacle.width / colors.length;
  colors.forEach((color, index) => {
    graphics.fillStyle(color);
    graphics.fillRect(obstacle.x + index * width, obstacle.y, width, obstacle.height);
  });
}

function outline(graphics: Phaser.GameObjects.Graphics, obstacle: Obstacle): void {
  graphics.lineStyle(2, palette.ink);
  graphics.strokeRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
}

function shifted(obstacle: Obstacle, hoist: number): Obstacle {
  return {
    ...obstacle,
    x: obstacle.x + hoist,
    width: obstacle.width - hoist,
  };
}

function centerX(obstacle: Obstacle): number {
  return obstacle.x + obstacle.width / 2;
}

function centerY(obstacle: Obstacle): number {
  return obstacle.y + obstacle.height / 2;
}

function star(cx: number, cy: number, radius: number): { x: number; y: number }[] {
  const points: { x: number; y: number }[] = [];
  for (let index = 0; index < 10; index += 1) {
    const angle = -Math.PI / 2 + (index * Math.PI) / 5;
    const distance = index % 2 === 0 ? radius : radius * 0.42;
    points.push({
      x: cx + Math.cos(angle) * distance,
      y: cy + Math.sin(angle) * distance,
    });
  }
  return points;
}
