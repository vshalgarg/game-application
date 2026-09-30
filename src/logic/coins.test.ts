import { describe, expect, it } from "vitest";
import { gameConfig } from "../config/gameConfig";
import {
  cashOut,
  coinDelta,
  placeStake,
  readCoins,
  resetCoins,
  saveCoins,
  settleBalance,
  type KeyValueStore,
} from "./coins";

describe("coins", () => {
  it("starts from the configured bank when storage is empty or invalid", () => {
    expect(readCoins(memoryStore())).toBe(gameConfig.startingCoins);
    expect(readCoins(memoryStore({ [gameConfig.coinStorageKey]: "nope" }))).toBe(
      gameConfig.startingCoins,
    );
    expect(readCoins(memoryStore({ [gameConfig.coinStorageKey]: "-4" }))).toBe(
      gameConfig.startingCoins,
    );
  });

  it("rejects an empty stake", () => {
    expect(placeStake(100, 0)).toEqual({ accepted: false, reason: "empty", balance: 100 });
    expect(placeStake(100, -5)).toEqual({ accepted: false, reason: "empty", balance: 100 });
    expect(cashOut(0, 10)).toBe(0);
    expect(cashOut(10, Number.NaN)).toBe(0);
  });

  it("rejects a stake the bank cannot cover", () => {
    expect(placeStake(40, 50)).toEqual({
      accepted: false,
      reason: "insufficient",
      balance: 40,
    });
  });

  it("spends the stake and pays a cash out", () => {
    const placed = placeStake(100, 10);
    expect(placed).toEqual({ accepted: true, balance: 90 });
    expect(cashOut(10, 2.5)).toBe(25);
    expect(coinDelta(10, 25)).toBe(15);
    expect(coinDelta(10, 0)).toBe(-10);
    expect(settleBalance(90, 10, 2.5, "cashOut")).toBe(115);
  });

  it("drops the stake on a bust and pays a landing", () => {
    expect(settleBalance(90, 10, 4, "bust")).toBe(90);
    expect(settleBalance(90, 10, 4, "land")).toBe(130);
  });

  it("resets the saved bank", () => {
    const store = memoryStore();
    saveCoins(store, 12.9);
    expect(readCoins(store)).toBe(12);
    expect(resetCoins(store)).toBe(gameConfig.startingCoins);
    expect(readCoins(store)).toBe(gameConfig.startingCoins);
  });
});

function memoryStore(initial: Record<string, string> = {}): KeyValueStore {
  const items = new Map(Object.entries(initial));
  return {
    getItem(key: string): string | null {
      return items.get(key) ?? null;
    },
    setItem(key: string, value: string): void {
      items.set(key, value);
    },
  };
}
