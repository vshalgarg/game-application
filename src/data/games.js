import { FaTicketAlt, FaDice, FaFlagCheckered, FaTimes } from "react-icons/fa";

export const popularGames = [
  {
    id: "tic-tac-toe",
    title: "Tic Tac Toe",
    genre: "Puzzle",
    path: "/tic-tac-toe",
    icon: FaTimes,
    accent: "cyan",
  },
  {
    id: "ludo",
    title: "Ludo",
    genre: "Board",
    path: "/createjoin-room",
    icon: FaDice,
    accent: "purple",
  },
  {
    id: "tambola",
    title: "Tambola",
    genre: "Numbers",
    path: "/tambola-mode",
    icon: FaTicketAlt,
    accent: "cyan",
  },
  {
    id: "racing",
    title: "Speed Legends",
    genre: "Racing",
    path: null,
    icon: FaFlagCheckered,
    accent: "purple",
  },
];
