import Phaser from "phaser";
import { columnOffsets, layout, padAnchor, padOrder } from "../../config/layout";
import { padLabel } from "../../config/copy";
import { css, palette, skyBands } from "../../config/palette";
import { STAGE_WIDTH } from "../../config/stage";
import type { RoundScript } from "../../logic/rollRound";
import { labelStyle } from "../ui/textStyle";

export function paintFallWorld(scene: Phaser.Scene, script: RoundScript): void {
  const world = scene.add.graphics();
  paintSky(world);
  paintGround(world);
  paintHouse(world);
  paintPads(scene, world, script);
}

function paintSky(graphics: Phaser.GameObjects.Graphics): void {
  const bandHeight = layout.worldHeight / skyBands.length;
  for (const [index, color] of skyBands.entries()) {
    graphics.fillStyle(color);
    graphics.fillRect(0, index * bandHeight, STAGE_WIDTH, bandHeight + 1);
  }
}

function paintGround(graphics: Phaser.GameObjects.Graphics): void {
  graphics.fillStyle(palette.lawn);
  graphics.fillRect(0, layout.groundY, STAGE_WIDTH, layout.worldHeight - layout.groundY);
  graphics.fillStyle(palette.lawnDark);
  graphics.fillRect(0, layout.groundY, STAGE_WIDTH, 10);
}

function paintHouse(graphics: Phaser.GameObjects.Graphics): void {
  const left = layout.houseX;
  const top = layout.houseY;
  const width = layout.houseWidth;
  graphics.fillStyle(palette.stone);
  graphics.fillTriangle(
    left,
    top,
    left + width / 2,
    top - layout.pedimentHeight,
    left + width,
    top,
  );
  graphics.fillRect(left, top, width, layout.houseHeight);
  graphics.fillStyle(palette.column);
  for (const offset of columnOffsets) {
    graphics.fillRect(left + offset, top + 24, layout.columnWidth, layout.houseHeight - 24);
  }
  graphics.fillStyle(palette.door);
  graphics.fillRect(left + width / 2 - 28, top + layout.houseHeight - 90, 56, 90);
  graphics.fillStyle(palette.flagPole);
  graphics.fillRect(left + width / 2 - 3, top - layout.pedimentHeight - 40, 6, 50);
  graphics.fillStyle(palette.flagRed);
  graphics.fillRect(left + width / 2 + 3, top - layout.pedimentHeight - 36, 36, 22);
}

function paintPads(
  scene: Phaser.Scene,
  graphics: Phaser.GameObjects.Graphics,
  script: RoundScript,
): void {
  for (const bonus of padOrder) {
    const selected = script.end === "land" && script.landBonus === bonus;
    const x = padAnchor[bonus];
    graphics.fillStyle(selected ? palette.gold : palette.pad);
    graphics.fillRoundedRect(
      x - layout.padWidth / 2,
      layout.padY,
      layout.padWidth,
      layout.padHeight,
      8,
    );
    const label = scene.add.text(
      x,
      layout.padY + layout.padHeight / 2,
      padLabel[bonus],
      labelStyle(22, css(palette.ink)),
    );
    label.setOrigin(0.5);
    label.setDepth(3);
  }
}
