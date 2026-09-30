import Phaser from "phaser";
import { layout } from "../../config/layout";

export function labelStyle(size: number, color: string): Phaser.Types.GameObjects.Text.TextStyle {
  return {
    fontFamily: layout.fontFamily,
    fontSize: `${String(size)}px`,
    color,
    align: "center",
    fontStyle: "bold",
  };
}
