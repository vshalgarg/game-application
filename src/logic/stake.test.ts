import { describe, expect, it } from "vitest";
import { openingStake, stepStake } from "./stake";

describe("stake", () => {
  it("opens at 10 when the bank can cover it", () => {
    expect(openingStake(1000)).toBe(10);
    expect(openingStake(4)).toBe(4);
  });

  it("opens at 0 when the bank cannot drop", () => {
    expect(openingStake(0)).toBe(0);
    expect(openingStake(Number.NaN)).toBe(0);
  });

  it("steps up to the bank and down to the minimum", () => {
    expect(stepStake(50, 10, "up")).toBe(20);
    expect(stepStake(15, 10, "up")).toBe(15);
    expect(stepStake(100, 10, "down")).toBe(1);
    expect(stepStake(0, 10, "up")).toBe(0);
  });
});
