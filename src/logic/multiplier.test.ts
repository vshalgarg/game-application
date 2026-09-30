import { describe, expect, it } from "vitest";
import { gameConfig } from "../config/gameConfig";
import type { Contact } from "./contacts";
import type { FallTimeline } from "./fallSim";
import { multiplierAt } from "./multiplier";
import type { RoundScript } from "./rollRound";

const fall: RoundScript = {
  durationMs: 4_000,
  end: "land",
  landBonus: "none",
  events: [],
};

describe("multiplierAt", () => {
  it("starts at 1x and grows with time", () => {
    expect(multiplierAt(fall, 0)).toBe(1);
    expect(multiplierAt(fall, 2_000)).toBe(3);
    expect(multiplierAt(fall, 4_000)).toBe(5);
  });

  it("ignores a clock that has not started", () => {
    expect(multiplierAt(fall, Number.NaN)).toBe(1);
    expect(multiplierAt(fall, -10)).toBe(1);
  });

  it("adds a boost only after it happens", () => {
    const script: RoundScript = {
      ...fall,
      events: [{ kind: "boost", amount: 0.5, atFraction: 0.25 }],
    };

    expect(multiplierAt(script, 999)).toBe(2);
    expect(multiplierAt(script, 1_000)).toBe(2.5);
  });

  it("applies a cut and does not drop below the minimum", () => {
    const cut: RoundScript = {
      ...fall,
      events: [{ kind: "cut", amount: 0.25, atFraction: 0.25 }],
    };

    expect(multiplierAt(cut, 1_000)).toBe(1.75);
    expect(multiplierAt({ ...fall, events: fourCuts() }, 0)).toBe(0.25);
  });

  it("pays 0 after a bust", () => {
    const script: RoundScript = {
      durationMs: 4_000,
      end: "bust",
      bustAt: 0.5,
      landBonus: "none",
      events: [{ kind: "boost", amount: 0.5, atFraction: 0.5 }],
    };

    expect(multiplierAt(script, 1_999)).toBeGreaterThan(0);
    expect(multiplierAt(script, 2_000)).toBe(0);
  });

  it("applies each landing pad only when the fall ends", () => {
    expect(multiplierAt({ ...fall, landBonus: "x5" }, 3_999)).toBe(5);
    expect(multiplierAt({ ...fall, landBonus: "x5" }, 4_000)).toBe(25);
    expect(multiplierAt({ ...fall, durationMs: 2_000, landBonus: "square" }, 2_000)).toBe(9);
    expect(multiplierAt({ ...fall, landBonus: "x50" }, 4_000)).toBe(250);
    expect(multiplierAt({ ...fall, landBonus: "flat5000" }, 4_000)).toBe(5000);
  });

  it("adds a flag or a cloud only after the hit", () => {
    const friend = hit("boost", gameConfig.boostAmount, 1_000);
    const cloud = hit("boost", gameConfig.cutAmount, 1_000);

    expect(multiplierAt(fall, 999, clock([friend]))).toBe(2);
    expect(multiplierAt(fall, 1_000, clock([friend]))).toBe(2.5);
    expect(multiplierAt(fall, 1_000, clock([cloud]))).toBe(2.25);
  });

  it("halves on contact and keeps the floor", () => {
    const halves = [hit("halve", 0, 0), hit("halve", 0, 0), hit("halve", 0, 0)];

    expect(multiplierAt(fall, 0, clock(halves))).toBe(gameConfig.minimumMultiplier);
  });

  it("drops to zero on a fatal and ignores later hits", () => {
    const timeline = clock([
      hit("boost", gameConfig.boostAmount, 500),
      hit("fatal", 0, 1_000, "china"),
      hit("boost", gameConfig.boostAmount, 2_000),
    ]);

    expect(multiplierAt(fall, 999, timeline)).toBeGreaterThan(0);
    expect(multiplierAt(fall, 1_000, timeline)).toBe(0);
    expect(multiplierAt(fall, 4_000, timeline)).toBe(0);
  });

  it("adds a cloud rest without landing early", () => {
    const stick = hit("stick", gameConfig.boostAmount, 1_000);
    const airborne = clock([stick], { landed: false, endMs: 4_000 });

    expect(multiplierAt({ ...fall, landBonus: "x5" }, 1_000, airborne)).toBe(2.5);
    expect(multiplierAt({ ...fall, landBonus: "x5" }, 4_000, airborne)).toBe(5.5);
  });

  it("applies a landing bonus only after a survived fall", () => {
    const survived = clock([], { landed: true, endMs: 4_000 });
    const crashed = clock([], { landed: false, endMs: 4_000 });

    expect(multiplierAt({ ...fall, landBonus: "x5" }, 3_999, survived)).toBe(5);
    expect(multiplierAt({ ...fall, landBonus: "x5" }, 4_000, survived)).toBe(25);
    expect(multiplierAt({ ...fall, landBonus: "x5" }, 4_000, crashed)).toBe(5);
  });

  it("caps a squared fall at 5000x", () => {
    const script: RoundScript = {
      durationMs: 100_000,
      end: "land",
      landBonus: "square",
      events: [],
    };

    expect(multiplierAt(script, 100_000)).toBe(5000);
    expect(multiplierAt(script, 100_000, clock([], { landed: true, endMs: 100_000 }))).toBe(5000);
  });
});

function clock(
  contacts: readonly Contact[],
  extra: { readonly landed?: boolean; readonly endMs?: number } = {},
): FallTimeline {
  return {
    obstacles: [],
    contacts,
    samples: [],
    endMs: extra.endMs ?? fall.durationMs,
    landed: extra.landed ?? false,
    stuck: false,
  };
}

function hit(
  effect: Contact["effect"],
  amount: number,
  atMs: number,
  cause: Contact["cause"] = "none",
): Contact {
  return {
    atMs,
    obstacleId: atMs,
    effect,
    amount,
    label: "",
    cause,
  };
}

function fourCuts(): RoundScript["events"] {
  return [
    { kind: "cut", amount: 0.25, atFraction: 0 },
    { kind: "cut", amount: 0.25, atFraction: 0 },
    { kind: "cut", amount: 0.25, atFraction: 0 },
    { kind: "cut", amount: 0.25, atFraction: 0 },
  ];
}
