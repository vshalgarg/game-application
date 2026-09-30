import { gameConfig, type LandBonus } from "../config/gameConfig";
import { resolveContact, type Contact } from "./contacts";
import type { FallTimeline } from "./fallSim";
import type { RoundEvent, RoundScript } from "./rollRound";

export function multiplierAt(
  script: RoundScript,
  elapsedMs: number,
  timeline?: FallTimeline,
): number {
  const elapsed = finiteElapsed(elapsedMs);
  if (timeline !== undefined) {
    return fromTimeline(script, elapsed, timeline);
  }
  if (hasBusted(script, elapsed)) {
    return 0;
  }
  const fall = fallMultiplier(script, Math.min(elapsed, script.durationMs));
  if (hasLanded(script, elapsed)) {
    return capMultiplier(applyLandBonus(fall, script.landBonus));
  }
  return capMultiplier(fall);
}

export function applyLandBonus(multiplier: number, bonus: LandBonus): number {
  if (bonus === "x5") {
    return multiplier * gameConfig.bonusX5;
  }
  if (bonus === "square") {
    return multiplier * multiplier;
  }
  if (bonus === "x50") {
    return multiplier * gameConfig.bonusX50;
  }
  if (bonus === "flat5000") {
    return gameConfig.maxMultiplier;
  }
  return multiplier;
}

function fallMultiplier(script: RoundScript, elapsed: number): number {
  const fromTime =
    gameConfig.startingMultiplier + (elapsed / 1000) * gameConfig.multiplierPerSecond;
  const raw = fromTime + eventDelta(script, elapsed);
  return Math.max(gameConfig.minimumMultiplier, raw);
}

function eventDelta(script: RoundScript, elapsed: number): number {
  const limit = eventLimitMs(script);
  let total = 0;
  for (const event of script.events) {
    const eventMs = event.atFraction * script.durationMs;
    if (eventMs <= elapsed && eventMs < limit) {
      total += signedAmount(event);
    }
  }
  return total;
}

function eventLimitMs(script: RoundScript): number {
  if (script.end === "bust") {
    return script.bustAt * script.durationMs;
  }
  return script.durationMs + 1;
}

function signedAmount(event: RoundEvent): number {
  if (event.kind === "boost") {
    return event.amount;
  }
  return -event.amount;
}

function fromTimeline(script: RoundScript, elapsed: number, timeline: FallTimeline): number {
  if (timeline.contacts.some((contact) => contact.effect === "fatal" && contact.atMs <= elapsed)) {
    return 0;
  }
  const fall = Math.max(gameConfig.minimumMultiplier, valueAt(script, elapsed, timeline.contacts));
  if (timeline.landed && elapsed >= timeline.endMs) {
    return capMultiplier(applyLandBonus(fall, script.landBonus));
  }
  return capMultiplier(fall);
}

function valueAt(script: RoundScript, elapsed: number, contacts: readonly Contact[]): number {
  let value: number = gameConfig.startingMultiplier;
  let cursor = 0;
  for (const contact of contacts) {
    if (contact.atMs > elapsed || contact.effect === "fatal") {
      break;
    }
    value = accrue(script, value, cursor, contact.atMs);
    value = resolveContact(value, contact);
    cursor = contact.atMs;
  }
  return accrue(script, value, cursor, elapsed);
}

function accrue(script: RoundScript, value: number, fromMs: number, toMs: number): number {
  const seconds = Math.max(0, toMs - fromMs) / 1000;
  return value + seconds * gameConfig.multiplierPerSecond + eventsBetween(script, fromMs, toMs);
}

function eventsBetween(script: RoundScript, fromMs: number, toMs: number): number {
  let total = 0;
  for (const event of script.events) {
    const eventMs = event.atFraction * script.durationMs;
    if (inWindow(eventMs, fromMs, toMs)) {
      total += signedAmount(event);
    }
  }
  return total;
}

function inWindow(eventMs: number, fromMs: number, toMs: number): boolean {
  if (eventMs > toMs) {
    return false;
  }
  if (fromMs === 0) {
    return eventMs >= 0;
  }
  return eventMs > fromMs;
}

function hasBusted(script: RoundScript, elapsed: number): boolean {
  return script.end === "bust" && elapsed >= script.bustAt * script.durationMs;
}

function hasLanded(script: RoundScript, elapsed: number): boolean {
  return script.end === "land" && elapsed >= script.durationMs;
}

function finiteElapsed(elapsedMs: number): number {
  if (!Number.isFinite(elapsedMs) || elapsedMs < 0) {
    return 0;
  }
  return elapsedMs;
}

function capMultiplier(value: number): number {
  return Math.min(gameConfig.maxMultiplier, roundMultiplier(value));
}

function roundMultiplier(value: number): number {
  return Math.round(value * 100) / 100;
}
