import { gameConfig } from "../config/gameConfig";
import { layout } from "../config/layout";
import {
  fatalKinds,
  obstacleCatalog,
  pathKinds,
  type ObstacleKind,
  type ObstacleShape,
} from "../config/obstacles";
import type { RoundScript } from "./rollRound";

export interface Obstacle extends ObstacleShape {
  readonly id: number;
  readonly kind: ObstacleKind;
  readonly x: number;
  readonly y: number;
  readonly onPath: boolean;
}

export function rollField(
  script: RoundScript,
  originX: number,
  random: () => number = Math.random,
): Obstacle[] {
  const fallX = doorX(originX);
  const slots = slotTops(doorY());
  const fatalIndex = script.end === "bust" ? fatalSlot(script.bustAt, slots.length) : -1;
  const obstacles: Obstacle[] = [];
  slots.forEach((top, index) => {
    const kind = index === fatalIndex ? pickFatal(random) : pickPath(random);
    obstacles.push(place(obstacles.length, kind, fallX, top, true));
  });
  const extras = gameConfig.sideObstacleMin + Math.floor(random() * gameConfig.sideObstacleExtra);
  for (let index = 0; index < extras; index += 1) {
    const slot = slots[Math.min(slots.length - 1, Math.floor(random() * slots.length))];
    const top = (slot ?? doorY()) + Math.round((random() - 0.5) * 80);
    const side = random() < 0.5 ? -1 : 1;
    obstacles.push(
      place(obstacles.length, pickPath(random), fallX + side * gameConfig.obstacleSide, top, false),
    );
  }
  return obstacles;
}

export function doorX(originX: number): number {
  return originX + layout.doorOffsetX * layout.fallPlaneScale;
}

export function doorY(): number {
  return layout.planeCruiseY + layout.doorOffsetY * layout.fallPlaneScale;
}

function slotTops(startY: number): number[] {
  const first = startY + gameConfig.obstacleLead;
  const last = layout.characterEndY - gameConfig.obstacleTail;
  const tops: number[] = [];
  for (let top = first; top <= last; top += gameConfig.obstacleGap) {
    tops.push(top);
  }
  return tops;
}

function fatalSlot(bustAt: number, count: number): number {
  if (count <= 1) {
    return 0;
  }
  return Math.round(bustAt * (count - 1));
}

function pickFatal(random: () => number): ObstacleKind {
  const index = Math.min(fatalKinds.length - 1, Math.floor(random() * fatalKinds.length));
  return fatalKinds[index] ?? "angry";
}

function pickPath(random: () => number): ObstacleKind {
  const index = Math.min(pathKinds.length - 1, Math.floor(random() * pathKinds.length));
  return pathKinds[index] ?? "cloud";
}

function place(id: number, kind: ObstacleKind, centerX: number, top: number, onPath: boolean): Obstacle {
  const shape = obstacleCatalog[kind];
  return {
    ...shape,
    id,
    kind,
    x: Math.round(centerX - shape.width / 2),
    y: Math.round(top),
    onPath,
  };
}
