import { describe, expect, it } from "vitest";
import { layout, padAnchor } from "../../config/layout";
import type { RoundScript } from "../../logic/rollRound";
import { characterPoint, dropStart, fallFraction, planePoint, scriptEndMs, stormAlpha } from "./flight";

const land: RoundScript = {
  durationMs: 4_000,
  end: "land",
  landBonus: "x5",
  events: [],
};

const bust: RoundScript = {
  durationMs: 4_000,
  end: "bust",
  bustAt: 0.5,
  landBonus: "none",
  events: [],
};

describe("flight", () => {
  it("ends a bust in the air and a landing on the pad", () => {
    expect(scriptEndMs(bust)).toBe(2_000);
    expect(scriptEndMs(land)).toBe(4_000);
    const start = dropStart();
    expect(characterPoint(land, 0)).toEqual(start);
    expect(characterPoint(land, 4_000)).toEqual({
      x: padAnchor.x5,
      y: layout.characterEndY,
    });
    expect(characterPoint(bust, 2_000).y).toBe(start.y + (layout.characterEndY - start.y) * 0.5);
    expect(planePoint(0)).toEqual({ x: layout.releasePlaneX, y: layout.planeCruiseY });
    expect(Math.abs(planePoint(1_000).x - layout.releasePlaneX)).toBeLessThanOrEqual(layout.planeSwayX);
  });

  it("ignores a clock that has not started", () => {
    expect(fallFraction(land, Number.NaN)).toBe(0);
    expect(characterPoint({ ...land, durationMs: 0 }, 10).y).toBe(layout.characterEndY);
  });

  it("brings the storm in only at the end of a bust", () => {
    expect(stormAlpha(land, 4_000)).toBe(0);
    expect(stormAlpha(bust, 0)).toBe(0);
    expect(stormAlpha(bust, 2_000)).toBe(1);
  });
});
