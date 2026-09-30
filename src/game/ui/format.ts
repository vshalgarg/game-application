import type { RoundEvent } from "../../logic/rollRound";

export function formatMultiplier(value: number): string {
  if (!Number.isFinite(value)) {
    return "0.00x";
  }
  const rounded = Math.round(value * 100) / 100;
  return `${rounded.toFixed(2)}x`;
}

export function formatEvent(event: RoundEvent): string {
  const amount = event.amount.toFixed(2);
  if (event.kind === "boost") {
    return `+${amount}`;
  }
  return `-${amount}`;
}

export function formatDelta(delta: number): string {
  if (delta > 0) {
    return `+${String(delta)}`;
  }
  return String(delta);
}
