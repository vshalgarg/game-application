import { gameConfig } from "../config/gameConfig";

export interface KeyValueStore {
  getItem(key: string): string | null;
  setItem(key: string, value: string): void;
}

export type StakeRejection = "empty" | "insufficient";
export type RoundSettlement = "cashOut" | "bust" | "land";

export interface StakeAccepted {
  readonly accepted: true;
  readonly balance: number;
}

export interface StakeRejected {
  readonly accepted: false;
  readonly reason: StakeRejection;
  readonly balance: number;
}

export type PlaceStakeResult = StakeAccepted | StakeRejected;

export function readCoins(store: KeyValueStore): number {
  const raw = store.getItem(gameConfig.coinStorageKey);
  if (raw === null) {
    return gameConfig.startingCoins;
  }
  const parsed = Number(raw);
  if (!Number.isFinite(parsed) || parsed < 0) {
    return gameConfig.startingCoins;
  }
  return wholeCoins(parsed);
}

export function saveCoins(store: KeyValueStore, coins: number): void {
  const stored = wholeCoins(Math.max(0, coins));
  store.setItem(gameConfig.coinStorageKey, String(stored));
}

export function resetCoins(store: KeyValueStore): number {
  saveCoins(store, gameConfig.startingCoins);
  return gameConfig.startingCoins;
}

export function placeStake(balance: number, stake: number): PlaceStakeResult {
  if (stake <= 0) {
    return { accepted: false, reason: "empty", balance };
  }
  if (stake > balance) {
    return { accepted: false, reason: "insufficient", balance };
  }
  return { accepted: true, balance: balance - stake };
}

export function coinDelta(stake: number, payout: number): number {
  return payout - stake;
}

export function cashOut(stake: number, multiplier: number): number {
  if (stake <= 0 || multiplier <= 0) {
    return 0;
  }
  return wholeCoins(stake * multiplier);
}

export function settleBalance(
  balanceAfterStake: number,
  stake: number,
  multiplier: number,
  outcome: RoundSettlement,
): number {
  if (outcome === "bust") {
    return balanceAfterStake;
  }
  return balanceAfterStake + cashOut(stake, multiplier);
}

function wholeCoins(value: number): number {
  if (!Number.isFinite(value)) {
    return 0;
  }
  return Math.floor(value);
}
