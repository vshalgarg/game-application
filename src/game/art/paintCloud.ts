import { css, palette } from "../../config/palette";

export function paintCloudCanvas(ctx: CanvasRenderingContext2D): void {
  ctx.lineJoin = "round";
  ctx.lineCap = "round";
  traceCloud(ctx);
  ctx.fillStyle = css(palette.cloudShade);
  ctx.fill();
  ctx.save();
  traceCloud(ctx);
  ctx.clip();
  ctx.fillStyle = css(palette.white);
  ctx.beginPath();
  ctx.ellipse(118, 58, 78, 36, 0, 0, Math.PI * 2);
  ctx.ellipse(78, 78, 48, 28, 0, 0, Math.PI * 2);
  ctx.ellipse(168, 74, 52, 30, 0, 0, Math.PI * 2);
  ctx.fill();
  ctx.fillStyle = css(palette.paper);
  ctx.beginPath();
  ctx.ellipse(108, 46, 36, 16, -0.4, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();
  traceCloud(ctx);
  ctx.lineWidth = 8;
  ctx.strokeStyle = css(palette.ink);
  ctx.stroke();
}

function traceCloud(ctx: CanvasRenderingContext2D): void {
  ctx.beginPath();
  ctx.moveTo(54, 96);
  ctx.bezierCurveTo(16, 98, 8, 58, 52, 50);
  ctx.bezierCurveTo(58, 16, 118, 8, 136, 42);
  ctx.bezierCurveTo(168, 8, 232, 22, 228, 58);
  ctx.bezierCurveTo(256, 64, 252, 104, 214, 108);
  ctx.bezierCurveTo(196, 132, 112, 134, 96, 108);
  ctx.bezierCurveTo(72, 130, 28, 122, 54, 96);
  ctx.closePath();
}
