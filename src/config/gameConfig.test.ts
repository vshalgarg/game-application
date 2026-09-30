import { describe, expect, it } from "vitest";
import { gameConfig } from "./gameConfig";

describe("gameConfig", () => {
  it("caps a round at 5000x", () => {
    expect(gameConfig.maxMultiplier).toBe(5000);
  });

  it("starts a drop at 10 coins and steps by 10", () => {
    expect(gameConfig.minStake).toBe(1);
    expect(gameConfig.defaultStake).toBe(10);
    expect(gameConfig.stakeStep).toBe(10);
  });

  it("busts most runs in the middle of the fall", () => {
    expect(gameConfig.bustChance).toBeGreaterThan(0.5);
    expect(gameConfig.bustAt.min).toBeGreaterThanOrEqual(0.3);
    expect(gameConfig.bustAt.max).toBeLessThanOrEqual(0.8);
    expect(gameConfig.bustAt.min).toBeLessThan(gameConfig.bustAt.max);
  });

  it("keeps the 5000x pad rarer than every other landing", () => {
    const weights = gameConfig.landBonusWeights.map((row) => row.weight);
    const total = weights.reduce((sum, weight) => sum + weight, 0);
    const rare = gameConfig.landBonusWeights.find((row) => row.bonus === "flat5000");
    const smallest = Math.min(...weights);

    expect(rare?.weight).toBe(smallest);
    expect(rare?.weight).toBeLessThanOrEqual(total * 0.01);
  });

  it("uses small boosts and cuts", () => {
    expect(gameConfig.boostAmount).toBe(0.5);
    expect(gameConfig.cutAmount).toBe(0.25);
    expect(gameConfig.boostEventShare).toBeGreaterThan(0.5);
  });
});
