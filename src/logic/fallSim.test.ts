import { describe, expect, it } from "vitest";
import { gameConfig } from "../config/gameConfig";
import { layout } from "../config/layout";
import { obstacleCatalog } from "../config/obstacles";
import { simulateFall } from "./fallSim";
import { doorX, doorY, type Obstacle } from "./field";
import { rollDrop } from "./rollDrop";
import type { RoundScript } from "./rollRound";

const land: RoundScript = {
  durationMs: 4_000,
  end: "land",
  landBonus: "none",
  events: [],
};

const bust: RoundScript = {
  durationMs: 4_000,
  end: "bust",
  bustAt: 0.5,
  landBonus: "none",
  events: [],
};

describe("simulateFall", () => {
  it("reaches the ground when the path is clear", () => {
    const timeline = simulateFall(land, []);
    const last = timeline.samples.at(-1);

    expect(timeline.landed).toBe(true);
    expect(timeline.contacts).toEqual([]);
    expect(timeline.endMs).toBeGreaterThanOrEqual(gameConfig.durationMs.min);
    expect(timeline.endMs).toBeLessThanOrEqual(gameConfig.simMaxMs);
    expect(last?.y).toBe(layout.characterEndY);
  });

  it("cashes out when a large cloud stops the fall", () => {
    const platform = obstacle("platform", doorY() + 400);
    const timeline = simulateFall(land, [platform]);
    const resting = timeline.samples.filter((sample) => sample.y === platform.y);
    const first = resting[0];
    const last = resting.at(-1);

    expect(timeline.stuck).toBe(true);
    expect(timeline.landed).toBe(false);
    expect(timeline.contacts.filter((contact) => contact.effect === "stick")).toHaveLength(1);
    expect(timeline.samples.every((sample) => sample.y <= platform.y)).toBe(true);
    if (first !== undefined && last !== undefined) {
      expect(last.atMs - first.atMs).toBeGreaterThanOrEqual(gameConfig.stickMs - gameConfig.simStepMs);
    }
  });

  it("kicks sideways on a flag and still stops on china", () => {
    const russia = obstacle("russia", doorY() + 280);
    const china = obstacle("china", doorY() + 900, -70);
    const timeline = simulateFall(bust, [russia, china]);
    const start = timeline.samples[0];
    const bent = timeline.samples.find((sample) => sample.atMs > (timeline.contacts[0]?.atMs ?? 0) + 120);
    const spun = timeline.samples.filter((sample) => sample.angle > layout.wobbleDegrees);

    expect(timeline.contacts.map((contact) => contact.effect)).toEqual(["halve", "fatal"]);
    expect(timeline.contacts[1]?.cause).toBe("china");
    expect(timeline.stuck).toBe(false);
    expect(timeline.samples.at(-1)?.y).toBeLessThan(layout.characterEndY);
    expect(spun.length).toBeGreaterThan(0);
    expect(start).toBeDefined();
    expect(bent).toBeDefined();
    if (start !== undefined && bent !== undefined) {
      expect(Math.abs(bent.x - start.x)).toBeGreaterThan(12);
    }
  });
});

describe("rollDrop", () => {
  it("clears hidden boosts and follows the bust roll", () => {
    const drop = rollDrop(() => 0);

    expect(drop.script.end).toBe("bust");
    expect(drop.script.events).toEqual([]);
    expect(drop.timeline.landed).toBe(false);
    expect(drop.timeline.contacts.some((contact) => contact.effect === "fatal")).toBe(true);
  });

  it("lands when the flags do not stop him", () => {
    const drop = rollDrop(() => 0.8);

    expect(drop.script.end).toBe("land");
    expect(drop.timeline.landed).toBe(true);
    expect(drop.timeline.stuck).toBe(false);
    expect(drop.timeline.contacts.some((contact) => contact.effect === "fatal")).toBe(false);
  });

  it("stops the round on a field of large clouds", () => {
    const drop = rollDrop(() => 0.99);

    expect(drop.timeline.stuck).toBe(true);
    expect(drop.timeline.landed).toBe(false);
    expect(drop.timeline.contacts.some((contact) => contact.effect === "fatal")).toBe(false);
  });
});

function obstacle(kind: "platform" | "russia" | "china", top: number, xShift = 0): Obstacle {
  const shape = obstacleCatalog[kind];
  return {
    ...shape,
    id: top,
    kind,
    x: Math.round(doorX(layout.releasePlaneX) - shape.width / 2 + xShift),
    y: top,
    onPath: true,
  };
}
