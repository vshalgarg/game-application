import Phaser from "phaser";
import { copy } from "../../config/copy";
import { layout } from "../../config/layout";
import { css, palette } from "../../config/palette";
import { formatMultiplier } from "../ui/format";
import { createButton } from "../ui/button";
import { labelStyle } from "../ui/textStyle";

export function createFallHud(
  scene: Phaser.Scene,
  stake: number,
  onCashOut: () => void,
): Phaser.GameObjects.Text {
  const stakeText = scene.add.text(
    layout.hudStakeX,
    layout.hudStakeY,
    `${copy.stake} ${String(stake)}`,
    labelStyle(layout.smallSize, css(palette.ink)),
  );
  stakeText.setOrigin(0, 0.5).setScrollFactor(0).setDepth(layout.hudDepth);
  const multiplier = scene.add.text(
    layout.centerX,
    layout.hudMultiplierY,
    formatMultiplier(1),
    labelStyle(layout.hudSize, css(palette.ink)),
  );
  multiplier.setOrigin(0.5).setScrollFactor(0).setDepth(layout.hudDepth);
  multiplier.setStroke(css(palette.paper), 6);
  createButton(scene, {
    x: layout.centerX,
    y: layout.hudButtonY,
    label: copy.cashOut,
    fill: palette.cash,
    hover: palette.cashHover,
    fixed: true,
    onClick: onCashOut,
  });
  return multiplier;
}
