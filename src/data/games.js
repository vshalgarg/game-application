import { FaChessKnight, FaDice, FaFlagCheckered, FaTimes } from "react-icons/fa";

export const popularGames = [
  {
    id: "tic-tac-toe",
    title: "Tic Tac Toe",
    genre: "Puzzle",
    path: "/game-mode",
    icon: FaTimes,
    accent: "cyan",
  },
  {
    id: "ludo",
    title: "Ludo",
    genre: "Board",
    path: "/ludoGame-mode",
    icon: FaDice,
    accent: "purple",
  },
  {
    id: "chess",
    title: "Chess",
    genre: "Strategy",
    path: null,
    icon: FaChessKnight,
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
