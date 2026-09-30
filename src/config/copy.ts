import type { LandBonus } from "./gameConfig";

export const copy = {
  title: "CHIEF\nDROP",
  clickToPlay: "Click to Play",
  resetCoins: "Reset coins",
  coins: "Coins",
  stake: "Stake",
  drop: "Drop",
  cashOutHint: "Cash out before an angry cloud.",
  china: "China",
  angryCloud: "Angry cloud",
  halve: "1/2",
  cashOut: "Cash out",
  menu: "Menu",
  playAgain: "Play again",
  cashedOut: "Cashed out",
  stormed: "Stormed",
  landed: "Landed",
  plus: "+",
  minus: "−",
  emptyStake: "Set a stake first.",
  insufficient: "Not enough coins. Reset from the menu.",
} as const;

export const padLabel: Record<LandBonus, string> = {
  none: "Lawn",
  x5: "x5",
  square: "x²",
  x50: "x50",
  flat5000: "5000x",
};
