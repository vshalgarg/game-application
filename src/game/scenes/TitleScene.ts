import Phaser from "phaser";
import { copy } from "../../config/copy";
import { layout } from "../../config/layout";
import { css, palette } from "../../config/palette";
import { sceneKeys } from "../../config/sceneKeys";
import { readCoins, resetCoins } from "../../logic/coins";
import { paintOffice } from "../art/paintOffice";
import { coinStore } from "../storage/coinStore";
import { createButton } from "../ui/button";
import { labelStyle } from "../ui/textStyle";

export class TitleScene extends Phaser.Scene {
  private coinsText: Phaser.GameObjects.Text | undefined;

  public constructor() {
    super({ key: sceneKeys.title });
  }

  public create(): void {
    paintOffice(this);
    this.addTitle();
    this.coinsText = this.add
      .text(layout.centerX, layout.coinsY, "", labelStyle(layout.bodySize, css(palette.paper)))
      .setOrigin(0.5);
    this.refreshCoins();
    createButton(this, {
      x: layout.centerX,
      y: layout.playY,
      label: copy.clickToPlay,
      onClick: () => {
        this.scene.start(sceneKeys.ready);
      },
    });
    createButton(this, {
      x: layout.resetX,
      y: layout.resetY,
      label: copy.resetCoins,
      width: 220,
      onClick: () => {
        resetCoins(coinStore);
        this.refreshCoins();
      },
    });
  }

  private addTitle(): void {
    const title = this.add
      .text(
        layout.centerX,
        layout.titleY,
        copy.title,
        labelStyle(layout.titleSize, css(palette.gold)),
      )
      .setOrigin(0.5)
      .setStroke(css(palette.ink), 8)
      .setAlign("center");
    title.setLineSpacing(-8);
  }

  private refreshCoins(): void {
    this.coinsText?.setText(`${copy.coins} ${String(readCoins(coinStore))}`);
  }
}
