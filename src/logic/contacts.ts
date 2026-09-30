import { gameConfig } from "../config/gameConfig";
import type { BustCause, ContactEffect } from "../config/obstacles";

export interface Contact {
  readonly atMs: number;
  readonly obstacleId: number;
  readonly effect: ContactEffect;
  readonly amount: number;
  readonly label: string;
  readonly cause: BustCause;
}

export function resolveContact(multiplier: number, contact: Contact): number {
  if (contact.effect === "fatal") {
    return 0;
  }
  if (contact.effect === "halve") {
    return roundContact(Math.max(gameConfig.minimumMultiplier, multiplier * 0.5));
  }
  return roundContact(multiplier + contact.amount);
}

function roundContact(value: number): number {
  return Math.round(value * 100) / 100;
}
