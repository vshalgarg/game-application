import { gameConfig } from "../config/gameConfig";

export type StakeDirection = "up" | "down";

export function openingStake(balance: number): number {
  if (!Number.isFinite(balance) || balance < gameConfig.minStake) {
    return 0;
  }
  return Math.floor(Math.min(gameConfig.defaultStake, balance));
}

export function stepStake(balance: number, stake: number, direction: StakeDirection): number {
  if (!Number.isFinite(balance) || balance < gameConfig.minStake) {
    return 0;
  }
  const sign = direction === "up" ? 1 : -1;
  const next = stake + sign * gameConfig.stakeStep;
  return Math.floor(Math.min(balance, Math.max(gameConfig.minStake, next)));
}
