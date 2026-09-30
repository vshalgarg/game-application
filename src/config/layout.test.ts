import { describe, expect, it } from "vitest";
import { gameConfig } from "./gameConfig";
import { layout, padAnchor, padOrder, planeArt } from "./layout";
import { STAGE_HEIGHT, STAGE_WIDTH } from "./stage";

describe("layout", () => {
  it("places every landing pad", () => {
    const bonuses = gameConfig.landBonusWeights.map((row) => row.bonus);
    expect(padOrder).toHaveLength(bonuses.length);
    for (const bonus of bonuses) {
      expect(padOrder).toContain(bonus);
      expect(padAnchor[bonus]).toBeGreaterThan(0);
    }
  });

  it("keeps the cruising jet inside the frame", () => {
    const left = layout.centerX - layout.planeSwayX - planeArt.width / 2;
    const right = layout.centerX + layout.planeSwayX + planeArt.width / 2;
    const top = layout.planeCruiseY - layout.planeSwayY - planeArt.height / 2;
    const bottom = layout.planeCruiseY + layout.planeSwayY + planeArt.height / 2;
    expect(left).toBeGreaterThan(0);
    expect(right).toBeLessThan(STAGE_WIDTH);
    expect(top).toBeGreaterThan(0);
    expect(bottom).toBeLessThan(STAGE_HEIGHT);
    expect(planeArt.doorX + planeArt.doorWidth).toBeLessThan(planeArt.width);
    expect(planeArt.doorY + planeArt.doorHeight).toBeLessThan(planeArt.height);
  });
});
