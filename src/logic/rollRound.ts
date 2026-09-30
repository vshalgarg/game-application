import { gameConfig, type LandBonus } from "../config/gameConfig";

export type RoundEventKind = "boost" | "cut";

export interface RoundEvent {
  readonly atFraction: number;
  readonly kind: RoundEventKind;
  readonly amount: number;
}

interface RoundBase {
  readonly durationMs: number;
  readonly events: readonly RoundEvent[];
}

export interface BustRound extends RoundBase {
  readonly end: "bust";
  readonly bustAt: number;
  readonly landBonus: "none";
}

export interface LandRound extends RoundBase {
  readonly end: "land";
  readonly landBonus: LandBonus;
}

export type RoundScript = BustRound | LandRound;

export function rollRound(random: () => number = Math.random): RoundScript {
  const durationMs = rollDuration(random);
  if (random() < gameConfig.bustChance) {
    return rollBustRound(random, durationMs);
  }
  return rollLandRound(random, durationMs);
}

function rollDuration(random: () => number): number {
  const span = gameConfig.durationMs.max - gameConfig.durationMs.min;
  return Math.round(gameConfig.durationMs.min + random() * span);
}

function rollBustRound(random: () => number, durationMs: number): BustRound {
  const span = gameConfig.bustAt.max - gameConfig.bustAt.min;
  const bustAt = gameConfig.bustAt.min + random() * span;
  return {
    durationMs,
    end: "bust",
    bustAt,
    landBonus: "none",
    events: rollEvents(random, bustAt),
  };
}

function rollLandRound(random: () => number, durationMs: number): LandRound {
  const landBonus = pickLandBonus(random);
  return {
    durationMs,
    end: "land",
    landBonus,
    events: rollEvents(random, 1),
  };
}

function pickLandBonus(random: () => number): LandBonus {
  let cursor = random() * landBonusTotal();
  for (const row of gameConfig.landBonusWeights) {
    if (cursor < row.weight) {
      return row.bonus;
    }
    cursor -= row.weight;
  }
  return lastLandBonus();
}

function landBonusTotal(): number {
  return gameConfig.landBonusWeights.reduce((sum, row) => sum + row.weight, 0);
}

function lastLandBonus(): LandBonus {
  const last = gameConfig.landBonusWeights.at(-1);
  if (last === undefined) {
    return "none";
  }
  return last.bonus;
}

function rollEvents(random: () => number, latestFraction: number): RoundEvent[] {
  const count = Math.floor(random() * (gameConfig.maxEvents + 1));
  const events: RoundEvent[] = [];
  for (let index = 0; index < count; index += 1) {
    events.push(rollEvent(random, latestFraction));
  }
  events.sort(byFraction);
  return events;
}

function rollEvent(random: () => number, latestFraction: number): RoundEvent {
  const atFraction = random() * latestFraction;
  if (random() < gameConfig.boostEventShare) {
    return { kind: "boost", amount: gameConfig.boostAmount, atFraction };
  }
  return { kind: "cut", amount: gameConfig.cutAmount, atFraction };
}

function byFraction(left: RoundEvent, right: RoundEvent): number {
  return left.atFraction - right.atFraction;
}
