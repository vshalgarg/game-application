import { describe, expect, it } from "vitest";
import { formatDelta, formatEvent, formatMultiplier } from "./format";

describe("format", () => {
  it("prints the running multiplier", () => {
    expect(formatMultiplier(1)).toBe("1.00x");
    expect(formatMultiplier(2.5)).toBe("2.50x");
    expect(formatMultiplier(5000)).toBe("5000.00x");
    expect(formatMultiplier(Number.NaN)).toBe("0.00x");
  });

  it("prints boosts and cuts", () => {
    expect(formatEvent({ kind: "boost", amount: 0.5, atFraction: 0.2 })).toBe("+0.50");
    expect(formatEvent({ kind: "cut", amount: 0.25, atFraction: 0.4 })).toBe("-0.25");
  });

  it("prints the coin change", () => {
    expect(formatDelta(15)).toBe("+15");
    expect(formatDelta(-10)).toBe("-10");
    expect(formatDelta(0)).toBe("0");
  });
});
