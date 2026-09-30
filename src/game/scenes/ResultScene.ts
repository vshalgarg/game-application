import Phaser from "phaser";
import { copy, padLabel } from "../../config/copy";
import { layout } from "../../config/layout";
import { css, palette } from "../../config/palette";
import { sceneKeys } from "../../config/sceneKeys";
import { coinDelta, type RoundSettlement } from "../../logic/coins";
import { paintPanel } from "../art/paintPanel";
import { readResult, type RoundResult } from "../flow/session";
import { createButton } from "../ui/button";
import { formatDelta, formatMultiplier } from "../ui/format";
import { labelStyle } from "../ui/textStyle";

export class ResultScene extends Phaser.Scene {
  public constructor() {
    super({ key: sceneKeys.result });
  }

  public create(): void {
    const result = readResult();
    if (result === undefined) {
      this.scene.start(sceneKeys.title);
      return;
    }
    paintPanel(this, false);
    this.addOutcome(result);
    this.add
      .text(
        layout.centerX,
        layout.resultMultiplierY,
        formatMultiplier(result.multiplier),
        labelStyle(layout.hudSize, css(palette.paper)),
      )
      .setOrigin(0.5);
    this.add
      .text(
        layout.centerX,
        layout.resultDetailY,
        `${copy.stake} ${String(result.stake)}    ${formatDelta(coinDelta(result.stake, result.payout))}`,
        labelStyle(layout.bodySize, css(palette.paper)),
      )
      .setOrigin(0.5);
    this.add
      .text(
        layout.centerX,
        layout.resultBalanceY,
        `${copy.coins} ${String(result.balance)}`,
        labelStyle(layout.headingSize, css(palette.gold)),
      )
      .setOrigin(0.5);
    this.addPad(result);
    createButton(this, {
      x: layout.centerX,
      y: layout.resultPlayY,
      label: copy.playAgain,
      onClick: () => {
        this.scene.start(sceneKeys.ready);
      },
    });
  }

  private addOutcome(result: RoundResult): void {
    this.add
      .text(
        layout.centerX,
        layout.resultTitleY,
        outcomeTitle(result),
        labelStyle(layout.headingSize, outcomeColor(result.outcome)),
      )
      .setOrigin(0.5);
  }

  private addPad(result: RoundResult): void {
    if (result.outcome !== "land") {
      return;
    }
    this.add
      .text(
        layout.centerX,
        layout.resultPadY,
        padLabel[result.landBonus],
        labelStyle(layout.bodySize, css(palette.gold)),
      )
      .setOrigin(0.5);
  }
}

function outcomeTitle(result: RoundResult): string {
  if (result.outcome === "cashOut") {
    return copy.cashedOut;
  }
  if (result.outcome === "land") {
    return copy.landed;
  }
  if (result.bustCause === "china") {
    return copy.china;
  }
  if (result.bustCause === "angry") {
    return copy.angryCloud;
  }
  return copy.stormed;
}

function outcomeColor(outcome: RoundSettlement): string {
  if (outcome === "bust") {
    return css(palette.notice);
  }
  return css(palette.gold);
}
