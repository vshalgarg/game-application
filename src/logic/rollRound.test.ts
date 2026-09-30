import { describe, expect, it } from "vitest";
import { gameConfig } from "../config/gameConfig";
import { rollRound, type RoundScript } from "./rollRound";

describe("rollRound", () => {
  it("busts inside the middle of the fall", () => {
    const script = rollRound(sequence([0, 0, 0, 0]));

    expect(script.end).toBe("bust");
    expect(script.durationMs).toBe(gameConfig.durationMs.min);
    expect(script.landBonus).toBe("none");
    if (script.end === "bust") {
      expect(script.bustAt).toBe(gameConfig.bustAt.min);
    }
    expect(script.events).toEqual([]);
  });

  it("lands with no bonus", () => {
    expect(landBonusOf(sequence([0, 0.99, 0, 0]))).toBe("none");
  });

  it("lands on the x5 pad", () => {
    expect(landBonusOf(sequence([0, 0.99, 0.7, 0]))).toBe("x5");
  });

  it("lands on the square pad", () => {
    expect(landBonusOf(sequence([0, 0.99, 0.88, 0]))).toBe("square");
  });

  it("lands on the x50 pad", () => {
    expect(landBonusOf(sequence([0, 0.99, 0.96, 0]))).toBe("x50");
  });

  it("lands on the rare 5000x pad", () => {
    expect(landBonusOf(sequence([0, 0.99, 0.995, 0]))).toBe("flat5000");
  });

  it("uses the last pad when the bonus roll sits on the end of the table", () => {
    expect(landBonusOf(sequence([0, 0.99, 1, 0]))).toBe("flat5000");
  });

  it("keeps boosts and cuts before a bust", () => {
    const script = rollRound(sequence([0, 0, 0, 0.2, 0.5, 0]));

    expect(script.end).toBe("bust");
    expect(script.events).toHaveLength(1);
    const event = script.events[0];
    expect(event?.kind).toBe("boost");
    if (script.end === "bust" && event !== undefined) {
      expect(event.atFraction).toBeLessThan(script.bustAt);
    }
  });

  it("sorts a boost and a cut by time", () => {
    const script = rollRound(sequence([0, 0.99, 0, 0.4, 0.8, 0, 0.2, 0.9]));

    expect(script.events.map((event) => event.kind)).toEqual(["cut", "boost"]);
    expect(script.events.map((event) => event.atFraction)).toEqual([0.2, 0.8]);
  });

  it("returns a script when the caller does not inject a random source", () => {
    const script = rollRound();

    expect(script.durationMs).toBeGreaterThanOrEqual(gameConfig.durationMs.min);
    expect(script.durationMs).toBeLessThanOrEqual(gameConfig.durationMs.max);
    expect(script.events.length).toBeLessThanOrEqual(gameConfig.maxEvents);
    if (script.end === "bust") {
      expect(script.bustAt).toBeGreaterThanOrEqual(gameConfig.bustAt.min);
      expect(script.bustAt).toBeLessThanOrEqual(gameConfig.bustAt.max);
      expect(script.landBonus).toBe("none");
    }
  });
});

function landBonusOf(random: () => number): RoundScript["landBonus"] {
  return rollRound(random).landBonus;
}

function sequence(values: readonly number[]): () => number {
  let index = 0;
  return () => {
    const value = values[index];
    index += 1;
    if (value === undefined) {
      throw new Error(`random sequence exhausted at call ${String(index)}`);
    }
    return value;
  };
}
