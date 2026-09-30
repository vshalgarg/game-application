import { describe, expect, it } from "vitest";
import { STAGE_BACKGROUND, STAGE_HEIGHT, STAGE_WIDTH } from "./stage";

describe("stage", () => {
  it("uses a 16:9 landscape stage", () => {
    expect(STAGE_WIDTH).toBe(1280);
    expect(STAGE_HEIGHT).toBe(720);
    expect(STAGE_WIDTH / STAGE_HEIGHT).toBeCloseTo(16 / 9);
  });

  it("uses a hex background color", () => {
    expect(STAGE_BACKGROUND).toMatch(/^#[0-9a-f]{6}$/i);
  });
});
