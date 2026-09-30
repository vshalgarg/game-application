import { layout } from "../config/layout";
import { simulateFall, type FallTimeline } from "./fallSim";
import { rollField } from "./field";
import { rollRound, type RoundScript } from "./rollRound";

export interface RolledDrop {
  readonly script: RoundScript;
  readonly timeline: FallTimeline;
}

export function rollDrop(
  random: () => number = Math.random,
  originX: number = layout.releasePlaneX,
): RolledDrop {
  const rolled = rollRound(random);
  const script: RoundScript = { ...rolled, events: [] };
  const obstacles = rollField(script, originX, random);
  return {
    script,
    timeline: simulateFall(script, obstacles, originX),
  };
}
