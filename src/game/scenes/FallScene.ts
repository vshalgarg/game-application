import Phaser from "phaser";
import { layout } from "../../config/layout";
import type { BustCause } from "../../config/obstacles";
import { sceneKeys } from "../../config/sceneKeys";
import { STAGE_WIDTH } from "../../config/stage";
import { spawnWorldClouds, stepDrift, type DriftCloud } from "../art/driftClouds";
import { cashOut, saveCoins, settleBalance, type RoundSettlement } from "../../logic/coins";
import type { FallTimeline } from "../../logic/fallSim";
import { multiplierAt } from "../../logic/multiplier";
import { playChime, playSting, startWind } from "../audio/cues";
import { readLaunch, setResult } from "../flow/session";
import { coinStore } from "../storage/coinStore";
import { createFallHud } from "../fall/FallHud";
import { paintFallWorld } from "../fall/paintFallWorld";
import { paintObstacles } from "../fall/paintObstacles";
import { createFlight, syncFlight, type FlightState } from "../fall/syncFlight";

export class FallScene extends Phaser.Scene {
  private flight: FlightState | undefined;
  private startedAt = 0;
  private finished = false;
  private stopWind: (() => void) | undefined;
  private spaceKey: Phaser.Input.Keyboard.Key | undefined;
  private cloudBank: readonly DriftCloud[] = [];

  public constructor() {
    super({ key: sceneKeys.fall });
  }

  public create(): void {
    const launch = readLaunch();
    if (launch === undefined) {
      this.scene.start(sceneKeys.ready);
      return;
    }
    this.finished = false;
    paintFallWorld(this, launch.script);
    const obstacles = paintObstacles(this, launch.timeline.obstacles);
    this.cloudBank = spawnWorldClouds(this);
    const multiplier = createFallHud(this, launch.stake, () => {
      this.finish("cashOut");
    });
    this.flight = createFlight(
      this,
      launch.script,
      launch.timeline,
      multiplier,
      launch.planeX,
      obstacles,
    );
    this.cameras.main.setBounds(0, 0, STAGE_WIDTH, layout.worldHeight);
    this.cameras.main.startFollow(
      this.flight.character,
      true,
      layout.cameraLerpX,
      layout.cameraLerpY,
    );
    this.startedAt = this.game.loop.time;
    syncFlight(this.flight, 0);
    this.stopWind = startWind(this);
    this.spaceKey = this.bindSpace();
    this.events.once(Phaser.Scenes.Events.SHUTDOWN, () => {
      this.haltWind();
      this.releaseSpace();
    });
  }

  public override update(_time: number, delta: number): void {
    stepDrift(this.cloudBank, delta, STAGE_WIDTH + 520);
    if (this.flight === undefined || this.finished) {
      return;
    }
    const elapsed = this.elapsedMs();
    syncFlight(this.flight, elapsed);
    if (elapsed >= this.flight.timeline.endMs) {
      const outcome = this.flight.timeline.stuck ? "cashOut" : this.flight.script.end;
      this.finish(outcome);
    }
  }

  private finish(outcome: RoundSettlement): void {
    if (this.finished || this.flight === undefined) {
      return;
    }
    const launch = readLaunch();
    if (launch === undefined) {
      this.scene.start(sceneKeys.ready);
      return;
    }
    this.finished = true;
    this.haltWind();
    const elapsed = Math.min(this.elapsedMs(), launch.timeline.endMs);
    const multiplier = multiplierAt(launch.script, elapsed, launch.timeline);
    const payout = cashOut(launch.stake, multiplier);
    const balance = settleBalance(launch.balanceAfterStake, launch.stake, multiplier, outcome);
    saveCoins(coinStore, balance);
    this.playEndCue(outcome, payout);
    setResult({
      outcome,
      stake: launch.stake,
      multiplier,
      payout,
      balance,
      landBonus: launch.script.landBonus,
      bustCause: outcome === "bust" ? bustCauseOf(launch.timeline) : "none",
    });
    this.scene.start(sceneKeys.result);
  }

  private playEndCue(outcome: RoundSettlement, payout: number): void {
    if (outcome === "bust" || payout <= 0) {
      playSting(this);
      return;
    }
    playChime(this);
  }

  private bindSpace(): Phaser.Input.Keyboard.Key | undefined {
    const keyboard = this.input.keyboard;
    if (keyboard === null) {
      return undefined;
    }
    const space = keyboard.addKey(Phaser.Input.Keyboard.KeyCodes.SPACE);
    space.on("down", () => {
      this.finish("cashOut");
    });
    return space;
  }

  private releaseSpace(): void {
    if (this.spaceKey === undefined) {
      return;
    }
    this.input.keyboard?.removeKey(this.spaceKey);
    this.spaceKey = undefined;
  }

  private haltWind(): void {
    this.stopWind?.();
    this.stopWind = undefined;
  }

  private elapsedMs(): number {
    return Math.max(0, this.game.loop.time - this.startedAt);
  }
}

function bustCauseOf(timeline: FallTimeline): BustCause {
  const fatal = timeline.contacts.find((contact) => contact.effect === "fatal");
  if (fatal?.cause === "china" || fatal?.cause === "angry") {
    return fatal.cause;
  }
  return "angry";
}
