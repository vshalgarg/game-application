import Phaser from "phaser";
import { copy } from "../../config/copy";
import { gameConfig } from "../../config/gameConfig";
import { clouds, layout } from "../../config/layout";
import { css, palette } from "../../config/palette";
import { sceneKeys } from "../../config/sceneKeys";
import { STAGE_HEIGHT, STAGE_WIDTH } from "../../config/stage";
import { placeStake, readCoins, saveCoins, type StakeRejection } from "../../logic/coins";
import { rollDrop } from "../../logic/rollDrop";
import { openingStake, stepStake, type StakeDirection } from "../../logic/stake";
import { spawnClouds, stepDrift, type DriftCloud } from "../art/driftClouds";
import { textureKeys } from "../art/textureKeys";
import { planePoint, planeTilt } from "../fall/flight";
import { setLaunch } from "../flow/session";
import { coinStore } from "../storage/coinStore";
import { createButton } from "../ui/button";
import { labelStyle } from "../ui/textStyle";

export class ReadyScene extends Phaser.Scene {
  private balance = 0;
  private stake = 0;
  private dropping = false;
  private balanceText: Phaser.GameObjects.Text | undefined;
  private stakeText: Phaser.GameObjects.Text | undefined;
  private noticeText: Phaser.GameObjects.Text | undefined;
  private plane: Phaser.GameObjects.Image | undefined;
  private cloudBank: readonly DriftCloud[] = [];

  public constructor() {
    super({ key: sceneKeys.ready });
  }

  public create(): void {
    this.dropping = false;
    this.balance = readCoins(coinStore);
    this.stake = openingStake(this.balance);
    this.paintSky();
    this.cloudBank = spawnClouds(
      this,
      clouds.filter((cloud) => cloud.y < 400),
      { back: 1, front: 2.5 },
    );
    this.plane = this.add.image(layout.centerX, layout.planeCruiseY, textureKeys.planeBoarded);
    this.plane.setScale(layout.fallPlaneScale).setDepth(2);
    this.flyPlane(0);
    this.addCopy();
    this.addStakeButtons();
    createButton(this, {
      x: layout.centerX,
      y: layout.readyDropY,
      label: copy.drop,
      fill: palette.cash,
      hover: palette.cashHover,
      depth: layout.readyUiDepth,
      onClick: () => {
        this.drop();
      },
    });
    createButton(this, {
      x: layout.readyMenuX,
      y: layout.readyMenuY,
      label: copy.menu,
      width: 180,
      depth: layout.readyUiDepth,
      onClick: () => {
        this.scene.start(sceneKeys.title);
      },
    });
    this.refresh();
  }

  public override update(time: number, delta: number): void {
    this.flyPlane(time);
    stepDrift(this.cloudBank, delta, STAGE_WIDTH + 480);
  }

  private paintSky(): void {
    const sky = this.add.graphics();
    sky.fillStyle(palette.sky);
    sky.fillRect(0, 0, STAGE_WIDTH, STAGE_HEIGHT);
    const dock = this.add.graphics();
    dock.fillStyle(palette.panel, 0.94);
    dock.fillRoundedRect(60, 430, STAGE_WIDTH - 120, 270, 24);
    dock.setDepth(layout.readyDockDepth);
  }

  private flyPlane(time: number): void {
    const plane = this.plane;
    if (plane === undefined) {
      return;
    }
    const spot = planePoint(time, layout.centerX);
    plane.setPosition(spot.x, spot.y);
    plane.setAngle(planeTilt(time));
  }

  private addCopy(): void {
    const paper = css(palette.paper);
    this.balanceText = this.add
      .text(
        layout.centerX,
        layout.readyCoinsY,
        "",
        labelStyle(layout.headingSize, css(palette.gold)),
      )
      .setOrigin(0.5)
      .setDepth(layout.readyUiDepth);
    this.add
      .text(
        layout.centerX,
        layout.readyHintY,
        copy.cashOutHint,
        labelStyle(layout.smallSize, paper),
      )
      .setOrigin(0.5)
      .setDepth(layout.readyUiDepth);
    this.noticeText = this.add
      .text(
        layout.centerX,
        layout.readyNoticeY,
        "",
        labelStyle(layout.smallSize, css(palette.notice)),
      )
      .setOrigin(0.5)
      .setDepth(layout.readyUiDepth);
    this.stakeText = this.add
      .text(layout.centerX, layout.readyStakeY, "", labelStyle(layout.bodySize, paper))
      .setOrigin(0.5)
      .setDepth(layout.readyUiDepth);
  }

  private addStakeButtons(): void {
    createButton(this, {
      x: layout.centerX - 180,
      y: layout.readyStepY,
      label: copy.minus,
      width: layout.smallButtonWidth,
      depth: layout.readyUiDepth,
      onClick: () => {
        this.changeStake("down");
      },
    });
    createButton(this, {
      x: layout.centerX + 180,
      y: layout.readyStepY,
      label: copy.plus,
      width: layout.smallButtonWidth,
      depth: layout.readyUiDepth,
      onClick: () => {
        this.changeStake("up");
      },
    });
  }

  private changeStake(direction: StakeDirection): void {
    this.stake = stepStake(this.balance, this.stake, direction);
    this.refresh();
  }

  private drop(): void {
    if (this.dropping) {
      return;
    }
    if (this.balance < gameConfig.minStake) {
      this.noticeText?.setText(copy.insufficient);
      return;
    }
    const placed = placeStake(this.balance, this.stake);
    if (!placed.accepted) {
      this.noticeText?.setText(messageFor(placed.reason));
      return;
    }
    this.dropping = true;
    saveCoins(coinStore, placed.balance);
    const planeX = this.plane?.x ?? layout.releasePlaneX;
    const drop = rollDrop(Math.random, planeX);
    setLaunch({
      script: drop.script,
      timeline: drop.timeline,
      stake: this.stake,
      balanceAfterStake: placed.balance,
      planeX,
    });
    this.scene.start(sceneKeys.fall);
  }

  private refresh(): void {
    this.balanceText?.setText(`${copy.coins} ${String(this.balance)}`);
    this.stakeText?.setText(`${copy.stake} ${String(this.stake)}`);
    this.noticeText?.setText("");
  }
}

function messageFor(reason: StakeRejection): string {
  if (reason === "empty") {
    return copy.emptyStake;
  }
  return copy.insufficient;
}
