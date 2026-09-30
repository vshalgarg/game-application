import Phaser from "phaser";
import { layout } from "../../config/layout";
import { palette } from "../../config/palette";
import { STAGE_HEIGHT, STAGE_WIDTH } from "../../config/stage";
import { textureKeys } from "./textureKeys";

export function paintOffice(scene: Phaser.Scene): void {
  const room = scene.add.graphics();
  room.fillStyle(palette.skyLight);
  room.fillRect(0, 0, STAGE_WIDTH, STAGE_HEIGHT);
  room.fillStyle(palette.stone);
  room.fillRect(0, 70, STAGE_WIDTH, 470);
  room.fillStyle(palette.sky);
  room.fillRoundedRect(360, 110, 560, 250, 12);
  drawColumns(room);
  drawFlag(room, 250, 150);
  drawFlag(room, 980, 150);
  room.fillStyle(palette.carpet);
  room.fillRect(0, 540, STAGE_WIDTH, STAGE_HEIGHT - 540);

  const chief = scene.add.image(layout.titleChiefX, layout.titleChiefFeetY, textureKeys.chief);
  chief.setOrigin(0.5, 1);
  chief.setScale(layout.titleChiefScale);
  chief.texture.setFilter(Phaser.Textures.FilterMode.LINEAR);

  const desk = scene.add.graphics();
  desk.fillStyle(palette.desk);
  desk.fillRoundedRect(layout.deskX, layout.deskY, layout.deskWidth, layout.deskHeight, 10);
  desk.fillStyle(palette.gold);
  desk.fillRect(layout.deskX + 40, layout.deskY + 28, 70, 8);
}

function drawColumns(graphics: Phaser.GameObjects.Graphics): void {
  const columns = [80, 180, 1040, 1140];
  graphics.fillStyle(palette.column);
  for (const x of columns) {
    graphics.fillRect(x, 70, 46, 470);
    graphics.fillCircle(x + 23, 70, 28);
  }
}

function drawFlag(graphics: Phaser.GameObjects.Graphics, x: number, y: number): void {
  graphics.fillStyle(palette.flagPole);
  graphics.fillRect(x, y, 8, 220);
  graphics.fillStyle(palette.flagRed);
  graphics.fillRect(x + 8, y + 8, 78, 52);
  graphics.fillStyle(palette.white);
  graphics.fillRect(x + 8, y + 20, 78, 8);
  graphics.fillRect(x + 8, y + 36, 78, 8);
  graphics.fillStyle(palette.flagBlue);
  graphics.fillRect(x + 8, y + 8, 30, 24);
}
