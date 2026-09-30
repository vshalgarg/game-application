import { describe, expect, it } from "vitest";
import { gameConfig } from "../config/gameConfig";
import { resolveContact, type Contact } from "./contacts";

describe("resolveContact", () => {
  it("adds a friendly flag and a white cloud", () => {
    expect(resolveContact(2, contact("boost", gameConfig.boostAmount))).toBe(2.5);
    expect(resolveContact(2, contact("boost", gameConfig.cutAmount))).toBe(2.25);
  });

  it("halves the multiplier and does not go under the floor", () => {
    expect(resolveContact(2, contact("halve", 0))).toBe(1);
    expect(resolveContact(gameConfig.minimumMultiplier, contact("halve", 0))).toBe(
      gameConfig.minimumMultiplier,
    );
  });

  it("ends the round on a fatal hit", () => {
    expect(resolveContact(4, contact("fatal", 0))).toBe(0);
  });
});

function contact(effect: Contact["effect"], amount: number): Contact {
  return {
    atMs: 0,
    obstacleId: 1,
    effect,
    amount,
    label: "",
    cause: effect === "fatal" ? "china" : "none",
  };
}
