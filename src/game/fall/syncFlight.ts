import Phaser from "phaser";
import { layout } from "../../config/layout";
import { css, palette } from "../../config/palette";
import type { Contact } from "../../logic/contacts";
import { sampleAt, type FallTimeline } from "../../logic/fallSim";
import { multiplierAt } from "../../logic/multiplier";
import type { RoundScript } from "../../logic/rollRound";
import { textureKeys } from "../art/textureKeys";
import { formatEvent, formatMultiplier } from "../ui/format";
import { labelStyle } from "../ui/textStyle";
import { playPop } from "../audio/cues";
import { characterPoint, planePoint, planeTilt } from "./flight";
import { playImpact } from "./impacts";
import type { ObstacleView } from "./paintObstacles";

export interface FlightState {
  readonly script: RoundScript;
  readonly timeline: FallTimeline;
  readonly originX: number;
  readonly plane: Phaser.GameObjects.Image;
  readonly character: Phaser.GameObjects.Image;
  readonly multiplier: Phaser.GameObjects.Text;
  readonly popup: Phaser.GameObjects.Text;
  readonly scene: Phaser.Scene;
  readonly views: ReadonlyMap<number, ObstacleView>;
  nextEvent: number;
  nextContact: number;
  popupUntil: number;
  impactedAt: number;
}

export function createFlight(
  scene: Phaser.Scene,
  script: RoundScript,
  timeline: FallTimeline,
  multiplier: Phaser.GameObjects.Text,
  originX: number,
  views: readonly ObstacleView[],
): FlightState {
  const plane = scene.add.image(layout.releasePlaneX, layout.planeCruiseY, textureKeys.plane);
  plane.setScale(layout.fallPlaneScale).setDepth(layout.characterDepth - 1);
  const character = scene.add.image(layout.releasePlaneX, layout.planeCruiseY, textureKeys.chief);
  character.setOrigin(0.5, 1).setScale(layout.riderScale).setDepth(layout.characterDepth);
  character.texture.setFilter(Phaser.Textures.FilterMode.NEAREST);
  const popup = scene.add.text(0, 0, "", labelStyle(layout.popupSize, css(palette.gold)));
  popup.setOrigin(0.5).setStroke(css(palette.ink), 5).setDepth(layout.stormDepth).setAlpha(0);
  return {
    script,
    timeline,
    originX,
    plane,
    character,
    multiplier,
    popup,
    scene,
    views: new Map(views.map((view) => [view.id, view])),
    nextEvent: 0,
    nextContact: 0,
    popupUntil: 0,
    impactedAt: -1_000,
  };
}

export function syncFlight(state: FlightState, elapsedMs: number): void {
  const plane = planePoint(elapsedMs, state.originX);
  state.plane.setPosition(plane.x, plane.y);
  state.plane.setAngle(planeTilt(elapsedMs));
  const point = placeCharacter(state, elapsedMs);
  state.multiplier.setText(formatMultiplier(multiplierAt(state.script, elapsedMs, state.timeline)));
  revealEvents(state, elapsedMs, point.x, point.y);
  revealContacts(state, elapsedMs, point.x, point.y);
  if (elapsedMs > state.popupUntil) {
    state.popup.setAlpha(0);
  }
}

function placeCharacter(state: FlightState, elapsedMs: number): { x: number; y: number } {
  const sampled = sampleAt(state.timeline.samples, elapsedMs);
  if (sampled === undefined) {
    const point = characterPoint(state.script, elapsedMs, state.originX);
    state.character.setPosition(point.x, point.y);
    state.character.setAngle(Math.sin(elapsedMs / layout.wobbleMs) * layout.wobbleDegrees);
    return point;
  }
  state.character.setPosition(sampled.x, sampled.y);
  state.character.setAngle(sampled.angle);
  const punch = elapsedMs - state.impactedAt < 140;
  const wide = punch ? 1.14 : 1;
  const flat = punch ? 0.84 : 1;
  state.character.setScale(layout.riderScale * wide, layout.riderScale * flat);
  return sampled;
}

function revealEvents(state: FlightState, elapsedMs: number, x: number, y: number): void {
  let event = state.script.events[state.nextEvent];
  while (event !== undefined && elapsedMs >= event.atFraction * state.script.durationMs) {
    showPopup(state, formatEvent(event), false, elapsedMs, x, y);
    state.nextEvent += 1;
    event = state.script.events[state.nextEvent];
  }
}

function revealContacts(state: FlightState, elapsedMs: number, x: number, y: number): void {
  let contact = state.timeline.contacts[state.nextContact];
  while (contact !== undefined && elapsedMs >= contact.atMs) {
    showPopup(state, contact.label, isDanger(contact), elapsedMs, x, y);
    state.impactedAt = elapsedMs;
    playImpact(state.scene, state.views.get(contact.obstacleId), contact, x, y);
    playPop(state.scene, isDanger(contact));
    state.nextContact += 1;
    contact = state.timeline.contacts[state.nextContact];
  }
}

function showPopup(
  state: FlightState,
  label: string,
  danger: boolean,
  elapsedMs: number,
  x: number,
  y: number,
): void {
  state.popup.setText(label);
  state.popup.setColor(danger ? css(palette.notice) : css(palette.gold));
  state.popup.setPosition(x, y - layout.popupLift);
  state.popup.setAlpha(1);
  state.popupUntil = elapsedMs + layout.popupMs;
}

function isDanger(contact: Contact): boolean {
  return contact.effect === "halve" || contact.effect === "fatal";
}
