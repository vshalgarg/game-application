import { copy } from "../config/copy";
import { gameConfig } from "../config/gameConfig";
import { layout } from "../config/layout";
import { riderBox } from "../config/obstacles";
import { STAGE_WIDTH } from "../config/stage";
import type { Contact } from "./contacts";
import { doorX, doorY, type Obstacle } from "./field";
import type { RoundScript } from "./rollRound";

export interface FallSample {
  readonly atMs: number;
  readonly x: number;
  readonly y: number;
  readonly angle: number;
}

export interface FallTimeline {
  readonly obstacles: readonly Obstacle[];
  readonly contacts: readonly Contact[];
  readonly samples: readonly FallSample[];
  readonly endMs: number;
  readonly landed: boolean;
  readonly stuck: boolean;
}

interface Body {
  x: number;
  y: number;
  vx: number;
  vy: number;
  time: number;
  spinUntil: number;
}

type Stop = "none" | "fatal" | "stuck";

export function simulateFall(
  script: RoundScript,
  obstacles: readonly Obstacle[],
  originX: number = layout.releasePlaneX,
): FallTimeline {
  const body = createBody(originX);
  const samples: FallSample[] = [sampleOf(body)];
  const contacts: Contact[] = [];
  const hit = new Set<number>();
  const pending = [...obstacles].sort(byTop);
  let stop: Stop = "none";
  while (body.y < layout.characterEndY && body.time < gameConfig.simMaxMs && stop === "none") {
    const previousFeet = body.y;
    stepFall(body);
    stop = touchObstacles(body, previousFeet, pending, hit, contacts);
    if (body.y > layout.characterEndY) {
      body.y = layout.characterEndY;
    }
    samples.push(sampleOf(body));
  }
  if (stop === "stuck") {
    holdPose(body, samples);
  }
  if (script.end === "bust" && stop === "none") {
    contacts.push(safetyFatal(body.time));
    stop = "fatal";
  }
  const last = samples.at(-1);
  return {
    obstacles,
    contacts,
    samples,
    endMs: last?.atMs ?? 0,
    landed: stop === "none" && script.end === "land",
    stuck: stop === "stuck",
  };
}

export function sampleAt(samples: readonly FallSample[], elapsedMs: number): FallSample | undefined {
  const elapsed = Number.isFinite(elapsedMs) ? Math.max(0, elapsedMs) : 0;
  let chosen = samples[0];
  for (const sample of samples) {
    if (sample.atMs > elapsed) {
      break;
    }
    chosen = sample;
  }
  return chosen;
}

function createBody(originX: number): Body {
  return {
    x: doorX(originX),
    y: doorY(),
    vx: 0,
    vy: gameConfig.fallInitialSpeed,
    time: 0,
    spinUntil: 0,
  };
}

function stepFall(body: Body): void {
  body.vy = Math.min(gameConfig.fallTerminalSpeed, body.vy + gameConfig.fallGravity * gameConfig.simStepMs);
  body.y += body.vy * gameConfig.simStepMs;
  body.x = clampX(body.x + body.vx * gameConfig.simStepMs);
  body.vx *= gameConfig.deflectDamping;
  if (Math.abs(body.vx) < 0.02) {
    body.vx = 0;
  }
  body.time += gameConfig.simStepMs;
}

function holdPose(body: Body, samples: FallSample[]): void {
  const until = body.time + gameConfig.stickMs;
  while (body.time < until) {
    body.time += gameConfig.simStepMs;
    samples.push(sampleOf(body));
  }
}

function touchObstacles(
  body: Body,
  previousFeet: number,
  pending: readonly Obstacle[],
  hit: Set<number>,
  contacts: Contact[],
): Stop {
  for (const obstacle of pending) {
    if (hit.has(obstacle.id) || !overlaps(body, obstacle)) {
      continue;
    }
    if (!holds(obstacle)) {
      hit.add(obstacle.id);
      contacts.push(contactFrom(obstacle, body.time));
      deflect(body, obstacle);
      if (obstacle.effect === "halve") {
        body.spinUntil = body.time + gameConfig.spinMs;
      }
      if (obstacle.effect === "fatal") {
        return "fatal";
      }
      continue;
    }
    catchCloud(body, previousFeet, obstacle, hit, contacts);
    return "stuck";
  }
  return "none";
}

function holds(obstacle: Obstacle): boolean {
  return obstacle.effect === "stick" && obstacle.width > riderBox.width;
}

function crossesTop(previousFeet: number, body: Body, obstacle: Obstacle): boolean {
  const bodyLeft = body.x - riderBox.width / 2;
  const bodyRight = body.x + riderBox.width / 2;
  const across = bodyRight > obstacle.x && bodyLeft < obstacle.x + obstacle.width;
  return across && previousFeet <= obstacle.y + 10 && body.y >= obstacle.y;
}

function catchCloud(
  body: Body,
  previousFeet: number,
  obstacle: Obstacle,
  hit: Set<number>,
  contacts: Contact[],
): void {
  if (crossesTop(previousFeet, body, obstacle)) {
    body.y = obstacle.y;
  }
  body.vy = 0;
  body.vx = 0;
  hit.add(obstacle.id);
  contacts.push(contactFrom(obstacle, body.time));
}

function deflect(body: Body, obstacle: Obstacle): void {
  const center = obstacle.x + obstacle.width / 2;
  let direction = 1;
  if (body.x < center) {
    direction = -1;
  } else if (body.x === center) {
    direction = obstacle.id % 2 === 0 ? -1 : 1;
  }
  body.vx = direction * gameConfig.deflectSpeed;
  body.x = clampX(body.x + direction * 10);
}

function overlaps(body: Body, obstacle: Obstacle): boolean {
  const left = body.x - riderBox.width / 2;
  const top = body.y - riderBox.height;
  return (
    left < obstacle.x + obstacle.width &&
    left + riderBox.width > obstacle.x &&
    top < obstacle.y + obstacle.height &&
    top + riderBox.height > obstacle.y
  );
}

function contactFrom(obstacle: Obstacle, atMs: number): Contact {
  return {
    atMs,
    obstacleId: obstacle.id,
    effect: obstacle.effect,
    amount: obstacle.amount,
    label: obstacle.label,
    cause: obstacle.cause,
  };
}

function safetyFatal(atMs: number): Contact {
  return {
    atMs,
    obstacleId: -1,
    effect: "fatal",
    amount: 0,
    label: copy.angryCloud,
    cause: "angry",
  };
}

function sampleOf(body: Body): FallSample {
  return {
    atMs: body.time,
    x: body.x,
    y: body.y,
    angle: spinAngle(body),
  };
}

function spinAngle(body: Body): number {
  if (body.time >= body.spinUntil) {
    return Math.sin(body.time / layout.wobbleMs) * layout.wobbleDegrees;
  }
  const spun = gameConfig.spinMs - (body.spinUntil - body.time);
  return (spun / gameConfig.spinMs) * gameConfig.spinDegrees;
}

function clampX(x: number): number {
  const margin = riderBox.width;
  return Math.min(STAGE_WIDTH - margin, Math.max(margin, x));
}

function byTop(left: Obstacle, right: Obstacle): number {
  return left.y - right.y;
}
