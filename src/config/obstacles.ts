import { copy } from "./copy";
import { gameConfig } from "./gameConfig";

export const riderBox = {
  width: 34,
  height: 64,
} as const;

export type ObstacleKind =
  | "india"
  | "canada"
  | "uae"
  | "korea"
  | "japan"
  | "greenland"
  | "russia"
  | "iran"
  | "brazil"
  | "china"
  | "cloud"
  | "platform"
  | "angry";

export type ContactEffect = "boost" | "halve" | "fatal" | "stick";

export type BustCause = "none" | "china" | "angry";

export interface ObstacleShape {
  readonly effect: ContactEffect;
  readonly amount: number;
  readonly width: number;
  readonly height: number;
  readonly label: string;
  readonly cause: BustCause;
}

const flag = {
  width: 64,
  height: 42,
} as const;

export const obstacleCatalog: Record<ObstacleKind, ObstacleShape> = {
  india: {
    effect: "boost",
    amount: gameConfig.boostAmount,
    ...flag,
    label: "+0.50",
    cause: "none",
  },
  canada: {
    effect: "boost",
    amount: gameConfig.boostAmount,
    ...flag,
    label: "+0.50",
    cause: "none",
  },
  uae: {
    effect: "boost",
    amount: gameConfig.boostAmount,
    ...flag,
    label: "+0.50",
    cause: "none",
  },
  korea: {
    effect: "boost",
    amount: gameConfig.boostAmount,
    ...flag,
    label: "+0.50",
    cause: "none",
  },
  japan: {
    effect: "boost",
    amount: gameConfig.boostAmount,
    ...flag,
    label: "+0.50",
    cause: "none",
  },
  greenland: {
    effect: "boost",
    amount: gameConfig.boostAmount,
    ...flag,
    label: "+0.50",
    cause: "none",
  },
  russia: {
    effect: "halve",
    amount: 0,
    ...flag,
    label: copy.halve,
    cause: "none",
  },
  iran: {
    effect: "halve",
    amount: 0,
    ...flag,
    label: copy.halve,
    cause: "none",
  },
  brazil: {
    effect: "halve",
    amount: 0,
    ...flag,
    label: copy.halve,
    cause: "none",
  },
  china: {
    effect: "fatal",
    amount: 0,
    ...flag,
    label: copy.china,
    cause: "china",
  },
  cloud: {
    effect: "boost",
    amount: gameConfig.cutAmount,
    width: 100,
    height: 54,
    label: "+0.25",
    cause: "none",
  },
  platform: {
    effect: "stick",
    amount: gameConfig.boostAmount,
    width: 220,
    height: 96,
    label: "+0.50",
    cause: "none",
  },
  angry: {
    effect: "fatal",
    amount: 0,
    width: 120,
    height: 78,
    label: copy.angryCloud,
    cause: "angry",
  },
};

export const pathKinds = [
  "india",
  "canada",
  "uae",
  "korea",
  "japan",
  "greenland",
  "russia",
  "iran",
  "brazil",
  "cloud",
  "platform",
] as const;

export const fatalKinds = ["china", "angry"] as const;
