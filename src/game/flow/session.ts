import type { LandBonus } from "../../config/gameConfig";
import type { BustCause } from "../../config/obstacles";
import type { RoundSettlement } from "../../logic/coins";
import type { FallTimeline } from "../../logic/fallSim";
import type { RoundScript } from "../../logic/rollRound";

export interface FallLaunch {
  readonly script: RoundScript;
  readonly timeline: FallTimeline;
  readonly stake: number;
  readonly balanceAfterStake: number;
  readonly planeX: number;
}

export interface RoundResult {
  readonly outcome: RoundSettlement;
  readonly stake: number;
  readonly multiplier: number;
  readonly payout: number;
  readonly balance: number;
  readonly landBonus: LandBonus;
  readonly bustCause: BustCause;
}

let launch: FallLaunch | undefined;
let result: RoundResult | undefined;

export function setLaunch(value: FallLaunch): void {
  launch = value;
}

export function readLaunch(): FallLaunch | undefined {
  return launch;
}

export function setResult(value: RoundResult): void {
  result = value;
}

export function readResult(): RoundResult | undefined {
  return result;
}
