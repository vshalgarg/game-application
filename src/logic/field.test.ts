import { describe, expect, it } from "vitest";
import { layout } from "../config/layout";
import { riderBox } from "../config/obstacles";
import { doorX, rollField } from "./field";
import type { RoundScript } from "./rollRound";

const bust: RoundScript = {
  durationMs: 4_000,
  end: "bust",
  bustAt: 0.5,
  landBonus: "none",
  events: [],
};

const land: RoundScript = {
  durationMs: 4_000,
  end: "land",
  landBonus: "none",
  events: [],
};

describe("rollField", () => {
  it("puts one fatal on a bust path and none on a landing", () => {
    const crashed = rollField(bust, layout.releasePlaneX, () => 0);
    const safe = rollField(land, layout.releasePlaneX, () => 0);
    const fatals = crashed.filter((obstacle) => obstacle.effect === "fatal" && obstacle.onPath);

    expect(fatals).toHaveLength(1);
    expect(fatals[0]?.kind).toBe("china");
    expect(safe.some((obstacle) => obstacle.effect === "fatal")).toBe(false);
  });

  it("keeps side obstacles off the body", () => {
    const field = rollField(land, layout.releasePlaneX, () => 0);
    const center = doorX(layout.releasePlaneX);
    const bodyLeft = center - riderBox.width / 2;
    const bodyRight = center + riderBox.width / 2;

    for (const obstacle of field) {
      if (obstacle.onPath) {
        expect(obstacle.x).toBeLessThanOrEqual(center);
        expect(obstacle.x + obstacle.width).toBeGreaterThanOrEqual(center);
        continue;
      }
      const overlaps = obstacle.x < bodyRight && obstacle.x + obstacle.width > bodyLeft;
      expect(overlaps).toBe(false);
    }
  });
});
