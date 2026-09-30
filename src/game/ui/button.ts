import Phaser from "phaser";
import { layout } from "../../config/layout";
import { css, palette } from "../../config/palette";
import { labelStyle } from "./textStyle";

export interface ButtonOptions {
  readonly x: number;
  readonly y: number;
  readonly label: string;
  readonly onClick: () => void;
  readonly width?: number;
  readonly fill?: number;
  readonly hover?: number;
  readonly fixed?: boolean;
  readonly depth?: number;
}

export function createButton(
  scene: Phaser.Scene,
  options: ButtonOptions,
): Phaser.GameObjects.Rectangle {
  const width = options.width ?? layout.buttonWidth;
  const fill = options.fill ?? palette.button;
  const hover = options.hover ?? palette.buttonHover;
  const button = scene.add.rectangle(options.x, options.y, width, layout.buttonHeight, fill);
  button.setStrokeStyle(4, palette.ink);
  const label = scene.add.text(
    options.x,
    options.y,
    options.label,
    labelStyle(layout.smallSize, css(palette.paper)),
  );
  label.setOrigin(0.5);
  if (options.fixed === true) {
    button.setScrollFactor(0).setDepth(layout.hudDepth);
    label.setScrollFactor(0).setDepth(layout.hudDepth);
  }
  if (options.depth !== undefined) {
    button.setDepth(options.depth);
    label.setDepth(options.depth);
  }
  button.setInteractive({ useHandCursor: true });
  button.on("pointerover", () => {
    button.setFillStyle(hover);
  });
  button.on("pointerout", () => {
    button.setFillStyle(fill);
  });
  button.on("pointerdown", () => {
    options.onClick();
  });
  return button;
}
