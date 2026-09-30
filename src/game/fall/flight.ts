import { layout, padAnchor } from "../../config/layout";
import type { RoundScript } from "../../logic/rollRound";

export interface Point {
  readonly x: number;
  readonly y: number;
}

export function scriptEndMs(script: RoundScript): number {
  if (script.end === "bust") {
    return script.bustAt * script.durationMs;
  }
  return script.durationMs;
}

export function fallFraction(script: RoundScript, elapsedMs: number): number {
  const elapsed = finiteElapsed(elapsedMs);
  if (script.durationMs <= 0) {
    return 1;
  }
  const capped = Math.min(elapsed, scriptEndMs(script));
  return Math.min(1, capped / script.durationMs);
}

export function landingX(script: RoundScript): number {
  if (script.end === "bust") {
    return layout.centerX;
  }
  return padAnchor[script.landBonus];
}

export function doorFeet(planeX: number, planeY: number, scale: number, angle = 0): Point {
  const localX = layout.doorOffsetX * scale;
  const localY = layout.doorOffsetY * scale;
  const radians = (angle * Math.PI) / 180;
  const cos = Math.cos(radians);
  const sin = Math.sin(radians);
  return {
    x: planeX + localX * cos - localY * sin,
    y: planeY + localX * sin + localY * cos,
  };
}

export function planeTilt(elapsedMs: number): number {
  return Math.sin(finiteElapsed(elapsedMs) / layout.planeSwayMs) * layout.planeTilt;
}

export function dropStart(originX: number = layout.releasePlaneX): Point {
  return doorFeet(originX, layout.planeCruiseY, layout.fallPlaneScale);
}

export function planePoint(elapsedMs: number, originX: number = layout.releasePlaneX): Point {
  const elapsed = finiteElapsed(elapsedMs);
  return {
    x: originX + Math.sin(elapsed / layout.planeSwayMs) * layout.planeSwayX,
    y: layout.planeCruiseY + Math.sin(elapsed / layout.planeLiftMs) * layout.planeSwayY,
  };
}

export function characterPoint(
  script: RoundScript,
  elapsedMs: number,
  originX: number = layout.releasePlaneX,
): Point {
  const fraction = fallFraction(script, elapsedMs);
  const start = dropStart(originX);
  const targetX = landingX(script);
  return {
    x: start.x + (targetX - start.x) * fraction,
    y: start.y + (layout.characterEndY - start.y) * fraction,
  };
}

export function stormAlpha(script: RoundScript, elapsedMs: number): number {
  if (script.end !== "bust") {
    return 0;
  }
  const elapsed = finiteElapsed(elapsedMs);
  const start = scriptEndMs(script) - layout.stormWarningMs;
  if (elapsed <= start) {
    return 0;
  }
  return Math.min(1, (elapsed - start) / layout.stormWarningMs);
}

function finiteElapsed(elapsedMs: number): number {
  if (!Number.isFinite(elapsedMs) || elapsedMs < 0) {
    return 0;
  }
  return elapsedMs;
}
