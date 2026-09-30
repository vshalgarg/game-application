import Phaser from "phaser";
import { layout } from "../../config/layout";
import { palette } from "../../config/palette";
import { STAGE_HEIGHT, STAGE_WIDTH } from "../../config/stage";
import { textureKeys } from "./textureKeys";

export function paintPanel(scene: Phaser.Scene, withChief: boolean): void {
  const graphics = scene.add.graphics();
  graphics.fillStyle(palette.sky);
  graphics.fillRect(0, 0, STAGE_WIDTH, STAGE_HEIGHT);
  graphics.fillStyle(palette.lawn);
  graphics.fillRect(0, 560, STAGE_WIDTH, STAGE_HEIGHT - 560);
  graphics.fillStyle(palette.panel);
  graphics.fillRoundedRect(
    layout.panelX,
    layout.panelY,
    layout.panelWidth,
    layout.panelHeight,
    layout.panelRadius,
  );
  if (!withChief) {
    return;
  }
  const chief = scene.add.image(layout.readyChiefX, layout.readyChiefY, textureKeys.chief);
  chief.setOrigin(0.5, 1);
  chief.setScale(layout.readyChiefScale);
}
